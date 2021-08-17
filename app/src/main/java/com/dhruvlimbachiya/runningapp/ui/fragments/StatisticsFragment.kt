package com.dhruvlimbachiya.runningapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.CustomMarkerView
import com.dhruvlimbachiya.runningapp.others.TrackingUtility
import com.dhruvlimbachiya.runningapp.ui.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
        setUpBarChart()
    }

    /**
     * Set up the bar chart.
     */
    private fun setUpBarChart() {
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
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

        mViewModel.runsSortedByDate.observe(viewLifecycleOwner){
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(),it[i].avgSpeedInKMH) } // List of Bar Entry.
                val barDataSet = BarDataSet(allAvgSpeeds,"Avg Speed Over Time").apply {
                    valueTextColor = Color.GREEN
                    color = ContextCompat.getColor(requireContext(),R.color.colorAccent)
                }

                barChart.data = BarData(barDataSet)
                barChart.marker = CustomMarkerView(requireContext(),R.layout.marker_view,it)
                barChart.invalidate()
            }
        }

    }
}