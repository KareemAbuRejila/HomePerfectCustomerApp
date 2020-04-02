package com.codeshot.home_perfect.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.codeshot.home_perfect.Common.Common.REQUESTS_REF
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.databinding.ActivityRequestBinding
import com.codeshot.home_perfect.models.Request

class RequestActivity : AppCompatActivity() {
    private lateinit var activityRequest:ActivityRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRequest=DataBindingUtil
            .setContentView(this, R.layout.activity_request)


    }
    private fun getRequestFromServer():Request?{
        var request:Request?=null
        if (getRequestId()!=null){
            REQUESTS_REF.document(getRequestId()!!)
                .get().addOnSuccessListener {
                    request=it.toObject(Request::class.java)
                }
            return request
        }
        return request
    }
    private fun getRequestId():String?{
        val request:String?
        if (intent!=null){
            if (intent.extras!!.getString("requestId")!=null){
                request= intent.extras!!.getString("requestId").toString()
                return request
            }
        }
        return null
    }

}
