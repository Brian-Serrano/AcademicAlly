package com.serrano.academically.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.serrano.academically.activity.Support
import com.serrano.academically.ui.theme.AcademicAllyPrototypeTheme

@Composable
fun ChatField(
    inputName: String,
    input: String,
    onInputChange: (String) -> Unit,
    onMessageSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = input,
        onValueChange = onInputChange,
        label = {
            Text(
                text = "Enter ${inputName.lowercase()}",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        placeholder = {
            Text(
                text = "$inputName here",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        shape = MaterialTheme.shapes.extraSmall,
        colors = InputFieldColors(),
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { onMessageSend(input) }) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = null
                )
            }
        }
    )
}