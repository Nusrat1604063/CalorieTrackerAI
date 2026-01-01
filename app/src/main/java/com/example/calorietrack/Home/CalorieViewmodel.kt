package com.example.calorietrack.Home
import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import data.datastore.UserProfileKeys
import data.datastore.model.DailyIntakeKeys
import data.datastore.userProfileDataStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



import data.datastore.dailyIntakeDataStore

import kotlinx.coroutines.flow.SharingStarted

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class CalorieViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private val profileFlow = context.userProfileDataStore.data
    private val dailyFlow = context.dailyIntakeDataStore.data

    val uiState: StateFlow<DailySummaryUiState> = combine(profileFlow, dailyFlow) { profilePrefs, dailyPrefs ->

        val recommended = profilePrefs[UserProfileKeys.CALORIE_GOAL] ?: 0

        var consumed = dailyPrefs[DailyIntakeKeys.CONSUMED_CALORIES] ?: 0
        val lastUpdated = dailyPrefs[DailyIntakeKeys.LAST_UPDATED_DATE] ?: 0L

        // Get today's date at 00:00:00 in device timezone
        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayStart = calendar.timeInMillis

        val lastUpdatedDayStart = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = lastUpdated
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // If it's a new day → reset consumed
        if (lastUpdated == 0L || lastUpdatedDayStart < todayStart) {
            consumed = 0
            viewModelScope.launch {
                context.dailyIntakeDataStore.edit { prefs ->
                    prefs[DailyIntakeKeys.CONSUMED_CALORIES] = 0
                    prefs[DailyIntakeKeys.LAST_UPDATED_DATE] = System.currentTimeMillis()
                }
            }
        }

        val left = (recommended - consumed).coerceAtLeast(0)
        val progress = if (recommended > 0) (consumed.toFloat() / recommended).coerceAtMost(1f) else 0f

        DailySummaryUiState(
            recommended = recommended,
            consumed = consumed,
            left = left,
            progress = progress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailySummaryUiState(recommended = 0, consumed = 0, left = 0, progress = 0f)
    )
}

data class DailySummaryUiState(
    val recommended: Int,
    val consumed: Int,
    val left: Int,
    val progress: Float
)