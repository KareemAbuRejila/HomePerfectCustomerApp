package com.codeshot.home_perfect.ui.mybooking

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.REQUESTS_REF
import com.codeshot.home_perfect.common.Common.SHARED_PREF
import com.codeshot.home_perfect.common.Common.USERS_REF
import com.codeshot.home_perfect.models.Request
import com.codeshot.home_perfect.models.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.gson.Gson

class MyBookingViewModel : ViewModel() {

    private var sharedPreferences: SharedPreferences? = null
    val bookingsOption = MutableLiveData<FirestoreRecyclerOptions<Request>>()

    fun getInstance(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = SHARED_PREF(context = context)
    }
    fun getRequest(){
        USERS_REF.document(CURRENT_USER_KEY)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) return@addSnapshotListener
                val user = documentSnapshot!!.toObject(User::class.java)
                if (user!!.requests!!.isNotEmpty()) {
                    val requestQuery = REQUESTS_REF.whereIn(FieldPath.documentId(), user.requests!!)
                    bookingsOption.value = FirestoreRecyclerOptions.Builder<Request>()
                        .setQuery(requestQuery, Request::class.java)
                        .build()
                } else bookingsOption.value = null
            }
    }
}