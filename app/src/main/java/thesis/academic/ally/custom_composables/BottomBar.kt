package thesis.academic.ally.custom_composables

import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomBar(
    items: List<String>,
    icons: List<List<ImageVector>>,
    navBarIndex: Int,
    onClick: (Int) -> Unit
): @Composable () -> Unit {
    return {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = navBarIndex == index,
                    onClick = { onClick(index) },
                    label = {
                        Text(text = item)
                    },
                    alwaysShowLabel = true,
                    icon = {
                        Icon(imageVector = icons[index][if (navBarIndex == index) 1 else 0], contentDescription = null)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = MaterialTheme.colorScheme.background,
                        indicatorColor = Color.LightGray,
                        unselectedIconColor = MaterialTheme.colorScheme.onTertiary,
                        unselectedTextColor = MaterialTheme.colorScheme.onTertiary,
                        disabledIconColor = Color.Transparent,
                        disabledTextColor = Color.Transparent
                    )
                )
            }
        }
    }
}