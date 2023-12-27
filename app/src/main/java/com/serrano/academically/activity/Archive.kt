package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassDisabled
import androidx.compose.material.icons.filled.SwipeLeftAlt
import androidx.compose.material.icons.filled.SwipeRightAlt
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HourglassDisabled
import androidx.compose.material.icons.outlined.SwipeLeftAlt
import androidx.compose.material.icons.outlined.SwipeRightAlt
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.BottomBar
import com.serrano.academically.custom_composables.CustomSearchBar
import com.serrano.academically.custom_composables.CustomTab
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RateDialog
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.TopBar
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.room.Assignment
import com.serrano.academically.room.Session
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.MessageNotifications
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.RateDialogStates
import com.serrano.academically.viewmodel.ArchiveViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Archive(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    userId: Int,
    tabs: List<String> = listOf("MESSAGES", "SESSIONS", "TASKS"),
    archiveViewModel: ArchiveViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        archiveViewModel.getData(userId, context)
    }

    val rejectedMessages by archiveViewModel.rejectedMessages.collectAsState()
    val acceptedMessages by archiveViewModel.acceptedMessages.collectAsState()
    val cancelledSessions by archiveViewModel.cancelledSessions.collectAsState()
    val completedSessions by archiveViewModel.completedSessions.collectAsState()
    val deadlinedTasks by archiveViewModel.deadlinedTasks.collectAsState()
    val completedTasks by archiveViewModel.completedTasks.collectAsState()
    val process by archiveViewModel.processState.collectAsState()
    val user by archiveViewModel.drawerData.collectAsState()
    val tabIndex by archiveViewModel.tabIndex.collectAsState()
    val navBarIndex by archiveViewModel.navBarIndex.collectAsState()
    val rating by archiveViewModel.rating.collectAsState()
    val dialogOpen by archiveViewModel.isDialogOpen.collectAsState()
    val search by archiveViewModel.searchInfo.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ARCHIVES",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ARCHIVES",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            Drawer(
                scope = scope,
                drawerState = drawerState,
                user = user,
                navController = navController,
                context = context,
                selected = "Archive"
            ) {
                Scaffold(
                    topBar = TopBar(
                        scope = scope,
                        drawerState = drawerState,
                        text = "ARCHIVES",
                        navController = navController
                    ),
                    bottomBar = BottomBar(
                        items = when (tabIndex) {
                            0 -> listOf("Accepted", "Rejected")
                            1 -> listOf("Completed", "Cancelled")
                            else -> listOf("Completed", "Deadlined")
                        },
                        icons = when (tabIndex) {
                            0 -> listOf(
                                listOf(
                                    Icons.Outlined.SwipeRightAlt,
                                    Icons.Filled.SwipeRightAlt
                                ), listOf(Icons.Outlined.SwipeLeftAlt, Icons.Filled.SwipeLeftAlt)
                            )

                            1 -> listOf(
                                listOf(
                                    Icons.Outlined.CheckCircle,
                                    Icons.Filled.CheckCircle
                                ), listOf(Icons.Outlined.Cancel, Icons.Filled.Cancel)
                            )

                            else -> listOf(
                                listOf(
                                    Icons.Outlined.CheckCircle,
                                    Icons.Filled.CheckCircle
                                ),
                                listOf(
                                    Icons.Outlined.HourglassDisabled,
                                    Icons.Filled.HourglassDisabled
                                )
                            )
                        },
                        navBarIndex = navBarIndex,
                        onClick = { archiveViewModel.updateNavBarIndex(it) }
                    )
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(paddingValues)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomTab(
                                tabIndex = tabIndex,
                                tabs = tabs,
                                onTabClick = { archiveViewModel.updateTabIndex(it) }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CustomSearchBar(
                                    placeHolder = "Search Archive",
                                    searchInfo = search,
                                    onQueryChange = {
                                        archiveViewModel.updateSearch(
                                            search.copy(
                                                searchQuery = it
                                            )
                                        )
                                    },
                                    onSearch = {
                                        archiveViewModel.updateSearch(
                                            search.copy(
                                                searchQuery = it,
                                                isActive = false
                                            )
                                        )
                                        archiveViewModel.search(it, context, userId, user.role)
                                    },
                                    onActiveChange = {
                                        archiveViewModel.updateSearch(
                                            search.copy(
                                                isActive = it
                                            )
                                        )
                                    },
                                    onTrailingIconClick = {
                                        if (search.searchQuery.isEmpty()) {
                                            archiveViewModel.updateSearch(search.copy(isActive = false))
                                        } else {
                                            archiveViewModel.updateSearch(search.copy(searchQuery = ""))
                                        }
                                    }
                                )
                            }
                            when (tabIndex) {
                                0 -> when (navBarIndex) {
                                    0 -> ArchiveMessages(acceptedMessages)
                                    1 -> ArchiveMessages(rejectedMessages)
                                }

                                1 -> when (navBarIndex) {
                                    0 -> ArchiveSessions(
                                        sessions = completedSessions,
                                        role = user.role,
                                        isRateButtonEnabled = true,
                                        onRateButtonClick = {
                                            archiveViewModel.updateRatingDialog(it)
                                            archiveViewModel.toggleDialog(true)
                                        }
                                    )

                                    1 -> ArchiveSessions(
                                        sessions = cancelledSessions,
                                        role = user.role
                                    )
                                }

                                2 -> when (navBarIndex) {
                                    0 -> ArchiveAssignments(completedTasks)
                                    1 -> ArchiveAssignments(deadlinedTasks)
                                }
                            }
                        }
                        if (dialogOpen) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x55000000))
                            )
                            RateDialog(
                                text = "Rate ${rating.name}",
                                buttonOneText = "Later",
                                buttonTwoText = "Rate",
                                star = rating.star,
                                onDismissRequest = { archiveViewModel.toggleDialog(false) },
                                onConfirmClick = {
                                    archiveViewModel.toggleDialog(false)
                                    archiveViewModel.rateUser(rating, user.id, user.role, context)
                                },
                                onCancelClick = { archiveViewModel.toggleDialog(false) },
                                onStarClick = { archiveViewModel.updateRatingDialog(rating.copy(star = it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArchiveMessages(
    messages: List<Triple<MessageNotifications, String, String>>
) {
    LazyColumn {
        items(items = messages) {
            YellowCard {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
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
}

@Composable
fun ArchiveSessions(
    sessions: List<Triple<Session, String, String>>,
    role: String,
    isRateButtonEnabled: Boolean = false,
    onRateButtonClick: (RateDialogStates) -> Unit = {}
) {
    LazyColumn {
        items(items = sessions) {
            YellowCard {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = HelperFunctions.formatDate(it.first.startTime),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                        )
                        Text(
                            text = HelperFunctions.formatTime(it.first.startTime, it.first.endTime),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        Text(
                            text = it.third,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        Text(
                            text = it.second,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                        )
                        if (isRateButtonEnabled && !(if (role == "STUDENT") it.first.studentRate else it.first.tutorRate)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                BlackButton(
                                    text = "Rate ${if (role == "STUDENT") "Tutor" else "Student"}",
                                    action = {
                                        onRateButtonClick(
                                            RateDialogStates(
                                                userId = if (role == "STUDENT") it.first.tutorId else it.first.studentId,
                                                sessionId = it.first.sessionId,
                                                name = it.second,
                                                star = 0
                                            )
                                        )
                                    },
                                    modifier = Modifier.padding(20.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArchiveAssignments(
    assignments: List<Triple<Assignment, String, String>>
) {
    LazyColumn {
        items(items = assignments) {
            YellowCard {
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
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Text(
                            text = "Student Score: ${it.first.studentScore}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                        )
                    }
                }
            }
        }
    }
}