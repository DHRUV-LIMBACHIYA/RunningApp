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
import com.dhruvlimbachiya.runningapp.others.Constants.TIME_ELAPSED_DELAY
import com.dhruvlimbachiya.runningapp.others.TrackingUtility
import com.dhruvlimbachiya.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 02-08-2021.
 */

typealias PolyLine = MutableList<LatLng> // Single line on map which contains list of LatLng.
typealias PolyLines = MutableList<PolyLine> // List containing multiple PolyLine(List of LatLng).

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isFirstTime = true

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private var totalTimeRunInSeconds = MutableLiveData<Long>() // LiveData to display time in notifications [00:00:00]

    companion object {
        var isTracking = MutableLiveData<Boolean>()
        var pathPoints = MutableLiveData<PolyLines>()
        var totalTimeRunInMillis = MutableLiveData<Long>() // LiveData for TrackingFragment stop-watch TextView.
    }

    /**
     * Add/Post initial values to LiveData vars.
     */
    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        totalTimeRunInSeconds.postValue(0L)
        totalTimeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()

        // Observe the changes in the isTracking LiveData.
        isTracking.observe(this) {
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
                        startTimer()
                        Timber.i("Resuming the service...")
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.i("Action service paused")
                }

                ACTION_STOP_SERVICE -> {
                    Timber.i("Action service stopped")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var timerStarted = 0L
    private var elapsedTime = 0L
    private var totalElapsedTime = 0L
    private var lastSecondsTimestamp = 0L

    private fun startTimer() {
        addEmptyPolyLine() // Add Empty PolyLine on fresh START or on RESUME
        isTracking.postValue(true) // Start tracking location.
        timerStarted = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value == true){
                // Time difference between time started and now.
                elapsedTime = System.currentTimeMillis() - timerStarted

                // Post the total time in millis by calculating the previous elapsedTime + current elapsed time.
                totalTimeRunInMillis.postValue(totalElapsedTime + elapsedTime)

                /**
                 * This if block will only execute when totalTimeRunInMills will completed one entire second in millis.
                 * One entire seconds = 1000L.
                 * It will allow to enter in the if block when totalTimeRunInMillis values are like 1000L,2000L,3000L as so on...(just an example)
                 */
                if(totalTimeRunInMillis.value!! >= lastSecondsTimestamp + 1000L){
                    totalTimeRunInSeconds.postValue(totalTimeRunInSeconds.value?: 0L + 1)  // Post the total seconds elapsed.
                    lastSecondsTimestamp += 1000L // Add 1 sec in millis in global variable which will be used when runner transition from pause => resume state.
                }

                delay(TIME_ELAPSED_DELAY) // Minor delay.
            }

            // Add elapsedTime with totalElapsed time so whenever runner start re-tracking it will start from where the runner left previously.
            totalElapsedTime += elapsedTime
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
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
        } else {
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
                Timber.i("Location List : ${pathPoints.value?.toList()}")
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
        }
            ?: pathPoints.postValue(mutableListOf(mutableListOf())) // If the PolyLines list has no data then add empty PolyLines with an empty PolyLine(LatLng)
    }

    /**
     * Function responsible for starting a foreground service displaying notification.
     */
    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true) // Start tracking.

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel if device OS is >= OREO.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        // Start the foreground service and displays notification.
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }


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