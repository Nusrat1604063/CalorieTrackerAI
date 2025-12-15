package com.example.calorietrack.Presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calorietrack.ui.theme.GreenDark
import com.example.calorietrack.ui.theme.GreenLight
import com.example.calorietrack.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdownRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(26.dp)
            )

            Spacer(Modifier.width(14.dp))

            Text(
                text = label,
                color = GreenDark,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = value,
                color = GreenPrimary,
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = GreenPrimary
            )
        }
    }
}

@Composable
fun SetupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GreenLight),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSetupScreen(onFormValidityChanged: (Boolean) -> Unit,
                    onDataChanged: (
                        gender: String,
                        year: Int?,
                        weight: Int?,
                        height: String,
                        activity: String
                    ) -> Unit) {

    var selectedGender by remember { mutableStateOf("Select") }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var selectedWeight by remember { mutableStateOf<Int?>(null) }
    var selectedHeight by remember { mutableStateOf("Select") }
    var selectedActivity by remember { mutableStateOf(value = "Select") }

    var genderExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var weightExpanded by remember { mutableStateOf(false) }
    var heightExpanded by remember { mutableStateOf(false) }
    var ActivityExpanded by remember {mutableStateOf(false)}

    SetupCard {

        // GENDER
        SettingsDropdownRow(
            icon = Icons.Default.Person,
            label = "Gender",
            value = selectedGender,
            onClick = { genderExpanded = true }
        )
        DropdownMenu(
            expanded = genderExpanded,
            onDismissRequest = { genderExpanded = false }
        ) {
            listOf("Male", "Female", "Other").forEach { g ->
                DropdownMenuItem(
                    text = { Text(g) },
                    onClick = {
                        selectedGender = g
                        genderExpanded = false
                    }
                )
            }
        }

        // YEAR OF BIRTH
        SettingsDropdownRow(
            icon = Icons.Default.Person,
            label = "Year of Birth",
            value = selectedYear?.toString() ?: "Select",
            onClick = { yearExpanded = true }
        )
        DropdownMenu(
            expanded = yearExpanded,
            onDismissRequest = { yearExpanded = false }
        ) {
            (1960..2025).toList().reversed().forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        selectedYear = year
                        yearExpanded = false
                    }
                )
            }
        }

        // WEIGHT
        SettingsDropdownRow(
            icon = Icons.Default.Info,
            label = "Weight (kg)",
            value = selectedWeight?.toString() ?: "Select",
            onClick = { weightExpanded = true }
        )
        DropdownMenu(
            expanded = weightExpanded,
            onDismissRequest = { weightExpanded = false }
        ) {
            (30..150).forEach { w ->
                DropdownMenuItem(
                    text = { Text("$w kg") },
                    onClick = {
                        selectedWeight = w
                        weightExpanded = false
                    }
                )
            }
        }

        // HEIGHT
        SettingsDropdownRow(
            icon = Icons.Default.AccountBox,
            label = "Height",
            value = selectedHeight,
            onClick = { heightExpanded = true }
        )
        DropdownMenu(
            expanded = heightExpanded,
            onDismissRequest = { heightExpanded = false }
        ) {
            val heights = listOf(
                "4 ft 10 in", "4 ft 11 in", "5 ft 0 in", "5 ft 1 in",
                "5 ft 2 in", "5 ft 3 in", "5 ft 4 in", "5 ft 5 in",
                "5 ft 6 in", "5 ft 7 in", "5 ft 8 in"
            )
            heights.forEach { h ->
                DropdownMenuItem(
                    text = { Text(h) },
                    onClick = {
                        selectedHeight = h
                        heightExpanded = false
                    }
                )
            }
        }

        //Physical ACtivity
        // HEIGHT
        SettingsDropdownRow(
            icon = Icons.Default.CheckCircle,
            label = "Activity level",
            value = selectedActivity,
            onClick = {ActivityExpanded = true }
        )
        DropdownMenu(
            expanded = ActivityExpanded,
            onDismissRequest = { heightExpanded = false }
        ) {
            val types = listOf(
                "Sedentary", "Lightly Active", "Moderately Active", "Very Active"
            )
            types.forEach { h ->
                DropdownMenuItem(
                    text = { Text(h) },
                    onClick = {
                        selectedActivity = h
                       ActivityExpanded = false
                    }
                )
            }
        }
    }

    Spacer(Modifier.height(20.dp))
    val isFormValid =
        selectedGender != "Select" &&
                selectedYear != null &&
                selectedWeight != null &&
                selectedHeight != "Select" &&
                selectedActivity != "Select"

    LaunchedEffect(isFormValid) {
        onFormValidityChanged(isFormValid)
    }

    LaunchedEffect(
        selectedGender,
        selectedYear,
        selectedWeight,
        selectedHeight,
        selectedActivity
    ) {
        onDataChanged(
            selectedGender,
            selectedYear,
            selectedWeight,
            selectedHeight,
            selectedActivity
        )
    }


}

@Preview(showBackground = true)
@Composable
fun PersonalDetailsScreen() {
  //  UserSetupScreen()
}
