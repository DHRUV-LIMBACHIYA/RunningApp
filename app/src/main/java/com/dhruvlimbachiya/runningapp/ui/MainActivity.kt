package com.dhruvlimbachiya.runningapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dhruvlimbachiya.runningapp.R
import com.dhruvlimbachiya.runningapp.db.RunDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment // Find the NavHostFragment.
        bottomNavigationView.setupWithNavController(navHost.findNavController()) // Set navController to BottomNavigationView

        navHost.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                // Display BottomNavigationView only on these fragments.
                R.id.runFragment,R.id.settingsFragment,R.id.statisticsFragment -> {
                    bottomNavigationView.isVisible = true
                }
                else -> bottomNavigationView.isVisible = false
            }
        }
    }
}
