package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun LoginTextField(
    inputName: String,
    input: String,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    supportingText: String = ""
) {
    TextField(
        value = input,
        onValueChange = onInputChange,
        label = {
            Text(
                text = "Enter ${inputName.lowercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        },
        placeholder = {
            Text(
                text = "$inputName here",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        },
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        minLines = minLines,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.extraLarge,
        colors = InputFieldColors(),
        modifier = modifier
            .fillMaxWidth(),
        supportingText = {
            Text(text = supportingText, color = Color.Black)
        }
    )
}