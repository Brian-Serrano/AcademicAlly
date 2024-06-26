package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.ConfirmDialog
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.CustomInputField
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.DropDown
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.GreenButton
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.QuizInfo
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.custom_composables.SimpleProgressIndicatorWithAnim
import thesis.academic.ally.utils.AssessmentType
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.CreateAssignmentViewModel
import java.time.LocalDateTime

@Composable
fun CreateAssignment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    sessionId: Int,
    items: String,
    type: String,
    deadline: String,
    rate: Int,
    createAssignmentViewModel: CreateAssignmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        createAssignmentViewModel.getData(sessionId, items.toInt(), type)
    }

    val process by createAssignmentViewModel.processState.collectAsState()
    val user by createAssignmentViewModel.drawerData.collectAsState()
    val session by createAssignmentViewModel.session.collectAsState()
    val assessmentFields by createAssignmentViewModel.quizFields.collectAsState()
    val item by createAssignmentViewModel.item.collectAsState()
    val dialogOpen by createAssignmentViewModel.isFilterDialogOpen.collectAsState()
    val isRefreshLoading by createAssignmentViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { createAssignmentViewModel.refreshData(sessionId, items.toInt(), type) }

    val onBackButtonClick = {
        if (item > 0) {
            createAssignmentViewModel.moveItem(false)
        }
    }
    val onNextButtonClick = {
        if (item < assessmentFields.size - 1) {
            createAssignmentViewModel.moveItem(true)
        }
    }
    val onFieldEdit = { quiz: AssessmentType ->
        createAssignmentViewModel.updateFields(
            assessmentFields.map {
                if (Utils.getFieldId(quiz) == Utils.getFieldId(it)) quiz else it
            }
        )
    }
    val onSaveButtonClick = { createAssignmentViewModel.toggleDialog(true) }

    val deadlineLocalDate = LocalDateTime.parse(deadline)

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "EDIT ASSIGNMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "EDIT ASSIGNMENT",
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
                topBarText = "EDIT ASSIGNMENT",
                navController = navController,
                context = context,
                selected = Routes.ASSESSMENT
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LazyRow {
                            item { QuizInfo(name = "Course", value = session.courseName) }
                            item { QuizInfo(name = "Items", value = items) }
                            item { QuizInfo(name = "Type", value = type) }
                            item { QuizInfo(name = "Module", value = session.moduleName) }
                            item {
                                QuizInfo(
                                    name = "Deadline",
                                    value = "${Utils.formatDate(deadlineLocalDate)} ${
                                        Utils.toMilitaryTime(
                                            deadlineLocalDate.hour,
                                            deadlineLocalDate.minute
                                        )
                                    }"
                                )
                            }
                        }
                        SimpleProgressIndicatorWithAnim(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth()
                                .height(10.dp),
                            cornerRadius = 35.dp,
                            thumbRadius = 1.dp,
                            thumbOffset = 1.5.dp,
                            progress = item / items.toFloat(),
                            progressBarColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "Item",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            when (val a = assessmentFields[item]) {
                                is AssessmentType.MultipleChoiceFields -> MultipleChoiceEditor(
                                    assessmentFields = a,
                                    onFieldEdit = onFieldEdit
                                )

                                is AssessmentType.IdentificationFields -> IdentificationEditor(
                                    assessmentFields = a,
                                    onFieldEdit = onFieldEdit
                                )

                                is AssessmentType.TrueOrFalseFields -> TrueOrFalseEditor(
                                    assessmentFields = a,
                                    onFieldEdit = onFieldEdit
                                )
                            }
                            Row {
                                GreenButton(
                                    action = onBackButtonClick,
                                    text = "Back"
                                )
                                GreenButton(
                                    action = onNextButtonClick,
                                    text = "Next"
                                )
                            }
                            Row {
                                GreenButton(
                                    action = onSaveButtonClick,
                                    text = "Save"
                                )
                            }
                        }
                    }
                    if (dialogOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000))
                        )
                        ConfirmDialog(
                            text = "Do you want to save the edited assignment?",
                            onDismissRequest = {
                                createAssignmentViewModel.toggleDialog(false)
                            },
                            onClickingYes = {
                                createAssignmentViewModel.toggleDialog(false)
                                createAssignmentViewModel.completeSessionAndSaveAssignment(
                                    session = session,
                                    rate = rate,
                                    type = type,
                                    deadLine = deadlineLocalDate,
                                    navigate = {
                                        Toast.makeText(
                                            context,
                                            "Assignment Created and Session Completed!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                        navController.popBackStack()
                                        navController.popBackStack()
                                        navController.popBackStack()
                                    },
                                    assessment = assessmentFields,
                                    name = user.name
                                )
                            },
                            onClickingNo = {
                                createAssignmentViewModel.toggleDialog(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MultipleChoiceEditor(
    assessmentFields: AssessmentType.MultipleChoiceFields,
    onFieldEdit: (AssessmentType.MultipleChoiceFields) -> Unit,
    choices: List<String> = listOf("A", "B", "C", "D")
) {
    CustomCard {
        Text(
            text = "Question #${assessmentFields.id + 1}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        CustomInputField(
            inputName = "Question",
            input = assessmentFields.question,
            onInputChange = { onFieldEdit(assessmentFields.copy(question = it)) },
            modifier = Modifier.padding(all = 20.dp),
            supportingText = "More than 15 characters"
        )
        choices.forEachIndexed { index, choice ->
            Text(
                text = "Letter $choice",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(all = 20.dp)
            )
            CustomInputField(
                inputName = "Letter $choice",
                input = assessmentFields.choices[index],
                onInputChange = { newString ->
                    onFieldEdit(
                        assessmentFields.copy(
                            choices = assessmentFields.choices.mapIndexed { idx, choice ->
                                if (idx == index) newString else choice
                            }
                        )
                    )
                },
                modifier = Modifier.padding(all = 20.dp),
                supportingText = "Not empty"
            )
        }
        Text(
            text = "Answer",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        DropDown(
            dropDownState = assessmentFields.answer,
            onArrowClick = {
                onFieldEdit(
                    assessmentFields.copy(
                        answer = assessmentFields.answer.copy(
                            expanded = true
                        )
                    )
                )
            },
            onDismissRequest = {
                onFieldEdit(
                    assessmentFields.copy(
                        answer = assessmentFields.answer.copy(
                            expanded = false
                        )
                    )
                )
            },
            onItemSelect = {
                onFieldEdit(
                    assessmentFields.copy(
                        answer = assessmentFields.answer.copy(
                            selected = it,
                            expanded = false
                        )
                    )
                )
            }
        )
    }
}

@Composable
fun IdentificationEditor(
    assessmentFields: AssessmentType.IdentificationFields,
    onFieldEdit: (AssessmentType.IdentificationFields) -> Unit
) {
    CustomCard {
        Text(
            text = "Question #${assessmentFields.id + 1}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        CustomInputField(
            inputName = "Question",
            input = assessmentFields.question,
            onInputChange = { onFieldEdit(assessmentFields.copy(question = it)) },
            modifier = Modifier.padding(all = 20.dp),
            supportingText = "More than 15 characters"
        )
        Text(
            text = "Answer",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        CustomInputField(
            inputName = "Answer",
            input = assessmentFields.answer,
            onInputChange = { onFieldEdit(assessmentFields.copy(answer = it)) },
            modifier = Modifier.padding(all = 20.dp),
            supportingText = "Not empty"
        )
    }
}

@Composable
fun TrueOrFalseEditor(
    assessmentFields: AssessmentType.TrueOrFalseFields,
    onFieldEdit: (AssessmentType.TrueOrFalseFields) -> Unit
) {
    CustomCard {
        Text(
            text = "Question #${assessmentFields.id + 1}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        CustomInputField(
            inputName = "Question",
            input = assessmentFields.question,
            onInputChange = { onFieldEdit(assessmentFields.copy(question = it)) },
            modifier = Modifier.padding(all = 20.dp),
            supportingText = "More than 15 characters"
        )
        Text(
            text = "Answer",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        DropDown(
            dropDownState = assessmentFields.answer,
            onArrowClick = {
                onFieldEdit(
                    assessmentFields.copy(
                        answer = assessmentFields.answer.copy(
                            expanded = true
                        )
                    )
                )
            },
            onDismissRequest = {
                onFieldEdit(
                    assessmentFields.copy(
                        answer = assessmentFields.answer.copy(
                            expanded = false
                        )
                    )
                )
            },
            onItemSelect = {
                onFieldEdit(
                    assessmentFields.copy(
                        answer = assessmentFields.answer.copy(
                            selected = it,
                            expanded = false
                        )
                    )
                )
            }
        )
    }
}