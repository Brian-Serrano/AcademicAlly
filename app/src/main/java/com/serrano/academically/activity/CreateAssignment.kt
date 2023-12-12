package com.serrano.academically.activity

import android.content.Context
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
import com.serrano.academically.custom_composables.ConfirmDialog
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.DropDown
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.custom_composables.SimpleProgressIndicatorWithAnim
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.utils.AssessmentType
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.IdentificationFields
import com.serrano.academically.utils.MultipleChoiceFields
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.TrueOrFalseFields
import com.serrano.academically.utils.toMilitaryTime
import com.serrano.academically.viewmodel.CreateAssignmentViewModel
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDateTime

@Composable
fun CreateAssignment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    userId: Int,
    sessionId: Int,
    items: String,
    type: String,
    deadline: String,
    createAssignmentViewModel: CreateAssignmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        createAssignmentViewModel.getData(userId, sessionId, items.toInt(), type)
    }

    val process by createAssignmentViewModel.processState.collectAsState()
    val user by createAssignmentViewModel.drawerData.collectAsState()
    val session by createAssignmentViewModel.sessionInfo.collectAsState()
    val assessmentFields by createAssignmentViewModel.quizFields.collectAsState()
    val item by createAssignmentViewModel.item.collectAsState()
    val dialogOpen by createAssignmentViewModel.isFilterDialogOpen.collectAsState()

    val onBackButtonClick = {
        if(item > 0) {
            createAssignmentViewModel.moveItem(false)
        }
    }
    val onNextButtonClick = {
        if(item < assessmentFields.size - 1) {
            createAssignmentViewModel.moveItem(true)
        }
    }
    val onFieldEdit = { quiz: AssessmentType -> createAssignmentViewModel.updateFields(assessmentFields.map { if (quiz.id == it.id) quiz else it }) }
    val onSaveButtonClick = { createAssignmentViewModel.toggleDialog(true) }

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "EDIT ASSIGNMENT",
                navController = navController,
                context = context
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
                        val deadlineLocalDate = LocalDateTime.parse(deadline)
                        LazyRow {
                            item { QuizInfo(name = "Course", value = GetCourses.getCourseNameById(session.courseId, context)) }
                            item { QuizInfo(name = "Items", value = items) }
                            item { QuizInfo(name = "Type", value = type) }
                            item { QuizInfo(name = "Module", value = GetModules.getModuleByCourseAndModuleId(session.courseId, session.moduleId, context)) }
                            item { QuizInfo(name = "Deadline", value = "${deadlineLocalDate.month} ${deadlineLocalDate.dayOfMonth}, ${deadlineLocalDate.year} ${toMilitaryTime(listOf(deadlineLocalDate.hour, deadlineLocalDate.minute))}") }
                        }
                        SimpleProgressIndicatorWithAnim(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth()
                                .height(10.dp), cornerRadius = 35.dp, thumbRadius = 1.dp, thumbOffset = 1.5.dp,
                            progress = item / items.toFloat(),
                            progressBarColor = Color.Cyan
                        )
                        Text(
                            text = "Item",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            when (type) {
                                "Multiple Choice" -> MultipleChoiceEditor(
                                    assessmentFields = assessmentFields[item] as MultipleChoiceFields,
                                    onFieldEdit = onFieldEdit
                                )
                                "Identification" -> IdentificationEditor(
                                    assessmentFields = assessmentFields[item] as IdentificationFields,
                                    onFieldEdit = onFieldEdit
                                )
                                "True or False" -> TrueOrFalseEditor(
                                    assessmentFields = assessmentFields[item] as TrueOrFalseFields,
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
    assessmentFields: MultipleChoiceFields,
    onFieldEdit: (MultipleChoiceFields) -> Unit,
    choices: List<String> = listOf("A", "B", "C", "D")
) {
    YellowCard(MaterialTheme.colorScheme.tertiary) {
        Text(
            text = "Question #${assessmentFields.id + 1}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        LoginTextField(
            inputName = "Question",
            input = assessmentFields.question,
            onInputChange = { onFieldEdit(assessmentFields.copy(question = it)) },
            modifier = Modifier.padding(all = 20.dp)
        )
        choices.forEachIndexed { index, choice ->
            Text(
                text = "Letter $choice",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(all = 20.dp)
            )
            LoginTextField(
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
                modifier = Modifier.padding(all = 20.dp)
            )
        }
        Text(
            text = "Answer",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        DropDown(
            dropDownState = assessmentFields.answer,
            onArrowClick = { onFieldEdit(assessmentFields.copy(answer = assessmentFields.answer.copy(expanded = true))) },
            onDismissRequest = { onFieldEdit(assessmentFields.copy(answer = assessmentFields.answer.copy(expanded = false))) },
            onItemSelect = { onFieldEdit(assessmentFields.copy(answer = assessmentFields.answer.copy(selected = it, expanded = false))) }
        )
    }
}

@Composable
fun IdentificationEditor(
    assessmentFields: IdentificationFields,
    onFieldEdit: (IdentificationFields) -> Unit
) {
    YellowCard(MaterialTheme.colorScheme.tertiary) {
        Text(
            text = "Question #${assessmentFields.id + 1}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        LoginTextField(
            inputName = "Question",
            input = assessmentFields.question,
            onInputChange = { onFieldEdit(assessmentFields.copy(question = it)) },
            modifier = Modifier.padding(all = 20.dp)
        )
        Text(
            text = "Answer",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        LoginTextField(
            inputName = "Answer",
            input = assessmentFields.answer,
            onInputChange = { onFieldEdit(assessmentFields.copy(answer = it)) },
            modifier = Modifier.padding(all = 20.dp)
        )
    }
}

@Composable
fun TrueOrFalseEditor(
    assessmentFields: TrueOrFalseFields,
    onFieldEdit: (TrueOrFalseFields) -> Unit
) {
    YellowCard(MaterialTheme.colorScheme.tertiary) {
        Text(
            text = "Question #${assessmentFields.id + 1}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        LoginTextField(
            inputName = "Question",
            input = assessmentFields.question,
            onInputChange = { onFieldEdit(assessmentFields.copy(question = it)) },
            modifier = Modifier.padding(all = 20.dp)
        )
        Text(
            text = "Answer",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        DropDown(
            dropDownState = assessmentFields.answer,
            onArrowClick = { onFieldEdit(assessmentFields.copy(answer = assessmentFields.answer.copy(expanded = true))) },
            onDismissRequest = { onFieldEdit(assessmentFields.copy(answer = assessmentFields.answer.copy(expanded = false))) },
            onItemSelect = { onFieldEdit(assessmentFields.copy(answer = assessmentFields.answer.copy(selected = it, expanded = false))) }
        )
    }
}