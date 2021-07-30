package com.dhruvlimbachiya.runningapp.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */


@Entity(tableName = "runs")
data class Run(
    var image: Bitmap? = null,
    var timeStamp: Long = 0L, // When your run was - Date.
    var avgSpeedInKMH: Float = 0f, // What was your AvgSpeed in KiloMeter per Hour.
    var distanceInMeters: Int = 0, // How much distance you covered in Meters.
    var timeInMillis: Long = 0L, // How long your run was in millis
    var calorieBurned: Int = 0 // How much calorie you burned.
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}