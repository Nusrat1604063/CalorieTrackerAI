package com.example.calorietrack.Presentation

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import data.datastore.model.ScannedMeal
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material3.*
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


@Composable
fun WaterIntakeCard() {
    var glassesTaken by rememberSaveable { mutableStateOf(0) } // ← Mutable state, survives rotation
    val goalGlasses = 8
    val glassSizeMl = 250
    val totalTakenMl = glassesTaken * glassSizeMl
    val goalMl = goalGlasses * glassSizeMl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E3A8A).copy(alpha = 0.85f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Title + Recommended (same as before)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalDrink, null, tint = Color(0xFF60A5FA), modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Water Intake", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text("Recommended: $goalGlasses glasses", fontSize = 14.sp, color = Color(0xFFB3E5FC))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Glass icons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(goalGlasses) { index ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = if (index < glassesTaken) Icons.Filled.LocalDrink else Icons.Outlined.LocalDrink,
                            contentDescription = "Glass ${index + 1}",
                            tint = if (index < glassesTaken) Color(0xFF60A5FA) else Color(0xFF1E3A8A),
                            modifier = Modifier.size(36.dp).align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Taken: $glassesTaken / $goalGlasses glasses",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "$totalTakenMl ml / $goalMl ml",
                fontSize = 14.sp,
                color = Color(0xFFB3E5FC),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            // Intake button – direct increment here
            Button(
                onClick = {
                    if (glassesTaken < goalGlasses) {
                        glassesTaken++ // ← Direct increment – triggers recomposition
                    }
                },
                enabled = glassesTaken < goalGlasses,
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
                Text("Intake +1 Glass", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun WaterIntake() {
    WaterIntakeCard()
}
