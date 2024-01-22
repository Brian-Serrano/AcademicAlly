package com.serrano.academically.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.serrano.academically.R
import com.serrano.academically.custom_composables.MainButton

@Composable
fun Main(navController: NavController) {
    SelectionContainer {
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
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.student_choose),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .align(Alignment.Start)
                )
                Image(
                    painter = painterResource(id = R.drawable.tutor_choose),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .align(Alignment.End)
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(125.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "SIGN IN",
                    style = MaterialTheme.typography.headlineMedium
                )
                MainButton(
                    text = "AS A STUDENT",
                    route = "Login/STUDENT",
                    color = MaterialTheme.colorScheme.secondary,
                    navController = navController
                )
                MainButton(
                    text = "AS A TUTOR",
                    route = "Login/TUTOR",
                    color = MaterialTheme.colorScheme.primary,
                    navController = navController
                )
                Text(
                    text = "Not quite sure? Let us help you!",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.clickable { navController.navigate("ChooseAssessment") }
                )
            }
        }
    }
}