package com.serrano.academically.custom_composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serrano.academically.api.SessionNotifications
import com.serrano.academically.utils.Utils

@Composable
fun ScheduleBlueCard(
    session: SessionNotifications,
    sessionCourse: String,
    onArrowClick: () -> Unit,
    badge: @Composable (BoxScope.() -> Unit)
) {
    BadgedBox(badge = badge) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Utils.formatTime(Utils.convertToDate(session.startTime), Utils.convertToDate(session.endTime)),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                    )
                    Text(
                        text = sessionCourse,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 15.dp, bottom = 15.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .clickable(onClick = onArrowClick)
                )
            }
        }
    }
}