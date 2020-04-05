package com.codeshot.home_perfect.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.TOKENS_REF
import com.codeshot.home_perfect.models.Token
import com.codeshot.home_perfect.models.User
import com.codeshot.home_perfect_provider.Helpers.NotificationsHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class MyFreebaseMessagingService : FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    private var notificationsHelper: NotificationsHelper? = null
    private var user: User? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        notificationManager =
            baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationsHelper = NotificationsHelper(baseContext)

        val sharedPreferences =
            getSharedPreferences("com.codeshot.home_perfect", Context.MODE_PRIVATE)
        val userGSON = sharedPreferences.getString("user", null)
        if (userGSON != null)
            user = Gson().fromJson(userGSON, User::class.java)


        val dataMessage=remoteMessage.data
        val title=dataMessage["title"]
        val requestStatus=dataMessage["requestStatus"]
        if (title=="Booking"){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showStatusRequestNotification(requestStatus)
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showStatusRequestNotification(requestStatus: String?) {
        val defaultSound =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val contentIntent = PendingIntent.getActivity(
            baseContext,
            0, Intent(), PendingIntent.FLAG_ONE_SHOT
        )
        val builder: Notification.Builder = notificationsHelper!!.getCarsNotification(
            "Booking",
            "Your Request : $requestStatus",
            contentIntent,
            defaultSound
        )
        builder.setAutoCancel(true)
        notificationsHelper!!.manager!!.notify(1, builder.build())

        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            Toast.makeText(this@MyFreebaseMessagingService,"Provider has $requestStatus Your Request", Toast.LENGTH_LONG).show()
        })

    }


    override fun onNewToken(s: String) {
        super.onNewToken(s)
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { instanceIdResult ->
                val newToken = instanceIdResult.token
                updateTokenToServer(newToken) //When have new token, i need update to our realtime db
                val sharedPreferences = getSharedPreferences(
                    "com.codeshot.home_perfect",
                    Context.MODE_PRIVATE
                )
                sharedPreferences.edit().putString("token", s).apply()
            }
        Log.d("NEW_TOKEN", s)
    }
    private fun updateTokenToServer(newToken: String) {
        val token = Token(newToken)
        if (FirebaseAuth.getInstance()
                .currentUser != null
        ) //if already login, must update Token
        {
            TOKENS_REF.document(CURRENT_USER_KEY)
                .set(token)
                .addOnSuccessListener { Log.i("Saved Token", "Yesssssssssssssssssssssss") }
                .addOnFailureListener { e -> Log.i("ERROR TOKEN", e.message!!) }

        }
    }
}