package com.kickplan.training

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.kickplan.training.ui.AppViewModel
import com.kickplan.training.ui.AppViewModelFactory
import com.kickplan.training.ui.match.MatchViewModel
import com.kickplan.training.ui.navigation.AppNavGraph
import com.kickplan.training.ui.navigation.Routes
import com.kickplan.training.ui.theme.KickPlanTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Activity-scoped ViewModels so state is shared across all screens.
        val factory = AppViewModelFactory(applicationContext)
        val provider = ViewModelProvider(this, factory)
        val appViewModel = provider[AppViewModel::class.java]
        val matchViewModel = provider[MatchViewModel::class.java]

        // Keep the splash visible until DataStore has produced its first value.
        splashScreen.setKeepOnScreenCondition { !appViewModel.isLoaded.value }

        setContent {
            KickPlanTheme {
                val loaded by appViewModel.isLoaded.collectAsState()
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    if (loaded) {
                        val navController = rememberNavController()
                        val start = if (appViewModel.onboardingCompletedAtStart) {
                            Routes.TODAY
                        } else {
                            Routes.ONBOARDING
                        }
                        AppNavGraph(
                            navController = navController,
                            appViewModel = appViewModel,
                            matchViewModel = matchViewModel,
                            startDestination = start
                        )
                    }
                }
            }
        }
    }
}
