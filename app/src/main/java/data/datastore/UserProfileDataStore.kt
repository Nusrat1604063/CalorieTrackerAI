package data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

val Context.UserProfileDataStore by preferencesDataStore(
    name = "user_profile_prefs"
)

object UserProfileKeys {

    val GENDER = stringPreferencesKey("gender")
    val AGE = intPreferencesKey("age")
    val HEIGHT_CM = intPreferencesKey("height_cm")
    val WEIGHT_KG = intPreferencesKey("weight_kg")
    val ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
    val CALORIE_GOAL = intPreferencesKey("calorie_goal")
    val CREATED_AT = longPreferencesKey("created_at")
}