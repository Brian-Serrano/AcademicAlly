package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.GreenButton(
    action: () -> Unit,
    text: String,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    enabled: Boolean = true,
    clickable: Boolean = true
) {
    Button(
        onClick = action,
        shape = MaterialTheme.shapes.extraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .weight(1f),
        enabled = enabled && clickable
    ) {
        Box(
            modifier = Modifier.height(25.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                style = style,
                maxLines = 1
            )
            if (!enabled) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
            }
        }
    }
}