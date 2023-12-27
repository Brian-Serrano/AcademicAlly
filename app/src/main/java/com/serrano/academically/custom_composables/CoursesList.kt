package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CoursesList(courses: List<Pair<String, String>>) {
    Text(
        text = "Courses",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(20.dp)
    )
    courses.forEach {
        Text(
            text = it.first,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = it.second,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(20.dp)
        )
    }
}