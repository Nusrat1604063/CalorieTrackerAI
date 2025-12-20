package com.example.calorietrack.Networking

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import androidx.compose.runtime.State


class CameraViewModel : ViewModel() {
    private val _uiState = mutableStateOf<CameraUiState>(CameraUiState.Idle)
    val uiState: State<CameraUiState> = _uiState

    fun analyzePhoto(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = CameraUiState.Loading

            try {
                val file = File(uri.path!!)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

                val response = EdamamClient.api.analyzeFoodImage(
                    appId = "com.example.calorietrack",
                    appKey = "YOUR_APP_KEY",
                    image = imagePart
                )

                val food = response.parsed.firstOrNull()?.food
                    ?: response.hints.firstOrNull()?.food

                if (food != null) {
                    _uiState.value = CameraUiState.Success(
                        photoUri = uri,
                        foodName = food.label,
                        calories = food.nutrients?.ENERC_KCAL ?: 0.0,
                        protein = food.nutrients?.PROCNT ?: 0.0,
                        fat = food.nutrients?.FAT ?: 0.0,
                        carbs = food.nutrients?.CHOCDF ?: 0.0
                    )
                } else {
                    _uiState.value = CameraUiState.Error("No food detected")
                }
            } catch (e: Exception) {
                _uiState.value = CameraUiState.Error("Analysis failed: ${e.message}")
            }
        }
    }
}

sealed class CameraUiState {
    object Idle : CameraUiState()
    object Loading : CameraUiState()
    data class Success(
        val photoUri: Uri,
        val foodName: String,
        val calories: Double,
        val protein: Double,
        val fat: Double,
        val carbs: Double
    ) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}