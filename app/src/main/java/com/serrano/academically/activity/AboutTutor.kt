package com.serrano.academically.activity

import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.InfoCard
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingBar
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AboutTutorViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.serrano.academically.custom_composables.Divider
import com.serrano.academically.utils.roundRating
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutTutor(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    tutorId: Int,
    navController: NavController,
    context: Context,
    aboutTutorViewModel: AboutTutorViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        aboutTutorViewModel.getData(userId, tutorId, context)
    }

    val user by aboutTutorViewModel.userData.collectAsState()
    val tutor by aboutTutorViewModel.tutorInfo.collectAsState()
    val tutorCourses by aboutTutorViewModel.tutorCourses.collectAsState()
    val courseNames by aboutTutorViewModel.courseNames.collectAsState()
    val process by aboutTutorViewModel.processState.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "ABOUT ${tutor.name}",
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(80.dp)
                                )
                                Column {
                                    Box(modifier = Modifier.clickable { navController.navigate("Profile/${user.id}/${tutor.id}") }) {
                                        Text_1(text = tutor.name)
                                    }
                                    Text_1(text = "Degree: ${tutor.degree}")
                                    Text_1(text = "Age: ${tutor.age}")
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val avgRating = roundRating(tutorCourses.map { (it.courseAssessmentScore.toDouble() / it.courseAssessmentItemsTotal) * 5 } .average())
                                RatingBar(rating = avgRating.toFloat(), modifier = Modifier
                                    .padding(10.dp)
                                    .height(20.dp))
                                Text_1(text = "$avgRating")
                                BlackButton(
                                    text = Strings.contact,
                                    action = { navController.navigate("MessageTutor/${user.id}/${tutor.id}") },
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(65.dp)
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Text_1(text = "Course Ratings")
                        tutorCourses.forEachIndexed { idx, course ->
                            Divider()
                            val rating = roundRating((course.courseAssessmentScore.toDouble() / course.courseAssessmentItemsTotal) * 5)
                            Text_1(text = courseNames[idx])
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
                    InfoCard(title = "SUMMARY", description = tutor.summary)
                    InfoCard(title = "ADDRESS", description = tutor.address)
                    InfoCard(title = "CONTACT NUMBER", description = tutor.contactNumber)
                    InfoCard(title = "EDUCATIONAL BACKGROUND", description = tutor.educationalBackground)
                }
            }
        }
    }
}