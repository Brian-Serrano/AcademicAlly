package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoCard(
    title: String,
    description: String
) {
    CustomCard {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(10.dp)
        )
        HorizontalDivider(thickness = 2.dp)
        Text(
            text = description,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(10.dp)
        )
    }
}