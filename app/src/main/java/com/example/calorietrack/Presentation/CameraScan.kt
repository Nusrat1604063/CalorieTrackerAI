import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.calorietrack.Networking.CameraUiState
import com.example.calorietrack.Networking.CameraViewModel
import data.datastore.Room.AppDatabase
import data.datastore.Room.ScannedMealRepository
import java.io.File

@Composable
fun CameraScreen(
    onBack: ()-> Unit
) {
    val context = LocalContext.current
    val viewModel: CameraViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    val db: AppDatabase = remember { AppDatabase.getInstance(context) }
    val repository: ScannedMealRepository = remember { ScannedMealRepository(db.scannedMealDao()) }

    LaunchedEffect(Unit) {
        viewModel.setRepository(repository)

    }
        val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    BackHandler(enabled = true) {
        onBack()
    }



    Box(modifier = Modifier.fillMaxSize()) {

        // Camera preview OR frozen image
        CameraBackground(
            uiState = uiState,
            imageCapture = imageCapture
        )


        // Spinner overlay when analyzing
        if (uiState is CameraUiState.FrozenAnalyzing) {
            AnalyzingOverlay()
        }

        // Bottom result sheet
        if (uiState is CameraUiState.FoodDetected) {
            FoodResultSheet(
                uiState = uiState  as CameraUiState.FoodDetected,
                onRetake = { viewModel.resetToIdle() },
                onDone = {
                    onBack()   // navigate back to Home
                }
            )
        }

        // Camera controls (only when idle)
        if (uiState is CameraUiState.Idle) {
            CameraControls(
                imageCapture = imageCapture,
                onCaptureSuccess = { uri ->
                    // Immediately show frozen image
                    viewModel.onNewCapture()
                    viewModel.freezeImageAndStartAnalyzing(uri)
                    // Start async detection
                    viewModel.detectFoodFromImage(context, uri)
                },
                onCaptureError = {
                    viewModel.onCaptureFailed()
                }
            )
        }
    }
}


@Composable
fun CameraBackground(
    uiState: CameraUiState,
    imageCapture: ImageCapture,       // used by CameraControls

) {
    when (uiState) {
        is CameraUiState.Idle -> {
            // Only show preview, no callbacks needed
            LiveCameraPreview(
                modifier = Modifier.fillMaxSize(),
                imageCapture = imageCapture
            )
        }

        is CameraUiState.Loading -> {
            // Show blank / spinner / fade overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        is CameraUiState.FrozenAnalyzing -> {
            Image(
                painter = rememberAsyncImagePainter(uiState.photoUri),
                contentDescription = "Frozen photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        is CameraUiState.FoodDetected -> {
            Image(
                painter = rememberAsyncImagePainter(uiState.photoUri),
                contentDescription = "Result photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
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
    }
}



@Composable
fun LiveCameraPreview(
    modifier: Modifier = Modifier.fillMaxSize(),
    imageCapture: ImageCapture
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()

                    // ✅ BIND BOTH Preview AND ImageCapture
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )

                } catch (e: Exception) {
                    Log.e("CameraX", "Binding failed", e)
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}



@Composable
fun AnalyzingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun FoodResultSheet(
    uiState: CameraUiState.FoodDetected,
    onRetake: () -> Unit,
    onDone: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Detected Food",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.detections.forEach {
                Text("${it.label} (${(it.confidence * 100).toInt()}%)")
            }

            Spacer(modifier = Modifier.height(12.dp))

            uiState.nutritionSummary?.let {
                Text("Calories: ${it.calories}")
                Text("Protein: ${it.proteinGrams}g")
                Text("Fat: ${it.fatGrams}g")
                Text("Carbs: ${it.carbsGrams}g")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retake")
                }

                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Done")
                }
            }

        }
    }
}

@Composable
fun CameraControls(
    imageCapture: ImageCapture,
    onCaptureSuccess: (Uri) -> Unit,
    onCaptureError: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        IconButton(
            modifier = Modifier
                .size(80.dp)
                .background(Color.White, CircleShape),
            onClick = {
                val photoFile = File(
                    context.cacheDir,
                    "photo_${System.currentTimeMillis()}.jpg"
                )

                val outputOptions =
                    ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {

                        override fun onImageSaved(
                            outputFileResults: ImageCapture.OutputFileResults
                        ) {
                            onCaptureSuccess(Uri.fromFile(photoFile))
                        }

                        override fun onError(
                            exception: ImageCaptureException
                        ) {
                            Log.e("CameraX", "Capture failed", exception)
                            onCaptureError()
                        }
                    }
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Capture",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

