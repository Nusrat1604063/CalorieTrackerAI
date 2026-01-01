package data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import data.datastore.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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



//fun saveUserProfile(context: Context, profile: UserProfile) {
//    CoroutineScope(Dispatchers.IO).launch {
//        context.dataStore.edit { prefs ->
//            prefs[UserProfileKeys.GENDER] = profile.gender
//            prefs[UserProfileKeys.AGE] = profile.age
//            prefs[UserProfileKeys.HEIGHT_CM] = profile.heightCm
//            prefs[UserProfileKeys.WEIGHT_KG] = profile.weightKg
//            prefs[UserProfileKeys.ACTIVITY_LEVEL] = profile.activityLevel
//            prefs[UserProfileKeys.CALORIE_GOAL] = profile.calorieGoal
//            prefs[UserProfileKeys.CREATED_AT] = System.currentTimeMillis()
//        }
//    }
//}
