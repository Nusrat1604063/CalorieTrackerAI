package data.datastore.model
data class ScannedMeal(
    val id: Int,
    val name: String,
    val calories: Int,
    val imageUrl: String? = null // For real photos later
)

