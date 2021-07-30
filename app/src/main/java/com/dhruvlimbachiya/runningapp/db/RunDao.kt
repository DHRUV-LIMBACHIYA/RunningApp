package com.dhruvlimbachiya.runningapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    // Get all the runs sorted by timeStamp column.
    @Query("SELECT * FROM runs ORDER BY timeStamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    // Get all the runs sorted by AvgSpeedInKHM column.
    @Query("SELECT * FROM runs ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    // Get all the runs sorted by distanceInMeters column.
    @Query("SELECT * FROM runs ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    // Get all the runs sorted by timeInMillis column.
    @Query("SELECT * FROM runs ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>>

    // Get all the runs sorted by calorieBurned column.
    @Query("SELECT * FROM runs ORDER BY calorieBurned DESC")
    fun getAllRunsSortedByCalorieBurned(): LiveData<List<Run>>

    /**
     * Functions to display statistics of run.
     */

    // Calculate and return total running time in millis.
    @Query("SELECT SUM(timeInMillis) from runs")
    fun getTotalTimeInMillis(): LiveData<Long>

    // Calculate the return total avg speed of runner in KMP
    @Query("SELECT AVG(avgSpeedInKMH) from runs")
    fun getTotalAvgSpeedInKMP(): LiveData<Int>

    // Calculate and return total distance covered by runner in float
    @Query("SELECT SUM(distanceInMeters) from runs")
    fun getTotalDistanceCovered(): LiveData<Float>

    // Calculate and return total calories burned.
    @Query("SELECT SUM(calorieBurned) from runs")
    fun getTotalCalorieBurned(): LiveData<Int>
}