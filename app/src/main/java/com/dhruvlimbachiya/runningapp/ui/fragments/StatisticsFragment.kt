package com.dhruvlimbachiya.runningapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.TrackingUtility
import com.dhruvlimbachiya.runningapp.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val mViewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeToObservers()
    }

    /**
     * Observe the live changes.
     */
    private fun subscribeToObservers() {
        mViewModel.totalTime.observe(viewLifecycleOwner){
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTimeRun
            }
        }

        mViewModel.totalDistance.observe(viewLifecycleOwner){
            it?.let {
                val totalDistanceInKm = round(it * 10f) / 10f
                tvTotalDistance.text = "${totalDistanceInKm}km"
            }
        }

        mViewModel.totalCaloriesBurned.observe(viewLifecycleOwner){
            it?.let {
                tvTotalCalories.text = "${it}kcal"
            }
        }

        mViewModel.totalAvgSpeed.observe(viewLifecycleOwner){
            it?.let {
                val totalAvgSpeedInKmh = round(it * 10f) / 10f
                tvAverageSpeed.text = "${totalAvgSpeedInKmh}km/h"
            }
        }
    }
}