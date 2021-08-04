package com.dhruvlimbachiya.runningapp.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.Constants
import com.dhruvlimbachiya.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

/**
 * Created by Dhruv Limbachiya on 04-08-2021.
 */

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    @SuppressLint("VisibleForTests")
    fun provideFusedLocationProviderClient(
        @ApplicationContext applicationContext: Context
    ) = FusedLocationProviderClient(applicationContext) // Create an instance of FusedLocationProviderClient.


    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext applicationContext: Context
    ): PendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        Intent(applicationContext, MainActivity::class.java).also {
            it.action =
                Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT  // Custom action to indicate the navigation from MainActivity to Tracking Activity.
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext applicationContext: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(applicationContext,
        Constants.NOTIFICATION_CHANNEL_ID
    ).apply {
        setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        setContentTitle(applicationContext.getString(R.string.app_name))
        setContentText("00:00:00")
        setAutoCancel(true)
        setOngoing(true)
        setContentIntent(pendingIntent)
    }
}