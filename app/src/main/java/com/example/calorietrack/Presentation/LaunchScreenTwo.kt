package com.example.calorietrack.Presentation

import kotlinx.coroutines.delay
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietrack.R


@Composable
fun LaunchScreenTwo( onContinue: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ==================== TOP: Progress Bar ====================
        Spacer(modifier = Modifier.height(60.dp)) // Small space from top

        Text(
            text = "Launching",
            color = Color(0xFF888888),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Animated Progress Bar (0% → 25%)
        val progress = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            progress.animateTo(
                targetValue = 0.25f,
                animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f) //  // 80% width, looks cleaner
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF4CAF50))
            )
        }

        // ==================== MIDDLE: Logo + Title (moved up) ====================
        Spacer(modifier = Modifier.height(200.dp)) // Reduced from 240.dp → brings logo higher

        val logoAlpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(100) // Slight delay so progress starts first
            logoAlpha.animateTo(1f, animationSpec = tween(200))
        }

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .alpha(logoAlpha.value)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CalorieTrack AI",
            color = Color(0xFF0B6623),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(logoAlpha.value)
        )

        // ==================== BOTTOM: Continue Button ====================
        Spacer(modifier = Modifier.height(250.dp)) // Pushes button to bottom

        Button(
            onClick = { onContinue() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B6623), // dark green
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun ShowScreenTwo() {
   LaunchScreenTwo()
}