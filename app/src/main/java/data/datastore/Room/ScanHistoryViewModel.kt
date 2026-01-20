package data.datastore.Room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.datastore.model.ScannedMeal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ScanHistoryViewModel(
    repository: ScannedMealRepository
) : ViewModel() {

    val todayMeals: StateFlow<List<ScannedMeal>> =
        repository.todayMeals()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}
