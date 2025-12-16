package practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.calorietrack.Presentation.SettingsDropdownRow

@Composable
fun ButtonDisableExercise() {
   var age by remember { mutableStateOf<Int?>(null)}
    var weight by remember { mutableStateOf<Int?>(null) }

    var ageexpanded by remember { mutableStateOf(false) }
    var weightexpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        SettingsDropdownRow(
            icon = Icons.Default.Person,
            label = "Age",
            value = age.toString(),
            onClick = {
                ageexpanded = true
            }
        )
        DropdownMenu(
            expanded = ageexpanded,
            onDismissRequest = {
                ageexpanded = false
            }
        ) {
            (15..120).forEach { w ->
                DropdownMenuItem(
                    text = { Text("$w ") },
                    onClick = {
                        age = w
                        ageexpanded = false
                    }
                )

            }
        }

        SettingsDropdownRow(
            icon = Icons.Default.Person,
            label = "Weight",
            value = weight.toString(),
            onClick = {
                weightexpanded = true
            }
        )
        DropdownMenu(
            expanded = weightexpanded,
            onDismissRequest = {
                weightexpanded = false
            }

        ) {
            (30..120).forEach { K->
                DropdownMenuItem(
                    text = {Text("$K kg")},
                    onClick = {
                        weight = K
                        weightexpanded = false
                    }
                )
            }
        }
        val isFormValid = age != null && weight!= null


        Button(onClick = {}, colors = ButtonDefaults.buttonColors(
            containerColor = if(isFormValid)   Color(0xFF0B6623)
            else
                Color(0xFF0B6623).copy(alpha = 0.5f),
        ), enabled = isFormValid) {
            Text("Continue")
                
        }


    }



}

@Preview(showBackground = true)
@Composable
fun previewDropDown() {
    ButtonDisableExercise()
}


