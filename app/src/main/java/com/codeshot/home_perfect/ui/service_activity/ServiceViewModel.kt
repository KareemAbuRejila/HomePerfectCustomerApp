package com.codeshot.home_perfect.ui.service_activity

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.models.Provider
import com.google.firebase.firestore.MetadataChanges

class ServiceViewModel : ViewModel() {
    private val TAG = "ServiceViewModel"
    private var sharedPreferences: SharedPreferences? = null
    val providers = MutableLiveData<ArrayList<Provider>>()
    private var providersId: List<String>? = ArrayList<String>()

    fun getInstance(context: Context, providersId: List<String>) {
        if (sharedPreferences != null) return
        sharedPreferences = Common.SHARED_PREF(context = context)
        this.providersId = providersId
    }


    private val all = ArrayList<Provider>()
    fun getAllProviders() {
        PROVIDERS_REF.addSnapshotListener(MetadataChanges.INCLUDE)
        { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) return@addSnapshotListener
            if (!querySnapshot!!.metadata.isFromCache) {
                Log.d(TAG, "Your Providers From Server")
                all.clear()
                querySnapshot.forEach { doc ->
                    if (providersId!!.contains(doc.id)) {
                        val provider = doc.toObject(Provider::class.java)
                        provider.id = doc.id
                        all.add(provider)
                    }
                }
            } else {
                Log.d(TAG, "Your Providers From Cache")
                all.clear()
                querySnapshot.forEach { doc ->
                    if (providersId!!.contains(doc.id)) {
                        val provider = doc.toObject(Provider::class.java)
                        provider.id = doc.id
                        provider.online = false
                        all.add(provider)
                    }

                }
            }
        }
    }

    fun getAll() {
        providers.value = all
    }

    fun getMales() {
        val males = ArrayList<Provider>()
        all.forEach {
            if (it.gender == "Male")
                males.add(it)
        }
        providers.value = males

    }

    fun getFemales() {
        val females = ArrayList<Provider>()
        all.forEach {
            if (it.gender == "Female")
                females.add(it)
        }
        providers.value = females

    }


    fun getMales2() {
        val providersLocal = ArrayList<Provider>()
        PROVIDERS_REF.whereEqualTo("gender", "Male")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                if (!querySnapshot!!.metadata.isFromCache) {
                    Log.d(TAG, "Your Providers From Server")
                    providersLocal.clear()
                    querySnapshot.forEach { doc ->
                        if (providersId!!.contains(doc.id)) {
                            val provider = doc.toObject(Provider::class.java)
                            provider.id = doc.id
                            providersLocal.add(provider)
                        }
                    }
                } else {
                    Log.d(TAG, "Your Providers From Cash")
                    providersLocal.clear()
                    querySnapshot.forEach { doc ->
                        if (providersId!!.contains(doc.id)) {
                            val provider = doc.toObject(Provider::class.java)
                            provider.id = doc.id
                            provider.online = false
                            providersLocal.add(provider)
                        }

                    }
                }

                providers.value = providersLocal
            }
    }

    fun getFemales2() {
        val providersLocal = ArrayList<Provider>()
        PROVIDERS_REF.whereEqualTo("gender", "Female")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                if (!querySnapshot!!.metadata.isFromCache) {
                    Log.d(TAG, "Your Providers From Server")
                    providersLocal.clear()
                    querySnapshot.forEach { doc ->
                        if (providersId!!.contains(doc.id)) {
                            val provider = doc.toObject(Provider::class.java)
                            provider.id = doc.id
                            providersLocal.add(provider)
                        }
                    }
                } else {
                    Log.d(TAG, "Your Providers From Cash")
                    providersLocal.clear()
                    querySnapshot.forEach { doc ->
                        if (providersId!!.contains(doc.id)) {
                            val provider = doc.toObject(Provider::class.java)
                            provider.id = doc.id
                            provider.online = false
                            providersLocal.add(provider)
                        }

                    }
                }

                providers.value = providersLocal
            }
    }


}