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
import androidx.compose.material3.HorizontalDivider
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
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.DropDown
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.YellowCard
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
    rate: Int,
    context: Context,
    assignmentOptionViewModel: AssignmentOptionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assignmentOptionViewModel.getData(userId, sessionId, context)
    }

    val process by assignmentOptionViewModel.processState.collectAsState()
    val user by assignmentOptionViewModel.drawerData.collectAsState()
    val itemsDropdown by assignmentOptionViewModel.itemsDropdown.collectAsState()
    val typeDropdown by assignmentOptionViewModel.typeDropdown.collectAsState()
    val deadline by assignmentOptionViewModel.deadlineField.collectAsState()
    val session by assignmentOptionViewModel.sessionInfo.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "EDIT ASSIGNMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "EDIT ASSIGNMENT",
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
                topBarText = "EDIT ASSIGNMENT",
                navController = navController,
                context = context,
                selected = "Assessment"
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
                    YellowCard {
                        Text(
                            text = "Course: ${session.second.courseName}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Text(
                            text = "Module: ${session.second.moduleName}",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Text(
                            text = "If you cancel making assignment, the session will remain uncompleted and the student you rate will be discarded.",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Text(
                            text = "Items",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        DropDown(
                            dropDownState = itemsDropdown,
                            onArrowClick = {
                                assignmentOptionViewModel.updateItemsDropdown(
                                    itemsDropdown.copy(expanded = true)
                                )
                            },
                            onDismissRequest = {
                                assignmentOptionViewModel.updateItemsDropdown(
                                    itemsDropdown.copy(expanded = false)
                                )
                            },
                            onItemSelect = {
                                assignmentOptionViewModel.updateItemsDropdown(
                                    itemsDropdown.copy(selected = it, expanded = false)
                                )
                            }
                        )
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Text(
                            text = "Type",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        DropDown(
                            dropDownState = typeDropdown,
                            onArrowClick = {
                                assignmentOptionViewModel.updateTypeDropdown(
                                    typeDropdown.copy(expanded = true)
                                )
                            },
                            onDismissRequest = {
                                assignmentOptionViewModel.updateTypeDropdown(
                                    typeDropdown.copy(expanded = false)
                                )
                            },
                            onItemSelect = {
                                assignmentOptionViewModel.updateTypeDropdown(
                                    typeDropdown.copy(selected = it, expanded = false)
                                )
                            }
                        )
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Text(
                            text = "Deadline Date",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        LoginTextField(
                            inputName = "Date",
                            input = deadline.date,
                            onInputChange = {
                                assignmentOptionViewModel.updateDeadline(
                                    deadline.copy(
                                        date = it
                                    )
                                )
                            },
                            modifier = Modifier.padding(10.dp),
                            supportingText = "Should be in dd/MM/yyyy format"
                        )
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Text(
                            text = "Deadline Time",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(10.dp)
                        )
                        LoginTextField(
                            inputName = "Time",
                            input = deadline.time,
                            onInputChange = {
                                assignmentOptionViewModel.updateDeadline(
                                    deadline.copy(
                                        time = it
                                    )
                                )
                            },
                            modifier = Modifier.padding(10.dp),
                            supportingText = "Should be in hh:mm AM/PM format"
                        )
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Text(
                            text = deadline.error,
                            color = Color.Red
                        )
                        Row {
                            GreenButton(
                                action = {
                                    assignmentOptionViewModel.validateDeadlineFormatAndNavigate(
                                        deadlineField = deadline,
                                        navigate = { navController.navigate("CreateAssignment/$userId/$sessionId/${itemsDropdown.selected}/${typeDropdown.selected}/$it/$rate") }
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