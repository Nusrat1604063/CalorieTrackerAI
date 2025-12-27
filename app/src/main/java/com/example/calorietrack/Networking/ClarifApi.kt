package com.example.calorietrack.Networking

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


// Retrofit service
interface LogMealApi {
    @Multipart
    @POST("v2/image/segmentation/complete")
    suspend fun detectFood(@Part image: MultipartBody.Part): LogMealResponse
}

// Response Classes (covers main cases)
data class LogMealResponse(
    val segmentation_results: List<SegmentationResult>? = null,
    val recognition_results: List<RecognitionItem>? = null  // fallback
)

data class SegmentationResult(
    val recognition_results: List<RecognitionItem>
)

data class RecognitionItem(
    val name: String?,
    val prob: Float?
)
//71b8ee56a61eb39f7eaabe0a37f6487c1ed716e1    logmeal

//3d09102543da4a94b05612422ab40fdc