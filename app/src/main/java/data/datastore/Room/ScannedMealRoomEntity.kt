package data.datastore.Room

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.datastore.model.ScannedMeal

@Entity(tableName = "scanned_meals")
data class ScannedMealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val calories: Int,
    val imageUrl: String?,

    val date: Long
)

fun toDomain(entity: ScannedMealEntity) =
    ScannedMeal(
        id = entity.id,
        name = entity.name,
        calories = entity.calories,
        imageUrl = entity.imageUrl
    )

fun ScannedMeal.toEntity(date: Long) =
    ScannedMealEntity(
        name = name,
        calories = calories,
        imageUrl = imageUrl,
        date = date
    )
