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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun detectFoodFromImage(context: Context, photoUri: Uri) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(photoUri))
            }

            if (bitmap == null) {
                setError("Failed to load image")
                return@launch
            }

            val results = withContext(Dispatchers.IO) {
                runFoodRecognitionModel(context, bitmap)
            }

            if (results.isEmpty()) {
                setError("No food detected")
                return@launch
            }

            // Optional: Add delay for longer "Calculating calories..." spinner
            delay(3000L)  // 3 seconds (adjust or remove as needed)

            _uiState.value = CameraUiState.FoodDetected(photoUri, results)
        }
    }

    private suspend fun runFoodRecognitionModel(context: Context, originalBitmap: Bitmap): List<DetectedFood> {
        return try {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer d7b92241e3f0b7c1459f99c2a6deaeef8bda2051")  // Your real token
                        .build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.logmeal.es/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(LogMealApi::class.java)

            // Resize and compress to stay under 1MB
            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 800, 800, true)

            val tempFile = File(context.cacheDir, "temp_food_small.jpg")
            val outputStream = FileOutputStream(tempFile)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.flush()
            outputStream.close()

            // Reduce quality if still too big
            var quality = 80
            while (tempFile.length() > 1048576 && quality > 20) {
                quality -= 10
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(tempFile))
            }

            Log.d("LOGMEAL", "Final image size: ${tempFile.length()} bytes")

            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaType())
            val body = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

            val response = service.detectFood(body)

            val confidenceThreshold = 0.1f
            val detectionsMap = mutableMapOf<String, DetectedFood>()  // Key: label, Value: highest confidence item

            // Collect and deduplicate (keep max confidence per unique label)
            response.segmentation_results?.forEach { segment ->
                segment.recognition_results?.forEach { item ->
                    val label = item.name?.lowercase() ?: "Unknown food"  // Normalize case for dedup
                    val prob = item.prob ?: 0f
                    if (prob >= confidenceThreshold) {
                        val existing = detectionsMap[label]
                        if (existing == null || prob > existing.confidence) {
                            detectionsMap[label] = DetectedFood(label.capitalize(), prob)
                        }
                    }
                }
            }

            // Fallback
            if (detectionsMap.isEmpty()) {
                response.recognition_results?.forEach { item ->
                    val label = item.name?.lowercase() ?: "Unknown food"
                    val prob = item.prob ?: 0f
                    if (prob >= confidenceThreshold) {
                        val existing = detectionsMap[label]
                        if (existing == null || prob > existing.confidence) {
                            detectionsMap[label] = DetectedFood(label.capitalize(), prob)
                        }
                    }
                }
            }

            // Sort unique by confidence, return top 3 for display (top 4 stored for USDA later)
            val sortedUnique = detectionsMap.values.sortedByDescending { it.confidence }
           // top4FoodsForUsda = sortedUnique.take(4)  // For USDA later
            sortedUnique.take(3)

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown"
            Log.e("LOGMEAL_ERROR", "HTTP ${e.code()} - Body: $errorBody")
            emptyList()
        } catch (e: Exception) {
            Log.e("LOGMEAL_ERROR", e.message ?: "Unknown error", e)
            emptyList()
        }
    }

    fun freezeImageAndStartAnalyzing(photoUri: Uri) {
        _uiState.value = CameraUiState.FrozenAnalyzing(photoUri)
    }

    fun setError(message: String) {
        _uiState.value = CameraUiState.Error(message)
    }
}

// UI State
sealed class CameraUiState {
    object Idle : CameraUiState()
    object Loading : CameraUiState()  // Keep for general loading

    data class FrozenAnalyzing(val photoUri: Uri) : CameraUiState()  // New: Frozen image + spinner
    data class FoodDetected(val photoUri: Uri, val detections: List<DetectedFood>) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}
data class DetectedFood(val label: String, val confidence: Float)
