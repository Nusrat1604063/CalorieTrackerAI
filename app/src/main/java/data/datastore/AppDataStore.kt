package data.datastore


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "app_prefs")

object AppPreferences {
    val SETUP_DONE = booleanPreferencesKey("setup_done")
}

suspend fun setupCompleted(context: Context) {
    context.dataStore.edit { prefs ->
        prefs[AppPreferences.SETUP_DONE] = true
    }
    println("Onboarding completed!!")
}