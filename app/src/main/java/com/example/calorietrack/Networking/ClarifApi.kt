package com.example.calorietrack.Networking

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


// Retrofit service
interface ClarifaiApi {
    @POST("models/food-item-recognition/outputs")
    suspend fun detectFood(@Body body: RequestBody): ClarifaiResponse
}

// Data classes
data class ClarifaiResponse(
    val outputs: List<ClarifaiOutput>
)

data class ClarifaiOutput(
    val data: ClarifaiData
)

data class ClarifaiData(
    val concepts: List<Concept>
)

data class Concept(
    val id: String,
    val name: String,
    val value: Float
)


//71b8ee56a61eb39f7eaabe0a37f6487c1ed716e1    logmeal

//3d09102543da4a94b05612422ab40fdc