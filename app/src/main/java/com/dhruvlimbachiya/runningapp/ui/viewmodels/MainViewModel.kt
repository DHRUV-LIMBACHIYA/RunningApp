package com.dhruvlimbachiya.runningapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.dhruvlimbachiya.runningapp.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RunRepository
) : ViewModel(){
}