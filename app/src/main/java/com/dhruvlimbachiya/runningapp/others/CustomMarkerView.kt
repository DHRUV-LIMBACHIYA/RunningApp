package com.dhruvlimbachiya.runningapp.others

import android.content.Context
import com.dhruvlimbachiya.runningapp.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Dhruv Limbachiya on 16-08-2021.
 */

class CustomMarkerView(
    context: Context,
    layoutId: Int,
    val runs: List<Run>
) : MarkerView(context,layoutId){

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        if(e == null){
            return
        }

        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeedInKMH}km/h"
        tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInMeters / 1000f}km"
        tvDistance.text = distanceInKm

        tvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

        val caloriesBurned = "${run.calorieBurned}kcal"
        tvCaloriesBurned.text = caloriesBurned

    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f,-height.toFloat())
    }
}