package com.serrano.academically.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.serrano.academically.ui.theme.AcademicAllyPrototypeTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    date: LocalDate,
    time: LocalTime,
    datePickerEnabled: Boolean,
    timePickerEnabled: Boolean,
    updateDateDialog: (Boolean) -> Unit,
    updateTimeDialog: (Boolean) -> Unit,
    selectDate: (LocalDate) -> Unit,
    selectTime: (LocalTime) -> Unit
) {
    if (datePickerEnabled) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0x55000000)))

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        val dateMapper = { dps: DatePickerState ->
            Instant.ofEpochMilli(dps.selectedDateMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        DatePickerDialog(
            onDismissRequest = { updateDateDialog(false) },
            confirmButton = {
                BlackButton(
                    text = "OK",
                    action = {
                        selectDate(dateMapper(datePickerState))
                    }
                )
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.tertiary),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            dayContentColor = MaterialTheme.colorScheme.background,
                            weekdayContentColor = MaterialTheme.colorScheme.background,
                            todayContentColor = MaterialTheme.colorScheme.secondaryContainer,
                            todayDateBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                            dividerColor = MaterialTheme.colorScheme.background,
                            selectedDayContentColor = MaterialTheme.colorScheme.onBackground,
                            selectedDayContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            yearContentColor = MaterialTheme.colorScheme.background,
                            selectedYearContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            currentYearContentColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedYearContentColor = MaterialTheme.colorScheme.onBackground,
                            dateTextFieldColors = InputFieldColors()
                        )
                    )
                }
            }
        }
    }
    if (timePickerEnabled) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0x55000000)))

        val timePickerState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute
        )

        val timeMapper = { tps: TimePickerState ->
            LocalTime.of(tps.hour, tps.minute)
        }

        TimePickerDialog(
            onDismissRequest = { updateTimeDialog(false) },
            confirmButton = {
                BlackButton(
                    text = "OK",
                    action = {
                        selectTime(timeMapper(timePickerState))
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.tertiary),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.tertiaryContainer,
                        selectorColor = MaterialTheme.colorScheme.surface,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surface,
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onBackground,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}