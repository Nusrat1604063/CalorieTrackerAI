package com.example.calorietrack.Networking
data class EdamamResponse(
    val text: String?,
    val parsed: List<ParsedFood>,
    val hints: List<FoodHint>,
    val _links: Links?
)

data class ParsedFood(
    val food: FoodItem,
    val quantity: Double,
    val measure: Measure?,
    val weight: Double?
)

data class FoodHint(
    val food: FoodItem,
    val measures: List<Measure>?
)

data class FoodItem(
    val foodId: String,
    val label: String, // Food name
    val nutrients: Nutrients?,
    val category: String?,
    val categoryLabel: String?,
    val image: String? // Optional image URL
)

data class Nutrients(
    val ENERC_KCAL: Double?, // Calories
    val PROCNT: Double?,     // Protein (g)
    val FAT: Double?,        // Fat (g)
    val CHOCDF: Double?,     // Carbs (g)
    val FIBTG: Double?       // Fiber (g) — optional, add more if needed
)

data class Measure(
    val uri: String,
    val label: String // e.g., "Serving", "Gram"
)

data class Links(
    val next: NextLink?
)

data class NextLink(
    val href: String,
    val title: String
)