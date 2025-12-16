package practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ParentScreen() {

    var isFormValid by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ChildComposable(
            onValidityChange = { isValid ->
                isFormValid = isValid
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* navigate */ },
            enabled = isFormValid
        ) {
            Text("Continue")
        }
    }
}


@Composable
fun ChildComposable(
    onValidityChange: (Boolean) -> Unit
) {
    var firstInput by remember { mutableStateOf("") }
    var secondinput by remember { mutableStateOf("") }

    val isValid = firstInput.isNotBlank() && secondinput.isNotBlank()

    LaunchedEffect(isValid) {
        onValidityChange(isValid)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = firstInput,
            onValueChange = {
                firstInput = it
            },
            label = {
                Text("First input")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = secondinput,
            onValueChange = {
                secondinput = it
            },
            label = {
                Text("Second input")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExTwo () {
   ParentScreen()
}
