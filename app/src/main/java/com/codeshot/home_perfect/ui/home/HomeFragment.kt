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
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.SERVICES_REF
import com.codeshot.home_perfect.common.Common.SERVICE_Providers
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.OnlineProvidersAdapters
import com.codeshot.home_perfect.adapters.ServicesAdapters
import com.codeshot.home_perfect.adapters.TopProvidersAdapters
import com.codeshot.home_perfect.databinding.FragmentHomeBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Service
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.codeshot.home_perfect.ui.service_activity.ServiceActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import java.util.*

class HomeFragment : Fragment(), TopProvidersAdapters.OnItemTopProviderListener,
    ServicesAdapters.OnItemClickListener, OnlineProvidersAdapters.OnOnlineProviderListener {


    private var homeViewModel: HomeViewModel? = HomeViewModel()
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var providersAdapters: TopProvidersAdapters
    private lateinit var servicesAdapters: ServicesAdapters
    private lateinit var onlineProvidersAdapters: OnlineProvidersAdapters
    private lateinit var acProgressBaseDialog: ACProgressBaseDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (homeViewModel == null) {
            homeViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
                .create(HomeViewModel::class.java)
        }

        homeViewModel!!.getTopProviders()
        providersAdapters = TopProvidersAdapters(homeViewModel!!.providersOption.value!!)
        providersAdapters.setOnCLickListener(this)


        homeViewModel!!.getServices()
        servicesAdapters = ServicesAdapters(homeViewModel!!.servicesOption.value!!)
        servicesAdapters.setOnClickListener(this)

        homeViewModel!!.getOnlineProviders()
        onlineProvidersAdapters =
            OnlineProvidersAdapters(homeViewModel!!.onlineProvidersOption.value!!)
        onlineProvidersAdapters.setOnOnlineProviderCLickListener(this)
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

        homeBinding.topProviderAdapter = providersAdapters
        homeBinding.servicesAdapter = servicesAdapters
        homeBinding.onlineProviderAdapter = onlineProvidersAdapters

        homeBinding.homeLayout.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        providersAdapters.startListening()
        servicesAdapters.startListening()
        onlineProvidersAdapters.startListening()
    }


    override fun onItemClicked(providerId: String) {
        PROVIDERS_REF.document(providerId).get()
            .addOnSuccessListener {
                val provider = it.toObject(Provider::class.java)
                provider!!.id = it.id
                val profileDialog =
                    ProviderProfileDialog(
                        provider
                    )
                profileDialog.show(
                    requireActivity().supportFragmentManager,
                    "ProviderProfileDialog"
                )
            }

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


