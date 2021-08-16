package com.dhruvlimbachiya.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_IS_FIRST_TIME
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_NAME
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstTime = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If it is not first time then navigate to RunFragment.
        if(!isFirstTime){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true) // Pop all the fragments up to SetUpFragment including SetUpFragment.
                .build()
            findNavController().navigate(
                SetupFragmentDirections.actionSetupFragmentToRunFragment(),
                navOptions
            )
        }

        tvContinue.setOnClickListener {
            val success = writePersonalDataIntoSharePreferences()
            if(success){
                findNavController().navigate(
                    SetupFragmentDirections.actionSetupFragmentToRunFragment()
                )
            }else{
                Snackbar.make(requireView(),"Please enter all the fields",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Write Runner Name & Weight into Shared Preferences.
     */
    private fun writePersonalDataIntoSharePreferences(): Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()

        if(name.isEmpty() || weight.isEmpty()){
            return false
        }

        // write data into shared preferences
        sharedPreferences.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_IS_FIRST_TIME,false)
            .apply()

        // update toolbar text.
        val toolbarText = "Let's go, $name!"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }
}