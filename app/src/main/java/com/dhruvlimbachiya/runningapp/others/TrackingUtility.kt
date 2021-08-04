package com.dhruvlimbachiya.runningapp.others

import android.Manifest
import android.content.Context
import android.os.Build
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
}