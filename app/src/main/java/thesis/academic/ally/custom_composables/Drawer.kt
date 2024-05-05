package thesis.academic.ally.custom_composables

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import thesis.academic.ally.activity.userDataStore
import thesis.academic.ally.api.DrawerData
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.DrawerItem
import thesis.academic.ally.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
    user: DrawerData,
    navController: NavController,
    context: Context,
    selected: String,
    content: @Composable () -> Unit
) {
    val singleTop: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
    }

    val items = listOf(
        DrawerItem(Routes.DASHBOARD, Icons.Default.Dashboard) {
            navController.navigate(Routes.DASHBOARD, singleTop)
        },
        DrawerItem(Routes.LEADERBOARD, Icons.Default.Leaderboard) {
            navController.navigate(Routes.LEADERBOARD, singleTop)
        },
        DrawerItem(Routes.ANALYTICS, Icons.Default.Analytics) {
            navController.navigate(Routes.ANALYTICS, singleTop)
        },
        DrawerItem(Routes.ASSESSMENT, Icons.Default.Assessment) {
            navController.navigate(Routes.CHOOSE_ASSESSMENT, singleTop)
        },
        DrawerItem(Routes.NOTIFICATIONS, Icons.Default.Notifications) {
            navController.navigate(Routes.NOTIFICATIONS, singleTop)
        },
        DrawerItem(Routes.ACHIEVEMENTS, Icons.Default.Badge) {
            navController.navigate(Routes.ACHIEVEMENTS, singleTop)
        },
        DrawerItem(Routes.ACCOUNT, Icons.Default.ManageAccounts) {
            navController.navigate(Routes.ACCOUNT, singleTop)
        },
        DrawerItem(Routes.ARCHIVE, Icons.Default.Archive) {
            navController.navigate(Routes.ARCHIVE, singleTop)
        },
        DrawerItem(Routes.SUPPORT, Icons.Default.Support) {
            navController.navigate(Routes.SUPPORT, singleTop)
        },
        DrawerItem("Log Out", Icons.AutoMirrored.Filled.Logout) {
            context.userDataStore.updateData {
                it.copy(authToken = "", role = "")
            }
            ActivityCacheManager.clearCache()
            navController.navigate(Routes.MAIN) {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    )
    SelectionContainer {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    drawerContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                ) {
                                    append(user.name + "  ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    append(user.degree)
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selected = item.name == selected,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    item.action()
                                }
                            },
                            modifier = Modifier.padding(
                                paddingValues = NavigationDrawerItemDefaults.ItemPadding
                            ),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedBadgeColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                unselectedBadgeColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            },
            content = content
        )
    }
}