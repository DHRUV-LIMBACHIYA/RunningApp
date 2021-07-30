package com.dhruvlimbachiya.runningapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val mViewModel: StatisticsViewModel by viewModels()
}