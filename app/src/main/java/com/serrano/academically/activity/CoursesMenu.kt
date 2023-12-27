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
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingBar
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.CoursesMenuViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun CoursesMenu(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    id: Int,
    navController: NavController,
    coursesMenuViewModel: CoursesMenuViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        coursesMenuViewModel.getData(id, context)
    }

    val user by coursesMenuViewModel.user.collectAsState()
    val process by coursesMenuViewModel.processState.collectAsState()
    val courseSkills by coursesMenuViewModel.courseSkills.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = Strings.courses,
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = Strings.courses,
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
                topBarText = Strings.courses,
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
                    LazyColumn {
                        items(items = courseSkills) {
                            YellowCard {
                                val rating =
                                    HelperFunctions.roundRating((it.first.assessmentRating / it.first.assessmentTaken) * 5)
                                Text(
                                    text = it.second.first,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(10.dp)
                                )
                                Text(
                                    text = it.second.second,
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
                                    text = Strings.takeAssessment,
                                    action = { navController.navigate("ChooseAssessment/${user.id}") },
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