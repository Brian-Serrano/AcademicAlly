package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingBar
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.CoursesMenuViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun CoursesMenu(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    coursesMenuViewModel: CoursesMenuViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        coursesMenuViewModel.getData(context)
    }

    val user by coursesMenuViewModel.drawerData.collectAsState()
    val process by coursesMenuViewModel.processState.collectAsState()
    val courseSkills by coursesMenuViewModel.courseSkills.collectAsState()
    val isRefreshLoading by coursesMenuViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { coursesMenuViewModel.refreshData(context) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "COURSES",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "COURSES",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "COURSES",
                navController = navController,
                context = context,
                selected = "Dashboard"
            ) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = onRefresh,
                        refreshTriggerDistance = 50.dp
                    ) {
                        LazyColumn {
                            items(items = courseSkills) {
                                CustomCard {
                                    val rating =
                                        Utils.roundRating((it.assessmentRating / it.assessmentTaken) * 5)
                                    Text(
                                        text = it.courseName,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                    Text(
                                        text = it.courseDescription,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                    Text(
                                        text = "Rating: $rating",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                    RatingBar(
                                        rating = rating.toFloat(),
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .height(20.dp)
                                    )
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    BlackButton(
                                        text = "TAKE ASSESSMENT",
                                        action = { navController.navigate("ChooseAssessment") },
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