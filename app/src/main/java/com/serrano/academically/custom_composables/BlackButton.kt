package com.serrano.academically.custom_composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController

@Composable
fun BlackButton(
    text: String,
    action: () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    Button(
        onClick = action,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground),
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        content = {
            Text(
                text = text,
                color = Color.White,
                style = style
            )
        }
    )
}