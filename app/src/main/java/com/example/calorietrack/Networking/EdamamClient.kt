package com.example.calorietrack.Networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object EdamamClient {
    private const val BASE_URL = "https://api.edamam.com/"

    val api: EdamamVisionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EdamamVisionApi::class.java)
    }
}