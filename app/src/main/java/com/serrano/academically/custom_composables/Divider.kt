package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Divider() {
    HorizontalDivider(
        color = Color.Gray,
        thickness = 5.dp,
        modifier = Modifier.padding(10.dp)
    )
}