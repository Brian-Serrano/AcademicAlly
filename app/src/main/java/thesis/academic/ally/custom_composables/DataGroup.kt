package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DataGroup(
    names: List<String>,
    values: List<String>
) {
    names.forEachIndexed { index, name ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = values[index],
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}