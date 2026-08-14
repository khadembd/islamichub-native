package com.islamichub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity — single-activity host.
 * Splash screen → Navigation graph with BottomNavigationView.
 *
 * শুধুমাত্র একটি Activity — সব স্ক্রিন Fragment হিসেবে রান হবে।
 * WebView/Capacitor ব্যবহার করা হয় না।
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen must be installed before super.onCreate()
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Prevent bottom nav from recreating the back stack when reselecting
        bottomNav.setOnItemReselectedListener { /* no-op — keep state */ }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
