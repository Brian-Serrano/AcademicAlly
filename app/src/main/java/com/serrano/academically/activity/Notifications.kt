package com.serrano.academically.activity

import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScheduleBlueCard
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.MessageNotifications
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionNotifications
import com.serrano.academically.viewmodel.NotificationsViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.room.Assignment
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.toMilitaryTime
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate

@Composable
fun Notifications(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    navController: NavController,
    context: Context,
    tabs: List<String> = listOf("REQUESTS", "SESSIONS", "TASKS"),
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        notificationsViewModel.getData(userId)
    }

    val user by notificationsViewModel.userData.collectAsState()
    val process by notificationsViewModel.processState.collectAsState()
    val tabIndex by notificationsViewModel.tabIndex.collectAsState()
    val message by notificationsViewModel.message.collectAsState()
    val session by notificationsViewModel.session.collectAsState()
    val messageUsers by notificationsViewModel.messageUsers.collectAsState()
    val assignment by notificationsViewModel.assignment.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = Strings.notifications,
                navController = navController,
                context = context
            ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                        .background(MaterialTheme.colorScheme.primary),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TabRow(
                        selectedTabIndex = tabIndex,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.Black
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                selected = tabIndex == index,
                                onClick = { notificationsViewModel.updateTabIndex(index) }
                            )
                        }
                    }
                    when (tabIndex) {
                        0 -> Requests(
                            context = context,
                            messages = message,
                            messageInfo = messageUsers,
                            onClick = { navController.navigate("AboutStudent/${user.id}/$it") }
                        )
                        1 -> Sessions(
                            context = context,
                            sessions = session.groupBy { LocalDate.of(it.startTime.year, it.startTime.monthValue, it.startTime.dayOfMonth) },
                            onClick = { navController.navigate("AboutSession/${user.id}/$it") }
                        )
                        2 -> Assignments(
                            context = context,
                            assignments = assignment,
                            onClick = {
                                if (user.role == "STUDENT") {

                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Requests(
    context: Context,
    messages: List<MessageNotifications>,
    messageInfo: List<String>,
    onClick: (Int) -> Unit
) {
    LazyColumn {
        items(messages.size) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .clickable { onClick(messages[it].messageId) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(10.dp)
                )
                Column {
                    Text(
                        text = messageInfo[it],
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                    )
                    Text(
                        text = GetCourses.getCourseNameById(messages[it].courseId, context),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Sessions(
    context: Context,
    sessions: Map<LocalDate, List<SessionNotifications>>,
    onClick: (Int) -> Unit
) {
    YellowCard(MaterialTheme.colorScheme.secondary) {
        LazyColumn {
            items(items = sessions.toList()) { session ->
                Text_1(
                    text = "${session.first.month} ${session.first.dayOfMonth}, ${session.first.year}"
                )
                session.second.forEach {
                    ScheduleBlueCard(
                        session = it,
                        sessionCourse = GetCourses.getCourseNameById(it.courseId, context),
                        onArrowClick = { onClick(it.sessionId) }
                    )
                }
            }
        }
    }
}

@Composable
fun Assignments(
    context: Context,
    assignments: List<Assignment>,
    onClick: (Int) -> Unit
) {
    LazyColumn {
        items(items = assignments) {
            YellowCard(MaterialTheme.colorScheme.tertiary) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${it.deadLine.month} ${it.deadLine.dayOfMonth}, ${it.deadLine.year} ${toMilitaryTime(listOf(it.deadLine.hour, it.deadLine.minute))}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                        )
                        Text(
                            text = GetCourses.getCourseNameById(it.courseId, context),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Text(
                            text = it.type,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Text(
                            text = GetModules.getModuleByCourseAndModuleId(it.courseId, it.moduleId, context),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(35.dp)
                            .clickable { onClick(it.assignmentId) }
                    )
                }
            }
        }
    }
}