package com.example.calorietrack.Networking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorietrack.utlity.bitmapToBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun detectFoodFromImage(context: Context, photoUri: Uri) {
        viewModelScope.launch {
            _uiState.value = CameraUiState.Loading

            val bitmap = withContext(Dispatchers.IO) {
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(photoUri))
            }

            if (bitmap == null) {
                _uiState.value = CameraUiState.Error("Failed to load image")
                return@launch
            }

            val results = withContext(Dispatchers.IO) {
                runClarifaiModel(bitmap)
            }

            if (results.isEmpty()) {
                _uiState.value = CameraUiState.Error("No food detected")
            } else {
                _uiState.value = CameraUiState.FoodDetected(photoUri, results)
            }
        }
    }


    private suspend fun runClarifaiModel(bitmap: Bitmap): List<DetectedFood> {
        return try {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Key 3d09102543da4a94b05612422ab40fdc")  // Replace with your actual PAT
                        .build()
                    chain.proceed(request)
                }
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.clarifai.com/v2/users/clarifai/apps/main/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ClarifaiApi::class.java)

            // Resize image
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val base64Image = bitmapToBase64(resizedBitmap)

            // Build JSON request correctly
            val json = """
        {
          "inputs": [
            {
              "data": {
                "image": {
                  "base64": "$base64Image"
                }
              }
            }
          ]
        }
        """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaType())

            // Make API call with PAT in header
            val response = service.detectFood(requestBody)

            val confidenceThreshold = 0.25f
            val results = mutableListOf<DetectedFood>()
            response.outputs.firstOrNull()?.data?.concepts?.forEach { concept ->
                if (concept.value >= confidenceThreshold) {
                    results.add(DetectedFood(label = concept.name, confidence = concept.value))
                }
            }
            results
        } catch (e: retrofit2.HttpException) {
            Log.e("CLARIFAI_ERROR", "HTTP ${e.code()} - ${e.message()}")
            emptyList()
        } catch (e: Exception) {
            Log.e("CLARIFAI_ERROR", e.message ?: "Unknown error", e)
            emptyList()
        }
    }







}


// UI State
sealed class CameraUiState {
    object Idle : CameraUiState()
    object Loading : CameraUiState()
    data class FoodDetected(val photoUri: Uri, val detections: List<DetectedFood>) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}

data class DetectedFood(val label: String, val confidence: Float)
