package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Text_1(text: String, paddingSize: Dp = 10.dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Color.DarkGray,
        modifier = Modifier.padding(paddingSize)
    )
}