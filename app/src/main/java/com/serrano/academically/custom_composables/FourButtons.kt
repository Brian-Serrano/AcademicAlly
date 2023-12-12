package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FourButtons(
    texts: List<String>,
    actions: List<() -> Unit>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GreenButton(action = actions[0], text = texts[0], style = MaterialTheme.typography.bodyMedium)
        GreenButton(action = actions[1], text = texts[1], style = MaterialTheme.typography.bodyMedium)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GreenButton(action = actions[2], text = texts[2], style = MaterialTheme.typography.bodyMedium)
        GreenButton(action = actions[3], text = texts[3], style = MaterialTheme.typography.bodyMedium)
    }
}