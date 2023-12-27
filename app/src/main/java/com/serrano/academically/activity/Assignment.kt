package com.serrano.academically.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.AssessmentMenu
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AssignmentViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun Assignment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    userId: Int,
    assignmentId: Int,
    assignmentViewModel: AssignmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assignmentViewModel.getData(userId, assignmentId, context)
    }

    val process by assignmentViewModel.processState.collectAsState()
    val user by assignmentViewModel.drawerData.collectAsState()
    val assignment by assignmentViewModel.assignment.collectAsState()
    val assessmentData by assignmentViewModel.assessmentData.collectAsState()
    val item by assignmentViewModel.item.collectAsState()
    val assessmentAnswer by assignmentViewModel.assessmentAnswers.collectAsState()
    val nextEnabled by assignmentViewModel.nextButtonEnabled.collectAsState()
    val course by assignmentViewModel.courseName.collectAsState()

    val onBackButtonClick = {
        if (item > 0) {
            assignmentViewModel.moveItem(false)
        }
    }
    val onNextButtonClick = {
        if (item < assessmentData.size - 1) {
            assignmentViewModel.moveItem(true)
        } else {
            assignmentViewModel.completeAssignment(
                id = userId,
                score = HelperFunctions.evaluateAnswer(
                    assessmentData,
                    assessmentAnswer,
                    assignment.type
                ),
                assignmentId = assignmentId,
                context = context,
                navigate = {
                    Toast.makeText(
                        context,
                        "Assignment Completed! Your score is $it.",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigateUp()
                }
            )
        }
    }

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ASSIGNMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ASSIGNMENT",
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
                topBarText = "ASSIGNMENT",
                navController = navController,
                context = context,
                selected = "Assessment"
            ) { paddingValues ->
                AssessmentMenu(
                    items = assessmentData.size.toString(),
                    type = assignment.type,
                    course = course,
                    item = item,
                    assessmentData = assessmentData,
                    assessmentAnswers = assessmentAnswer,
                    onBackButtonClick = onBackButtonClick,
                    onNextButtonClick = onNextButtonClick,
                    padding = paddingValues,
                    onAddAnswer = { assignmentViewModel.addAnswer(it, item) },
                    nextButtonEnabled = nextEnabled
                )
            }
        }
    }

}