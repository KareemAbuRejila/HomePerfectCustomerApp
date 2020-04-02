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
import androidx.lifecycle.ViewModelProviders
import cc.cloudist.acplibrary.ACProgressBaseDialog
import com.codeshot.home_perfect.Common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.Common.Common.SERVICES_REF
import com.codeshot.home_perfect.Common.Common.SERVICE_Providers
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.adapters.RecentProvidersAdapters
import com.codeshot.home_perfect.adapters.ServicesAdapters
import com.codeshot.home_perfect.adapters.TopProvidersAdapters
import com.codeshot.home_perfect.databinding.FragmentHomeBinding
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Service
import com.codeshot.home_perfect.ui.provider_profile.ProviderProfileDialog
import com.codeshot.home_perfect.ui.service_activity.ServiceActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import java.util.*

class HomeFragment : Fragment(), TopProvidersAdapters.OnItemTopProviderLinstener,
    ServicesAdapters.OnItemClickListener {


    private var homeViewModel: HomeViewModel? = HomeViewModel()
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var providersAdapters: TopProvidersAdapters
    private lateinit var servicesAdapters: ServicesAdapters
    private lateinit var recentProvidersAdapters: RecentProvidersAdapters
    private lateinit var acProgressBaseDialog: ACProgressBaseDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (homeViewModel == null) {
            homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        }
        // Access a Cloud Firestore instance from Activity
        val queryTopProvider = PROVIDERS_REF.orderBy("rate", Query.Direction.DESCENDING)
            .whereGreaterThan("rate", 3).limit(10)
            .orderBy("online")
        val options = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(queryTopProvider, Provider::class.java)
            .build()
        providersAdapters = TopProvidersAdapters(options)


//        homeViewModel!!.providers.observe(this, Observer {
//            providersAdapters.setList(it)
//        })
        val servicesOption = FirestoreRecyclerOptions.Builder<Service>()
            .setQuery(SERVICES_REF.limit(9), Service::class.java)
            .build()
        servicesAdapters = ServicesAdapters(servicesOption)
        recentProvidersAdapters = RecentProvidersAdapters(options)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return homeBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel!!.providers.observe(viewLifecycleOwner, Observer { providrslist ->
            providersAdapters.setList(providrslist)
            recentProvidersAdapters.setList(providrslist)
        })
        homeBinding.topProviderAdapter = providersAdapters
        providersAdapters.setOnCLickLinstener(this)


        homeViewModel!!.services.observe(viewLifecycleOwner, Observer { servicesList ->
            servicesAdapters.setList(servicesList)
        })
        homeBinding.servicesAdapter = servicesAdapters
        servicesAdapters.setOnClickListener(this)

        homeBinding.recentProviderAdapter = recentProvidersAdapters
        homeBinding.homeLayout.visibility = View.VISIBLE


    }

    override fun onStart() {
        super.onStart()
        providersAdapters.startListening()
        servicesAdapters.startListening()
        recentProvidersAdapters.startListening()
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
//        Toast.makeText(activity,service.providers!!.size.toString(),Toast.LENGTH_SHORT).show()
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


