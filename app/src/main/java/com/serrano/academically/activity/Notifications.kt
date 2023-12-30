package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.CustomTab
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.ScheduleBlueCard
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.room.Assignment
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.MessageNotifications
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionNotifications
import com.serrano.academically.viewmodel.NotificationsViewModel
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
        notificationsViewModel.getData(userId, context)
    }

    val user by notificationsViewModel.userData.collectAsState()
    val process by notificationsViewModel.processState.collectAsState()
    val tabIndex by notificationsViewModel.tabIndex.collectAsState()
    val message by notificationsViewModel.message.collectAsState()
    val session by notificationsViewModel.session.collectAsState()
    val assignment by notificationsViewModel.assignment.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "Notifications",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "Notifications",
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
                topBarText = "Notifications",
                navController = navController,
                context = context,
                selected = "Notifications"
            ) { values ->
                val unseen = listOf(
                    message.count { !it.first.tutorViewed },
                    session.count { !it.first.studentViewed },
                    assignment.count { !it.first.studentViewed }
                )
                val badgeAvailability = when (user.role) {
                    "STUDENT" -> listOf(false, true, true)
                    else -> listOf(true, false, false)
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                        .background(MaterialTheme.colorScheme.primary),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTab(
                        tabIndex = tabIndex,
                        tabs = tabs,
                        onTabClick = { notificationsViewModel.updateTabIndex(it) },
                        badgeEnabled = true,
                        badge = {
                            if (unseen[it] > 0 && badgeAvailability[it]) {
                                Badge(
                                    modifier = Modifier.offset(5.dp, (-5).dp)
                                ) {
                                    Text(unseen[it].toString())
                                }
                            }
                        }
                    )
                    when (tabIndex) {
                        0 -> Requests(
                            messages = message,
                            badgeAvailability = badgeAvailability[tabIndex],
                            onClick = { navController.navigate("AboutStudent/${user.id}/$it") }
                        )

                        1 -> Sessions(
                            sessions = session.groupBy {
                                LocalDate.of(
                                    it.first.startTime.year,
                                    it.first.startTime.monthValue,
                                    it.first.startTime.dayOfMonth
                                )
                            },
                            badgeAvailability = badgeAvailability[tabIndex],
                            onClick = { navController.navigate("AboutSession/${user.id}/$it") }
                        )

                        2 -> Assignments(
                            assignments = assignment,
                            badgeAvailability = badgeAvailability[tabIndex],
                            onClick = {
                                if (user.role == "STUDENT") {
                                    navController.navigate("Assignment/${user.id}/$it")
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
    messages: List<Triple<MessageNotifications, String, String>>,
    badgeAvailability: Boolean,
    onClick: (Int) -> Unit
) {
    LazyColumn {
        items(items = messages) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .clickable { onClick(it.first.messageId) },
                verticalAlignment = Alignment.CenterVertically
            ){
                BadgedBox(badge = {
                    if (!it.first.tutorViewed && badgeAvailability) {
                        Badge(
                            modifier = Modifier
                                .size(10.dp)
                                .offset((-15).dp, 15.dp)
                        )
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(10.dp)
                    )
                }
                Column {
                    Text(
                        text = it.second,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                    )
                    Text(
                        text = it.third,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Sessions(
    sessions: Map<LocalDate, List<Pair<SessionNotifications, String>>>,
    badgeAvailability: Boolean,
    onClick: (Int) -> Unit
) {
    CustomCard {
        LazyColumn {
            items(items = sessions.toList()) { session ->
                Text(
                    text = HelperFunctions.formatDate(session.first),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(10.dp)
                )
                session.second.forEach {
                    ScheduleBlueCard(
                        session = it.first,
                        sessionCourse = it.second,
                        onArrowClick = { onClick(it.first.sessionId) },
                        badge = {
                            if (!it.first.studentViewed && badgeAvailability) {
                                Badge(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .offset((-30).dp, 30.dp)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Assignments(
    assignments: List<Triple<Assignment, String, String>>,
    badgeAvailability: Boolean,
    onClick: (Int) -> Unit
) {
    LazyColumn {
        items(items = assignments) {
            BadgedBox(
                badge = {
                    if (!it.first.studentViewed && badgeAvailability) {
                        Badge(
                            modifier = Modifier
                                .size(10.dp)
                                .offset((-30).dp, 30.dp)
                        )
                    }
                }
            ) {
                CustomCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${HelperFunctions.formatDate(it.first.deadLine)} ${
                                    HelperFunctions.toMilitaryTime(
                                        it.first.deadLine.hour,
                                        it.first.deadLine.minute
                                    )
                                }",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                            )
                            Text(
                                text = it.second,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 15.dp)
                            )
                            Text(
                                text = it.first.type,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 15.dp)
                            )
                            Text(
                                text = it.third,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .clickable { onClick(it.first.assignmentId) }
                        )
                    }
                }
            }
        }
    }
}