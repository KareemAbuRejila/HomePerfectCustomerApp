package com.codeshot.home_perfect.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.SERVICES_REF
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Service
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {

    val providersOption = MutableLiveData<FirestoreRecyclerOptions<Provider>>()
    val servicesOption = MutableLiveData<FirestoreRecyclerOptions<Service>>()
    val onlineProvidersOption = MutableLiveData<FirestoreRecyclerOptions<Provider>>()


    fun getTopProviders() {
        val queryTopProvider = PROVIDERS_REF.orderBy("rate", Query.Direction.DESCENDING)
            .whereGreaterThan("rate", 3).limit(10)
            .orderBy("online")

        providersOption.value = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(queryTopProvider, Provider::class.java)
            .build()

    }

    fun getOnlineProviders() {
        val queryOnlineProvider = PROVIDERS_REF.whereEqualTo("online", true)
        onlineProvidersOption.value = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(queryOnlineProvider, Provider::class.java)
            .build()
    }

    fun getServices() {
        servicesOption.value = FirestoreRecyclerOptions.Builder<Service>()
            .setQuery(SERVICES_REF.limit(9), Service::class.java)
            .build()
    }



}