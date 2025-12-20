package com.example.calorietrack.Navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calorietrack.Presentation.AllSetScreen
import com.example.calorietrack.Presentation.CameraScreen
import com.example.calorietrack.Presentation.HomeScreen
import com.example.calorietrack.Presentation.LaunchScreenTwo
import com.example.calorietrack.Presentation.PersonalDetailsWithProgressScreen
import com.example.calorietrack.Presentation.ScreenOneMain

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.ScreenOne.routes
        ) {

        composable(AppScreen.ScreenOne.routes,
            exitTransition = {
                fadeOut(tween(600))
            }) {
            ScreenOneMain(
                onFinished = {
                    navController.navigate(AppScreen.LaunchTwo.routes) {
                        popUpTo(AppScreen.ScreenOne.routes) {
                            inclusive = true
                        }
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

        composable(AppScreen.Camera.routes) {
            CameraScreen(
                onBack = { navController.popBackStack() }

            )
        }


        composable(AppScreen.AllSet.routes) {
            AllSetScreen(
                onContinue = {
                    navController.navigate(AppScreen.Home.routes)
                }
            )
        }


          composable(AppScreen.Home.routes) {
            HomeScreen(onNavigateToCamera = {
                navController.navigate(AppScreen.Camera.routes)
            })
        }

    }
}