package com.dhruvlimbachiya.runningapp.others

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by Dhruv Limbachiya on 02-08-2021.
 */

object RunAppUtility {

    /**
     * Function to check app has location permission or not.
     * "ACCESS_BACKGROUND_LOCATION" is only available on version Q or above Q.
     * @return boolean - true if it has permissions or false if it hasn't.
     */
    fun hasLocationPermission(context: Context) =
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }else{
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
}