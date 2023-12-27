package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.utils.HelperFunctions

@Composable
fun CoursesRating(courses: List<Pair<CourseSkill, String>>) {
    Text(
        text = "Courses Ratings",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    courses.forEach {
        HorizontalDivider(color = Color.Black, thickness = 2.dp)
        val rating =
            HelperFunctions.roundRating((it.first.assessmentRating / it.first.assessmentTaken) * 5)
        RatingCard(text = it.second, rating = rating)
    }
}