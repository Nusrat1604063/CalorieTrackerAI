package com.example.calorietrack.Presentation
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue

import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi

import android.Manifest
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(onNavigateToCamera: () -> Unit) {
    var selectedTab by remember { mutableStateOf(BottomBarTab.Home) }
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.CAMERA
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            HelloThereCard()
            DailySummaryCard()
            WaterIntakeCard2()
            MealScansCard()
        }

        FloatingBottomBar(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab

                if (tab == BottomBarTab.Scan) {
                    if (cameraPermissionState.status.isGranted) {
                        onNavigateToCamera()
                        // Permission granted → open camera
                        // TODO: Navigate to camera screen here
                        // e.g., Toast.makeText(LocalContext.current, "Opening camera...", Toast.LENGTH_SHORT).show()
                    } else {
                        // Always launch request → system popup appears
                        Toast.makeText(context, "Requesting camera permission...", Toast.LENGTH_SHORT).show()
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun HelloThereCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF19443C).copy(alpha = 0.85f) // Professional deep teal-green
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 28.dp, bottom = 28.dp, end = 24.dp)
        ) {
            // Greeting + waving emoji
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello there",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = " 👋",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Today's date
            Text(
                text = "Tuesday, December 16",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB2DFDB), // Soft light green for secondary text
            )

            Spacer(modifier = Modifier.height(15.dp)) // Your requested gap

            // Month + Year with dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "December 2025",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB2DFDB)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFB2DFDB),
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar row - days 15 to 21 (Dec 15 Mon → Dec 21 Sun)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("15", "16", "17", "18", "19", "20", "21")
                val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                days.forEachIndexed { index, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = weekdays[index],
                            fontSize = 10.sp,
                            color = if (index == 1) Color(0xFF4DB6AC) else Color(0xFF80CBC4), // Highlight Tue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    if (index == 1) Color(0xFF4DB6AC) else Color.Transparent, // Highlight 16
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

//#35554F -- calm Sage Teal
@Composable
fun DailySummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF045).copy(alpha = .4f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, top = 20.dp, bottom = 20.dp, end = 24.dp)
        ) {
            Text(
                text = "Daily Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Circular progress ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        val strokeWidth = 20f
                        val radius = size.minDimension / 2 - strokeWidth / 2

                        // Full gray track
                        drawCircle(
                            color = Color(0xFF555555),
                            radius = radius,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        val progress = 0.76f // Adjust as needed (e.g., 76% consumed → 24% left)
                        drawArc(
                            color = Color(0xFFFF6B6B),
                            startAngle = 30f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "300",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Cal",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Left",
                            fontSize = 14.sp,
                            color = Color(0xFFAAAAAA)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Recommended",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB2DFDB)
                    )
                    Text(
                        text = "1400 calories",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    MacroProgressBar(label = "Protein", progress = 0.75f, color = Color(0xFF4FC3F7))
                    Spacer(modifier = Modifier.height(10.dp))
                    MacroProgressBar(label = "Fat",       progress = 0.55f, color = Color(0xFFFFD54F))
                    Spacer(modifier = Modifier.height(10.dp))
                    MacroProgressBar(label = "Carbs",     progress = 0.40f, color = Color(0xFF81C784))
                }
            }
        }
    }
}

@Composable
fun MacroProgressBar(label: String, progress: Float, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color.White,
            modifier = Modifier.width(80.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = Color(0xFF444444),
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        )
    }
}


@Preview(showBackground = true)
@Composable()
fun ShowScreenHome() {
   //GreetingCard()
   // HelloThereCard()
    //DailySummaryCard()
    //HomeScreen()
}
