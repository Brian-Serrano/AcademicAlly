package thesis.academic.ally.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import thesis.academic.ally.api.CourseRating
import thesis.academic.ally.utils.Utils

@Composable
fun CoursesRating(courses: List<CourseRating>) {
    Text(
        text = "Courses Ratings",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(10.dp)
    )
    courses.forEach {
        HorizontalDivider(thickness = 2.dp)
        val rating =
            Utils.roundRating((it.assessmentRating / it.assessmentTaken) * 5)
        RatingCard(text = it.courseName, rating = rating)
    }
}