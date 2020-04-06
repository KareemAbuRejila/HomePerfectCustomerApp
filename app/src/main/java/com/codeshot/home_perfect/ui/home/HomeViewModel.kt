package com.codeshot.home_perfect.ui.home

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.SERVICES_REF
import com.codeshot.home_perfect.common.Common.SHARED_PREF
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Service
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.core.FirestoreClient

class HomeViewModel : ViewModel() {
    var sharedPreferences: SharedPreferences? = null

    val providersOption = MutableLiveData<FirestoreRecyclerOptions<Provider>>()
    val servicesOption = MutableLiveData<FirestoreRecyclerOptions<Service>>()
    val onlineProvidersOption = MutableLiveData<FirestoreRecyclerOptions<Provider>>()

    fun getInstance(context: Context) {
        sharedPreferences = SHARED_PREF(context = context)
    }

    fun getTopProviders() {
        val queryTopProvider = PROVIDERS_REF.orderBy("rate", Query.Direction.DESCENDING)
            .whereGreaterThan("rate", 3).limit(10)
            .orderBy("online")
        val options = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(queryTopProvider, Provider::class.java)
            .build()

        providersOption.value = options



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