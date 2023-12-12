package com.serrano.academically.activity

import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.EditSessionMenu
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.EditSessionViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.serrano.academically.custom_composables.ConfirmDialog
import com.serrano.academically.ui.theme.Strings
import kotlinx.coroutines.CoroutineScope

@Composable
fun EditSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    userId: Int,
    sessionId: Int,
    editSessionViewModel: EditSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        editSessionViewModel.getData(userId, sessionId)
    }

    val user by editSessionViewModel.drawerData.collectAsState()
    val process by editSessionViewModel.processState.collectAsState()
    val sessionSettings by editSessionViewModel.sessionSettings.collectAsState()
    val dialogOpen by editSessionViewModel.isFilterDialogOpen.collectAsState()
    val session by editSessionViewModel.session.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "EDIT SESSION",
                navController = navController,
                context = context
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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
                                onDateInputChange = { editSessionViewModel.updateSessionSettings(sessionSettings.copy(date = it)) },
                                onStartTimeInputChange = { editSessionViewModel.updateSessionSettings(sessionSettings.copy(startTime = it)) },
                                onEndTimeInputChange = { editSessionViewModel.updateSessionSettings(sessionSettings.copy(endTime = it)) },
                                onLocationInputChange = { editSessionViewModel.updateSessionSettings(sessionSettings.copy(location = it)) },
                                onButtonClick = {
                                    editSessionViewModel.updateSession(
                                        settings = sessionSettings,
                                        sessionId = sessionId,
                                        navigate = {
                                            navController.navigateUp()
                                            navController.navigateUp()
                                        }
                                    )
                                },
                                buttonText = "Update Session"
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
                                navController.navigate("AssignmentOption/$userId/${session.sessionId}")
                            },
                            onClickingNo = {
                                editSessionViewModel.toggleDialog(false)
                            }
                        )
                    }
                }
            }
        }
    }
}