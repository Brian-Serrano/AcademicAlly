package com.serrano.academically.custom_composables

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
    onDateInputChange: (String) -> Unit,
    onStartTimeInputChange: (String) -> Unit,
    onEndTimeInputChange: (String) -> Unit,
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
    CustomInputField(
        inputName = "Date",
        input = sessionSettings.date,
        onInputChange = onDateInputChange,
        modifier = Modifier.padding(15.dp),
        supportingText = "Should be in dd/MM/yyyy format"
    )
    Text(
        text = "Start Time",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(15.dp)
    )
    CustomInputField(
        inputName = "Start Time",
        input = sessionSettings.startTime,
        onInputChange = onStartTimeInputChange,
        modifier = Modifier.padding(15.dp),
        supportingText = "Should be in hh:mm AM/PM format"
    )
    Text(
        text = "End Time",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(15.dp)
    )
    CustomInputField(
        inputName = "End Time",
        input = sessionSettings.endTime,
        onInputChange = onEndTimeInputChange,
        modifier = Modifier.padding(15.dp),
        supportingText = "Should be in hh:mm AM/PM format"
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