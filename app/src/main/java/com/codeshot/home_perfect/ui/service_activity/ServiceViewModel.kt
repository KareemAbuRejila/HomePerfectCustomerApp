package com.codeshot.home_perfect.ui.service_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.Common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.Common.Common.SERVICE_Providers
import com.codeshot.home_perfect.models.Provider

class ServiceViewModel() : ViewModel() {

    private val _providers=MutableLiveData<List<Provider>>().apply {
        val privdersList= ArrayList<Provider>()
        SERVICE_Providers!!.forEach {providerId ->
            PROVIDERS_REF.document(providerId).get()
                .addOnSuccessListener {document ->
                    val provider =document.toObject(Provider::class.java)
//                    privdersList.add(provider!!)
                }
        }
        value=privdersList
    }

    var providers:LiveData<List<Provider>> =_providers
}