package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
fun BlackButton(
    text: String,
    action: () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    enabled: Boolean = true
) {
    Button(
        onClick = action,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier.height(if (style == MaterialTheme.typography.titleMedium) 30.dp else 25.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = style,
                color = if (enabled) MaterialTheme.colorScheme.onBackground else Color.Transparent
            )
            if (!enabled) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
            }
        }
    }
}