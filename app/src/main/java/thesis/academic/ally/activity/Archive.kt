package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.api.AssignmentNotifications
import thesis.academic.ally.api.MessageNotifications
import thesis.academic.ally.api.SessionArchive
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.BottomBar
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.CustomSearchBar
import thesis.academic.ally.custom_composables.CustomTab
import thesis.academic.ally.custom_composables.Drawer
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.RateDialog
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.custom_composables.TopBar
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.RateDialogStates
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.ArchiveViewModel

@Composable
fun Archive(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    tabs: List<String> = listOf("MESSAGES", "SESSIONS", "TASKS"),
    archiveViewModel: ArchiveViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        archiveViewModel.getData()
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
    val isRefreshLoading by archiveViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ARCHIVES",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState) {
                    archiveViewModel.refreshData()
                }
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ARCHIVES",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            Drawer(
                scope = scope,
                drawerState = drawerState,
                user = user,
                navController = navController,
                context = context,
                selected = Routes.ARCHIVE
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
                                        archiveViewModel.search(it)
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
                            SwipeRefresh(
                                state = swipeRefreshState,
                                onRefresh = { archiveViewModel.refreshPage(search.searchQuery) },
                                refreshTriggerDistance = 50.dp
                            ) {
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
                                    archiveViewModel.rateUser(rating)
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
    messages: List<MessageNotifications>
) {
    if (messages.isNotEmpty()) {
        LazyColumn {
            items(items = messages) {
                CustomCard {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = Utils.convertToImage(it.image),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(60.dp)
                                .clip(RoundedCornerShape(30.dp))
                        )
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
        }
    } else {
        // To make swipe refresh work
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.padding(100.dp))
        }
    }
}

@Composable
fun ArchiveSessions(
    sessions: List<SessionArchive>,
    role: String,
    isRateButtonEnabled: Boolean = false,
    onRateButtonClick: (RateDialogStates) -> Unit = {}
) {
    if (sessions.isNotEmpty()) {
        LazyColumn {
            items(items = sessions) {
                CustomCard {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Utils.formatDate(Utils.convertToDate(it.startTime)),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                            )
                            Text(
                                text = Utils.formatTime(Utils.convertToDate(it.startTime), Utils.convertToDate(it.endTime)),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = it.courseName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                            )
                            if (isRateButtonEnabled && !(if (role == "STUDENT") it.studentRate else it.tutorRate)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    BlackButton(
                                        text = "Rate ${if (role == "STUDENT") "Tutor" else "Student"}",
                                        action = {
                                            onRateButtonClick(
                                                RateDialogStates(
                                                    userId = if (role == "STUDENT") it.tutorId else it.studentId,
                                                    sessionId = it.sessionId,
                                                    name = it.name,
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
    } else {
        // To make swipe refresh work
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Spacer(modifier = Modifier.padding(100.dp))
        }
    }
}

@Composable
fun ArchiveAssignments(
    assignments: List<AssignmentNotifications>
) {
    if (assignments.isNotEmpty()) {
        LazyColumn {
            items(items = assignments) {
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
                                modifier = Modifier.padding(start = 15.dp)
                            )
                            Text(
                                text = "Student Score: ${it.studentScore}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
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