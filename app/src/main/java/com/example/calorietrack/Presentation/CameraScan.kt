import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.calorietrack.Networking.CameraUiState
import com.example.calorietrack.Networking.CameraViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = viewModel(),
    onBack: () -> Unit = {}  // You had this parameter but didn't use it — added default
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER  // Optional: better fit
        }
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission required", color = Color.White)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
        return  // Don't show camera UI until granted
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background: Live preview, frozen analyzing, or final result
        when (uiState) {
            is CameraUiState.FoodDetected -> {
                val detections = (uiState as CameraUiState.FoodDetected).detections

                // Professional results card with nutrition summary
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .padding(vertical = 20.dp, horizontal = 20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Nutrition Summary (only if available)
                        val nutrition = (uiState as CameraUiState.FoodDetected).nutritionSummary

                        Text(
                            text = "Estimated Calories: ${nutrition?.calories ?: 0} kcal",
                            color = Color(0xFF81C784),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Protein: ${nutrition?.proteinGrams?.toInt() ?: 0}g • " +
                                    "Carbs: ${nutrition?.carbsGrams?.toInt() ?: 0}g • " +
                                    "Fat: ${nutrition?.fatGrams?.toInt() ?: 0}g",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(20.dp))

                        // Detected Foods List
                        Text(
                            text = "Detected Foods",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))

                        detections.forEachIndexed { index, food ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${index + 1}. ${food.label}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${(food.confidence * 100).toInt()}% confidence",
                                    color = Color(0xFF81C784),
                                    fontSize = 16.sp
                                )
                            }
                        }

                        if (detections.isEmpty()) {
                            Text(
                                text = "No food detected",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            is CameraUiState.FrozenAnalyzing -> {
                // Frozen photo with "Calculating calories..." spinner
                Image(
                    painter = rememberAsyncImagePainter((uiState as CameraUiState.FrozenAnalyzing).photoUri),
                    contentDescription = "Analyzing photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 6.dp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Calculating calories...",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            is CameraUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as CameraUiState.Error).message,
                        color = Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                // Live camera preview
                AndroidView(
                    factory = { ctx ->
                        previewView.apply {
                            post {
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(surfaceProvider)
                                    }
                                    imageCapture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_BACK_CAMERA,
                                            preview,
                                            imageCapture
                                        )
                                    } catch (e: Exception) {
                                        Log.e("Camera", "Camera bind failed", e)
                                    }
                                }, ContextCompat.getMainExecutor(ctx))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Center guide box (only during live preview)
        if (uiState is CameraUiState.Idle) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center)
                    .border(3.dp, Color(0xFF81C784), RoundedCornerShape(28.dp))
            ) {
                Text(
                    text = "Place food here",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Capture button (only during live preview)
        if (uiState is CameraUiState.Idle) {
            IconButton(
                onClick = {
                    val capture = imageCapture ?: return@IconButton

                    val file = File(context.externalCacheDir, "food_${System.currentTimeMillis()}.jpg")

                    // Instant freeze on click with "Calculating calories..." spinner
                    val tempUri = Uri.fromFile(file)  // Temp URI for immediate display
                    viewModel.freezeImageAndStartAnalyzing(tempUri)

                    capture.takePicture(
                        ImageCapture.OutputFileOptions.Builder(file).build(),
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                Log.d("Camera", "Photo saved: ${file.absolutePath}")

                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )

                                // Refresh with real URI (spinner stays until analysis done)
                                viewModel.freezeImageAndStartAnalyzing(uri)
                                viewModel.detectFoodFromImage(context = context, photoUri = uri)
                            }

                            override fun onError(exc: ImageCaptureException) {
                                Log.e("Camera", "Capture failed: ${exc.message}", exc)
                                viewModel.setError("Capture failed. Please try again.")
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
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = "Take photo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
