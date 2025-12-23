package com.example.calorietrack.Presentation

import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.calorietrack.Networking.CameraUiState
import com.example.calorietrack.Networking.CameraViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.mlkit.vision.common.InputImage
import java.io.File
import coil.compose.rememberAsyncImagePainter



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState

    val previewView = remember { PreviewView(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Bind CameraX
    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 🔹 Freeze-frame when food detected
        if (uiState is CameraUiState.FoodDetected) {
            Image(
                painter = rememberAsyncImagePainter(
                    (uiState as CameraUiState.FoodDetected).photoUri
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Center guide
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .border(3.dp, Color(0xFF81C784), RoundedCornerShape(28.dp))
        ) {
            Text(
                text = "Place food here",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Capture button
        IconButton(
            onClick = {
                val capture = imageCapture ?: return@IconButton

                val file = File(
                    context.externalCacheDir,
                    "food_${System.currentTimeMillis()}.jpg"
                )

                capture.takePicture(
                    ImageCapture.OutputFileOptions.Builder(file).build(),
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            android.util.Log.d(
                                "Camera",
                                "Captured: exists=${file.exists()} size=${file.length()}"
                            )

                            val uri = Uri.fromFile(file)
                            val inputImage =
                                InputImage.fromFilePath(context, uri)

                            viewModel.detectFoodFromImage(
                                image = inputImage,
                                photoUri = uri
                            )
                        }

                        override fun onError(exc: ImageCaptureException) {
                            android.util.Log.e("Camera", exc.message ?: "Capture error")
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .size(80.dp)
                .background(Color(0xFF81C784), CircleShape)
        ) {
            Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(36.dp))
        }

        // Loading
        if (uiState is CameraUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Text("Analyzing food...", color = Color.White)
                }
            }
        }

        // Error
        if (uiState is CameraUiState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (uiState as CameraUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

