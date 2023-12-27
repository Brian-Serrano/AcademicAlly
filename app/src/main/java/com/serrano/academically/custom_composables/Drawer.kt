package com.serrano.academically.custom_composables

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.utils.UserDrawerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Drawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
    user: UserDrawerData,
    navController: NavController,
    context: Context,
    selected: String,
    content: @Composable () -> Unit
) {
    val items = listOf(
        Icons.Default.Dashboard,
        Icons.Default.Leaderboard,
        Icons.Default.Analytics,
        Icons.Default.Assessment,
        Icons.Default.Notifications,
        Icons.Default.Badge,
        Icons.Default.ManageAccounts,
        Icons.Default.Archive,
        Icons.Default.Logout
    )
    SelectionContainer {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.primaryContainer) {
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
                                        color = Color.White
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
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            },
                            label = {
                                Text(
                                    text = item.name.substring("Filled.".length),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selected = item.name.substring("Filled.".length) == selected,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    when (item.name.substring("Filled.".length)) {
                                        "Dashboard" -> navController.navigate("Dashboard/${user.id}")
                                        "Leaderboard" -> navController.navigate("Leaderboard/${user.id}")
                                        "Analytics" -> navController.navigate("Analytics/${user.id}")
                                        "Assessment" -> navController.navigate("ChooseAssessment/${user.id}")
                                        "Notifications" -> navController.navigate("Notifications/${user.id}")
                                        "Badge" -> navController.navigate("Achievements/${user.id}")
                                        "ManageAccounts" -> navController.navigate("Account/${user.id}")
                                        "Archive" -> navController.navigate("Archive/${user.id}")
                                        "Logout" -> {
                                            UpdateUserPref.clearDataByLoggingOut(context)
                                            navController.navigate("Main")
                                        }

                                        else -> navController.navigate("Dashboard/${user.id}")
                                    }
                                }
                            },
                            modifier = Modifier.padding(
                                paddingValues = NavigationDrawerItemDefaults.ItemPadding
                            ),
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            },
            content = content
        )
    }
}