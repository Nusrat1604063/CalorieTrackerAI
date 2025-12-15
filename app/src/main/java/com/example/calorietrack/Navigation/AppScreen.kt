package com.example.calorietrack.Navigation

sealed class AppScreen(val routes: String) {
    object ScreenOne: AppScreen("screen_one")
    object LaunchTwo: AppScreen("launch-two")
    object PersonalDetails: AppScreen("personal_details")
    object AllSet: AppScreen("all_set")
    object Home: AppScreen("home")

}