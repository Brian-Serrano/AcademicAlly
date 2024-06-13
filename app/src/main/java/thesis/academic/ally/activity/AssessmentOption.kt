package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.api.Course
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.Drawer
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.GreenButton
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.custom_composables.TopBar
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.viewmodel.AssessmentOptionViewModel

@Composable
fun AssessmentOption(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    courseId: Int,
    navController: NavController,
    assessmentOptionViewModel: AssessmentOptionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assessmentOptionViewModel.getData(courseId)
    }

    val user by assessmentOptionViewModel.drawerData.collectAsState()
    val process by assessmentOptionViewModel.processState.collectAsState()
    val course by assessmentOptionViewModel.course.collectAsState()
    val isAuthorized by assessmentOptionViewModel.isAuthorized.collectAsState()
    val startEnabled by assessmentOptionViewModel.startButtonEnabled.collectAsState()
    val isRefreshLoading by assessmentOptionViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { assessmentOptionViewModel.refreshData(courseId) }

    val onClick = {
        assessmentOptionViewModel.saveAssessmentType { item, type ->
            navController.navigate("${Routes.ASSESSMENT}/$courseId/$item/$type") {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }
            }
        }
    }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "START ASSESSMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "START ASSESSMENT",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            if (isAuthorized) {
                Drawer(
                    scope = scope,
                    drawerState = drawerState,
                    user = user,
                    navController = navController,
                    context = context,
                    selected = Routes.ASSESSMENT
                ) {
                    Scaffold(
                        topBar = TopBar(
                            scope = scope,
                            drawerState = drawerState,
                            text = "START ASSESSMENT",
                            navController = navController
                        )
                    ) {
                        AssessmentOptionMenu(
                            course = course,
                            padding = it,
                            enabled = startEnabled,
                            onClick = onClick
                        )
                    }
                }
            } else {
                ScaffoldNoDrawer(
                    text = "START ASSESSMENT",
                    navController = navController
                ) {
                    AssessmentOptionMenu(
                        course = course,
                        padding = it,
                        enabled = startEnabled,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
fun AssessmentOptionMenu(
    course: Course,
    padding: PaddingValues,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        CustomCard {
            Text(
                text = "Course: ${course.name}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(10.dp)
            )
            HorizontalDivider(thickness = 2.dp)
            Text(
                text = "Description: ${course.description}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(10.dp)
            )
            HorizontalDivider(thickness = 2.dp)
            Row {
                GreenButton(
                    action = onClick,
                    text = "Start Assessment",
                    enabled = enabled
                )
            }
        }
    }
}