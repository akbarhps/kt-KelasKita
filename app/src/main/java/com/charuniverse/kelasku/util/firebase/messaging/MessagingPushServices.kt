package com.charuniverse.kelasku.util.firebase.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.charuniverse.kelasku.R
import com.charuniverse.kelasku.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingPushServices : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val channelId = "KelasKita"
        val messageData = message.data

        val title = messageData["title"] ?: "Pemberitahuan Baru"
        val body = messageData["body"]

        val destination = when (messageData["destination"]) {
            "assignment" -> R.id.assignmentDetailFragment
            "announcement" -> R.id.announcementDetailFragment
            else -> R.id.announcementFragment
        }

        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(destination)
            .setArguments(bundleOf("id" to messageData["id"]))
            .createPendingIntent()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.img_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Push Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

}