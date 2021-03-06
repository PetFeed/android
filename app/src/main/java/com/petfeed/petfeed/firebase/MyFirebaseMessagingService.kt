package com.petfeed.petfeed.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val pushDataMap = remoteMessage!!.data
        Log.e("asdf", "${remoteMessage}")
        Log.e("asdf", "${remoteMessage.data}")
        Log.e("asdf", "${remoteMessage.from}")
        sendNotification(pushDataMap)
    }

    private fun sendNotification(dataMap: Map<String, String>) {
        val pref = applicationContext.getSharedPreferences("test", Context.MODE_PRIVATE)
        val edit = pref.edit()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val nBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(dataMap["title"])
                .setContentText(dataMap["body"])
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000))
                .setLights(Color.WHITE, 1500, 1500)
                .setContentIntent(contentIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (pref.getBoolean("isNotice", true)) {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationChannel = NotificationChannel("petfeed", "Petfeed", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Petfeed"
                    enableLights(true)
                    enableVibration(true)
                    lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                }
                notificationManager.createNotificationChannel(notificationChannel)
                edit.putBoolean("isNotice", false)
                edit.apply()
            }
            nBuilder.setChannelId("petfeed")
        }
        val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = pref.getInt("notificationId", 0)
        nManager.notify(id /* ID of notification */, nBuilder.build())
        edit.putInt("notificationId", id + 1)
        edit.apply()
    }
}