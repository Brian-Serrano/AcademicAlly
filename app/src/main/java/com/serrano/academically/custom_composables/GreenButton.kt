package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RowScope.GreenButton(
    action: () -> Unit,
    text: String,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    Button(
        onClick = action,
        shape = MaterialTheme.shapes.extraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(10.dp).fillMaxWidth().weight(1f)
    ) {
        Text(text = text, color = Color.White, style = style, maxLines = 1)
    }
}