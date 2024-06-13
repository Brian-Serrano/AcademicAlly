package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.ConfirmDialog
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.DateTimePickerDialog
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.EditSessionMenu
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.RateDialog
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.EditSessionViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun EditSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    sessionId: Int,
    editSessionViewModel: EditSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        editSessionViewModel.getData(sessionId)
    }

    val user by editSessionViewModel.drawerData.collectAsState()
    val process by editSessionViewModel.processState.collectAsState()
    val sessionSettings by editSessionViewModel.sessionSettings.collectAsState()
    val dialogOpen by editSessionViewModel.isFilterDialogOpen.collectAsState()
    val rateDialogOpen by editSessionViewModel.isRateDialogOpen.collectAsState()
    val rateDialogStates by editSessionViewModel.rateDialogStates.collectAsState()
    val enabled by editSessionViewModel.buttonEnabled.collectAsState()
    val isRefreshLoading by editSessionViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { editSessionViewModel.refreshData(sessionId) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "EDIT SESSION",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "EDIT SESSION",
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
                topBarText = "EDIT SESSION",
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                BlackButton(
                                    text = "COMPLETE SESSION",
                                    action = {
                                        editSessionViewModel.toggleDialog(true)
                                    },
                                    modifier = Modifier.padding(15.dp)
                                )
                            }
                            EditSessionMenu(
                                sessionSettings = sessionSettings,
                                openDateDialog = {
                                    editSessionViewModel.updateSessionSettings(
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
                                    editSessionViewModel.updateSessionSettings(
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
                                    editSessionViewModel.updateSessionSettings(
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
                                    editSessionViewModel.updateSessionSettings(
                                        sessionSettings.copy(location = it)
                                    )
                                },
                                onButtonClick = {
                                    editSessionViewModel.updateSession(
                                        settings = sessionSettings,
                                        sessionId = sessionId,
                                        navigate = {
                                            Toast.makeText(
                                                context,
                                                "Session Updated!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            navController.popBackStack()
                                            navController.popBackStack()
                                        }
                                    )
                                },
                                buttonText = "Update Session",
                                enabled = enabled
                            )
                        }
                    }
                    if (dialogOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000))
                        )
                        ConfirmDialog(
                            text = "Do you want to complete the session?",
                            onDismissRequest = {
                                editSessionViewModel.toggleDialog(false)
                            },
                            onClickingYes = {
                                editSessionViewModel.toggleDialog(false)
                                editSessionViewModel.toggleRateDialog(true)
                            },
                            onClickingNo = {
                                editSessionViewModel.toggleDialog(false)
                            }
                        )
                    }
                    if (rateDialogOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000))
                        )
                        RateDialog(
                            text = "Rate Student",
                            buttonOneText = "Later",
                            buttonTwoText = "Rate",
                            star = rateDialogStates.star,
                            onDismissRequest = { editSessionViewModel.toggleRateDialog(false) },
                            onConfirmClick = {
                                editSessionViewModel.toggleRateDialog(false)
                                navController.navigate("${Routes.ASSIGNMENT_OPTION}/$sessionId/${rateDialogStates.star}")
                            },
                            onCancelClick = {
                                editSessionViewModel.toggleRateDialog(false)
                                navController.navigate("${Routes.ASSIGNMENT_OPTION}/$sessionId/0")
                            },
                            onStarClick = {
                                editSessionViewModel.updateRateDialogStates(
                                    rateDialogStates.copy(star = it)
                                )
                            }
                        )
                    }
                    DateTimePickerDialog(
                        date = sessionSettings.dialogDate,
                        time = sessionSettings.dialogTime,
                        datePickerEnabled = sessionSettings.datePickerEnabled,
                        timePickerEnabled = sessionSettings.timePickerEnabled,
                        updateDateDialog = {
                            editSessionViewModel.updateSessionSettings(
                                sessionSettings.copy(datePickerEnabled = it)
                            )
                        },
                        updateTimeDialog = {
                            editSessionViewModel.updateSessionSettings(
                                sessionSettings.copy(timePickerEnabled = it)
                            )
                        },
                        selectDate = {
                            editSessionViewModel.updateSessionSettings(
                                sessionSettings.copy(
                                    date = String.format("%02d/%02d/%04d", it.dayOfMonth, it.monthValue, it.year),
                                    datePickerEnabled = false
                                )
                            )
                        },
                        selectTime = {
                            if (sessionSettings.isStartTime) {
                                editSessionViewModel.updateSessionSettings(
                                    sessionSettings.copy(
                                        startTime = Utils.toMilitaryTime(it.hour, it.minute),
                                        timePickerEnabled = false
                                    )
                                )
                            } else {
                                editSessionViewModel.updateSessionSettings(
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