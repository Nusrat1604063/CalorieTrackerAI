package data.datastore.model

data class UserProfile(
    val gender: String,
    val age: Int,
    val heightCm: Int,
    val weightKg: Int,
    val activityLevel: String,
    val calorieGoal: Int,
    val createdAt: Long = System.currentTimeMillis()

)