package thesis.academic.ally.custom_composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainButton(
    text: String,
    route: String,
    color: Color,
    navController: NavController
) {
    Button(
        onClick = {
            navController.navigate(route)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = MaterialTheme.colorScheme.onBackground,
            disabledContentColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .width(280.dp)
            .height(72.dp)
            .border(
                width = 10.dp,
                color = color,
                shape = MaterialTheme.shapes.extraLarge
            ),
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.displayMedium
            )
        }
    )
}