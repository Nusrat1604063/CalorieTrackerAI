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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.lazy.items



@Composable
fun MealScansCard(
    scannedMeals: List<ScannedMeal>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262626).copy(alpha = 0.85f)
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Text(
                text = "Meal Scans",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (scannedMeals.isEmpty()) {
                Text(
                    text = "No scans yet",
                    color = Color.Gray
                )
            } else {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(scannedMeals) { meal ->
                        ScannedMealItem(meal)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total calories: ${scannedMeals.sumOf { it.calories }} Cal",
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
       // MealScansCard(dummyMeals) // With data
       // Spacer(modifier = Modifier.height(24.dp))
       // MealScansCard(scannedMeals = emptyList()) // Empty state
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MealScansCarditem() {
    //ScannedMealItem(   ScannedMeal(1, "Scrambled Eggs with Toast", 380))

}
