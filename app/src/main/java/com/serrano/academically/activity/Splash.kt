package com.serrano.academically.activity

import com.serrano.academically.R
import com.serrano.academically.datastore.UserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.ui.theme.Strings
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import kotlinx.coroutines.delay

@Composable
fun Splash(
    navController: NavController,
    context: Context
) {
    val userPref by context.dataStore.data.collectAsState(initial = UserPref())

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(3000)
    )
    LaunchedEffect(key1 = true) {

        startAnimation = true
        delay(4000)
        navController.popBackStack()

        when {
            userPref.isLoggedIn -> navController.navigate("Dashboard/${userPref.id}")
            userPref.isNotFirstTimeUser -> navController.navigate("Main")
            else -> navController.navigate("About")
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
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(450.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.splash_screen_vector),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(500.dp).offset(y = 300.dp)
        )
        Column(
            modifier = Modifier.fillMaxHeight().width(375.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = Strings.splashSchoolName,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = Strings.splashProjectName,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = Strings.splashProjectDesc,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}