package com.dhruvlimbachiya.runningapp.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvlimbachiya.runningapp.db.Run
import com.dhruvlimbachiya.runningapp.others.SortType
import com.dhruvlimbachiya.runningapp.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RunRepository
) : ViewModel() {

    private val runsSortedByDate = repository.getAllRunsSortedByDateFromDb()
    private val runsSortedByAvgSpeed = repository.getAllRunsSortedByAvgSpeedFromDb()
    private val runsSortedByDistance = repository.getAllRunsSortedByDistanceFromDb()
    private val runsSortedByTime = repository.getAllRunsSortedByTimeInMillisFromDb()
    private val runsSortedByCalorieBurned = repository.getAllRunsSortedByCalorieBurnedFromDb()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE // Default sort state.

    init {
        runs.addSource(runsSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByCalorieBurned) { result ->
            if (sortType == SortType.CALORIES_BURNED) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByTime) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                result?.let { runs.value = it }
            }
        }
    }

    /**
     * Function for inserting run.
     */
    fun insertRun(run: Run) = viewModelScope.launch {
        repository.insertRunIntoDb(run)
    }

    /**
     * Sort the runs based on sort type.
     */
    fun sortRuns(sortType: SortType) = when (sortType) {
        SortType.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTime.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCalorieBurned.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }
}