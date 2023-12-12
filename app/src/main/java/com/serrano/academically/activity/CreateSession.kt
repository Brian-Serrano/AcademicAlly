package com.serrano.academically.activity

import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.EditSessionMenu
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.CreateSessionViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun CreateSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    userId: Int,
    studentId: Int,
    courseId: Int,
    moduleId: Int,
    messageId: Int,
    createSessionViewModel: CreateSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        createSessionViewModel.getData(userId)
    }

    val user by createSessionViewModel.drawerData.collectAsState()
    val sessionSettings by createSessionViewModel.sessionSettings.collectAsState()
    val process by createSessionViewModel.processState.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "CREATE SESSION",
                navController = navController,
                context = context
            ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                        .verticalScroll(rememberScrollState()),
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
                        EditSessionMenu(
                            sessionSettings = sessionSettings,
                            onDateInputChange = { createSessionViewModel.updateSessionSettings(sessionSettings.copy(date = it)) },
                            onStartTimeInputChange = { createSessionViewModel.updateSessionSettings(sessionSettings.copy(startTime = it)) },
                            onEndTimeInputChange = { createSessionViewModel.updateSessionSettings(sessionSettings.copy(endTime = it)) },
                            onLocationInputChange = { createSessionViewModel.updateSessionSettings(sessionSettings.copy(location = it)) },
                            onButtonClick = {
                                createSessionViewModel.createSession(
                                    settings = sessionSettings,
                                    courseId = courseId,
                                    moduleId = moduleId,
                                    tutorId = userId,
                                    studentId = studentId,
                                    messageId = messageId,
                                    navigate = {
                                        navController.navigateUp()
                                        navController.navigateUp()
                                    },
                                    context = context
                                )
                            },
                            buttonText = "Create Session"
                        )
                    }
                }
            }
        }
    }
}