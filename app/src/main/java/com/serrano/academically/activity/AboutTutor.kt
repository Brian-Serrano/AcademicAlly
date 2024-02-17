package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
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
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.CoursesRating
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.InfoCard
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingBar
import com.serrano.academically.custom_composables.RatingCard
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AboutTutorViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutTutor(
    scope: CoroutineScope,
    drawerState: DrawerState,
    tutorId: Int,
    navController: NavController,
    context: Context,
    aboutTutorViewModel: AboutTutorViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        aboutTutorViewModel.getData(tutorId, context)
    }

    val user by aboutTutorViewModel.drawerData.collectAsState()
    val tutor by aboutTutorViewModel.tutor.collectAsState()
    val process by aboutTutorViewModel.processState.collectAsState()
    val isRefreshLoading by aboutTutorViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { aboutTutorViewModel.refreshData(tutorId, context) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ABOUT TUTOR",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ABOUT TUTOR",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {

            val performanceRating = Utils.roundRating((if (tutor.numberOfRates > 0) tutor.performanceRating / tutor.numberOfRates else 0.0) * 5)
            val courseRating = Utils.roundRating(if (tutor.tutorCourses.isNotEmpty()) tutor.tutorCourses.map { (it.assessmentRating / it.assessmentTaken) * 5 }.average() else 0.0)

            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "ABOUT ${tutor.name}",
                navController = navController,
                context = context,
                selected = "FindTutor"
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
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        bitmap = Utils.convertToImage(tutor.image),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(40.dp))
                                    )
                                    Column {
                                        Box(modifier = Modifier.clickable { navController.navigate("Profile/${tutor.userId}") }) {
                                            Text(
                                                text = tutor.name,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                        Text(
                                            text = "Program: ${tutor.degree}",
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                        Text(
                                            text = "Age: ${tutor.age}",
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val avgRating = Utils.roundRating((performanceRating + courseRating) / 2)
                                    RatingBar(
                                        rating = avgRating.toFloat(),
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .height(20.dp)
                                    )
                                    Text(
                                        text = "$avgRating",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                    BlackButton(
                                        text = "CONTACT",
                                        action = { navController.navigate("MessageTutor/${tutor.userId}") },
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .width(200.dp)
                                            .padding(10.dp)
                                    )
                                }
                            }
                        }
                        CustomCard {
                            Text(
                                text = "Learning Pattern",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            HorizontalDivider(thickness = 2.dp)
                            Text(
                                text = "${tutor.name} is ${if (tutor.primaryLearning == user.primaryLearning) "also" else "not"} ${user.primaryLearning}.",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp),
                                color = if (tutor.primaryLearning == user.primaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${tutor.name} is ${if (tutor.secondaryLearning == user.secondaryLearning) "also" else "not"} ${user.secondaryLearning}.",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp),
                                color = if (tutor.secondaryLearning == user.secondaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                            )
                        }
                        CustomCard {
                            RatingCard(text = "Performance Rating", rating = performanceRating)
                        }
                        CustomCard {
                            RatingCard(text = "Overall Course Rating", rating = courseRating)
                        }
                        CustomCard {
                            CoursesRating(tutor.tutorCourses)
                        }
                        InfoCard(title = "SUMMARY", description = tutor.summary)
                        InfoCard(title = "ADDRESS", description = tutor.address)
                        InfoCard(title = "CONTACT NUMBER", description = tutor.contactNumber)
                        InfoCard(title = "EDUCATIONAL BACKGROUND", description = tutor.educationalBackground)
                        InfoCard(title = "FREE TUTORING TIME", description = tutor.freeTutoringTime)
                    }
                }
            }
        }
    }
}