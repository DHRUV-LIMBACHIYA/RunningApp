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
    private var isServiceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private var totalTimeRunInSeconds =
        MutableLiveData<Long>() // LiveData to display time in notifications [00:00:00]

    private lateinit var mCurrentNotificationBuilder: NotificationCompat.Builder

    companion object {
        var isTracking = MutableLiveData<Boolean>()
        var pathPoints = MutableLiveData<PolyLines>()
        var totalTimeRunInMillis =
            MutableLiveData<Long>() // LiveData for TrackingFragment stop-watch TextView.
    }

    /**
     * Add/Post initial values to LiveData vars.
     */
    private fun postInitialValues() {
        isTracking.value = false
        pathPoints.value = mutableListOf()
        totalTimeRunInSeconds.value = 0L
        totalTimeRunInMillis.value = 0L
    }

    override fun onCreate() {
        super.onCreate()
        mCurrentNotificationBuilder = baseNotificationBuilder

        postInitialValues()

        // Observe the changes in the isTracking LiveData.
        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
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
                        startTimer()
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.i("Action service paused")
                    pauseService()
                }

                ACTION_STOP_SERVICE -> {
                    Timber.i("Action service stopped")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Kill the service,reset vars & LiveData.
     */
    private fun killService() {
        postInitialValues()
        isServiceKilled = true
        isFirstTime = true
        stopForeground(true) // Remove the notification from the status bar.
        stopSelf() // Stop the service manually.
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
            while (isTracking.value == true) {
                // Time difference between time started and now.
                elapsedTime = System.currentTimeMillis() - timerStarted

                // Post the total time in millis by calculating the previous elapsedTime + current elapsed time.
                totalTimeRunInMillis.postValue(totalElapsedTime + elapsedTime)

                /**
                 * This if block will only execute when totalTimeRunInMills will completed one entire second in millis.
                 * One entire seconds = 1000L.
                 * It will allow to enter in the if block when totalTimeRunInMillis values are like 1000L,2000L,3000L as so on...(just an example)
                 */
                if (totalTimeRunInMillis.value!! >= lastSecondsTimestamp + 1000L) {
                    totalTimeRunInSeconds.postValue(
                        totalTimeRunInSeconds.value!! + 1
                    )  // Post the total seconds elapsed.
                    lastSecondsTimestamp += 1000L // Add 1 sec in millis in global variable which will be used when runner transition from pause => resume state.
                }

                delay(TIME_ELAPSED_DELAY) // Minor delay.
            }

            // Add elapsedTime with totalElapsed time so whenever runner start re-tracking it will start from where the runner left previously.
            totalElapsedTime += elapsedTime
        }
    }

    /**
     * Pause the tracking.
     */
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
     * Update the notification action button base on tracking state.
     */
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        // Action Text
        val actionText =
            if (isTracking) "Pause" else "Start" // Action Button Text based on current tracking state.

        // Intent for Action Buttons.
        val actionIntent = Intent(this, TrackingService::class.java).apply {
            action = if (isTracking) ACTION_PAUSE_SERVICE else ACTION_START_OR_RESUME_SERVICE
        }

        // Request Code
        val requestCode = if (isTracking) 1 else 2

        // Pending Intent for Action Buttons.
        val actionPendingIntent =
            PendingIntent.getService(this, requestCode, actionIntent, FLAG_UPDATE_CURRENT)

        // Action Button Icon.
        val actionIcon =
            if (isTracking) R.drawable.ic_pause_black_24dp else R.drawable.ic_baseline_play

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Remove all the action button before updating with the new action buttons.
        mCurrentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true // Make this field accessible.
            set(
                mCurrentNotificationBuilder,
                ArrayList<NotificationCompat.Action>()
            ) // set an empty list of action buttons.
        }

        mCurrentNotificationBuilder = baseNotificationBuilder
            .addAction(actionIcon, actionText, actionPendingIntent)

        if(!isServiceKilled){
            notificationManager.notify(
                NOTIFICATION_ID,
                mCurrentNotificationBuilder.build()
            ) // Update the notification with action buttons.
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

        // Observe the changes in totalTimeRunInSeconds LiveData.
        totalTimeRunInSeconds.observe(this) {
            if(!isServiceKilled){
                mCurrentNotificationBuilder.setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(
                    NOTIFICATION_ID,
                    mCurrentNotificationBuilder.build()
                ) // Update the notification with Elapsed Time in HH:mm:ss format.
            }
        }
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