package com.serrano.academically.custom_composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.serrano.academically.room.User
import com.serrano.academically.utils.DashboardIcons

@Composable
fun InputFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.background,
        unfocusedTextColor = MaterialTheme.colorScheme.background,
        disabledTextColor = MaterialTheme.colorScheme.background,
        errorTextColor = MaterialTheme.colorScheme.background,
        focusedContainerColor = MaterialTheme.colorScheme.onBackground,
        unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
        disabledContainerColor = MaterialTheme.colorScheme.onBackground,
        errorContainerColor = MaterialTheme.colorScheme.onBackground,
        cursorColor = MaterialTheme.colorScheme.background,
        errorCursorColor = MaterialTheme.colorScheme.background,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = MaterialTheme.colorScheme.background,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.background,
        disabledLeadingIconColor = MaterialTheme.colorScheme.background,
        errorLeadingIconColor = MaterialTheme.colorScheme.background,
        focusedTrailingIconColor = MaterialTheme.colorScheme.background,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.background,
        disabledTrailingIconColor = MaterialTheme.colorScheme.background,
        errorTrailingIconColor = MaterialTheme.colorScheme.background,
        focusedLabelColor = MaterialTheme.colorScheme.background,
        unfocusedLabelColor = MaterialTheme.colorScheme.background,
        disabledLabelColor = MaterialTheme.colorScheme.background,
        errorLabelColor = MaterialTheme.colorScheme.background,
        focusedPlaceholderColor = MaterialTheme.colorScheme.background,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.background,
        disabledPlaceholderColor = MaterialTheme.colorScheme.background,
        errorPlaceholderColor = MaterialTheme.colorScheme.background,
        focusedSupportingTextColor = MaterialTheme.colorScheme.background,
        unfocusedSupportingTextColor = MaterialTheme.colorScheme.background,
        disabledSupportingTextColor = MaterialTheme.colorScheme.background,
        errorSupportingTextColor = MaterialTheme.colorScheme.background,
        focusedPrefixColor = MaterialTheme.colorScheme.background,
        unfocusedPrefixColor = MaterialTheme.colorScheme.background,
        disabledPrefixColor = MaterialTheme.colorScheme.background,
        errorPrefixColor = MaterialTheme.colorScheme.background,
        focusedSuffixColor = MaterialTheme.colorScheme.background,
        unfocusedSuffixColor = MaterialTheme.colorScheme.background,
        disabledSuffixColor = MaterialTheme.colorScheme.background,
        errorSuffixColor =MaterialTheme.colorScheme.background
    )
}