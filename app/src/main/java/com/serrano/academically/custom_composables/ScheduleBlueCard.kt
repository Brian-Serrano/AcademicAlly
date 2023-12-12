package com.serrano.academically.custom_composables

import com.serrano.academically.utils.SessionNotifications
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.serrano.academically.utils.toMilitaryTime

@Composable
fun ScheduleBlueCard(
    session: SessionNotifications,
    sessionCourse: String,
    onArrowClick: () -> Unit
) {
    YellowCard(MaterialTheme.colorScheme.primaryContainer) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${toMilitaryTime(listOf(session.startTime.hour, session.startTime.minute))} - ${toMilitaryTime(listOf(session.endTime.hour, session.endTime.minute))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                )
                Text(
                    text = sessionCourse,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(35.dp)
                    .clickable(onClick = onArrowClick)
            )
        }
    }
}