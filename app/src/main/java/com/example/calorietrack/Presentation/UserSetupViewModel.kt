package com.example.calorietrack.Presentation

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.datastore.UserProfileKeys
import data.datastore.dataStore
import data.datastore.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class UserSetupViewModel : ViewModel() {

    private fun yearToAge(year: Int) : Int {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return currentYear - year
    }

    private fun heightStringToCm(height: String) : Int {
        val parts = height.split(" ")
        val feet = parts[0].toInt()
        val inches = parts[2].toInt()

        val cm = (feet * 30.48) + (inches * 2.54)
        return cm.roundToInt()
    }

    private fun activityToMultiplier(activity: String): Double {
        return when (activity) {
            "Sedentary" -> 1.2
            "Lightly Active" -> 1.375
            "Moderately Active" -> 1.55
            "Very Active" -> 1.725
            else -> 1.2
        }
    }

    private fun calculateCalories(
        gender: String,
        age: Int,
        heightCm: Int,
        weightKg: Int,
        activityLevel: String
    ): Int {

        val bmr = if (gender == "Male") {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
        }

        val calories = bmr * activityToMultiplier(activityLevel)
        return calories.roundToInt()
    }

    fun buildUserProfile(
        gender: String,
        yearOfBirth: Int,
        heightString: String,
        weightKg: Int,
        activityLevel: String
    ): UserProfile {

        val age = yearToAge(yearOfBirth)
        val heightCm = heightStringToCm(heightString)
        val calorieGoal = calculateCalories(
            gender = gender,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            activityLevel = activityLevel
        )

        return UserProfile(
            gender = gender,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            activityLevel = activityLevel,
            calorieGoal = calorieGoal
        )
    }

    fun saveAndLogUserProfile(context: Context, profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            // Save to DataStore
            context.dataStore.edit { prefs ->
                prefs[UserProfileKeys.GENDER] = profile.gender
                prefs[UserProfileKeys.AGE] = profile.age
                prefs[UserProfileKeys.HEIGHT_CM] = profile.heightCm
                prefs[UserProfileKeys.WEIGHT_KG] = profile.weightKg
                prefs[UserProfileKeys.ACTIVITY_LEVEL] = profile.activityLevel
                prefs[UserProfileKeys.CALORIE_GOAL] = profile.calorieGoal
                prefs[UserProfileKeys.CREATED_AT] = System.currentTimeMillis()
            }

            // Read back once and log
            val savedProfile = context.dataStore.data.firstOrNull()?.let { prefs ->
                UserProfile(
                    gender = prefs[UserProfileKeys.GENDER] ?: profile.gender,
                    age = prefs[UserProfileKeys.AGE] ?: profile.age,
                    heightCm = prefs[UserProfileKeys.HEIGHT_CM] ?: profile.heightCm,
                    weightKg = prefs[UserProfileKeys.WEIGHT_KG] ?: profile.weightKg,
                    activityLevel = prefs[UserProfileKeys.ACTIVITY_LEVEL] ?: profile.activityLevel,
                    calorieGoal = prefs[UserProfileKeys.CALORIE_GOAL] ?: profile.calorieGoal
                )
            }
            Log.d("UserProfile", "Saved profile: $savedProfile")
        }
    }

}