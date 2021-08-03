package com.dhruvlimbachiya.runningapp.others

import android.graphics.Color

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

object Constants {
    const val RUN_DATABASE_NAME = "runs_db"

    const val REQUEST_CODE_LOCATION_PERMISSION = 1

    // Tracking Service Actions
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_NAVIGATE_TO_TRACKING_FRAGMENT = "ACTION_NAVIGATE_TO_TRACKING_FRAGMENT"

    // Notifications constants.
    const val NOTIFICATION_CHANNEL_ID  = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking Run"
    const val NOTIFICATION_ID = 1

    // Location Constants
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val LOCATION_UPDATE_FASTEST_INTERVAL = 2000L

    // PolyLine Options
    const val POLYLINE_WIDTH = 8f
    const val POLYLINE_COLOR = Color.RED
    const val CAMERA_ZOOM = 15f

    const val TIME_ELAPSED_DELAY = 50L
}