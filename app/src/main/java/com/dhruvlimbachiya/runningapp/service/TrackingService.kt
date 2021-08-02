package com.dhruvlimbachiya.runningapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_PAUSE_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_STOP_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.LOCATION_UPDATE_FASTEST_INTERVAL
import com.dhruvlimbachiya.runningapp.others.Constants.LOCATION_UPDATE_INTERVAL
import com.dhruvlimbachiya.runningapp.others.Constants.NOTIFICATION_CHANNEL_ID
import com.dhruvlimbachiya.runningapp.others.Constants.NOTIFICATION_CHANNEL_NAME
import com.dhruvlimbachiya.runningapp.others.Constants.NOTIFICATION_ID
import com.dhruvlimbachiya.runningapp.others.TrackingUtility
import com.dhruvlimbachiya.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

/**
 * Created by Dhruv Limbachiya on 02-08-2021.
 */

typealias PolyLine = MutableList<LatLng> // Single line on map which contains list of LatLng.
typealias PolyLines = MutableList<PolyLine> // List containing multiple PolyLine(List of LatLng).

class TrackingService : LifecycleService() {

    private var isFirstTime = true
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        var isTracking = MutableLiveData<Boolean>()
        var pathPoints = MutableLiveData<PolyLines>()
    }

    /**
     * Add/Post initial values to LiveData vars.
     */
    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this) // Create an instance of FusedLocationProviderClient.

        // Observe the changes in the isTracking LiveData.
        isTracking.observe(this){
            updateLocationTracking(it)
        }
    }

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
     * Function responsible for requesting location update through FusedLocationProviderClient
     * 1. Construct the LocationRequest.
     * 2. Client request for the location updates by specifying the request,callback & Looper
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermission(this)) {
                // Construct a location request for FusedLocationProvider Client
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = LOCATION_UPDATE_FASTEST_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }

                // Client request for the location update.
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else {
            // Detach client from getting the location updates by specifying locationCallback.
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    /**
     * Callback for receiving location updates,extract LatLng from LocationResult and pass it to addPathPoints() fun.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (isTracking.value == true) {
                locationResult.locations.let { locations ->
                    for (location in locations) {
                        addPathPoints(location)
                        Timber.i("Updated Location : Lat => ${location.latitude} Lng => ${location.longitude}")
                    }
                }
            }
        }
    }

    /**
     * Add the LatLng object into last element of PolyLines list.
     */
    private fun addPathPoints(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos) // Get the last element(PolyLines => (Last PolyLine - ListOf<LatLng>)) and add an pos(LatLng) to it.
                pathPoints.postValue(this) // Update the pathPoint livedata.
            }
        }
    }

    /**
     * Add Empty PolyLine(List<LatLng>) into PolyLines List indicating runner "Stop/Pause" Activity.
     */
    private fun addEmptyPolyLine() {
        pathPoints.value?.apply {
            add(mutableListOf()) // add an empty polyLine(LatLng) in PolyLines list.
            pathPoints.postValue(this)
        } ?: pathPoints.postValue(mutableListOf(mutableListOf())) // If the PolyLines list has no data then add empty PolyLines with an empty PolyLine(LatLng)
    }

    /**
     * Function responsible for starting a foreground service displaying notification.
     */
    private fun startForegroundService() {
        addEmptyPolyLine()
        isTracking.postValue(true) // Start tracking.

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