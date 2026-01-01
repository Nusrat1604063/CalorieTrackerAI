package com.example.calorietrack.Navigation

import CameraScreen
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calorietrack.Presentation.AllSetScreen
import com.example.calorietrack.Presentation.HomeScreen
import com.example.calorietrack.Presentation.LaunchScreenTwo
import com.example.calorietrack.Presentation.PersonalDetailsWithProgressScreen
import com.example.calorietrack.Presentation.ScreenOneMain
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import data.datastore.AppPreferences
import data.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current  // To access DataStore
) {
    // State to hold the starting route
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Load the setup status once at startup
    LaunchedEffect(Unit) {
        val isSetupDone = context.dataStore.data
            .map { preferences ->
                preferences[AppPreferences.SETUP_DONE] ?: false
            }
            .first()  // Wait for the first emission

        startDestination = if (isSetupDone) {
            AppScreen.Home.routes
        } else {
            AppScreen.ScreenOne.routes
        }
    }

    // Show a simple splash/loading screen until we know where to go
    if (startDestination == null) {
        // Optional: Show a nice splash screen here
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            // Or your app logo + "Loading..."
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = modifier
        ) {
            composable(AppScreen.ScreenOne.routes,
                exitTransition = { fadeOut(tween(600)) }
            ) {
                ScreenOneMain(
                    onFinished = {
                        navController.navigate(AppScreen.LaunchTwo.routes) {
                            popUpTo(AppScreen.ScreenOne.routes) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppScreen.LaunchTwo.routes) {
                LaunchScreenTwo(
                    onContinue = {
                        navController.navigate(AppScreen.PersonalDetails.routes)
                    }
                )
            }

            composable(AppScreen.PersonalDetails.routes) {
                PersonalDetailsWithProgressScreen(
                    onContinue = {
                        navController.navigate(AppScreen.AllSet.routes)
                    }
                )
            }

            composable(AppScreen.AllSet.routes) {
                AllSetScreen(
                    onContinue = { /* can be empty or used for analytics */ },   // already navhost decides home navigation based on datastore onboarding value
                    navController = navController,
                    viewModel = viewModel()  // or hiltViewModel() if using Hilt
                )
            }

            composable(AppScreen.Home.routes) {
                HomeScreen(
                    onNavigateToCamera = {
                        navController.navigate(AppScreen.Camera.routes)
                    }
                )
            }

            composable(AppScreen.Camera.routes) {
                CameraScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}