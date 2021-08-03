package com.dhruvlimbachiya.runningapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.indexOf
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.Constants
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_PAUSE_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.CAMERA_ZOOM
import com.dhruvlimbachiya.runningapp.others.Constants.POLYLINE_COLOR
import com.dhruvlimbachiya.runningapp.others.Constants.POLYLINE_WIDTH
import com.dhruvlimbachiya.runningapp.others.TrackingUtility
import com.dhruvlimbachiya.runningapp.service.PolyLine
import com.dhruvlimbachiya.runningapp.service.TrackingService
import com.dhruvlimbachiya.runningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val mViewModel: MainViewModel by viewModels()

    private var mGoogleMap: GoogleMap? = null

    private var isTracking = false
    private var pathPoints = mutableListOf<PolyLine>()
    private var timeInMills = 0L

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)

        // Load the Google Map Asynchronously.
        mapView.getMapAsync { map ->
            mGoogleMap = map
            drawAllPolyLines()
        }

        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        subscribeToObservers()
    }

    /**
     * Toggle the run from Start to Stop and vice-versa.
     */
    private fun toggleRun() {
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    /**
     * Observe the changes from the LiveData.
     */
    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner){
            updateTrackingStatus(it)
        }

        TrackingService.pathPoints.observe(viewLifecycleOwner){
            pathPoints = it // Get the fresh list of PolyLines.
            drawPolyLineUsingLatestLatLng()
            moveCameraToRunner()
        }

        TrackingService.totalTimeRunInMillis.observe(viewLifecycleOwner){
            timeInMills = it
            tvTimer.text = TrackingUtility.getFormattedStopWatchTime(it,true)
        }
    }

    /**
     * Update UI based on isTracking LiveData.
     */
    private fun updateTrackingStatus(isTracking: Boolean) {
        this.isTracking = isTracking
        if(isTracking){
            btnToggleRun.text = getString(R.string.text_stop)
            btnFinishRun.isVisible = false
        }else {
            btnToggleRun.text = getString(R.string.text_start)
            btnFinishRun.isVisible = true
        }
    }

    /**
     * Move camera to latest position(LatLng) of Runner.
     */
    private fun moveCameraToRunner() {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            mGoogleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(), // get the latest LatLng
                    CAMERA_ZOOM
                )
            )
        }
    }

    /**
     * Draw all the polyLines in case of Activity re-creation
     */
    private fun drawAllPolyLines(){
        for(polyLine in pathPoints){
            val polyLineOptions = PolylineOptions().apply {
                width(POLYLINE_WIDTH)
                color(POLYLINE_COLOR)
                addAll(polyLine) // add the entire list of PolyLine.
            }
            mGoogleMap?.addPolyline(polyLineOptions)
        }
    }


    /**
     * Function will draw polyLine using last second and last element(LatLng) from list of PolyLine.
     */
    private fun drawPolyLineUsingLatestLatLng() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) { // PathPoints should not be empty and its last(current) elements at-least contains 2 LatLng Objects.
            val lastSecondLatLng = pathPoints.last()[pathPoints.last().size - 2]// Get the last second item.
            val lastLatLng = pathPoints.last().last() // Get the last item.

            val polylineOptions = PolylineOptions().apply {
                width(POLYLINE_WIDTH)
                color(POLYLINE_COLOR)
                add(lastSecondLatLng)
                add(lastLatLng)
            }

            mGoogleMap?.addPolyline(polylineOptions) // Add a polyLine in the map.
        }
    }

    /**
     * Send commands to the [TrackingService]
     */
    private fun sendCommandToService(command: String) =
        Intent(requireContext(),TrackingService::class.java).apply {
            this.action = command
            requireContext().startService(this)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
}