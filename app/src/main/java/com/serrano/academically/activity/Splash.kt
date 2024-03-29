package com.serrano.academically.activity

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.serrano.academically.R
import com.serrano.academically.datastore.UserCache
import com.serrano.academically.utils.Routes
import kotlinx.coroutines.delay

@Composable
fun Splash(
    navController: NavController,
    context: Context
) {
    val userCache by context.userDataStore.data.collectAsState(initial = UserCache())

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(3000),
        label = ""
    )
    LaunchedEffect(key1 = true) {

        startAnimation = true
        delay(4000)
        navController.popBackStack()

        when {
            userCache.authToken.isNotEmpty() -> navController.navigate(Routes.DASHBOARD)
            userCache.isNotFirstTimeUser -> navController.navigate(Routes.MAIN)
            else -> navController.navigate(Routes.ABOUT)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .alpha(alphaAnimation.value)
    ) {
        Image(
            painter = painterResource(id = R.drawable.brushstroke),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            painter = painterResource(id = R.drawable.splash_screen_vector),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "STI COLLEGE LAOAG, BSCS-4",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "ACADEMIC\nALLY",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "IMPROVING ACADEMIC PERFORMANCE\nTHROUGH STUDENT-TO-STUDENT TUTORING",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}