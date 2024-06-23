package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.AssessmentMenu
import thesis.academic.ally.custom_composables.BlackButton
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
    val dialogOpen by assignmentViewModel.dialogOpen.collectAsState()
    val assessmentResults by assignmentViewModel.assessmentResults.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { assignmentViewModel.refreshData(assignmentId) }
    val dialogScroll = rememberScrollState()

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
                score = Utils.evaluateAssignment(
                    assessmentData,
                    assessmentAnswer,
                    assignment.type
                ),
                assignmentId = assignmentId,
                openDialog = {
                    Toast.makeText(
                        context,
                        "Assignment Completed! Your score is $it.",
                        Toast.LENGTH_LONG
                    ).show()
                    assignmentViewModel.toggleDialog()
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
                if (dialogOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x55000000))
                    )
                    Dialog(onDismissRequest = {}) {
                        SelectionContainer {
                            Column(
                                modifier = Modifier
                                    .size(300.dp, 400.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.onBackground),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .verticalScroll(dialogScroll)
                                    ) {
                                        Text(
                                            text = "Assignment Complete",
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(10.dp),
                                            color = MaterialTheme.colorScheme.background
                                        )
                                        assessmentResults.forEachIndexed { idx, res ->
                                            val answerIdx = if (assignment.type == "Multiple Choice") 6 else 2
                                            Text(
                                                text = "${idx + 1}). ${assessmentData[idx][1]}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(horizontal = 20.dp),
                                                color = MaterialTheme.colorScheme.background
                                            )
                                            Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                                                Text(
                                                    text = "Your answer: ${assessmentAnswer[idx]}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.background
                                                )
                                                Icon(
                                                    imageVector = if (res) Icons.Filled.Check else Icons.Filled.Close,
                                                    contentDescription = null,
                                                    tint = if (res) Color.Green else Color.Red
                                                )
                                            }
                                            Text(
                                                text = "Correct Answer: ${assessmentData[idx][answerIdx]}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(horizontal = 20.dp),
                                                color = MaterialTheme.colorScheme.background
                                            )
                                        }
                                        BlackButton(text = "Proceed", action = navController::popBackStack, modifier = Modifier.padding(10.dp))
                                    }
                                    val scrollHeight = (400f / dialogScroll.maxValue) * 400f
                                    Box(
                                        modifier = Modifier
                                            .width(5.dp)
                                            .height(scrollHeight.dp)
                                            .offset(
                                                0.dp,
                                                ((dialogScroll.value * ((400f - scrollHeight) / dialogScroll.maxValue))).dp
                                            )
                                            .background(Color.DarkGray)
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