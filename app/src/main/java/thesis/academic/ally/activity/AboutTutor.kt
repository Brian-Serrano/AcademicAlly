package thesis.academic.ally.activity

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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
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
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.CoursesRating
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.InfoCard
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.RatingBar
import thesis.academic.ally.custom_composables.RatingCard
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.AboutTutorViewModel

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
        aboutTutorViewModel.getData(tutorId)
    }

    val user by aboutTutorViewModel.drawerData.collectAsState()
    val tutor by aboutTutorViewModel.tutor.collectAsState()
    val process by aboutTutorViewModel.processState.collectAsState()
    val isRefreshLoading by aboutTutorViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { aboutTutorViewModel.refreshData(tutorId) }

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
                selected = Routes.FIND_TUTOR
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
                                        val isTutorAvailable = Utils.isTutorAvailable(tutor.freeTutoringTime)
                                        Box(modifier = Modifier.clickable { navController.navigate("${Routes.PROFILE}/${tutor.userId}") }) {
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
                                        Text(
                                            text = if (isTutorAvailable) "Available" else "Not Available",
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(10.dp),
                                            color = if (isTutorAvailable) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
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
                                        action = { navController.navigate("${Routes.MESSAGE_TUTOR}/${tutor.userId}") },
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
                        InfoCard(title = "FREE TUTORING TIME", description = Utils.formatTutoringAvailability(tutor.freeTutoringTime))
                    }
                }
            }
        }
    }
}