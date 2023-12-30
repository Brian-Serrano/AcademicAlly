package com.serrano.academically.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.serrano.academically.R
import com.serrano.academically.custom_composables.CircularProgressBar
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.viewmodel.AssessmentResultViewModel

@Composable
fun AssessmentResult(
    id: Int,
    score: Int,
    items: Int,
    eligibility: String,
    navController: NavController,
    assessmentResultViewModel: AssessmentResultViewModel = hiltViewModel()
) {
    val animationPlayed by assessmentResultViewModel.animationPlayed.collectAsState()

    LaunchedEffect(Unit) {
        assessmentResultViewModel.playAnimation()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.brushstroke),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Assessment Completed",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            CircularProgressBar(
                percentage = score.toFloat() / items,
                color = MaterialTheme.colorScheme.surfaceVariant,
                radius = 100.dp,
                animationPlayed = animationPlayed
            )
            Text(
                text = "You got a score of $score out of $items. You are eligible to be $eligibility for this course. This eligibility is only for this course assessment. It might change base on computation of your past and future assessments.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            if (id == 0) {
                GreenButton(
                    action = { navController.navigate("Signup/STUDENT") },
                    text = "STUDENT",
                    style = MaterialTheme.typography.titleMedium
                )
                GreenButton(
                    action = { navController.navigate("Signup/TUTOR") },
                    text = "TUTOR",
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                GreenButton(
                    action = { navController.navigate("Dashboard/$id") },
                    text = "CONTINUE",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}