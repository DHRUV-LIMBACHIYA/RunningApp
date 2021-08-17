package com.dhruvlimbachiya.runningapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.others.Constants.ACTION_NAVIGATE_TO_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        navigateToTrackingFragmentIfNeeded(intent)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment // Find the NavHostFragment.
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController()) // Set navController to BottomNavigationView
        bottomNavigationView.setOnItemReselectedListener { /* NO-OP */ } // Don't load fragment again on bottom menu reselect.

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Display BottomNavigationView only on these fragments.
                R.id.runFragment, R.id.settingsFragment, R.id.statisticsFragment -> {
                    bottomNavigationView.isVisible = true
                }
                else -> bottomNavigationView.isVisible = false
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    /**
     * Navigate to Tracking Fragment if the intent action == "ACTION_NAVIGATE_TO_TRACKING_FRAGMENT"
     */
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        intent?.action.let {
            if (it == ACTION_NAVIGATE_TO_TRACKING_FRAGMENT) {
                navHostFragment.findNavController().navigate(
                    R.id.action_global_tracking_fragment
                )
            }
        }
    }
}
