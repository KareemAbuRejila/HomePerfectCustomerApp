package com.codeshot.home_perfect.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.SERVICES_REF
import com.codeshot.home_perfect.common.Common.SHARED_PREF
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.Service
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.core.FirestoreClient

class HomeViewModel : ViewModel() {
    private val TAG = "HomeViewModel"

    var sharedPreferences: SharedPreferences? = null

    val topProviders = MutableLiveData<List<Provider>>()
    val onlineProviders = MutableLiveData<List<Provider>>()
    val servicesOption = MutableLiveData<FirestoreRecyclerOptions<Service>>()
    val onlineProvidersOption = MutableLiveData<FirestoreRecyclerOptions<Provider>>()

    fun getInstance(context: Context) {
        sharedPreferences = SHARED_PREF(context = context)
    }

    fun getTopProviders() {
        val providers = ArrayList<Provider>()
        PROVIDERS_REF.orderBy("rate", Query.Direction.DESCENDING)
            .whereGreaterThan("rate", 3).limit(10)
            .orderBy("online")
            .addSnapshotListener(MetadataChanges.INCLUDE)
            { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                if (!querySnapshot!!.metadata.isFromCache) {
                    Log.d(TAG, "Your TopProviders From Server")
                    providers.clear()
                    querySnapshot.forEach { doc ->
                        val provider = doc.toObject(Provider::class.java)
                        provider.id = doc.id
                        providers.add(provider)
                    }
                } else {
                    Log.d(TAG, "Your TopProviders From Cache")
                    providers.clear()
                    querySnapshot.forEach { doc ->
                        val provider = doc.toObject(Provider::class.java)
                        provider.id = doc.id
                        provider.online = false
                        providers.add(provider)
                    }
                }
                topProviders.value = providers
            }
    }

    fun getOnlineProviders() {
        val providers = ArrayList<Provider>()
        PROVIDERS_REF
            .whereEqualTo("online", true)
            .addSnapshotListener(MetadataChanges.INCLUDE)
            { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                if (!querySnapshot!!.metadata.isFromCache) {
                    Log.d(TAG, "Your OnlineProviders From Server")
                    providers.clear()
                    querySnapshot.forEach { doc ->
                        val provider = doc.toObject(Provider::class.java)
                        provider.id = doc.id
                        providers.add(provider)
                    }
                } else {
                    Log.d(TAG, "Your OnlineProviders From Cache")
                    providers.clear()
//                    querySnapshot.forEach { doc ->
//                        val provider = doc.toObject(Provider::class.java)
//                        provider.id=doc.id
//                        provider.online = false
//                        providers.add(provider)
//                    }
                }
                onlineProviders.value = providers
            }

    }

    fun getServices() {
        servicesOption.value = FirestoreRecyclerOptions.Builder<Service>()
            .setQuery(SERVICES_REF.limit(9), Service::class.java)
            .build()
    }


}