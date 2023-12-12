package com.serrano.academically.activity

import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RowData
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.ProfileViewModel
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
            "Points", "Assessment Points", "Request Points", "Session Points", "Sessions Completed", "Requests Sent", "Requests Denied", "Requests Accepted", "Assignments Taken", "Assessments Taken", "Badges Earned"
        ),
        listOf(
            "Points", "Assessment Points", "Request Points", "Session Points", "Sessions Completed", "Requests Received", "Requests Denied", "Requests Accepted", "Assignments Created", "Assessments Taken", "Badges Earned"
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
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "",
                navController = navController,
                context = context
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
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Current Role: ${profile.role}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Degree: ${profile.degree}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Age: ${profile.age}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Address: ${profile.address}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Contact Number: ${profile.contactNumber}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    item {
                        TabRow(
                            selectedTabIndex = tabIndex,
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.Black
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    text = {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    selected = tabIndex == index,
                                    onClick = { profileViewModel.updateTabIndex(index) }
                                )
                            }
                        }
                    }
                    item {
                        YellowCard(MaterialTheme.colorScheme.secondary) {
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
                        YellowCard(MaterialTheme.colorScheme.secondary) {
                            Text(
                                text = "Statistics",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(20.dp)
                            )
                            for (idx in 0..10) {
                                RowData(name = statisticNames[tabIndex][idx], value = statistics[tabIndex][idx])
                            }
                        }
                    }
                    item {
                        YellowCard(MaterialTheme.colorScheme.secondary) {
                            Text(
                                text = "Courses",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(20.dp)
                            )
                            courses[tabIndex].forEach {
                                Text(
                                    text = it.first,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                                Text(
                                    text = it.second,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}