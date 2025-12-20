package com.example.calorietrack.Networking

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface EdamamVisionApi {
    @Multipart
    @POST("api/food-database/v2/parser")
    suspend fun analyzeFoodImage(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Part image: MultipartBody.Part
    ): EdamamResponse // Your response data class
}