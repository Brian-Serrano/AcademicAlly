package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.api.Course
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.CircularProgressBar
import thesis.academic.ally.custom_composables.CoursesList
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.DashboardTopBar
import thesis.academic.ally.custom_composables.DashboardTopBarNoDrawer
import thesis.academic.ally.custom_composables.Drawer
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.ui.theme.montserrat
import thesis.academic.ally.utils.DashboardIcons
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.DashboardViewModel

@Composable
fun Dashboard(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        dashboardViewModel.getData {
            navController.navigate(Routes.PATTERN_ASSESSMENT)
        }
    }

    val user by dashboardViewModel.drawerData.collectAsState()
    val dashboard by dashboardViewModel.dashboard.collectAsState()
    val process by dashboardViewModel.processState.collectAsState()
    val animationPlayed by dashboardViewModel.animationPlayed.collectAsState()
    val isRefreshLoading by dashboardViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { dashboardViewModel.refreshData() }

    when (val p = process) {
        is ProcessState.Error -> {
            DashboardTopBarNoDrawer {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            DashboardTopBarNoDrawer {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            val icons = if (user.role == "STUDENT") arrayOf(
                DashboardIcons(Routes.LEADERBOARD, "Leaderboard", Icons.Filled.Leaderboard),
                DashboardIcons(Routes.ANALYTICS, "Analytics", Icons.Filled.Analytics),
                DashboardIcons(Routes.FIND_TUTOR, "Find Tutor", Icons.Filled.PersonSearch),
                DashboardIcons(Routes.NOTIFICATIONS, "Notifications", Icons.Filled.BrowseGallery)
            ) else arrayOf(
                DashboardIcons(Routes.LEADERBOARD, "Leaderboard", Icons.Filled.Leaderboard),
                DashboardIcons(Routes.ANALYTICS, "Analytics", Icons.Filled.Analytics),
                DashboardIcons(Routes.NOTIFICATIONS, "Notifications", Icons.Filled.BrowseGallery)
            )

            LaunchedEffect(Unit) {
                dashboardViewModel.playAnimation()
            }

            Drawer(
                scope = scope,
                drawerState = drawerState,
                user = user,
                navController = navController,
                context = context,
                selected = Routes.DASHBOARD
            ) {
                Scaffold(
                    topBar = DashboardTopBar(
                        scope = scope,
                        drawerState = drawerState,
                        onIconClick = {
                            navController.navigate("${Routes.PROFILE}/${user.id}")
                        },
                        image = Utils.convertToImage(dashboard.image)
                    )
                ) { values ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(values)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                ) {
                                    append("Good Morning, ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = montserrat,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    append(user.name)
                                }
                            },
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(20.dp)
                        )
                        LazyRow(modifier = Modifier.padding(10.dp)) {
                            items(items = icons) {
                                Card(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(105.dp)
                                        .clip(MaterialTheme.shapes.extraSmall)
                                        .clickable { navController.navigate(it.route) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = it.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(50.dp),
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                        Text(
                                            text = it.name,
                                            fontFamily = montserrat,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = onRefresh,
                            refreshTriggerDistance = 50.dp
                        ) {
                            LazyColumn {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .padding(20.dp)
                                                .clip(MaterialTheme.shapes.extraSmall),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "Knowledge",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                                CircularProgressBar(
                                                    percentage = Utils.roundRating(if (dashboard.courses.isNotEmpty()) dashboard.courses.map { it.assessmentRating / it.assessmentTaken }.average() else 0.0).toFloat(),
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    radius = 55.dp,
                                                    animationPlayed = animationPlayed
                                                )
                                            }
                                        }
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .padding(20.dp)
                                                .clip(MaterialTheme.shapes.extraSmall),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "Performance",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                                CircularProgressBar(
                                                    percentage = Utils.roundRating(if (dashboard.rateNumber > 0) dashboard.rating / dashboard.rateNumber else 0.0).toFloat(),
                                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                                    radius = 55.dp,
                                                    animationPlayed = animationPlayed
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    CustomCard {
                                        CoursesList(dashboard.courses.map { Course(it.courseName, it.courseDescription) })
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            BlackButton(
                                                text = "SHOW ALL COURSES",
                                                action = { navController.navigate(Routes.COURSES_MENU) },
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
        }
    }
}