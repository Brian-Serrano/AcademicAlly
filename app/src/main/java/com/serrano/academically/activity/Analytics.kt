package com.serrano.academically.activity

import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.LineChart
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingBar
import com.serrano.academically.custom_composables.RowData
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.viewmodel.AnalyticsViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.Divider
import com.serrano.academically.utils.roundRating
import kotlinx.coroutines.CoroutineScope
import kotlin.math.ceil

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
    val courseName by analyticsViewModel.courseName.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = UserDrawerData(user.id, user.name, user.role, user.email, user.degree),
                topBarText = Strings.analytics,
                navController = navController,
                context = context
            ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                        .verticalScroll(rememberScrollState())
                ) {
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Text_1(text = Strings.progressGraph)
                        val names = listOf(
                            "Total Points", "Assessment Points", "Request Points", "Session Points"
                        )
                        when (user.role) {
                            "STUDENT" -> {
                                val points = listOf(
                                    user.studentPoints,
                                    user.studentAssessmentPoints,
                                    user.studentRequestPoints,
                                    user.studentSessionPoints
                                ).map { roundRating(it) }
                                points.forEachIndexed { idx, point ->
                                    RowData(name = names[idx], value = point.toString())
                                }
                                val yValuesMapper = ceil((points.max() * 1.5) / 4).toInt()
                                LineChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .padding(20.dp),
                                    xValues = listOf(1, 2, 3, 4),
                                    yValues = List(5) { yValuesMapper * it },
                                    points = points.map { it.toFloat() },
                                    paddingSpace = 20.dp,
                                    verticalStep = 5
                                )
                            }
                            "TUTOR" -> {
                                val points = listOf(
                                    user.tutorPoints,
                                    user.tutorAssessmentPoints,
                                    user.tutorRequestPoints,
                                    user.tutorSessionPoints
                                ).map { roundRating(it) }
                                points.forEachIndexed { idx, point ->
                                    RowData(name = names[idx], value = point.toString())
                                }
                                val yValuesMapper = ceil((points.max() * 1.5) / 4).toInt()
                                LineChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .padding(20.dp),
                                    xValues = listOf(1, 2, 3, 4),
                                    yValues = List(5) { yValuesMapper * it },
                                    points = points.map { it.toFloat() },
                                    paddingSpace = 20.dp,
                                    verticalStep = 5
                                )
                            }
                        }
                    }
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Text_1(text = Strings.statistics)
                        when (user.role) {
                            "STUDENT" -> {
                                RowData(name = "Sessions Completed", value = user.sessionsCompletedAsStudent.toString())
                                RowData(name = "Requests Sent", value = user.requestsSent.toString())
                                RowData(name = "Requests Accepted", value = user.acceptedRequests.toString())
                                RowData(name = "Requests Denied", value = user.deniedRequests.toString())
                                RowData(name = "Assignments Taken", value = user.assignmentsTaken.toString())
                                RowData(name = "Assessments Taken", value = user.assessmentsTakenAsStudent.toString())
                            }
                            "TUTOR" -> {
                                RowData(name = "Sessions Completed", value = user.sessionsCompletedAsTutor.toString())
                                RowData(name = "Requests Received", value = user.requestsReceived.toString())
                                RowData(name = "Accepted Student Requests", value = user.requestsAccepted.toString())
                                RowData(name = "Denied Student Requests", value = user.requestsDenied.toString())
                                RowData(name = "Assignments Created", value = user.assignmentsTaken.toString())
                                RowData(name = "Assessments Taken", value = user.assessmentsTakenAsTutor.toString())
                            }
                        }
                    }
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        val avgRating = roundRating(courses.map { (it.courseAssessmentScore.toDouble() / it.courseAssessmentItemsTotal) * 5 } .average())
                        Text_1(text = "Overall Rating")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RatingBar(rating = avgRating.toFloat(), modifier = Modifier
                                .padding(10.dp)
                                .height(20.dp))
                            Text_1(text = "$avgRating")
                        }
                    }
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Text_1(text = "Courses Rating")
                        courses.forEachIndexed { idx, course ->
                            Divider()
                            val rating = roundRating((course.courseAssessmentScore.toDouble() / course.courseAssessmentItemsTotal) * 5)
                            Text_1(text = courseName[idx])
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                RatingBar(rating = rating.toFloat(), modifier = Modifier
                                    .padding(10.dp)
                                    .height(20.dp))
                                Text_1(text = "$rating")
                            }
                        }
                    }
                }
            }
        }
    }
}