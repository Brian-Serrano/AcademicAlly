package com.serrano.academically.activity

import com.serrano.academically.R
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.CircularProgressBar
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.viewmodel.AssessmentResultViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun AssessmentResult(
    id: Int,
    courseId: Int,
    score: Int,
    items: Int,
    eval: Float,
    eligibility: String,
    navController: NavController,
    assessmentResultViewModel: AssessmentResultViewModel = hiltViewModel()
) {
    val error by assessmentResultViewModel.error.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.brushstroke),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(450.dp)
                .height(450.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = Strings.completedAssess,
                style = MaterialTheme.typography.displayMedium
            )
            CircularProgressBar(
                percentage = score.toFloat() / items,
                number = 100,
                color = MaterialTheme.colorScheme.surfaceVariant,
                radius = 75.dp
            )
            Text(
                text = "You got a score of $score out of $items. You are eligible to be $eligibility for this course. This eligibility is only for this course assessment. It might change base on computation of your past and future assessments.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            if (id == 0) {
                GreenButton(
                    action = { navController.navigate("Signup/$eligibility/$courseId/$score/$items/$eval") },
                    text = "Signup as $eligibility",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            else {
                GreenButton(
                    action = {
                        assessmentResultViewModel.updateCourseSkill(
                            userId = id,
                            courseId = courseId,
                            score = score,
                            items = items,
                            evaluator = eval,
                            navigate = { navController.navigate("Dashboard/$id") }
                        )
                    },
                    text = "CONTINUE",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}