package com.example.calorietrack.Networking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorietrack.utlity.bitmapToBase64
import com.google.android.gms.common.data.FreezableUtils.freeze
import data.datastore.Room.ScannedMealRepository
import data.datastore.model.ScannedMeal
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
import javax.inject.Inject

class CameraViewModel () : ViewModel() {

    private var mealSaved = false

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var top4FoodsForUsda: List<DetectedFood> = emptyList()
    private var scannedMealRepository: ScannedMealRepository? = null

    fun setRepository(repo: ScannedMealRepository) {
        scannedMealRepository = repo
    }

    fun onCaptureFailed() {
        setError("Capture failed. Please try again.")
    }

    fun resetToIdle() {
        _uiState.value = CameraUiState.Idle
    }


    //INTERNAL FLOW (VIEWMODEL DECIDES STATE)

    private fun analyzeImage(context: Context, photoUri: Uri) {
        viewModelScope.launch {

            val bitmap = loadBitmap(context, photoUri)
            if (bitmap == null) {
                setError("Failed to load image")
                return@launch
            }

            val outcome = withContext(Dispatchers.IO) {
                runFoodRecognitionModel(context, bitmap)

            }

            when (outcome) {
                is RecognitionOutcome.Success -> {
                    // Optional UX delay (remove if you want)
                    delay(500)

                    if (!mealSaved) {
                        val meal = ScannedMeal(
                            id = 0,
                            name = outcome.detections.joinToString { it.label },
                            calories = outcome.nutritionSummary.calories,
                            imageUrl = photoUri.toString()
                        )

                        // Launch coroutine to save
                       viewModelScope.launch {
                            scannedMealRepository?.saveMeal(meal)
                            Log.d("ScanHistory", "Saved meal: ${meal.name}, Calories: ${meal.calories}")

                            // Optional: print all today's meals
                            scannedMealRepository?.todayMeals()?.collect { meals ->
                                Log.d("ScanHistory", "Today's meals in DB:")
                                meals.forEach {
                                    Log.d("ScanHistory", "- ${it.name}: ${it.calories} Cal")
                                }
                            }
                        }

                        mealSaved = true
                    }


                    _uiState.value = CameraUiState.FoodDetected(
                        photoUri = photoUri,
                        detections = outcome.detections,
                        nutritionSummary = outcome.nutritionSummary
                    )
                }

                RecognitionOutcome.Failure -> {
                    setError("No food detected or analysis failed")
                }
            }
        }
    }

