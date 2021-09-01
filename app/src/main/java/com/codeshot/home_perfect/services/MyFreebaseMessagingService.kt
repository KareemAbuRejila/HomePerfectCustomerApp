package com.codeshot.home_perfect.services

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import com.codeshot.home_perfect.HomeActivity
import com.codeshot.home_perfect.R
import com.codeshot.home_perfect.common.Common.CURRENT_USER_KEY
import com.codeshot.home_perfect.common.Common.TOKENS_REF
import com.codeshot.home_perfect.init.MyApplication
import com.codeshot.home_perfect.models.Token
import com.codeshot.home_perfect.models.User
import com.codeshot.home_perfect.util.UIUtil
import com.codeshot.home_perfect_provider.Helpers.NotificationsHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class MyFreebaseMessagingService : FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    private var notificationsHelper: NotificationsHelper? = null
    private val REQUEST_CODE_HOMEACTIVITY = 2
    private val REQUEST_CODE_NOTHING = 0

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
        val requestStatus = dataMessage["requestStatus"]
        val requestId = dataMessage["requestId"]!!
        if (title=="Booking"){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    when (requestStatus) {
                        "accepted" -> showStatusRequestNotification(
                            baseContext.resources.getString(
                                R.string.accepted
                            ), requestId, 1
                        )
                        "canceled" -> showStatusRequestNotification(
                            baseContext.resources.getString(
                                R.string.canceled
                            ), requestId, 2
                        )
                        "inProgress" -> showStatusRequestNotification(
                            baseContext.resources.getString(
                                R.string.in_progress
                            ), requestId, 3
                        )
                        "done" -> {
                            showStatusRequestNotification(
                                baseContext.resources.getString(R.string.done),
                                requestId,
                                4
                            )
                        }
                    }
                }
        }

    }


    private fun showStatusRequestNotification(
        requestStatus: String?,
        requestId: String,
        idNoti: Int
    ) {
        val defaultSound =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val homeIntent = Intent(baseContext, HomeActivity::class.java)
        homeIntent.putExtra("type", "rating")
        val contentIntent = when (idNoti) {
            4 -> {
                PendingIntent.getActivity(
                    baseContext,
                    REQUEST_CODE_HOMEACTIVITY, homeIntent, PendingIntent.FLAG_ONE_SHOT
                )
            }
            else -> {
                PendingIntent.getActivity(
                    baseContext,
                    REQUEST_CODE_NOTHING, Intent(), PendingIntent.FLAG_ONE_SHOT
                )
            }
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationsHelper!!.getCarsNotificationApi26(
                baseContext.resources.getString(R.string.booking),
                baseContext.resources.getString(R.string.your_request) + " $requestStatus id:$requestId",
                contentIntent!!,
                defaultSound
            )
        } else {
            notificationsHelper!!.getCarsNotification(
                "Booking",
                "Your Request : $requestStatus",
                contentIntent!!,
                defaultSound
            )
        }

        builder.setAutoCancel(true)
        builder.setDeleteIntent(contentIntent)
        notificationsHelper!!.manager!!.notify(idNoti, builder.build())
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(
                this@MyFreebaseMessagingService,
                baseContext.resources.getString(R.string.your_request) + " $requestStatus",
                Toast.LENGTH_LONG
            ).show()
        }

    }



    override fun onNewToken(s: String) {
        super.onNewToken(s)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val newToken = task.result
                updateTokenToServer(newToken) //When have new token, i need update to our realtime db
                val sharedPreferences = getSharedPreferences(
                    "com.codeshot.home_perfect",
                    Context.MODE_PRIVATE
                )
                sharedPreferences.edit().putString("token", s).apply()
            }
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