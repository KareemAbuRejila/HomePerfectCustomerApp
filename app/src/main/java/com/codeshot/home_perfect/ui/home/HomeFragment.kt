package com.codeshot.home_perfect.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.common.Common.SERVICE_Providers
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.OnlineProvidersAdapters
import com.codeshot.home_perfect.adapters.ProvidersAdapter
import com.codeshot.home_perfect.adapters.ServicesAdapters
import com.codeshot.home_perfect.databinding.FragmentHomeBinding
import com.codeshot.home_perfect.models.Service
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.codeshot.home_perfect.ui.service_activity.ServiceActivity
import java.util.*

class HomeFragment : Fragment(), ProvidersAdapter.OnItemClickListener,
    ServicesAdapters.OnItemClickListener, OnlineProvidersAdapters.OnOnlineProviderListener {


    private var homeViewModel: HomeViewModel? = HomeViewModel()
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var topProvidersAdapter: ProvidersAdapter
    private lateinit var servicesAdapters: ServicesAdapters
    private lateinit var onlineProvidersAdapter: ProvidersAdapter
    private lateinit var loadingDialog: ACProgressBaseDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (homeViewModel == null) {
            homeViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                .create(HomeViewModel::class.java)
        }

        homeViewModel!!.getTopProviders()
        topProvidersAdapter = ProvidersAdapter()
        topProvidersAdapter.setViewType(topProvidersAdapter.PROVIDER_TOP)
        topProvidersAdapter.setOnCLickListener(this)


        homeViewModel!!.getServices()
        servicesAdapters = ServicesAdapters(homeViewModel!!.servicesOption.value!!)
        servicesAdapters.setOnClickListener(this)

        homeViewModel!!.getOnlineProviders()
        onlineProvidersAdapter = ProvidersAdapter()
        onlineProvidersAdapter.setViewType(topProvidersAdapter.PROVIDER_ONLINE)
        onlineProvidersAdapter.setOnCLickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeBinding.topProviderAdapter = topProvidersAdapter
        homeViewModel!!.topProviders.observe(viewLifecycleOwner, Observer {
            topProvidersAdapter.setList(it)
        })


        homeBinding.servicesAdapter = servicesAdapters
        homeViewModel!!.servicesOption.observe(viewLifecycleOwner, Observer {
            servicesAdapters.updateOptions(it)
        })
        homeBinding.onlineProviderAdapter = onlineProvidersAdapter
        homeViewModel!!.onlineProviders.observe(viewLifecycleOwner, Observer {
            onlineProvidersAdapter.setList(it)
        })

        homeBinding.homeLayout.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        servicesAdapters.startListening()
    }


    override fun onItemClicked(providerId: String) {
        val profileDialog =
            ProviderProfileDialog(
                providerId
            )
        profileDialog.show(
            requireActivity().supportFragmentManager,
            "ProviderProfileDialog"
        )


    }

    override fun onItemClicked(service: Service) {
        SERVICE_Providers = service.providers
        if (service.providers!!.isNotEmpty()) {
            val serviceIntent = Intent(activity, ServiceActivity::class.java)
            serviceIntent.putExtra("providersId", service.providers as ArrayList<String>)
            serviceIntent.putExtra("serviceId", service.id)
            serviceIntent.putExtra("serviceName", service.name)
            startActivity(serviceIntent)
        } else
            Toast.makeText(activity, "No Providers in ${service.name}", Toast.LENGTH_SHORT).show()

    }


}


