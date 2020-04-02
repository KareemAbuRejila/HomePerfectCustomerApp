package com.codeshot.home_perfect.ui.mybooking

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codeshot.home_perfect.Common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.Common.Common.REQUESTS_REF
import com.codeshot.home_perfect.Common.Common.USERS_REF
import com.codeshot.home_perfect.models.Request
import com.codeshot.home_perfect.models.User
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source

class MyBookingViewModel : ViewModel() {

    private lateinit var context: Context

    private val _text = MutableLiveData<String>().apply {
        value = "This is My Booking Fragment"
    }
    val text: LiveData<String> = _text

    private val _bookings=MutableLiveData<List<Request>>().apply {
        val requests=ArrayList<Request>()
        USERS_REF.document(CURRENT_USER_KEY)
            .get(Source.SERVER).addOnSuccessListener {
                val user=it.toObject(User::class.java)
                REQUESTS_REF
                    .orderBy("time",Query.Direction.DESCENDING)
                    .get().addOnSuccessListener {queryDoc->
                        queryDoc.forEach {doc->
                            if (user!!.requests!!.contains(doc.id)){
                                val request=doc.toObject(Request::class.java)
                                request.id=doc.id
                                requests.add(request)
                            }

                        }
                        value=requests
                    }
            }
    }
    var bookings:MutableLiveData<List<Request>> =MutableLiveData()

    fun getRequest(){
        val requests=ArrayList<Request>()
        USERS_REF.document(CURRENT_USER_KEY)
            .get(Source.SERVER).addOnSuccessListener {
                val user=it.toObject(User::class.java)
                REQUESTS_REF
                    .orderBy("time",Query.Direction.DESCENDING)
                    .get().addOnSuccessListener {queryDoc->
                        queryDoc.forEach {doc->
                            if (user!!.requests!!.contains(doc.id)){
                                val request=doc.toObject(Request::class.java)
                                request.id=doc.id
                                requests.add(request)
                            }

                        }
                        bookings.value=requests
                    }
            }
    }
}