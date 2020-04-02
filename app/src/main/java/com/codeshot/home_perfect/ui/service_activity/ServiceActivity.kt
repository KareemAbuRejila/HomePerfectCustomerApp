package com.codeshot.home_perfect.ui.service_activity

import android.os.Bundle
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeshot.home_perfect.Common.Common
import com.codeshot.home_perfect.Common.StandardActivity
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.ProvidersAdapters
import com.codeshot.home_perfect.databinding.ActivityServiceBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath

class ServiceActivity : StandardActivity(), ProvidersAdapters.OnItemClickLinstener {
    private lateinit var activityServiceBinding: ActivityServiceBinding
    private lateinit var serviceViewModel: ServiceViewModel
    private var serviceId: String? = null
    private var providersId: List<String>? = ArrayList()
    private var providersAdapter: ProvidersAdapters? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityServiceBinding = DataBindingUtil.setContentView(this, R.layout.activity_service)
        serviceViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(ServiceViewModel::class.java)
        activityServiceBinding.tabLayoutService.selectTab(
            activityServiceBinding.tabLayoutService.getTabAt(
                1
            )
        )
        activityServiceBinding.back.setOnClickListener { onBackPressed() }
        checkIntent()

        val query = Common.PROVIDERS_REF.whereIn(FieldPath.documentId().toString(), providersId!!)
        val options = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(query, Provider::class.java)
            .build()
        providersAdapter = ProvidersAdapters(options)
        providersAdapter!!.setOnCLickLinstener(this)
        activityServiceBinding.adapter = providersAdapter

//        serviceViewModel.providers.observe(this, Observer { prviders ->
//            providersAdapter.setList(prviders)
//        })
        activityServiceBinding.tabLayoutService.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.text == "All") {
                    val query = Common.PROVIDERS_REF.whereIn(
                        FieldPath.documentId().toString(),
                        providersId!!
                    )
                    val options = FirestoreRecyclerOptions.Builder<Provider>()
                        .setQuery(query, Provider::class.java)
                        .build()
                    providersAdapter!!.updateOptions(options)
                } else if (tab!!.text == "Male") {
                    val query = Common.PROVIDERS_REF.whereIn(
                        FieldPath.documentId().toString(),
                        providersId!!
                    ).whereEqualTo("gender", "Male")
                    val options = FirestoreRecyclerOptions.Builder<Provider>()
                        .setQuery(query, Provider::class.java)
                        .build()
                    providersAdapter!!.updateOptions(options)
                } else if (tab!!.text == "Female") {
                    val query = Common.PROVIDERS_REF.whereIn(
                        FieldPath.documentId().toString(),
                        providersId!!
                    ).whereEqualTo("gender", "Female")
                    val options = FirestoreRecyclerOptions.Builder<Provider>()
                        .setQuery(query, Provider::class.java)
                        .build()
                    providersAdapter!!.updateOptions(options)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                return
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                return
            }
        })


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

    override fun onStart() {
        super.onStart()
        providersAdapter!!.startListening()

    }

    private fun checkIntent() {
        if (intent != null) {
            serviceId = intent.getStringExtra("serviceId").toString()
            providersId = intent.getStringArrayListExtra("providersId")

        }
    }

    override fun onItemClicked(providerId: String) {
        Common.PROVIDERS_REF.document(providerId).get()
            .addOnSuccessListener {
                val provider = it.toObject(Provider::class.java)
                provider!!.id = it.id
                val profileDialog =ProviderProfileDialog(provider)
                profileDialog.show(this.supportFragmentManager,"ProviderProfileDialog")
            }
    }


}