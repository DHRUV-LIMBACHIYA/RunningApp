package com.dhruvlimbachiya.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.dhruvlimbachiya.runningapp.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val runRepository: RunRepository
) : ViewModel(){

    val totalTime = runRepository.getTotalTimeInMillisFromDb()
    val totalDistance = runRepository.getTotalDistanceCoveredFromDb()
    val totalCaloriesBurned = runRepository.getTotalCalorieBurnedFromDb()
    val totalAvgSpeed = runRepository.getTotalAvgSpeedInKMPFromDb()

    val runsSortedByDate = runRepository.getAllRunsSortedByDateFromDb()
}