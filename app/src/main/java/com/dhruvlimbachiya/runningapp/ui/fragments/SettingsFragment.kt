package com.dhruvlimbachiya.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_NAME
import com.dhruvlimbachiya.runningapp.others.Constants.KEY_WEIGHT
import com.dhruvlimbachiya.runningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

/**
 * Created by Dhruv Limbachiya on 30-07-2021.
 */

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var name: String

    @set:Inject
    var weight: Float = 80f

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPreferences()

        btnApplyChanges.setOnClickListener {
            applyChangesOnSharePreference()
        }
    }

    /**
     * Load the details from shared preferences into EditTextViews.
     */
    private fun loadFieldsFromSharedPreferences() {
        etName.setText(name)
        etWeight.setText(weight.toString())
    }

    /**
     * Update runner details in shared preferences.
     */
    private fun applyChangesOnSharePreference(): Boolean {
        val nameText = etName.text.toString()
        val weightText = etWeight.text.toString()

        if(nameText.isEmpty() || weightText.isEmpty()){
            return false
        }

        sharedPreferences.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go $nameText"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }

}