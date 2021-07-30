package com.dhruvlimbachiya.runningapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val mViewModel: MainViewModel by viewModels()
}