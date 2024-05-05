package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CustomTab(
    tabIndex: Int,
    tabs: List<String>,
    onTabClick: (Int) -> Unit,
    badgeEnabled: Boolean = false,
    badge: @Composable (BoxScope.(Int) -> Unit) = {}
) {
    TabRow(
        selectedTabIndex = tabIndex,
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.background
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = {
                    if (badgeEnabled) {
                        BadgedBox(badge = { badge(index) }) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                selected = tabIndex == index,
                onClick = { onTabClick(index) }
            )
        }
    }
}