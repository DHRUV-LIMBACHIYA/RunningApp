package com.dhruvlimbachiya.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhruvlimbachiya.runningapp.db.Run
import com.dhruvlimbachiya.runningapp.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RunRepository
) : ViewModel(){

    val runSortedByData = repository.getAllRunsSortedByDateFromDb()

    fun insertRun(run: Run) = viewModelScope.launch {
        repository.insertRunIntoDb(run)
    }
}