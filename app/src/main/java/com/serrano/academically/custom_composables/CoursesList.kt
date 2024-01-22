package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serrano.academically.api.Course

@Composable
fun CoursesList(courses: List<Course>) {
    Text(
        text = "Courses",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(20.dp)
    )
    courses.forEach {
        Text(
            text = it.name,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = it.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(20.dp)
        )
    }
}