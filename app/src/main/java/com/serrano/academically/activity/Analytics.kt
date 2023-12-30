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
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.viewmodel.AnalyticsViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Analytics(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    navController: NavController,
    context: Context,
    analyticsViewModel: AnalyticsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        analyticsViewModel.getData(userId, context)
    }

    val courses by analyticsViewModel.userCourses.collectAsState()
    val user by analyticsViewModel.userData.collectAsState()
    val process by analyticsViewModel.processState.collectAsState()
    val animationPlayed by analyticsViewModel.animationPlayed.collectAsState()
    val chartTab by analyticsViewModel.chartTabIndex.collectAsState()
    val chartState by analyticsViewModel.chartState.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ANALYTICS",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ANALYTICS",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {

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
            var dataNames: List<String>
            var chartDataNames: List<String>
            var points: List<Double>
            var data: List<Int>
            var yValuesMapper: Double
            var yValuesMapperData: Double

            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = UserDrawerData(user.id, user.name, user.role, user.email, user.degree),
                topBarText = "ANALYTICS",
                navController = navController,
                context = context,
                selected = "Analytics"
            ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (user.role) {
                        "STUDENT" -> {
                            points = listOf(
                                user.studentPoints,
                                user.studentAssessmentPoints,
                                user.studentRequestPoints,
                                user.studentSessionPoints,
                                user.studentAssignmentPoints
                            ).map { HelperFunctions.roundRating(it) }
                            dataNames = listOf(
                                "Sessions Completed",
                                "Requests Sent",
                                "Requests Accepted",
                                "Requests Denied",
                                "Assignments Taken",
                                "Assessments Taken",
                                "Rates Obtained",
                                "Tutors Rated",
                                "Badges Collected"
                            )
                            chartDataNames = listOf(
                                "Sessions",
                                "Sent",
                                "Accepted",
                                "Denied",
                                "Assignments",
                                "Assessments",
                                "Rates",
                                "Rated",
                                "Badges"
                            )
                            data = listOf(
                                user.sessionsCompletedAsStudent,
                                user.requestsSent,
                                user.acceptedRequests,
                                user.deniedRequests,
                                user.assignmentsTaken,
                                user.assessmentsTakenAsStudent,
                                user.numberOfRatesAsStudent,
                                user.tutorsRated,
                                user.badgeProgressAsStudent.count { it >= 100 }
                            )
                        }

                        else -> {
                            points = listOf(
                                user.tutorPoints,
                                user.tutorAssessmentPoints,
                                user.tutorRequestPoints,
                                user.tutorSessionPoints,
                                user.tutorAssignmentPoints
                            ).map { HelperFunctions.roundRating(it) }
                            dataNames = listOf(
                                "Sessions Completed",
                                "Requests Received",
                                "Accepted Student Requests",
                                "Denied Student Requests",
                                "Assignments Created",
                                "Assessments Taken",
                                "Rates Obtained",
                                "Students Rated",
                                "Badges Collected"
                            )
                            chartDataNames = listOf(
                                "Sessions",
                                "Received",
                                "Accepted",
                                "Denied",
                                "Assignments",
                                "Assessments",
                                "Rates",
                                "Rated",
                                "Badges"
                            )
                            data = listOf(
                                user.sessionsCompletedAsTutor,
                                user.requestsReceived,
                                user.requestsAccepted,
                                user.requestsDenied,
                                user.assignmentsTaken,
                                user.assessmentsTakenAsTutor,
                                user.numberOfRatesAsTutor,
                                user.studentsRated,
                                user.badgeProgressAsTutor.count { it >= 100 }
                            )
                        }
                    }
                    yValuesMapper = HelperFunctions.roundRating((points.max() * 1.5) / 4)
                    yValuesMapperData = HelperFunctions.roundRating((data.max() * 1.5) / 4)
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
                                        HelperFunctions.generateRandomColor(idx)
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
                                        HelperFunctions.generateRandomColor(idx)
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
                        val rating = if (user.role == "STUDENT") {
                            HelperFunctions.roundRating((if (user.numberOfRatesAsStudent > 0) user.totalRatingAsStudent / user.numberOfRatesAsStudent else 0.0) * 5)
                        } else {
                            HelperFunctions.roundRating((if (user.numberOfRatesAsTutor > 0) user.totalRatingAsTutor / user.numberOfRatesAsTutor else 0.0) * 5)
                        }
                        RatingCard(text = "Performance Rating", rating = rating)
                    }
                    CustomCard {
                        val avgRating =
                            HelperFunctions.roundRating(if (courses.isNotEmpty()) courses.map { (it.first.assessmentRating / it.first.assessmentTaken) * 5 }
                                .average() else 0.0)
                        RatingCard(text = "Overall Course Rating", rating = avgRating)
                    }
                    CustomCard {
                        CoursesRating(courses)
                    }
                }
            }
        }
    }
}