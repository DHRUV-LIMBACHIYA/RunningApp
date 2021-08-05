package com.dhruvlimbachiya.runningapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.db.Run
import com.dhruvlimbachiya.runningapp.others.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Dhruv Limbachiya on 05-08-2021.
 */

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(runs: List<Run>) = differ.submitList(runs)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_run
                    , parent,
                    false)
        )
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[0] // Current item.
        holder.itemView.apply {
            Glide.with(this).load(run.image).into(ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)

            tvAvgSpeed.text = "${run.avgSpeedInKMH}km/h"

            tvDistance.text = "${run.distanceInMeters / 1000f}km"

            tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            tvCalories.text = "${run.calorieBurned}kcal"
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}