package thesis.academic.ally.activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import thesis.academic.ally.R
import thesis.academic.ally.custom_composables.CircularProgressBar
import thesis.academic.ally.custom_composables.GreenButton
import thesis.academic.ally.ui.theme.AcademicAllyPrototypeTheme
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.viewmodel.AssessmentResultViewModel

@Composable
fun AssessmentResult(
    isAuthorized: Boolean,
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

    val navBuilder: NavOptionsBuilder.() -> Unit = {
        popUpTo(navController.graph.id) {
            inclusive = false
        }
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
            Card(
                modifier = Modifier.padding(10.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(4.dp, Color.DarkGray)
            ) {
                Text(
                    text = "Assessment Completed",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.wreath),
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .offset(y = 20.dp)
                )
                if (eligibility == "TUTOR") {
                    Image(
                        painter = painterResource(id = R.drawable.three_stars),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .offset(y = (-120).dp)
                    )
                }
                CircularProgressBar(
                    percentage = score.toFloat() / items,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    radius = 80.dp,
                    animationPlayed = animationPlayed
                )
            }
            Card(
                modifier = Modifier.padding(10.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(4.dp, Color.DarkGray)
            ) {
                Text(
                    text = "You got a score of $score out of $items. You are eligible to be $eligibility for this course. This eligibility is only for this course assessment. It might change base on computation of your past and future assessments.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            if (isAuthorized) {
                GreenButton(
                    action = {
                        navController.navigate(Routes.DASHBOARD, navBuilder)
                    },
                    text = "CONTINUE",
                    style = MaterialTheme.typography.titleMedium,
                )
            } else {
                GreenButton(
                    action = {
                        navController.navigate("${Routes.SIGNUP}/STUDENT", navBuilder)
                    },
                    text = "STUDENT",
                    style = MaterialTheme.typography.bodyMedium
                )
                GreenButton(
                    action = {
                        navController.navigate("${Routes.SIGNUP}/TUTOR", navBuilder)
                    },
                    text = "TUTOR",
                    style = MaterialTheme.typography.bodyMedium,
                    clickable = eligibility == "TUTOR"
                )
            }
        }
    }
}