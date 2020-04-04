package com.codeshot.home_perfect.ui.wishList

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.models.Provider
import com.codeshot.home_perfect.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath

class WishListViewModel : ViewModel() {
    private var sharedPreferences: SharedPreferences? = null
    val providersOptions = MutableLiveData<FirestoreRecyclerOptions<Provider>>()

    fun getInstance(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = Common.SHARED_PREF(context = context)
    }


    fun getProviders() {
        USERS_REF.document(CURRENT_USER_KEY)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                val user = documentSnapshot!!.toObject(User::class.java)
                if (user!!.wishList.isNotEmpty()) {
                    val query =
                        Common.PROVIDERS_REF.whereIn(FieldPath.documentId(), user!!.wishList)
                    providersOptions.value = FirestoreRecyclerOptions.Builder<Provider>()
                        .setQuery(query, com.codeshot.home_perfect.models.Provider::class.java)
                        .build()
                } else providersOptions.value = null

            }
    }
}