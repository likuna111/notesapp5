package com.example.todonotesapp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.todonotesapp.R
import com.example.todonotesapp.view.MyNotesActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMsgService"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, message.from)
        Log.d(TAG, message.notification?.body)
        setupNotification(message.notification?.body)
    }

    private fun setupNotification(body: String?) {
            val channelId =  "Todo ID"
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, MyNotesActivity::class.java)
// Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Todo Notes App")
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setSound(ringtone)
                .setAutoCancel(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,"Todo Notes",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)


    }
}