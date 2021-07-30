package com.dhruvlimbachiya.runningapp.repositories

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.dhruvlimbachiya.runningapp.db.Run
import com.dhruvlimbachiya.runningapp.db.RunDao
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

class RunRepository @Inject constructor(
    private val runDao: RunDao
) {

    /**
     * Functions for normal DB operations.
     */
    // Insert run into DB
    suspend fun insertRunIntoDb(run: Run) = runDao.insertRun(run)

    // Delete run into DB
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    /**
     * Functions for getting runs data in particular order(category-wise).
     */
    // Get all the runs sorted by timeStamp column from Database.
    fun getAllRunsSortedByDateFromDb() = runDao.getAllRunsSortedByDate()

    // Get all the runs sorted by AvgSpeedInKHM column from Database.
    fun getAllRunsSortedByAvgSpeedFromDb() = runDao.getAllRunsSortedByAvgSpeed()

    // Get all the runs sorted by distanceInMeters column from Database.
    fun getAllRunsSortedByDistanceFromDb() = runDao.getAllRunsSortedByDistance()

    // Get all the runs sorted by timeInMillis column from Database.
    fun getAllRunsSortedByTimeInMillisFromDb() = runDao.getAllRunsSortedByTimeInMillis()

    // Get all the runs sorted by calorieBurned column from Database.
    fun getAllRunsSortedByCalorieBurnedFromDb() = runDao.getAllRunsSortedByCalorieBurned()

    /**
     * Functions for getting statistical data.
     */

    // Get total running time in millis from Database.
    fun getTotalTimeInMillisFromDb() = runDao.getTotalTimeInMillis()

    // Get total avg speed of runner in KMP from Database.
    fun getTotalAvgSpeedInKMPFromDb() = runDao.getTotalAvgSpeedInKMP()

    // Get total distance covered by runner in float from Database.
    fun getTotalDistanceCoveredFromDb() = runDao.getTotalDistanceCovered()

    // Get total calories burned of runner from Database.
    fun getTotalCalorieBurnedFromDb() = runDao.getTotalCalorieBurned()

}