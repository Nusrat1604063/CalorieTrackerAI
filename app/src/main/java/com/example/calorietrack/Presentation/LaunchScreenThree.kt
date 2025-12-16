package com.example.calorietrack.Presentation

import androidx.compose.runtime.Composable


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun PersonalDetailsWithProgressScreen(
    onContinue: () -> Unit = {},
    viewModel: UserSetupViewModel = viewModel()
) {
    var gender by remember { mutableStateOf("") }
    var year by remember { mutableStateOf<Int?>(null) }
    var weight by remember { mutableStateOf<Int?>(null) }
    var height by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf("") }
    var isFormValid by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Progress",
            color = Color(0xFF888888),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))



        // Animate from 0 → 0.75
        val progress = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            progress.animateTo(
                targetValue = 0.75f,
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

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Personalize your daily calorie goals",
            color = Color(0xFF6D6D6D),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))


        // ==================== MIDDLE: Your existing form ====================
        // Just call your existing full screen here
        UserSetupScreen( onFormValidityChanged = { isFormValid = it },
            onDataChanged = { g, y, w, h, a ->
                gender = g
                 year = y
                weight = w
                height = h
                activity = a

            })

        // ==================== BOTTOM BUTTON ====================
        Spacer(modifier = Modifier.height(80.dp)) // Push button to bottom

        Button(
            enabled = isFormValid,
            onClick = {
                val profile = viewModel.buildUserProfile(
                    gender = gender,
                    yearOfBirth = year!!,
                    heightString = height,
                    weightKg = weight!!,
                    activityLevel = activity)
                //saveUserProfile(context, profile)
                viewModel.saveAndLogUserProfile(context, profile)

                onContinue()        //trigger navigatiopn
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid)
                    Color(0xFF0B6623)
                else
                    Color(0xFF0B6623).copy(alpha = 0.5f),
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
fun ShowScreenThree() {
    PersonalDetailsWithProgressScreen({})
}
