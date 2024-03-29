package com.serrano.academically.activity

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.api.DrawerData
import com.serrano.academically.custom_composables.BarGraph
import com.serrano.academically.custom_composables.CoursesRating
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingCard
import com.serrano.academically.custom_composables.DataGroup
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ChartData
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Routes
import com.serrano.academically.viewmodel.AnalyticsViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Analytics(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    analyticsViewModel: AnalyticsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        analyticsViewModel.getData()
    }

    val user by analyticsViewModel.drawerData.collectAsState()
    val userData by analyticsViewModel.userData.collectAsState()
    val process by analyticsViewModel.processState.collectAsState()
    val animationPlayed by analyticsViewModel.animationPlayed.collectAsState()
    val chartTab by analyticsViewModel.chartTabIndex.collectAsState()
    val chartState by analyticsViewModel.chartState.collectAsState()
    val isRefreshLoading by analyticsViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { analyticsViewModel.refreshData() }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ANALYTICS",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ANALYTICS",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {

            LaunchedEffect(chartTab) {
                analyticsViewModel.toggleAnimation(true)
            }

            val pointsNames = listOf(
                "Total Points",
                "Assessment Points",
                "Request Points",
                "Session Points",
                "Assignment Points"
            )
            val chartPointNames = pointsNames.map { it.replace(" Points", "") }
            val dataNames = listOf(
                "Sessions Completed",
                if (user.role == "STUDENT") "Requests Sent" else "Requests Received",
                "Requests Accepted",
                "Requests Denied",
                if (user.role == "STUDENT") "Assignments Taken" else "Assignments Created",
                "Assessments Taken",
                "Rates Obtained",
                if (user.role == "STUDENT") "Tutors Rated" else "Students Rated",
                "Badges Collected"
            )
            val chartDataNames = listOf(
                "Sessions",
                if (user.role == "STUDENT") "Sent" else "Received",
                "Accepted",
                "Denied",
                "Assignments",
                "Assessments",
                "Rates",
                "Rated",
                "Badges"
            )
            val points = listOf(
                userData.points,
                userData.assessmentPoints,
                userData.requestPoints,
                userData.sessionPoints,
                userData.assignmentPoints
            ).map { Utils.roundRating(it) }
            val data = listOf(
                userData.sessionsCompleted,
                userData.requestsSentReceived,
                userData.requestsAccepted,
                userData.requestsDenied,
                userData.assignments,
                userData.assessments,
                userData.rateNumber,
                userData.ratedUsers,
                userData.badgesCompleted
            )
            val yValuesMapper = Utils.roundRating((points.max() * 1.5) / 4)
            val yValuesMapperData = Utils.roundRating((data.max() * 1.5) / 4)

            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = DrawerData(user.id, user.name, user.role, user.email, user.degree),
                topBarText = "ANALYTICS",
                navController = navController,
                context = context,
                selected = Routes.ANALYTICS
            ) { values ->
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = onRefresh,
                    refreshTriggerDistance = 50.dp,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxSize()
                        .padding(values)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        CustomCard {
                            Text(
                                text = "Progress Graph",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            when (chartTab) {
                                0 -> BarGraph(
                                    text = "Points",
                                    yValues = List(4) { yValuesMapper * (it + 1) },
                                    data = points.mapIndexed { idx, value ->
                                        ChartData(
                                            chartPointNames[idx],
                                            animateFloatAsState(
                                                targetValue = if (animationPlayed) value.toFloat() else 0f,
                                                animationSpec = tween(
                                                    durationMillis = 1000
                                                ),
                                                label = ""
                                            ),
                                            Utils.generateRandomColor(idx)
                                        )
                                    },
                                    verticalStep = yValuesMapper.toFloat(),
                                    chartState = chartState,
                                    onChartStateChange = { analyticsViewModel.updateChartState(it) }
                                )

                                1 -> BarGraph(
                                    text = "Statistics",
                                    yValues = List(4) { yValuesMapperData * (it + 1) },
                                    data = data.mapIndexed { idx, value ->
                                        ChartData(
                                            chartDataNames[idx],
                                            animateFloatAsState(
                                                targetValue = if (animationPlayed) value.toFloat() else 0f,
                                                animationSpec = tween(
                                                    durationMillis = 1000
                                                ),
                                                label = ""
                                            ),
                                            Utils.generateRandomColor(idx)
                                        )
                                    },
                                    verticalStep = yValuesMapperData.toFloat(),
                                    chartState = chartState,
                                    onChartStateChange = { analyticsViewModel.updateChartState(it) }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                GreenButton(
                                    action = { analyticsViewModel.updateChartTab(0) },
                                    text = "POINTS",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                GreenButton(
                                    action = { analyticsViewModel.updateChartTab(1) },
                                    text = "DATA",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        CustomCard {
                            Text(
                                text = "Points",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            DataGroup(names = pointsNames, values = points.map { it.toString() })
                        }
                        CustomCard {
                            Text(
                                text = "Statistics",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            DataGroup(names = dataNames, values = data.map { it.toString() })
                        }
                        CustomCard {
                            RatingCard(text = "Performance Rating", rating = Utils.roundRating((if (userData.rateNumber > 0) userData.rating / userData.rateNumber else 0.0) * 5))
                        }
                        CustomCard {
                            val avgRating = Utils.roundRating(if (userData.courses.isNotEmpty()) userData.courses.map { (it.assessmentRating / it.assessmentTaken) * 5 }.average() else 0.0)
                            RatingCard(text = "Overall Course Rating", rating = avgRating)
                        }
                        CustomCard {
                            CoursesRating(userData.courses)
                        }
                    }
                }
            }
        }
    }
}