package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.AssessmentMenu
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.AssignmentViewModel

@Composable
fun Assignment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    assignmentId: Int,
    assignmentViewModel: AssignmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assignmentViewModel.getData(assignmentId)
    }

    val process by assignmentViewModel.processState.collectAsState()
    val user by assignmentViewModel.drawerData.collectAsState()
    val assignment by assignmentViewModel.assignment.collectAsState()
    val assessmentData by assignmentViewModel.assessmentData.collectAsState()
    val item by assignmentViewModel.item.collectAsState()
    val assessmentAnswer by assignmentViewModel.assessmentAnswers.collectAsState()
    val nextEnabled by assignmentViewModel.nextButtonEnabled.collectAsState()
    val isRefreshLoading by assignmentViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { assignmentViewModel.refreshData(assignmentId) }

    val focusManager = LocalFocusManager.current

    val onBackButtonClick = {
        if (item > 0) {
            if (assignment.type == "Identification") {
                focusManager.clearFocus()
            }
            assignmentViewModel.moveItem(false)
        }
    }
    val onNextButtonClick = {
        if (item < assessmentData.size - 1) {
            if (assignment.type == "Identification") {
                focusManager.clearFocus()
            }
            assignmentViewModel.moveItem(true)
        } else {
            assignmentViewModel.completeAssignment(
                score = Utils.evaluateAnswer(
                    assessmentData,
                    assessmentAnswer,
                    assignment.type
                ),
                assignmentId = assignmentId,
                navigate = {
                    Toast.makeText(
                        context,
                        "Assignment Completed! Your score is $it.",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.popBackStack()
                }
            )
        }
    }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ASSIGNMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ASSIGNMENT",
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
                topBarText = "ASSIGNMENT",
                navController = navController,
                context = context,
                selected = Routes.ASSESSMENT
            ) { paddingValues ->
                AssessmentMenu(
                    items = assessmentData.size.toString(),
                    type = assignment.type,
                    course = assignment.name,
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