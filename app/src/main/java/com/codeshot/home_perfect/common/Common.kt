package com.codeshot.home_perfect.common


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import cc.cloudist.acplibrary.ACProgressBaseDialog
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressFlower
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.remote.FCMClient
import com.codeshot.home_perfect.remote.IFCMService
import com.google.firebase.firestore.FirebaseFirestore

object Common {

    var CURRENT_USER_KEY =""
    var CURRENT_TOKEN=""
    var CURRENT_USER_PHONE=""
    var CURRENT_USER_NAME=""
    var CURRENT_USER_IMAGE=""
    var CURRENT_LOCATION:Location?=null
    var SERVICE_Providers:List<String>?=ArrayList<String>()


    val ROOT_REF = FirebaseFirestore.getInstance()
    val USERS_REF = ROOT_REF.collection("Users")
    val PROVIDERS_REF = ROOT_REF.collection("Providers")
    val SERVICES_REF= ROOT_REF.collection("Services")
    val REQUESTS_REF= ROOT_REF.collection("Requests")
    val RATINGS = ROOT_REF.collection("Ratings")
    val NOTIFICATIONS_REF = ROOT_REF.collection("Notifications")

    private const val FCM_URL = "https://fcm.googleapis.com/"
    val TOKENS_REF= ROOT_REF.collection("Tokens")

    val FCM_SERVICE:IFCMService
    get() = FCMClient.getClient(FCM_URL)!!.create(IFCMService::class.java)

    fun LOADING_DIALOG(context: Context): ACProgressBaseDialog {
        val acProgressBaseDialog = ACProgressFlower.Builder(context)
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .themeColor(Color.WHITE)
            .text(context.resources.getString(R.string.please_waite))
            .fadeColor(Color.DKGRAY).build()
        return acProgressBaseDialog
    }

    fun SHARED_PREF(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "com.codeshot.home_perfect",
            Context.MODE_PRIVATE
        )
    }


    const val FILE_PROVIDER_AUTHORITY = "com.codeshot.home_perfect.provider"
    const val FILE_PROVIDER_PATH =
        "/Android/data/com.codeshot.home_perfect/files/Pictures"
}