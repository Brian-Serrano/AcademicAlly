package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import com.serrano.academically.custom_composables.CoursesList
import com.serrano.academically.custom_composables.CustomTab
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingCard
import com.serrano.academically.custom_composables.DataGroup
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.ProfileViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Profile(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    otherId: Int,
    navController: NavController,
    context: Context,
    tabs: List<String> = listOf("AS STUDENT", "AS TUTOR"),
    statisticNames: List<List<String>> = listOf(
        listOf(
            "Points",
            "Assessment Points",
            "Request Points",
            "Session Points",
            "Assignment Points",
            "Sessions Completed",
            "Requests Sent",
            "Requests Denied",
            "Requests Accepted",
            "Assignments Taken",
            "Assessments Taken",
            "Badges Earned",
            "Rates Obtained",
            "Tutors Rated"
        ),
        listOf(
            "Points",
            "Assessment Points",
            "Request Points",
            "Session Points",
            "Assignment Points",
            "Sessions Completed",
            "Requests Received",
            "Requests Denied",
            "Requests Accepted",
            "Assignments Created",
            "Assessments Taken",
            "Badges Earned",
            "Rates Obtained",
            "Students Rated"
        )
    ),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        profileViewModel.getData(userId, otherId, context)
    }

    val user by profileViewModel.drawerData.collectAsState()
    val profile by profileViewModel.userData.collectAsState()
    val process by profileViewModel.processState.collectAsState()
    val tabIndex by profileViewModel.tabIndex.collectAsState()
    val statistics by profileViewModel.statisticsData.collectAsState()
    val courses by profileViewModel.courses.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "",
                navController = navController,
                context = context,
                selected = "Profile"
            ) { values ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(10.dp)
                            )
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = "Current Role: ${profile.role}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = "Program: ${profile.degree}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = "Age: ${profile.age}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = "Address: ${profile.address}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = "Contact Number: ${profile.contactNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                    item {
                        CustomTab(
                            tabIndex = tabIndex,
                            tabs = tabs,
                            onTabClick = { profileViewModel.updateTabIndex(it) }
                        )
                    }
                    item {
                        CustomCard {
                            Text(
                                text = "Summary: ${profile.summary}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(20.dp)
                            )
                            Text(
                                text = "Educational Background: ${profile.educationalBackground}",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                    item {
                        CustomCard {
                            val ratings = listOf(
                                HelperFunctions.roundRating((if (profile.numberOfRatesAsStudent > 0) profile.totalRatingAsStudent / profile.numberOfRatesAsStudent else 0.0) * 5),
                                HelperFunctions.roundRating((if (profile.numberOfRatesAsTutor > 0) profile.totalRatingAsTutor / profile.numberOfRatesAsTutor else 0.0) * 5)
                            )
                            RatingCard(text = "Performance Rating", rating = ratings[tabIndex])
                        }
                    }
                    item {
                        CustomCard {
                            Text(
                                text = "Statistics",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(20.dp)
                            )
                            DataGroup(names = statisticNames[tabIndex], values = statistics[tabIndex])
                        }
                    }
                    item {
                        CustomCard {
                            CoursesList(courses[tabIndex])
                        }
                    }
                    if (tabIndex == 1 && userId != otherId && courses[tabIndex].isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                BlackButton(
                                    text = "CONTACT TUTOR",
                                    action = { navController.navigate("MessageTutor/$userId/$otherId") },
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