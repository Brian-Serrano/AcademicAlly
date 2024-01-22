package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.api.AssignmentNotifications
import com.serrano.academically.api.MessageNotifications
import com.serrano.academically.api.SessionNotifications
import com.serrano.academically.custom_composables.CustomTab
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.ScheduleBlueCard
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.NotificationsViewModel
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate

@Composable
fun Notifications(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    tabs: List<String> = listOf("REQUESTS", "SESSIONS", "TASKS"),
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        notificationsViewModel.getData(context)
    }

    val user by notificationsViewModel.drawerData.collectAsState()
    val process by notificationsViewModel.processState.collectAsState()
    val tabIndex by notificationsViewModel.tabIndex.collectAsState()
    val message by notificationsViewModel.message.collectAsState()
    val session by notificationsViewModel.session.collectAsState()
    val assignment by notificationsViewModel.assignment.collectAsState()
    val isRefreshLoading by notificationsViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "Notifications",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState) {
                    notificationsViewModel.refreshData(context)
                }
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "Notifications",
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
                topBarText = "Notifications",
                navController = navController,
                context = context,
                selected = "Notifications"
            ) { values ->
                val unseen = listOf(
                    message.count { !it.tutorViewed },
                    session.count { !it.studentViewed },
                    assignment.count { !it.studentViewed }
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
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { notificationsViewModel.refreshPage(context) },
                        refreshTriggerDistance = 50.dp
                    ) {
                        when (tabIndex) {
                            0 -> Requests(
                                messages = message,
                                badgeAvailability = badgeAvailability[tabIndex],
                                onClick = { navController.navigate("AboutStudent/$it") }
                            )

                            1 -> Sessions(
                                sessions = session.groupBy {
                                    LocalDate.of(
                                        Utils.convertToDate(it.startTime).year,
                                        Utils.convertToDate(it.startTime).monthValue,
                                        Utils.convertToDate(it.startTime).dayOfMonth
                                    )
                                },
                                badgeAvailability = badgeAvailability[tabIndex],
                                onClick = { navController.navigate("AboutSession/$it") }
                            )

                            2 -> Assignments(
                                assignments = assignment,
                                badgeAvailability = badgeAvailability[tabIndex],
                                onClick = {
                                    if (user.role == "STUDENT") {
                                        navController.navigate("Assignment/$it")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Requests(
    messages: List<MessageNotifications>,
    badgeAvailability: Boolean,
    onClick: (Int) -> Unit
) {
    if (messages.isNotEmpty()) {
        LazyColumn {
            items(items = messages) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clickable { onClick(it.messageId) },
                    verticalAlignment = Alignment.CenterVertically
                ){
                    BadgedBox(badge = {
                        if (!it.tutorViewed && badgeAvailability) {
                            Badge(
                                modifier = Modifier
                                    .size(10.dp)
                                    .offset((-15).dp, 15.dp)
                            )
                        }
                    }) {
                        Image(
                            bitmap = Utils.convertToImage(it.image),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(60.dp)
                                .clip(RoundedCornerShape(30.dp))
                        )
                    }
                    Column {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                        )
                        Text(
                            text = it.courseName,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    } else {
        // To make swipe refresh work
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.padding(100.dp))
        }
    }
}

@Composable
fun Sessions(
    sessions: Map<LocalDate, List<SessionNotifications>>,
    badgeAvailability: Boolean,
    onClick: (Int) -> Unit
) {
    if (sessions.isNotEmpty()) {
        LazyColumn {
            items(items = sessions.toList()) { session ->
                CustomCard {
                    Text(
                        text = Utils.formatDate(session.first),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                    session.second.forEach {
                        ScheduleBlueCard(
                            session = it,
                            sessionCourse = it.courseName,
                            onArrowClick = { onClick(it.sessionId) },
                            badge = {
                                if (!it.studentViewed && badgeAvailability) {
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
    } else {
        // To make swipe refresh work
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.padding(100.dp))
        }
    }
}

@Composable
fun Assignments(
    assignments: List<AssignmentNotifications>,
    badgeAvailability: Boolean,
    onClick: (Int) -> Unit
) {
    if (assignments.isNotEmpty()) {
        LazyColumn {
            items(items = assignments) {
                BadgedBox(
                    badge = {
                        if (!it.studentViewed && badgeAvailability) {
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
                                    text = "${Utils.formatDate(Utils.convertToDate(it.deadLine))} ${
                                        Utils.toMilitaryTime(
                                            Utils.convertToDate(it.deadLine).hour,
                                            Utils.convertToDate(it.deadLine).minute
                                        )
                                    }",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                                )
                                Text(
                                    text = it.courseName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 15.dp)
                                )
                                Text(
                                    text = it.type,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 15.dp)
                                )
                                Text(
                                    text = it.moduleName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(35.dp)
                                    .clickable { onClick(it.assignmentId) }
                            )
                        }
                    }
                }
            }
        }
    } else {
        // To make swipe refresh work
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.padding(100.dp))
        }
    }
}