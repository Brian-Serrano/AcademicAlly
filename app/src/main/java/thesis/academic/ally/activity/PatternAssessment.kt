package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import thesis.academic.ally.custom_composables.AssessmentMenu
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.viewmodel.PatternAssessmentViewModel
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
        patternAssessmentViewModel.getData()
    }
    
    val process by patternAssessmentViewModel.processState.collectAsState()
    val user by patternAssessmentViewModel.drawerData.collectAsState()
    val assessmentAnswers by patternAssessmentViewModel.assessmentAnswers.collectAsState()
    val assessmentData by patternAssessmentViewModel.assessmentState.collectAsState()
    val item by patternAssessmentViewModel.item.collectAsState()
    val nextButtonEnabled by patternAssessmentViewModel.nextButtonEnabled.collectAsState()
    val isRefreshLoading by patternAssessmentViewModel.isRefreshLoading.collectAsState()
    val patternAssessment by patternAssessmentViewModel.patternAssessment.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { patternAssessmentViewModel.refreshData() }

    val onBackButtonClick = {
        if (item > 0) {
            patternAssessmentViewModel.moveItem(false)
        }
    }

    val navigate = {
        navController.navigate(Routes.DASHBOARD) {
            popUpTo(navController.graph.id) {
                inclusive = false
            }
        }
    }

    val onNextButtonClick = {
        if (item < assessmentData.size - 1) {
            patternAssessmentViewModel.moveItem(true)
        } else {
            patternAssessmentViewModel.sendAnswer()
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
                selected = Routes.ASSESSMENT
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
                if (patternAssessment.dialogOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x55000000))
                    )
                    Dialog(onDismissRequest = navigate) {
                        Column(
                            modifier = Modifier
                                .size(300.dp, 400.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.onBackground)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Learning Pattern Assessment Complete! You are ${patternAssessment.primaryPattern.title} and ${patternAssessment.secondaryPattern.title}.\n\n${patternAssessment.primaryPattern.description}.\n\n${patternAssessment.secondaryPattern.description}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(20.dp),
                                color = MaterialTheme.colorScheme.background
                            )
                            BlackButton(text = "Proceed", action = navigate, modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }
        }
    }
}