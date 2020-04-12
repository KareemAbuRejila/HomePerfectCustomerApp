package com.codeshot.home_perfect.ui.service_activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.ProvidersAdapter
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.StandardActivity
import com.codeshot.home_perfect.databinding.ActivityServiceBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.google.android.material.tabs.TabLayout

class ServiceActivity : StandardActivity(),
    ProvidersAdapter.OnItemClickListener
    , TabLayout.OnTabSelectedListener {
    private lateinit var activityServiceBinding: ActivityServiceBinding
    private lateinit var serviceViewModel: ServiceViewModel
    private var serviceId: String? = null
    private var providersId: List<String>? = ArrayList()
    private lateinit var providersAdapter: ProvidersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityServiceBinding = DataBindingUtil.setContentView(this, R.layout.activity_service)
        serviceViewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(ServiceViewModel::class.java)
        checkIntent()
        serviceViewModel.getInstance(context = this, providersId = providersId!!)
        serviceViewModel.getAllProviders()

        activityServiceBinding.back.setOnClickListener { onBackPressed() }
        setUpList()
        setUpTypeView()
        activityServiceBinding.tabLayoutService.selectTab(
            activityServiceBinding.tabLayoutService.getTabAt(
                1
            )
        )
    }

    private fun setUpTypeView() {
        var listType = false
        activityServiceBinding.btnviewType.setOnClickListener {
            if (!listType) {
                activityServiceBinding.rvProviders.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                activityServiceBinding.btnviewType.setImageResource(R.drawable.ic_view_list_black_24dp)
                listType = true
            } else {
                activityServiceBinding.rvProviders.layoutManager =
                    GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false)
                activityServiceBinding.btnviewType.setImageResource(R.drawable.ic_view_grid_black_24dp)
                listType = false
            }

        }
    }

    private fun setUpList() {
        providersAdapter = ProvidersAdapter()
        providersAdapter.setViewType(providersAdapter.PROVIDER_SERVICE)
        providersAdapter.setOnCLickListener(this)
        activityServiceBinding.adapter = providersAdapter
        serviceViewModel.providers.observe(this, Observer {
            providersAdapter.setList(it)
        })
        activityServiceBinding.tabLayoutService.addOnTabSelectedListener(this)
    }

    private fun checkIntent() {
        if (intent != null) {
            serviceId = intent.getStringExtra("serviceId")!!.toString()
            providersId = intent.getStringArrayListExtra("providersId")
        }
    }

    override fun onItemClicked(providerId: String) {
        val profileDialog = ProviderProfileDialog(providerId)
        profileDialog.show(
            this.supportFragmentManager,
            "ProviderProfileDialog"
        )
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        when {
            tab!!.text == "All" -> serviceViewModel.getAll()
            tab.text == "Male" -> serviceViewModel.getMales()
            tab.text == "Female" -> serviceViewModel.getFemales()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        return
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when {
            tab!!.text == "All" -> serviceViewModel.getAll()
            tab.text == "Male" -> serviceViewModel.getMales()
            tab.text == "Female" -> serviceViewModel.getFemales()
        }
    }


}
