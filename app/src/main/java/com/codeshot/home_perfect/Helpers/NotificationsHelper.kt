package com.codeshot.home_perfect_provider.Helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.codeshot.home_perfect.R

class NotificationsHelper(base: Context?) : ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannels() {
        val carsChannel = NotificationChannel(
              HomePerfect_CHANNEL_ID
            , HomePerfect_CHANNEL_NAME
            , NotificationManager.IMPORTANCE_DEFAULT
        )
        val defaultSound =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        carsChannel.enableLights(true)
        carsChannel.enableVibration(true)
        carsChannel.setSound(defaultSound,AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build())
        carsChannel.lightColor = getColor(R.color.colorPrimaryDark)
        carsChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(carsChannel)
    }


    val manager: NotificationManager?
        get() {
            if (notificationManager == null) notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return notificationManager
        }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getCarsNotification(
        title: String?,
        content: String?,
        contentIntent: PendingIntent?,
        soundUri: Uri?
    ): Notification.Builder {
        return Notification.Builder(
                applicationContext,
                HomePerfect_CHANNEL_ID
            )
            .setContentText(content)
            .setContentTitle(title)
            .setSound(soundUri,AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build())
            .setContentIntent(contentIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
    }

    companion object {
        private const val HomePerfect_CHANNEL_ID = "com.codeshot.home_perfect_provider"
        private const val HomePerfect_CHANNEL_NAME = "Home Perfect ProviderApp"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannels()
    }
}