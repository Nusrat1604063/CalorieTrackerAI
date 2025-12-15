package com.example.calorietrack.Presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietrack.R
import kotlinx.coroutines.delay

@Composable
fun ScreenOneMain(onFinished: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
        ) {

        LaunchedEffect(Unit) {
            delay(3000L)
            onFinished()
        }

       Spacer(modifier = Modifier.height(240.dp))

        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {

            val alpha = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                alpha.animateTo(1f, animationSpec = tween(1200))
            }
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CalorieTrack AI",
                color = Color(0xFF0B6623),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

        }

     Spacer(modifier = Modifier.height(300.dp))

     Column(modifier = Modifier.fillMaxWidth(),
         horizontalAlignment = Alignment.CenterHorizontally) {
         AnimatedSlogan()

     }

    }
}

@Composable
fun AnimatedSlogan() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val density = LocalDensity.current
    val offsetX = remember { Animatable(-screenWidth.value) } // animate as Float

    // Start the animation when the composable appears
    LaunchedEffect(Unit) {
        offsetX.animateTo(
            targetValue = 0f, // animate to 0
            animationSpec = tween(
                durationMillis = 1500,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.value.dp.roundToPx(), 0) }, // convert to px
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "KNOW WHAT YOU EAT",
            color = Color(0xFF0B6623),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.SansSerif
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "CONTROL WHAT YOU BECOME",
            color = Color(0xFF0B6623),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Preview(showBackground = true)
@Composable()
fun ShowScreenOne() {
   ScreenOneMain({})
}