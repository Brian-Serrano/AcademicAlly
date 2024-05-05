package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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