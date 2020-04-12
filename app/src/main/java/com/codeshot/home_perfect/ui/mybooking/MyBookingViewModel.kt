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
    val bookings = MutableLiveData<List<Request>>()

    fun getInstance(context: Context) {
        if (sharedPreferences != null) return
        sharedPreferences = SHARED_PREF(context = context)
    }

    fun getRequest() {
        USERS_REF.document(CURRENT_USER_KEY)
            .get().addOnSuccessListener { userDoc ->
                val requestsLocal = ArrayList<Request>()
                val user = userDoc!!.toObject(User::class.java)
                val requests = user!!.requests
                requests!!.forEach { id ->
                    REQUESTS_REF.document(id)
                        .get().addOnSuccessListener {
                            val request = it.toObject(Request::class.java)!!
                            requestsLocal.add(request)
                            bookings.value = requestsLocal
                        }
                }
            }
    }
}