package data.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "app_prefs")
val Context.userProfileDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")
val Context.dailyIntakeDataStore: DataStore<Preferences> by preferencesDataStore(name = "daily_intake")

object AppPreferences {
    val SETUP_DONE = booleanPreferencesKey("setup_done")
}

suspend fun setupCompleted(context: Context) {
    context.dataStore.edit { prefs ->
        prefs[AppPreferences.SETUP_DONE] = true
    }
    println("Onboarding completed!!")
}