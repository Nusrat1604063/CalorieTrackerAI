package com.example.calorietrack.Networking

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import androidx.compose.runtime.State


class CameraViewModel : ViewModel() {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
    )

    private val _uiState = mutableStateOf<CameraUiState>(CameraUiState.Idle)
    val uiState: State<CameraUiState> = _uiState

    fun detectFoodFromImage(image: InputImage, photoUri: Uri) {
        _uiState.value = CameraUiState.Loading

        labeler.process(image)
            .addOnSuccessListener { labels ->

                // DEBUG: see what ML Kit actually detects
                labels.forEach {
                    android.util.Log.d("ML", "${it.text} -> ${it.confidence}")
                }

                val bestLabel = labels
                    .filter { it.confidence >= 0.6f }
                    .firstOrNull {
                        it.text.lowercase() !in listOf("food", "dish", "meal", "tableware")
                    }

                if (bestLabel != null) {
                    _uiState.value = CameraUiState.FoodDetected(
                        photoUri = photoUri,
                        foodName = bestLabel.text,
                        confidence = bestLabel.confidence
                    )
                } else {
                    _uiState.value = CameraUiState.Error("No food detected")
                }
            }
            .addOnFailureListener {
                _uiState.value = CameraUiState.Error("ML Kit failed")
            }
    }

    override fun onCleared() {
        super.onCleared()
        labeler.close()
    }
}




sealed class CameraUiState {
    object Idle : CameraUiState()
    object Loading : CameraUiState()

    data class FoodDetected(
        val photoUri: Uri,
        val foodName: String,
        val confidence: Float
    ) : CameraUiState()

    data class Error(val message: String) : CameraUiState()
}
