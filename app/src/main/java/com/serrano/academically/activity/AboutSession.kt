package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.InfoCard
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AboutSessionViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    sessionId: Int,
    navController: NavController,
    context: Context,
    aboutSessionViewModel: AboutSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        aboutSessionViewModel.getData(userId, sessionId, context)
    }

    val session by aboutSessionViewModel.sessionDetails.collectAsState()
    val user by aboutSessionViewModel.userData.collectAsState()
    val process by aboutSessionViewModel.processState.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ABOUT SESSION",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ABOUT SESSION",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "ABOUT SESSION",
                navController = navController,
                context = context,
                selected = "Notifications"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(it)
                        .verticalScroll(rememberScrollState())
                ) {
                    CustomCard {
                        Text(
                            text = session.second.courseName,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("Profile/${user.id}/${if (user.role == "STUDENT") session.first.tutorId else session.first.studentId}")
                                }
                        ) {
                            Text(
                                text = if (user.role == "STUDENT") "Tutor: ${session.second.tutorName}" else "Student: ${session.second.studentName}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Text(
                            text = "Module: ${session.second.moduleName}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    InfoCard(
                        title = "SCHEDULE",
                        description = "Date: ${HelperFunctions.formatDate(session.first.startTime)}\n" +
                                "Time: ${
                                    HelperFunctions.formatTime(
                                        session.first.startTime,
                                        session.first.endTime
                                    )
                                }"
                    )
                    InfoCard(title = "LOCATION", description = session.first.location)

                    if (user.role == "TUTOR") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            BlackButton(
                                text = "EDIT",
                                action = { navController.navigate("EditSession/$userId/$sessionId") },
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}