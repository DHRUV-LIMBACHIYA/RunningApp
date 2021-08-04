package com.dhruvlimbachiya.runningapp.others

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import androidx.core.location.LocationCompat
import com.dhruvlimbachiya.runningapp.service.PolyLine
import com.google.android.gms.maps.model.LatLngBounds
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

/**
 * Created by Dhruv Limbachiya on 02-08-2021.
 */

object TrackingUtility {

    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }

    /**
     * Format the millis into TimeStamp string.
     */
    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliSeconds = ms

        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
        milliSeconds -= TimeUnit.HOURS.toMillis(hours) // Minus the milliseconds which represent hours.

        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
        milliSeconds -= TimeUnit.MINUTES.toMillis(minutes) // Minus the milliseconds which represent minutes

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
        if (!includeMillis) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
        milliSeconds -= TimeUnit.SECONDS.toMillis(seconds) // Minus the milliseconds which represent seconds
        milliSeconds /= 10
        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliSeconds)

    }

    /**
     * Calculate the given LatLng positions distance into meters of type INT.
     */
    fun calculateDistanceInMeters(polyLine: PolyLine) : Int {
        var distance = 0f
        for (i in 0..polyLine.size - 2){
            val firstPosition = polyLine[i]
            val nextPosition = polyLine[i + 1]

            val result = FloatArray(1) // Hold the results
            // Find out the distance between point 1 to point 2 using LAT & LNG.
            Location.distanceBetween(
                firstPosition.latitude,
                firstPosition.longitude,
                nextPosition.latitude,
                nextPosition.longitude,
                result
            )

            distance += result[0] // Result[0] will return distance
        }

       return distance.toInt()
    }
}