package com.example.androidclass.data

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder
import com.example.androidclass.R
import com.example.androidclass.presentation.MainActivity


class LocationTrackingService : Service() {
    private var locationManager: LocationManager? = null
    private var notificationProvider: NotificationProvider? = null

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        notificationProvider = NotificationProvider(this)
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationProvider?.createNotification(
            this.getString(R.string.default_notification_content_title),
            this.getString(R.string.default_notification_content_text),
            createPendingIntentToApp()
        )
        notification?.let { startForeground(DEFAULT_NOTIFICATION_ID, it) }
        // start location tracking
        // display notification for foreground work
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, LOCATION_UPDATE_PERIOD_MILLIS, MIN_CHANGED_DISTANCE_KM
        ) {
            // update existing notification
            notificationProvider?.updateNotification(
                DEFAULT_NOTIFICATION_ID,
                this.getString(R.string.default_notification_content_title),
                "$it",
                createPendingIntentToApp(),
            )
        }
        return START_STICKY
    }

    private fun createPendingIntentToApp(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java)
        return PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager = null
        notificationProvider = null
    }

    companion object {
        private const val LOCATION_UPDATE_PERIOD_MILLIS = 1000L
        private const val MIN_CHANGED_DISTANCE_KM = 1f
        private const val DEFAULT_NOTIFICATION_ID = 1
    }
}
