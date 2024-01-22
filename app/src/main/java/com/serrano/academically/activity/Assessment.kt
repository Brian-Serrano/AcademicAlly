package com.serrano.academically.activity

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.AssessmentMenu
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.TopBar
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AssessmentViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Assessment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    courseId: Int,
    items: String,
    type: String,
    navController: NavController,
    assessmentViewModel: AssessmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assessmentViewModel.getData(courseId, items.toInt(), type)
    }

    val user by assessmentViewModel.drawerData.collectAsState()
    val process by assessmentViewModel.processState.collectAsState()
    val assessment by assessmentViewModel.assessment.collectAsState()
    val assessmentAnswers by assessmentViewModel.assessmentAnswers.collectAsState()
    val item by assessmentViewModel.item.collectAsState()
    val isAuthorized by assessmentViewModel.isAuthorized.collectAsState()
    val nextEnabled by assessmentViewModel.nextButtonEnabled.collectAsState()
    val isRefreshLoading by assessmentViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { assessmentViewModel.refreshData(courseId, items.toInt(), type, context) }

    val focusManager = LocalFocusManager.current

    val navigate = { score: Int, it: Int, role: String ->
        navController.navigate("AssessmentResult/$score/$it/$role/$isAuthorized") {
            popUpTo(navController.graph.id) {
                inclusive = false
            }
        }
    }

    val onBackButtonClick = {
        if (item > 0) {
            if (type == "Identification") {
                focusManager.clearFocus()
            }
            assessmentViewModel.moveItem(false)
        }
    }

    val onNextButtonClick = {
        if (item < assessment.assessmentData.size - 1) {
            if (type == "Identification") {
                focusManager.clearFocus()
            }
            assessmentViewModel.moveItem(true)
        } else {
            if (isAuthorized) {
                assessmentViewModel.updateCourseSkill(
                    result = assessmentViewModel.evaluateAnswers(
                        assessment.assessmentData,
                        assessmentAnswers,
                        type,
                        courseId
                    ),
                    navigate = navigate,
                    context = context
                )
            } else {
                assessmentViewModel.saveResultToPreferences(
                    result = assessmentViewModel.evaluateAnswers(
                        assessment.assessmentData,
                        assessmentAnswers,
                        type,
                        courseId
                    ),
                    context = context,
                    navigate = navigate
                )
            }
        }
    }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ASSESSMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ASSESSMENT",
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
                    selected = "Assessment"
                ) {
                    Scaffold(
                        topBar = TopBar(
                            scope = scope,
                            drawerState = drawerState,
                            text = "ASSESSMENT",
                            navController = navController
                        )
                    ) { paddingValues ->
                        AssessmentMenu(
                            items = items,
                            type = type,
                            course = assessment.name,
                            item = item,
                            assessmentData = assessment.assessmentData,
                            assessmentAnswers = assessmentAnswers,
                            onBackButtonClick = onBackButtonClick,
                            onNextButtonClick = onNextButtonClick,
                            padding = paddingValues,
                            onAddAnswer = { assessmentViewModel.addAnswer(it, item) },
                            nextButtonEnabled = nextEnabled
                        )
                    }
                }
            } else {
                ScaffoldNoDrawer(
                    text = "ASSESSMENT",
                    navController = navController
                ) { paddingValues ->
                    AssessmentMenu(
                        items = items,
                        type = type,
                        course = assessment.name,
                        item = item,
                        assessmentData = assessment.assessmentData,
                        assessmentAnswers = assessmentAnswers,
                        onBackButtonClick = onBackButtonClick,
                        onNextButtonClick = onNextButtonClick,
                        padding = paddingValues,
                        onAddAnswer = { assessmentViewModel.addAnswer(it, item) },
                        nextButtonEnabled = nextEnabled
                    )
                }
            }
        }
    }
}