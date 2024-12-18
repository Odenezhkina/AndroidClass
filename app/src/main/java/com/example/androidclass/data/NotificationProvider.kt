package com.example.androidclass.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.androidclass.R

class NotificationProvider(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    fun updateNotification(
        id: Int,
        title: String,
        contentText: String,
        onContentClickIntent: PendingIntent
    ) {
        notificationManager.notify(
            id,
            createNotification(title, contentText, onContentClickIntent)
        )
    }

    fun createNotification(
        title: String,
        contentText: String,
        onContentClickIntent: PendingIntent,
    ): Notification {
        return NotificationCompat.Builder(
            context, context.getString(R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(contentText)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setChannelId(context.getString(R.string.default_notification_channel_id))
            .setContentIntent(onContentClickIntent)
            .build()
    }


    private fun createNotificationChannel() {
        NotificationChannel(
            context.getString(R.string.default_notification_channel_id),
            context.getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).also {
            notificationManager.createNotificationChannel(it)
        }
    }
}
