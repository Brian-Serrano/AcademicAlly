package com.serrano.academically.custom_composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serrano.academically.utils.SessionSettings

@Composable
fun EditSessionMenu(
    sessionSettings: SessionSettings,
    openDateDialog: () -> Unit,
    openStartTimeDialog: () -> Unit,
    openEndTimeDialog: () -> Unit,
    onLocationInputChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    buttonText: String,
    enabled: Boolean
) {
    Text(
        text = "Date",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(15.dp)
    )
    DateTimeBox(
        text = sessionSettings.date,
        action = openDateDialog,
        modifier = Modifier.padding(15.dp)
    )
    Text(
        text = "Start Time",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(15.dp)
    )
    DateTimeBox(
        text = sessionSettings.startTime,
        action = openStartTimeDialog,
        modifier = Modifier.padding(15.dp)
    )
    Text(
        text = "End Time",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(15.dp)
    )
    DateTimeBox(
        text = sessionSettings.endTime,
        action = openEndTimeDialog,
        modifier = Modifier.padding(15.dp)
    )
    Text(
        text = "Location",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(15.dp)
    )
    CustomInputField(
        inputName = "Location",
        input = sessionSettings.location,
        onInputChange = onLocationInputChange,
        modifier = Modifier.padding(15.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        BlackButton(
            text = buttonText,
            action = onButtonClick,
            modifier = Modifier.padding(15.dp),
            enabled = enabled
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = sessionSettings.error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(15.dp)
        )
    }
}