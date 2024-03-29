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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.InfoCard
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Routes
import com.serrano.academically.viewmodel.AboutSessionViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    sessionId: Int,
    navController: NavController,
    context: Context,
    aboutSessionViewModel: AboutSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        aboutSessionViewModel.getData(sessionId)
    }

    val session by aboutSessionViewModel.session.collectAsState()
    val user by aboutSessionViewModel.drawerData.collectAsState()
    val process by aboutSessionViewModel.processState.collectAsState()
    val isRefreshLoading by aboutSessionViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { aboutSessionViewModel.refreshData(sessionId) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ABOUT SESSION",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ABOUT SESSION",
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
                topBarText = "ABOUT SESSION",
                navController = navController,
                context = context,
                selected = Routes.NOTIFICATIONS
            ) {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = onRefresh,
                    refreshTriggerDistance = 50.dp,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        CustomCard {
                            Text(
                                text = session.courseName,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate("${Routes.PROFILE}/${if (user.role == "STUDENT") session.tutorId else session.studentId}")
                                    }
                            ) {
                                Text(
                                    text = if (user.role == "STUDENT") "Tutor: ${session.tutorName}" else "Student: ${session.studentName}",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Text(
                                text = "Module: ${session.moduleName}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        InfoCard(
                            title = "SCHEDULE",
                            description = "Date: ${Utils.formatDate(Utils.convertToDate(session.startTime))}\n" +
                                    "Time: ${
                                        Utils.formatTime(
                                            Utils.convertToDate(session.startTime),
                                            Utils.convertToDate(session.endTime)
                                        )
                                    }"
                        )
                        InfoCard(title = "LOCATION", description = session.location)

                        if (user.role == "TUTOR") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                BlackButton(
                                    text = "EDIT",
                                    action = { navController.navigate("${Routes.EDIT_SESSION}/$sessionId") },
                                    modifier = Modifier.padding(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}