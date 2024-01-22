package com.serrano.academically.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.ConfirmDialog
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.EditSessionMenu
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RateDialog
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.EditSessionViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun EditSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    sessionId: Int,
    editSessionViewModel: EditSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        editSessionViewModel.getData(sessionId, context)
    }

    val user by editSessionViewModel.drawerData.collectAsState()
    val process by editSessionViewModel.processState.collectAsState()
    val sessionSettings by editSessionViewModel.sessionSettings.collectAsState()
    val dialogOpen by editSessionViewModel.isFilterDialogOpen.collectAsState()
    val rateDialogOpen by editSessionViewModel.isRateDialogOpen.collectAsState()
    val rateDialogStates by editSessionViewModel.rateDialogStates.collectAsState()
    val enabled by editSessionViewModel.buttonEnabled.collectAsState()
    val isRefreshLoading by editSessionViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { editSessionViewModel.refreshData(sessionId, context) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "EDIT SESSION",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "EDIT SESSION",
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
                topBarText = "EDIT SESSION",
                navController = navController,
                context = context,
                selected = "Notifications"
            ) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CustomCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                BlackButton(
                                    text = "COMPLETE SESSION",
                                    action = {
                                        editSessionViewModel.toggleDialog(true)
                                    },
                                    modifier = Modifier.padding(15.dp)
                                )
                            }
                            EditSessionMenu(
                                sessionSettings = sessionSettings,
                                onDateInputChange = {
                                    editSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(date = it)
                                    )
                                },
                                onStartTimeInputChange = {
                                    editSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(startTime = it)
                                    )
                                },
                                onEndTimeInputChange = {
                                    editSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(endTime = it)
                                    )
                                },
                                onLocationInputChange = {
                                    editSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(location = it)
                                    )
                                },
                                onButtonClick = {
                                    editSessionViewModel.updateSession(
                                        context = context,
                                        settings = sessionSettings,
                                        sessionId = sessionId,
                                        navigate = {
                                            Toast.makeText(
                                                context,
                                                "Session Updated!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.popBackStack()
                                            navController.popBackStack()
                                        }
                                    )
                                },
                                buttonText = "Update Session",
                                enabled = enabled
                            )
                        }
                    }
                    if (dialogOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000))
                        )
                        ConfirmDialog(
                            text = "Do you want to complete the session?",
                            onDismissRequest = {
                                editSessionViewModel.toggleDialog(false)
                            },
                            onClickingYes = {
                                editSessionViewModel.toggleDialog(false)
                                editSessionViewModel.toggleRateDialog(true)
                            },
                            onClickingNo = {
                                editSessionViewModel.toggleDialog(false)
                            }
                        )
                    }
                    if (rateDialogOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000))
                        )
                        RateDialog(
                            text = "Rate Student",
                            buttonOneText = "Later",
                            buttonTwoText = "Rate",
                            star = rateDialogStates.star,
                            onDismissRequest = { editSessionViewModel.toggleRateDialog(false) },
                            onConfirmClick = {
                                editSessionViewModel.toggleRateDialog(false)
                                navController.navigate("AssignmentOption/$sessionId/${rateDialogStates.star}")
                            },
                            onCancelClick = {
                                editSessionViewModel.toggleRateDialog(false)
                                navController.navigate("AssignmentOption/$sessionId/0")
                            },
                            onStarClick = {
                                editSessionViewModel.updateRateDialogStates(
                                    rateDialogStates.copy(star = it)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}