package com.serrano.academically.activity

import android.content.Context
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AboutTutorViewModel
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
    val tutorRating by aboutTutorViewModel.tutorRating.collectAsState()
    val process by aboutTutorViewModel.processState.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ABOUT TUTOR",
                navController = navController
            ) {
                ErrorComposable(navController, it)
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

            val performanceRating =
                HelperFunctions.roundRating((if (tutorRating.number > 0) tutorRating.rating / tutorRating.number else 0.0) * 5)
            val courseRating =
                HelperFunctions.roundRating(if (tutorCourses.isNotEmpty()) tutorCourses.map { (it.first.assessmentRating / it.first.assessmentTaken) * 5 }
                    .average() else 0.0)

            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "ABOUT ${tutor.name}",
                navController = navController,
                context = context,
                selected = "FindTutor"
            ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                        .verticalScroll(rememberScrollState())
                ) {
                    CustomCard {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(80.dp)
                                )
                                Column {
                                    Box(modifier = Modifier.clickable { navController.navigate("Profile/${user.id}/${tutor.id}") }) {
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
                                val avgRating =
                                    HelperFunctions.roundRating((performanceRating + courseRating) / 2)
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
                                    action = { navController.navigate("MessageTutor/${user.id}/${tutor.id}") },
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .width(200.dp)
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                    CustomCard {
                        RatingCard(text = "Performance Rating", rating = performanceRating)
                    }
                    CustomCard {
                        RatingCard(text = "Overall Course Rating", rating = courseRating)
                    }
                    CustomCard {
                        CoursesRating(tutorCourses)
                    }
                    InfoCard(title = "SUMMARY", description = tutor.summary)
                    InfoCard(title = "ADDRESS", description = tutor.address)
                    InfoCard(title = "CONTACT NUMBER", description = tutor.contactNumber)
                    InfoCard(
                        title = "EDUCATIONAL BACKGROUND",
                        description = tutor.educationalBackground
                    )
                }
            }
        }
    }
}