package com.example.calorietrack.Presentation
import android.util.Log

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietrack.R
import data.datastore.AppPreferences
import data.datastore.dataStore
import data.datastore.setupCompleted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AllSetScreen(
    onContinue: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Launching",
            color = Color(0xFF888888),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))



        // Animate from 0 → 0.75
        val progress = remember { Animatable(0.75f) }

        LaunchedEffect(Unit) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
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




        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "YOU’RE ALL SET 🎉",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB04A4A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your profile is ready",
            fontSize = 16.sp,
            color = Color(0xFF777777)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to",
            fontSize = 18.sp,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Medium
        )

       Spacer(modifier = Modifier.height(40.dp))
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

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "CalorieTrack AI",
                color = Color(0xFF0B6623),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

        }


        Spacer(modifier = Modifier.height(240.dp))
                Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                   setupCompleted(context)
                    val value = context.dataStore.data.first()[AppPreferences.SETUP_DONE]
                    Log.d("DataStore", "setup_done = $value")
                }
                onContinue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B6623),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

    }



}

@Preview(showBackground = true)
@Composable()
fun Allset() {
    AllSetScreen(onContinue = {})
}
