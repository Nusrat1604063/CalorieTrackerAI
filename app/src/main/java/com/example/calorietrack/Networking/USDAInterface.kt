package com.example.calorietrack.Networking

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsdaApi {
    @GET("v1/foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 3
    ): UsdaSearchResponse

    @GET("v1/food/{fdcId}")
    suspend fun getFoodDetails(
        @Path("fdcId") fdcId: Int,
        @Query("api_key") apiKey: String
    ): UsdaFoodDetails
}

data class UsdaFoodDetails(
    val foodNutrients: List<UsdaNutrient>,
    val labelNutrients: LabelNutrients? = null
)

data class LabelNutrients(
    val fat: NutrientValue? = null,
    val saturatedFat: NutrientValue? = null,
    val carbohydrates: NutrientValue? = null,
    val protein: NutrientValue? = null,
    val calories: NutrientValue? = null
)

data class NutrientValue(
    val value: Float?
)
data class UsdaSearchResponse(
    val foods: List<UsdaFoodItem>
)

data class UsdaFoodItem(
    val fdcId: Int,
    val description: String
)



data class UsdaNutrient(
    val nutrientId: Int,
    val amount: Float?,
    val unitName: String?
)

// Simple nutrition result to pass to UI
data class NutritionSummary(
    val calories: Int,
    val proteinGrams: Float,
    val fatGrams: Float,
    val carbsGrams: Float
)