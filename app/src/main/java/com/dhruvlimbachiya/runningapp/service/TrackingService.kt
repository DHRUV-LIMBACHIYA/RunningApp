package com.dhruvlimbachiya.runningapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_PAUSE_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_STOP_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.NOTIFICATION_CHANNEL_ID
import com.dhruvlimbachiya.runningapp.others.Constants.NOTIFICATION_CHANNEL_NAME
import com.dhruvlimbachiya.runningapp.others.Constants.NOTIFICATION_ID
import com.dhruvlimbachiya.runningapp.ui.MainActivity
import timber.log.Timber

/**
 * Created by Dhruv Limbachiya on 02-08-2021.
 */

class TrackingService : LifecycleService() {

    private var isFirstTime = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstTime) {
                        startForegroundService()
                        isFirstTime = false
                    } else {
                        Timber.i("Resuming the service...")
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.i("Action service paused")
                }

                ACTION_STOP_SERVICE -> {
                    Timber.i("Action service stopped")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }


    /**
     * Function responsible for starting a foreground service displaying notification.
     */
    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel if device OS is >= OREO.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        // Build notification.
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            setContentTitle(getString(R.string.app_name))
            setContentText("00:00:00")
            setAutoCancel(true)
            setOngoing(true)
            setContentIntent(getMainActivityPendingIntent())
        }

        // Start the foreground service and displays notification.
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    /**
     * Get the MainActivity as the pending intent.
     */
    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action =
                ACTION_NAVIGATE_TO_TRACKING_FRAGMENT  // Custom action to indicate the navigation from MainActivity to Tracking Activity.
        },
        FLAG_UPDATE_CURRENT
    )


    /**
     * Creates the Notification Channel for the device greater or equal to OREO.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        // Construct notification channel
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        // Create notification channel using NotificationManagerCompat class.
        notificationManager.createNotificationChannel(channel)
    }

}