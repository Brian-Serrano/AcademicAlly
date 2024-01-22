package com.serrano.academically.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.DropDown
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.MessageTutorViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun MessageTutor(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    tutorId: Int,
    messageTutorViewModel: MessageTutorViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        messageTutorViewModel.getData(tutorId, context)
    }

    val courses by messageTutorViewModel.coursesDropdown.collectAsState()
    val modules by messageTutorViewModel.modulesDropdown.collectAsState()
    val message by messageTutorViewModel.message.collectAsState()
    val process by messageTutorViewModel.processState.collectAsState()
    val user by messageTutorViewModel.drawerData.collectAsState()
    val tutorCourses by messageTutorViewModel.tutorCourses.collectAsState()
    val requestEnabled by messageTutorViewModel.requestButtonEnabled.collectAsState()
    val isRefreshLoading by messageTutorViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { messageTutorViewModel.refreshData(tutorId, context) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "MESSAGE TUTOR",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "MESSAGE TUTOR",
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
                topBarText = "MESSAGE ${tutorCourses.tutorName}",
                navController = navController,
                context = context,
                selected = "FindTutor"
            ) { values ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomCard {
                        Text(
                            text = "Message ${tutorCourses.tutorName}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(15.dp)
                        )
                        Text(
                            text = "Course",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(15.dp)
                        )
                        DropDown(
                            dropDownState = courses,
                            onArrowClick = {
                                messageTutorViewModel.updateCoursesDropdown(
                                    courses.copy(
                                        expanded = true
                                    )
                                )
                            },
                            onDismissRequest = {
                                messageTutorViewModel.updateCoursesDropdown(
                                    courses.copy(
                                        expanded = false
                                    )
                                )
                            },
                            onItemSelect = {
                                messageTutorViewModel.updateCoursesDropdown(
                                    courses.copy(
                                        selected = it,
                                        expanded = false
                                    )
                                )
                            }
                        )
                        Text(
                            text = "Module",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(15.dp)
                        )
                        DropDown(
                            dropDownState = modules,
                            onArrowClick = {
                                messageTutorViewModel.updateModulesDropdown(
                                    modules.copy(
                                        expanded = true
                                    )
                                )
                            },
                            onDismissRequest = {
                                messageTutorViewModel.updateModulesDropdown(
                                    modules.copy(
                                        expanded = false
                                    )
                                )
                            },
                            onItemSelect = {
                                messageTutorViewModel.updateModulesDropdown(
                                    modules.copy(
                                        selected = it,
                                        expanded = false
                                    )
                                )
                            }
                        )
                        Text(
                            text = "Message",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(15.dp)
                        )
                        LoginTextField(
                            inputName = "Message",
                            input = message,
                            onInputChange = { messageTutorViewModel.updateMessage(it) },
                            modifier = Modifier.padding(15.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            BlackButton(
                                text = "Send Request",
                                action = {
                                    messageTutorViewModel.sendRequest(
                                        course = courses,
                                        module = modules,
                                        studentId = user.id,
                                        tutorId = tutorId,
                                        message = message,
                                        navigate = {
                                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                            navController.popBackStack()
                                        },
                                        context = context
                                    )
                                },
                                modifier = Modifier.padding(15.dp),
                                enabled = requestEnabled
                            )
                        }
                    }
                }
            }
        }
    }
}
