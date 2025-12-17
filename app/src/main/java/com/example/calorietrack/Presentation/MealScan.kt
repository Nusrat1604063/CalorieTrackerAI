package com.example.calorietrack.Presentation

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

import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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



@Composable
fun MealScansCard(
    scannedMeals: List<ScannedMeal> = emptyList() // Replace with your ViewModel data later
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262626).copy(alpha = .85f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Title row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Meal Scans",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (scannedMeals.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No meal scans yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFCCCCCC),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Snap a photo of your food with the camera button below",
                        fontSize = 14.sp,
                        color = Color(0xFFAAAAAA),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            } else {
                // Horizontal list of scanned meals
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    items(scannedMeals) { meal ->
//                        ScannedMealItem(meal = meal)
//                    }
                }

                // Optional total footer
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Total from scans: ${scannedMeals.sumOf { it.calories }} Cal",
                    fontSize = 14.sp,
                    color = Color(0xFFB2DFDB),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

// Data class for a scanned meal

// Single meal item in the horizontal list
@Composable
fun ScannedMealItem(meal: ScannedMeal) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(140.dp)
    ) {
        // Placeholder for photo (replace with AsyncImage when you have real URLs)
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFF444444), RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFF888888),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = meal.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${meal.calories} Cal",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF81C784) // Soft green accent
        )
    }
}

@Preview(showBackground = true)
@Composable()
fun ShowScreenHome1() {
   //MealScansCard()
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MealScansCardPreview() {
    val dummyMeals = listOf(
        ScannedMeal(1, "Scrambled Eggs with Toast", 380),
        ScannedMeal(2, "Chicken Caesar Salad", 520),
        ScannedMeal(3, "Apple with Almonds", 220),
        ScannedMeal(4, "Spaghetti Bolognese", 680)
    )

    Column(modifier = Modifier.background(Color.Black)) {
        MealScansCard(scannedMeals = dummyMeals) // With data
        Spacer(modifier = Modifier.height(24.dp))
        MealScansCard(scannedMeals = emptyList()) // Empty state
    }
}
