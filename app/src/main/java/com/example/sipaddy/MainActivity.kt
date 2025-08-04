package com.example.sipaddy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sipaddy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationBar()
    }

    private fun navigationBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView = binding.bottomNavigation
        bottomNavView.setupWithNavController(navController)

        bottomNavView.setOnItemSelectedListener { item ->
            val navOptions = androidx.navigation.navOptions {
                popUpTo(R.id.mobile_navigation) {
                    inclusive = false
                }
                launchSingleTop = true
            }
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home, null, navOptions)
                    true
                }

                R.id.navigation_history -> {
                    navController.navigate(R.id.navigation_history, null, navOptions)
                    true
                }

                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile, null, navOptions)
                    true
                }

                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_profile -> {
                    bottomNavView.visibility = View.VISIBLE
                }

                else -> bottomNavView.visibility = View.GONE
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.navigation_history || navController.currentDestination?.id == R.id.navigation_profile) {
            if (navController.currentDestination?.id != R.id.navigation_home) {
                binding.bottomNavigation.selectedItemId = R.id.navigation_home
                navController.navigate(R.id.navigation_home)
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }

    }
}