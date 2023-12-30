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
import androidx.compose.material3.IconButton
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
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.CircularProgressBar
import com.serrano.academically.custom_composables.CoursesList
import com.serrano.academically.custom_composables.DashboardTopBar
import com.serrano.academically.custom_composables.DashboardTopBarNoDrawer
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.ui.theme.montserrat
import com.serrano.academically.utils.DashboardIcons
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Dashboard(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    userId: Int,
    context: Context,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        dashboardViewModel.getData(userId, context)
    }

    val user by dashboardViewModel.user.collectAsState()
    val course by dashboardViewModel.courseSkills.collectAsState()
    val process by dashboardViewModel.processState.collectAsState()
    val ratings by dashboardViewModel.ratings.collectAsState()
    val animationPlayed by dashboardViewModel.animationPlayed.collectAsState()

    when (process) {
        ProcessState.Error -> {
            DashboardTopBarNoDrawer {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            DashboardTopBarNoDrawer {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            val icons = if (user.role == "STUDENT") arrayOf(
                DashboardIcons("Leaderboard/${user.id}", "Leaderboard", Icons.Filled.Leaderboard),
                DashboardIcons("Analytics/${user.id}", "Analytics", Icons.Filled.Analytics),
                DashboardIcons("FindTutor/${user.id}", "Find Tutor", Icons.Filled.PersonSearch),
                DashboardIcons("Notifications/${user.id}", "Sessions", Icons.Filled.BrowseGallery)
            ) else arrayOf(
                DashboardIcons("Leaderboard/${user.id}", "Leaderboard", Icons.Filled.Leaderboard),
                DashboardIcons("Analytics/${user.id}", "Analytics", Icons.Filled.Analytics),
                DashboardIcons("Notifications/${user.id}", "Sessions", Icons.Filled.BrowseGallery)
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
                selected = "Dashboard"
            ) {
                Scaffold(
                    topBar = DashboardTopBar(
                        scope = scope,
                        drawerState = drawerState,
                        onIconClick = { navController.navigate("Profile/${user.id}/${user.id}") }
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
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
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
                                        IconButton(
                                            onClick = { navController.navigate(it.route) },
                                            modifier = Modifier.size(50.dp)
                                        ) {
                                            Icon(
                                                imageVector = it.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(50.dp)
                                            )
                                        }
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
                                                percentage = ratings.first.toFloat(),
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
                                                percentage = ratings.second.toFloat(),
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
                                    CoursesList(course)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        BlackButton(
                                            text = "SHOW ALL COURSES",
                                            action = { navController.navigate("CoursesMenu/${user.id}") },
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