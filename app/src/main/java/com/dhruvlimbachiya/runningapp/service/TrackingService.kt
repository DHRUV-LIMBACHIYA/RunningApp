package com.dhruvlimbachiya.runningapp.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_PAUSE_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

/**
 * Created by Dhruv Limbachiya on 02-08-2021.
 */

class TrackingService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.i("Action service started or resumed")
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.i("Action service paused")
                }

                ACTION_STOP_SERVICE -> {
                    Timber.i("Action service stopped")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}