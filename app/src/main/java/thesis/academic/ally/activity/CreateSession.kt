package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.DateTimePickerDialog
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.EditSessionMenu
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.CreateSessionViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun CreateSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    messageId: Int,
    createSessionViewModel: CreateSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        createSessionViewModel.getData(messageId)
    }

    val user by createSessionViewModel.drawerData.collectAsState()
    val sessionSettings by createSessionViewModel.sessionSettings.collectAsState()
    val process by createSessionViewModel.processState.collectAsState()
    val message by createSessionViewModel.message.collectAsState()
    val enabled by createSessionViewModel.buttonEnabled.collectAsState()
    val isRefreshLoading by createSessionViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { createSessionViewModel.refreshData(messageId) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "CREATE SESSION",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "CREATE SESSION",
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
                topBarText = "CREATE SESSION",
                navController = navController,
                context = context,
                selected = Routes.NOTIFICATIONS
            ) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CustomCard {
                            EditSessionMenu(
                                sessionSettings = sessionSettings,
                                openDateDialog = {
                                    createSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(
                                            datePickerEnabled = true,
                                            dialogDate = try {
                                                LocalDate.parse(
                                                    sessionSettings.date,
                                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                                )
                                            } catch (e: DateTimeParseException) {
                                                LocalDate.now()
                                            }
                                        )
                                    )
                                },
                                openStartTimeDialog = {
                                    createSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(
                                            timePickerEnabled = true,
                                            dialogTime = try {
                                                LocalTime.parse(
                                                    sessionSettings.startTime,
                                                    DateTimeFormatter.ofPattern("hh:mm a")
                                                )
                                            } catch (e: DateTimeParseException) {
                                                LocalTime.now()
                                            },
                                            isStartTime = true
                                        )
                                    )
                                },
                                openEndTimeDialog = {
                                    createSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(
                                            timePickerEnabled = true,
                                            dialogTime = try {
                                                LocalTime.parse(
                                                    sessionSettings.endTime,
                                                    DateTimeFormatter.ofPattern("hh:mm a")
                                                )
                                            } catch (e: DateTimeParseException) {
                                                LocalTime.now()
                                            },
                                            isStartTime = false
                                        )
                                    )
                                },
                                onLocationInputChange = {
                                    createSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(location = it)
                                    )
                                },
                                onButtonClick = {
                                    createSessionViewModel.createSession(
                                        settings = sessionSettings,
                                        message = message,
                                        navigate = {
                                            Toast.makeText(
                                                context,
                                                "Session Created and Student Accepted!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.popBackStack()
                                            navController.popBackStack()
                                        }
                                    )
                                },
                                buttonText = "Create Session",
                                enabled = enabled
                            )
                        }
                    }
                    DateTimePickerDialog(
                        date = sessionSettings.dialogDate,
                        time = sessionSettings.dialogTime,
                        datePickerEnabled = sessionSettings.datePickerEnabled,
                        timePickerEnabled = sessionSettings.timePickerEnabled,
                        updateDateDialog = {
                            createSessionViewModel.updateSessionSettings(
                                sessionSettings.copy(datePickerEnabled = it)
                            )
                        },
                        updateTimeDialog = {
                            createSessionViewModel.updateSessionSettings(
                                sessionSettings.copy(timePickerEnabled = it)
                            )
                        },
                        selectDate = {
                            createSessionViewModel.updateSessionSettings(
                                sessionSettings.copy(
                                    date = String.format("%02d/%02d/%04d", it.dayOfMonth, it.monthValue, it.year),
                                    datePickerEnabled = false
                                )
                            )
                        },
                        selectTime = {
                            if (sessionSettings.isStartTime) {
                                createSessionViewModel.updateSessionSettings(
                                    sessionSettings.copy(
                                        startTime = Utils.toMilitaryTime(it.hour, it.minute),
                                        timePickerEnabled = false
                                    )
                                )
                            } else {
                                createSessionViewModel.updateSessionSettings(
                                    sessionSettings.copy(
                                        endTime = Utils.toMilitaryTime(it.hour, it.minute),
                                        timePickerEnabled = false
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}