package com.codeshot.home_perfect.ui.wishList

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.PROVIDERS_REF
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.MetadataChanges

class WishListViewModel : ViewModel() {
    private var sharedPreferences: SharedPreferences? = null
    val providers = MutableLiveData<List<Provider>>()

    fun getInstance(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = Common.SHARED_PREF(context = context)
    }


    fun getProviders() {
        USERS_REF.document(CURRENT_USER_KEY)
            .get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val wishList = user!!.wishList
                val providersLocal = ArrayList<Provider>()
                PROVIDERS_REF.addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) return@addSnapshotListener
                    if (!querySnapshot!!.metadata.isFromCache) {
                        providersLocal.clear()
                        querySnapshot.forEach { doc ->
                            if (wishList.contains(doc.id)) {
                                val provider = doc.toObject(Provider::class.java)
                                provider.id = doc.id
                                providersLocal.add(provider)
                            }
                        }
                    } else {
                        providersLocal.clear()
                        querySnapshot.forEach { doc ->
                            if (wishList.contains(doc.id)) {
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
}