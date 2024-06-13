package thesis.academic.ally.custom_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShortText
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    scope: CoroutineScope,
    drawerState: DrawerState,
    onIconClick: () -> Unit,
    image: ImageBitmap
): @Composable () -> Unit {
    return {
        TopAppBar(
            title = {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)
                        ) {
                            append("Academic")
                        }
                        withStyle(
                            style = SpanStyle(color = MaterialTheme.colorScheme.secondary)
                        ) {
                            append("Ally")
                        }
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = onIconClick) {
                    Image(
                        bitmap = image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                }
            },
            actions = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShortText,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            colors = DashboardTopBarColors()
        )
    }
}