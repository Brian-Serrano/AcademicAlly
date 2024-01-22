package com.serrano.academically.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.EditSessionMenu
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.CreateSessionViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun CreateSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    messageId: Int,
    createSessionViewModel: CreateSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        createSessionViewModel.getData(messageId, context)
    }

    val user by createSessionViewModel.drawerData.collectAsState()
    val sessionSettings by createSessionViewModel.sessionSettings.collectAsState()
    val process by createSessionViewModel.processState.collectAsState()
    val message by createSessionViewModel.message.collectAsState()
    val enabled by createSessionViewModel.buttonEnabled.collectAsState()
    val isRefreshLoading by createSessionViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { createSessionViewModel.refreshData(messageId, context) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "CREATE SESSION",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "CREATE SESSION",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "CREATE SESSION",
                navController = navController,
                context = context,
                selected = "Notifications"
            ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomCard {
                        EditSessionMenu(
                            sessionSettings = sessionSettings,
                            onDateInputChange = {
                                createSessionViewModel.updateSessionSettings(
                                    sessionSettings.copy(date = it)
                                )
                            },
                            onStartTimeInputChange = {
                                createSessionViewModel.updateSessionSettings(
                                    sessionSettings.copy(startTime = it)
                                )
                            },
                            onEndTimeInputChange = {
                                createSessionViewModel.updateSessionSettings(
                                    sessionSettings.copy(endTime = it)
                                )
                            },
                            onLocationInputChange = {
                                createSessionViewModel.updateSessionSettings(
                                    sessionSettings.copy(location = it)
                                )
                            },
                            onButtonClick = {
                                createSessionViewModel.createSession(
                                    settings = sessionSettings,
                                    message = message,
                                    navigate = {
                                        Toast.makeText(
                                            context,
                                            "Session Created and Student Accepted!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                        navController.popBackStack()
                                    },
                                    context = context
                                )
                            },
                            buttonText = "Create Session",
                            enabled = enabled
                        )
                    }
                }
            }
        }
    }
}