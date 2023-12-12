package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.Divider
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.DropDown
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AssignmentOptionViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AssignmentOption(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    userId: Int,
    sessionId: Int,
    context: Context,
    assignmentOptionViewModel: AssignmentOptionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assignmentOptionViewModel.getData(userId, sessionId)
    }

    val process by assignmentOptionViewModel.processState.collectAsState()
    val user by assignmentOptionViewModel.drawerData.collectAsState()
    val itemsDropdown by assignmentOptionViewModel.itemsDropdown.collectAsState()
    val typeDropdown by assignmentOptionViewModel.typeDropdown.collectAsState()
    val deadline by assignmentOptionViewModel.deadlineField.collectAsState()
    val session by assignmentOptionViewModel.sessionInfo.collectAsState()

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.primary
                        )
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Text_1(text = "Course: ${GetCourses.getCourseNameById(session.courseId, context)}")
                        Divider()
                        Text_1(text = "Module: ${GetModules.getModuleByCourseAndModuleId(session.courseId, session.moduleId, context)}")
                        Divider()
                        Text_1(text = "If you cancel making assignment, the session will remain uncompleted.")
                        Divider()
                        Text_1(text = "Items")
                        DropDown(
                            dropDownState = itemsDropdown,
                            onArrowClick = { assignmentOptionViewModel.updateItemsDropdown(itemsDropdown.copy(expanded = true)) },
                            onDismissRequest = { assignmentOptionViewModel.updateItemsDropdown(itemsDropdown.copy(expanded = false)) },
                            onItemSelect = { assignmentOptionViewModel.updateItemsDropdown(itemsDropdown.copy(selected = it, expanded = false)) }
                        )
                        Divider()
                        Text_1(text = "Type")
                        DropDown(
                            dropDownState = typeDropdown,
                            onArrowClick = { assignmentOptionViewModel.updateTypeDropdown(typeDropdown.copy(expanded = true)) },
                            onDismissRequest = { assignmentOptionViewModel.updateTypeDropdown(typeDropdown.copy(expanded = false)) },
                            onItemSelect = { assignmentOptionViewModel.updateTypeDropdown(typeDropdown.copy(selected = it, expanded = false)) }
                        )
                        Divider()
                        Text_1(text = "Deadline Date")
                        LoginTextField(
                            inputName = "Date",
                            input = deadline.date,
                            onInputChange = { assignmentOptionViewModel.updateDeadline(deadline.copy(date = it)) },
                            modifier = Modifier.padding(10.dp),
                            supportingText = "Should be in dd/MM/yyyy format"
                        )
                        Divider()
                        Text_1(text = "Deadline Time")
                        LoginTextField(
                            inputName = "Time",
                            input = deadline.time,
                            onInputChange = { assignmentOptionViewModel.updateDeadline(deadline.copy(time = it)) },
                            modifier = Modifier.padding(10.dp),
                            supportingText = "Should be in hh:mm AM/PM format"
                        )
                        Divider()
                        Text(
                            text = deadline.error,
                            color = Color.Red
                        )
                        Row {
                            GreenButton(
                                action = {
                                    assignmentOptionViewModel.validateDeadlineFormatAndNavigate(
                                        deadlineField = deadline,
                                        navigate = { navController.navigate("CreateAssignment/$userId/$sessionId/${itemsDropdown.selected}/${typeDropdown.selected}/$it") }
                                    )
                                },
                                text = "Create Assignment"
                            )
                        }
                    }
                }
            }
        }
    }
}