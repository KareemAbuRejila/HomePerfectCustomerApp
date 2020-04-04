package com.codeshot.home_perfect.ui.service_activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.ProvidersAdapters
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.StandardActivity
import com.codeshot.home_perfect.databinding.ActivityServiceBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FieldPath

class ServiceActivity : StandardActivity(), ProvidersAdapters.OnItemClickListener {
    private lateinit var activityServiceBinding: ActivityServiceBinding
    private lateinit var serviceViewModel: ServiceViewModel
    private var serviceId: String? = null
    private var providersId: List<String>? = ArrayList()
    private var providersAdapter: ProvidersAdapters? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityServiceBinding = DataBindingUtil.setContentView(this, R.layout.activity_service)
        serviceViewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(ServiceViewModel::class.java)
        serviceViewModel.getInstance(this)
        activityServiceBinding.tabLayoutService.selectTab(
            activityServiceBinding.tabLayoutService.getTabAt(
                1
            )
        )
        activityServiceBinding.back.setOnClickListener { onBackPressed() }
        checkIntent()
        setUpList()
        var listType = false
        activityServiceBinding.button.setOnClickListener {
            if (!listType) {
                activityServiceBinding.rvProviders.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                listType = true
            } else {
                activityServiceBinding.rvProviders.layoutManager =
                    GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
                listType = false
            }


        }

    }

    private fun setUpList() {
        val query = Common.PROVIDERS_REF.whereIn(FieldPath.documentId().toString(), providersId!!)
        val options = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(query, Provider::class.java)
            .build()
        providersAdapter = ProvidersAdapters(options)
        providersAdapter!!.setViewType(providersAdapter!!.PROVIDERS_TYPE)
        providersAdapter!!.setOnCLickListener(this)
        activityServiceBinding.adapter = providersAdapter
        serviceViewModel.providersOption.observe(this, Observer {
            providersAdapter!!.updateOptions(it)
        })
        activityServiceBinding.tabLayoutService.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                when {
                    tab!!.text == "All" -> serviceViewModel.getAll(providersId = providersId!!)
                    tab.text == "Male" -> serviceViewModel.getMales(providersId = providersId!!)
                    tab.text == "Female" -> serviceViewModel.getFemales(providersId = providersId!!)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                return
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                return
            }
        })
    }

    override fun onStart() {
        super.onStart()
        providersAdapter!!.startListening()

    }

    private fun checkIntent() {
        if (intent != null) {
            serviceId = intent.getStringExtra("serviceId")!!.toString()
            providersId = intent.getStringArrayListExtra("providersId")

        }
    }

    override fun onItemClicked(providerId: String) {
        Common.PROVIDERS_REF.document(providerId).get()
            .addOnSuccessListener {
                val provider = it.toObject(Provider::class.java)
                provider!!.id = it.id
                val profileDialog = ProviderProfileDialog(provider)
                profileDialog.show(this.supportFragmentManager, "ProviderProfileDialog")
            }
    }


}
