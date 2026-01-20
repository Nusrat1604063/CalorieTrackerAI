package data.datastore.Room

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
interface ScannedMealDao {

    @Query("SELECT * FROM scanned_meals WHERE date = :date")
    fun mealsForDate(date: Long): Flow<List<ScannedMealEntity>>

    @Insert
    suspend fun insert(meal: ScannedMealEntity)

    @Query("DELETE FROM scanned_meals WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}

