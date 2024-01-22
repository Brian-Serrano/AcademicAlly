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
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.AssessmentMenu
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.PatternAssessmentViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun PatternAssessment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    patternAssessmentViewModel: PatternAssessmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        patternAssessmentViewModel.getData(context)
    }
    
    val process by patternAssessmentViewModel.processState.collectAsState()
    val user by patternAssessmentViewModel.drawerData.collectAsState()
    val assessmentAnswers by patternAssessmentViewModel.assessmentAnswers.collectAsState()
    val assessmentData by patternAssessmentViewModel.assessmentState.collectAsState()
    val item by patternAssessmentViewModel.item.collectAsState()
    val nextButtonEnabled by patternAssessmentViewModel.nextButtonEnabled.collectAsState()
    val isRefreshLoading by patternAssessmentViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { patternAssessmentViewModel.refreshData(context) }

    val onBackButtonClick = {
        if (item > 0) {
            patternAssessmentViewModel.moveItem(false)
        }
    }

    val onNextButtonClick = {
        if (item < assessmentData.size - 1) {
            patternAssessmentViewModel.moveItem(true)
        } else {
            patternAssessmentViewModel.sendAnswer(
                context = context,
                navigate = {
                    Toast.makeText(context, "Learning Pattern Assessment Complete!", Toast.LENGTH_LONG).show()
                    navController.navigate("Dashboard") {
                        popUpTo(navController.graph.id) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
    
    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "PATTERN ASSESSMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "PATTERN ASSESSMENT",
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
                topBarText = "PATTERN ASSESSMENT",
                navController = navController,
                context = context,
                selected = "Assessment"
            ) { paddingValues ->
                AssessmentMenu(
                    items = assessmentData.size.toString(),
                    type = "Multiple Choice",
                    course = "Pattern Assessment",
                    item = item,
                    assessmentData = assessmentData,
                    assessmentAnswers = assessmentAnswers,
                    onBackButtonClick = onBackButtonClick,
                    onNextButtonClick = onNextButtonClick,
                    padding = paddingValues,
                    onAddAnswer = { patternAssessmentViewModel.addAnswer(it, item) },
                    nextButtonEnabled = nextButtonEnabled
                )
            }
        }
    }
}