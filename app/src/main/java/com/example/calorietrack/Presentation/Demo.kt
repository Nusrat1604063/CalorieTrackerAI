package com.example.calorietrack.Presentation

import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import data.datastore.model.ScannedMeal
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.calorietrack.R
import kotlinx.coroutines.delay

@Composable
fun WaterIntakeCard1() {
    var takenMl by rememberSaveable { mutableStateOf(0) } // Starts at 0 ml
    val goalMl = 2000
    val progress = (takenMl.toFloat() / goalMl).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E3A8A).copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title + Recommended
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Water Intake",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Recommended: 2000 ml",
                        fontSize = 14.sp,
                        color = Color(0xFFB3E5FC)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Large circular progress with drop icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(180.dp)
            ) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    val strokeWidth = 16f
                    val radius = size.minDimension / 2 - strokeWidth / 2

                    // Gray track
                    drawCircle(
                        color = Color(0xFF444444),
                        radius = radius,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Blue progress arc
                    drawArc(
                        color = Color(0xFF60A5FA),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Central water drop icon
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = if (progress == 1f) Color(0xFF60A5FA) else Color.White,
                    modifier = Modifier.size(72.dp)
                )

                // Text inside ring
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$takenMl",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "ml",
                        fontSize = 18.sp,
                        color = Color(0xFFB3E5FC)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Taken / Goal text
            Text(
                text = "Taken: $takenMl / $goalMl ml",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            // Intake button (+250 ml)
            Button(
                onClick = {
                    if (takenMl + 250 <= goalMl) {
                        takenMl += 250
                    } else if (takenMl < goalMl) {
                        takenMl = goalMl // Cap at goal
                    }
                },
                enabled = takenMl < goalMl,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF60A5FA),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Intake +250 ml", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun WaterIntakeCard2() {
    var glassesTaken by rememberSaveable { mutableStateOf(0) }
    val goalGlasses = 8

    var triggerSplash by remember { mutableStateOf(false) }
    val splashAlpha by animateFloatAsState(
        targetValue = if (triggerSplash) 1f else 0f,
        animationSpec = tween(600),
        finishedListener = { triggerSplash = false }
    )

    val context = LocalContext.current

    // Safe SoundPool
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }

    var soundId by remember { mutableStateOf(0) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        soundId = soundPool.load(context, R.raw.water_pour, 1)
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) isLoaded = true
        }
    }

    LaunchedEffect(glassesTaken) {
        if (glassesTaken > 0 && isLoaded && soundId != 0) {
            val streamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f) // Start playing
            delay(1000L) // Wait exactly 3 seconds
            soundPool.stop(streamId) // Stop the stream after 3 seconds
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E3A8A).copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.Start)) {
                Image(
                    painter = painterResource(id = R.drawable.sp2),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Water Intake", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text("Recommended: 8 glasses (2000 ml)", fontSize = 14.sp, color = Color(0xFFB3E5FC))
                }
            }

            Spacer(Modifier.height(6.dp))

            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.wp),
                    contentDescription = "Water glass",
                    modifier = Modifier.size(130.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.sp2),
                    contentDescription = "Splash",
                    modifier = Modifier
                        .size(45.dp)
                        .graphicsLayer(alpha = splashAlpha)
                        .align(Alignment.TopCenter)
                        .offset(y = (-7).dp)
                )
            }

            Spacer(Modifier.height(0.dp))

            Text(
                text = "$glassesTaken/$goalGlasses",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Glasses today",
                fontSize = 12.sp,
                color = Color(0xFFB3E5FC)
            )

            Spacer(Modifier.height(6.dp))

            Button(
                onClick = {
                    if (glassesTaken < goalGlasses) {
                        glassesTaken++
                        triggerSplash = true
                    }
                },
                enabled = glassesTaken < goalGlasses,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA)),
                modifier = Modifier.fillMaxWidth().height(35.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Spacer(Modifier.width(6.dp))
                Text("Intake +1 Glass", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable()
fun WaterIntakeer() {
    WaterIntakeCard2()
}
