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
import com.serrano.academically.custom_composables.AssessmentMenu
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.TopBar
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AssessmentViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Assessment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    id: Int,
    courseId: Int,
    items: String,
    type: String,
    navController: NavController,
    assessmentViewModel: AssessmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assessmentViewModel.getData(id, courseId, items.toInt(), type, context)
    }

    val user by assessmentViewModel.drawerData.collectAsState()
    val process by assessmentViewModel.processState.collectAsState()
    val course by assessmentViewModel.courseName.collectAsState()
    val assessmentData by assessmentViewModel.assessmentData.collectAsState()
    val assessmentAnswers by assessmentViewModel.assessmentAnswers.collectAsState()
    val item by assessmentViewModel.item.collectAsState()
    val isDrawerShouldAvailable by assessmentViewModel.isDrawerShouldAvailable.collectAsState()
    val nextEnabled by assessmentViewModel.nextButtonEnabled.collectAsState()

    val focusManager = LocalFocusManager.current

    val onBackButtonClick = {
        if (item > 0) {
            if (type == "Identification") {
                focusManager.clearFocus()
            }
            assessmentViewModel.moveItem(false)
        }
    }
    val onNextButtonClick = {
        if (item < assessmentData.size - 1) {
            if (type == "Identification") {
                focusManager.clearFocus()
            }
            assessmentViewModel.moveItem(true)
        } else {
            if (id == 0) {
                assessmentViewModel.saveResultToPreferences(
                    result = assessmentViewModel.evaluateAnswers(
                        assessmentData,
                        assessmentAnswers,
                        type,
                        courseId
                    ),
                    context = context,
                    navigate = { score, item, role -> navController.navigate("AssessmentResult/0/$score/$item/$role") }
                )
            } else {
                assessmentViewModel.updateCourseSkill(
                    userId = id,
                    result = assessmentViewModel.evaluateAnswers(
                        assessmentData,
                        assessmentAnswers,
                        type,
                        courseId
                    ),
                    navigate = { score, item, role -> navController.navigate("AssessmentResult/$id/$score/$item/$role") },
                    context = context
                )
            }
        }
    }

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ASSESSMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ASSESSMENT",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            if (isDrawerShouldAvailable) {
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
                            course = course,
                            item = item,
                            assessmentData = assessmentData,
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
                        course = course,
                        item = item,
                        assessmentData = assessmentData,
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