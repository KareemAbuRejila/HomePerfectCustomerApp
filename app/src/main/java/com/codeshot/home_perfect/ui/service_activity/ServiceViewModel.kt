package com.codeshot.home_perfect.ui.service_activity

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.SERVICE_Providers
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Service
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath

class ServiceViewModel : ViewModel() {
    private var sharedPreferences: SharedPreferences? = null
    val providersOption = MutableLiveData<FirestoreRecyclerOptions<Provider>>()

    fun getInstance(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = Common.SHARED_PREF(context = context)
    }

    fun getAll(providersId: List<String>) {
        val queryAll = PROVIDERS_REF.whereIn(
            FieldPath.documentId().toString(),
            providersId
        )
        providersOption.value = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(queryAll, Provider::class.java)
            .build()
    }

    fun getMales(providersId: List<String>) {
        val queryAll = PROVIDERS_REF.whereIn(
            FieldPath.documentId().toString(),
            providersId
        ).whereEqualTo("gender", "Male")
        providersOption.value = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(queryAll, Provider::class.java)
            .build()
    }

    fun getFemales(providersId: List<String>) {
        val queryAll = PROVIDERS_REF.whereIn(
            FieldPath.documentId().toString(),
            providersId
        ).whereEqualTo("gender", "Female")
        providersOption.value = FirestoreRecyclerOptions.Builder<Provider>()
            .setQuery(queryAll, Provider::class.java)
            .build()
    }
}