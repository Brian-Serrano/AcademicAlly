package com.serrano.academically.activity

import com.serrano.academically.R
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.MainButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Main(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .weight(1f)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondary)
                    .weight(1f)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.student_choose),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset(x = (-100).dp, y = (-150).dp)
                .width(450.dp)
                .height(450.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.tutor_choose),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset(x = 100.dp, y = 100.dp)
                .width(450.dp)
                .height(450.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(125.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = Strings.signIn,
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
            MainButton(
                text = Strings.student,
                route = "Login/STUDENT",
                color = MaterialTheme.colorScheme.secondary,
                navController = navController
            )
            MainButton(
                text = Strings.tutor,
                route = "Login/TUTOR",
                color = MaterialTheme.colorScheme.primary,
                navController = navController
            )
            Text(
                text = Strings.notSure,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.clickable { navController.navigate("ChooseAssessment/0") }
            )
        }
    }
}