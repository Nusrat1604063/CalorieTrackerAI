package data.datastore.model

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object DailyIntakeKeys {
    val CONSUMED_CALORIES = intPreferencesKey("consumed_calories")
    val LAST_UPDATED_DATE = longPreferencesKey("last_updated_date") // Timestamp for reset check
}