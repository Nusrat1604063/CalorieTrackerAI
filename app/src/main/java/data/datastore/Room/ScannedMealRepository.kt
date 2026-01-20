package data.datastore.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.calorietrack.utlity.DateUtils.todayMidnight
import data.datastore.model.ScannedMeal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScannedMealRepository @Inject constructor(
    private val dao: ScannedMealDao
) {

    fun todayMeals(): Flow<List<ScannedMeal>> =
        dao.mealsForDate(todayMidnight())
            .map { it.map(::toDomain) }

    suspend fun saveMeal(meal: ScannedMeal) {
        dao.insert(meal.toEntity(todayMidnight()))
    }

    suspend fun cleanup() {
        dao.deleteOlderThan(todayMidnight())
    }
}

@Database(
    entities = [ScannedMealEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scannedMealDao(): ScannedMealDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build().also { INSTANCE = it }
            }
    }
}
