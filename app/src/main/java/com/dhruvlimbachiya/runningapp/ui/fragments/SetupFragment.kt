package com.dhruvlimbachiya.runningapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dhruvlimbachiya.runningapp.R
import kotlinx.android.synthetic.main.fragment_setup.*

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

class SetupFragment : Fragment(R.layout.fragment_setup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvContinue.setOnClickListener {
            findNavController().navigate(
                SetupFragmentDirections.actionSetupFragmentToRunFragment()
            )
        }
    }
}