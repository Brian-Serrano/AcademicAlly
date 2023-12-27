package com.serrano.academically.custom_composables

import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
            contentColor = Color.Black
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
                        BadgedBox(
                            badge = {

                            }
                        ) {
                            Icon(imageVector = icons[index][navBarIndex], contentDescription = null)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Black,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.DarkGray,
                        unselectedTextColor = Color.DarkGray
                    )
                )
            }
        }
    }
}