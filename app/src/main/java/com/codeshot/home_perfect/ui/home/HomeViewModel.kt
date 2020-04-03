package com.codeshot.home_perfect.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.SERVICES_REF
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Service
import com.google.firebase.firestore.Query

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    private val providersMDL=MutableLiveData<List<Provider>>().apply {
        PROVIDERS_REF.orderBy("rate", Query.Direction.DESCENDING)
            .whereGreaterThan("rate", 3).limit(10)
            .orderBy("online")

            .get().addOnSuccessListener { querySnapshot ->
            val providerList=ArrayList<Provider>()
            for (document in querySnapshot){
                val provider=document.toObject(Provider::class.java)
                provider.id=document.id
                providerList.add(provider)
            }
            value=providerList
        }
    }
    val providers: LiveData<List<Provider>> = providersMDL

    private val servicesMDL=MutableLiveData<List<Service>>().apply {
        SERVICES_REF.get().addOnSuccessListener { querySnapshot ->
            val servicesList=ArrayList<Service>()
            for (document in querySnapshot){
                val service=document.toObject(Service::class.java)
                service.id=document.id
                servicesList.add(service)
            }
            value=servicesList
        }
    }
    val services:LiveData<List<Service>> =servicesMDL



}