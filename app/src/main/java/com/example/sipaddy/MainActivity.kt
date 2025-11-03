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

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView = binding.bottomNavigation
        bottomNavView.setupWithNavController(navController)

        bottomNavView.setOnItemSelectedListener { item ->
            val navOptions = androidx.navigation.navOptions {
                popUpTo(R.id.main_navigation) {
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
            handleBottomNavigationVisibility(destination.id)

        }
    }

    private fun handleBottomNavigationVisibility(destinationId: Int) {
        when (destinationId) {
            R.id.navigation_home, R.id.navigation_history, R.id.navigation_profile -> {
                binding.bottomNavigation.visibility = View.VISIBLE

            }

            else -> binding.bottomNavigation.visibility = View.GONE

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentDestination = navController.currentDestination?.id

        when (currentDestination) {
            // Jika di history atau profile, kembali ke home
            R.id.navigation_history, R.id.navigation_profile -> {
                binding.bottomNavigation.selectedItemId = R.id.navigation_home
                navController.navigate(R.id.navigation_home)
            }

            // Jika di login, keluar dari app
            R.id.navigation_login -> {
                finish()
            }

            // Jika di home, keluar dari app
            R.id.navigation_home -> {
                finish()
            }

            // Default behavior
            else -> {
                super.onBackPressed()
            }
        }

    }
}