    private suspend fun loadBitmap(
        context: Context,
        uri: Uri
    ): Bitmap? = withContext(Dispatchers.IO) {
        BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(uri)
        )
    }




    fun detectFoodFromImage(context: Context, photoUri: Uri) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(photoUri))
            }

            if (bitmap == null) {
                setError("Failed to load image")
                return@launch
            }

            val outcome = withContext(Dispatchers.IO) {
                runFoodRecognitionModel(context, bitmap)
            }

            when (outcome) {
                is RecognitionOutcome.Success -> {
                    // Optional delay to keep spinner visible longer (for better UX)
                    delay(2000L)  // Adjust or remove as needed

                    _uiState.value = CameraUiState.FoodDetected(
                        photoUri = photoUri,
                        detections = outcome.detections,
                        nutritionSummary = outcome.nutritionSummary
                    )

                    // ---- SAVE MEAL HERE ----
                    if (!mealSaved) {
                        val meal = ScannedMeal(
                            id = 0,
                            name = outcome.detections.joinToString { it.label },
                            calories = outcome.nutritionSummary.calories,
                            imageUrl = photoUri.toString()
                        )

                        viewModelScope.launch {
                            scannedMealRepository?.saveMeal(meal)
                            Log.d("ScanHistory", "Saved meal: ${meal.name}, Calories: ${meal.calories}")
                        }

                        mealSaved = true
                    }
                }

                is RecognitionOutcome.Failure -> {
                    setError("No food detected or analysis failed")
                }
            }
        }
    }

    private suspend fun runFoodRecognitionModel(context: Context, originalBitmap: Bitmap): RecognitionOutcome {
        return try {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer d7b92241e3f0b7c1459f99c2a6deaeef8bda2051")  // token
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
            if (sortedUnique.isEmpty()) {
                return RecognitionOutcome.Failure
            }
            top4FoodsForUsda = sortedUnique.take(4)  // For USDA later
            val nutritionSummary = calculateNutritionFromDetectedFoods(top4FoodsForUsda)


            RecognitionOutcome.Success(
                detections = sortedUnique.take(3),
                nutritionSummary = nutritionSummary
            )

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown"
            Log.e("LOGMEAL_ERROR", "HTTP ${e.code()} - Body: $errorBody")
            RecognitionOutcome.Failure
        } catch (e: Exception) {
            Log.e("LOGMEAL_ERROR", e.message ?: "Unknown error", e)
            RecognitionOutcome.Failure
        }
    }

    private suspend fun calculateNutritionFromDetectedFoods(
        foods: List<DetectedFood>
    ): NutritionSummary {

        // LOCAL fallback
        val localNutrition = mapOf(
            "tomato" to NutritionSummary(18, 0.9f, 0.2f, 3.9f),
            "onion" to NutritionSummary(40, 1.1f, 0.1f, 9.3f),
            "pineapple" to NutritionSummary(50, 0.5f, 0.1f, 13.1f),
            "ketchup" to NutritionSummary(112, 1.3f, 0.2f, 26f)
        )

        fun normalize(label: String): String {
            return label.lowercase()
                .replace(Regex("\\(.*?\\)"), "")
                .replace("raw", "")
                .replace("fresh", "")
                .replace("sauce", "")
                .trim()
        }

        var totalCalories = 0f
        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbs = 0f

        // --- 2. Setup USDA once ---
        val usdaRetrofit = Retrofit.Builder()
            .baseUrl("https://api.nal.usda.gov/fdc/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val usdaService = usdaRetrofit.create(UsdaApi::class.java)
        val usdaApiKey = "DEMO_KEY"

        for (food in foods) {
            val key = normalize(food.label)
            Log.i("USDA_DEBUG", "Processing food: $key")

            // --- 3. LOCAL FIRST (always works) ---
            val local = localNutrition[key]

            if (local != null) {
                totalCalories += local.calories
                totalProtein += local.proteinGrams
                totalFat += local.fatGrams
                totalCarbs += local.carbsGrams
                Log.i("USDA_DEBUG", "Used local nutrition for $key")
                continue
            }

            // --- 4. USDA BEST-EFFORT (may fail, doesn’t break app) ---
            try {
                delay(1200) // respect rate limit

                val search = usdaService.searchFoods(
                    query = key,
                    apiKey = usdaApiKey,
                    pageSize = 5
                )

                val best = search.foods.firstOrNull() ?: continue
                val details = usdaService.getFoodDetails(best.fdcId, usdaApiKey)

                var calories = 0f
                var protein = 0f
                var fat = 0f
                var carbs = 0f

                details.foodNutrients.forEach { n ->
                    when (n.nutrientId) {
                        1008, 2047, 2048 -> calories += n.amount ?: 0f
                        1003, 203 -> protein += n.amount ?: 0f
                        1004, 204 -> fat += n.amount ?: 0f
                        1005, 205 -> carbs += n.amount ?: 0f
                    }
                }

                // fallback to label nutrients
                details.labelNutrients?.let {
                    calories += it.calories?.value ?: 0f
                    protein += it.protein?.value ?: 0f
                    fat += it.fat?.value ?: 0f
                    carbs += it.carbohydrates?.value ?: 0f
                }

                // USDA sometimes returns per 100g — clamp sanity
                if (calories > 0) {
                    totalCalories += calories
                    totalProtein += protein
                    totalFat += fat
                    totalCarbs += carbs
                    Log.i("USDA_DEBUG", "USDA success for $key")
                }

            } catch (e: Exception) {
                Log.e("USDA_DEBUG", "USDA failed for $key: ${e.message}")
            }
        }

        // --- 5. Absolute safety net ---
        if (totalCalories == 0f) {
            totalCalories = foods.size * 50f // basic estimate
        }

        return NutritionSummary(
            calories = totalCalories.toInt(),
            proteinGrams = totalProtein,
            fatGrams = totalFat,
            carbsGrams = totalCarbs
        )
    }

    fun onNewCapture() {
        mealSaved = false
    }

    fun freezeImageAndStartAnalyzing(photoUri: Uri) {
        _uiState.value = CameraUiState.FrozenAnalyzing(photoUri)
    }

    fun setError(message: String) {
        _uiState.value = CameraUiState.Error(message)
    }

    private fun freeze(photoUri: Uri) {
        _uiState.value = CameraUiState.FrozenAnalyzing(photoUri)
    }





}

// UI State
sealed class CameraUiState {
    object Idle : CameraUiState()
    object Loading : CameraUiState()  // Keep for general loading

    data class FrozenAnalyzing(val photoUri: Uri) : CameraUiState()  // New: Frozen image + spinner
    data class FoodDetected(val photoUri: Uri, val detections: List<DetectedFood>, val nutritionSummary: NutritionSummary? = null) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}


sealed class RecognitionOutcome {
    data class Success(
        val detections: List<DetectedFood>,  // top 3 for display
        val nutritionSummary: NutritionSummary
    ) : RecognitionOutcome()

    object Failure : RecognitionOutcome()
}
data class DetectedFood(val label: String, val confidence: Float)

