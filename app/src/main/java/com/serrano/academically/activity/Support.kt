package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.ui.theme.AcademicAllyPrototypeTheme
import com.serrano.academically.utils.AboutText
import com.serrano.academically.utils.Routes
import kotlinx.coroutines.CoroutineScope

@Composable
fun Support(navController: NavController) {

    val topics = listOf(
        AboutText("Toxic User", "Users that do hacking, bad words in requests and their info, setting inappropriate schedule sessions and assignment deadlines or contents, etc."),
        AboutText("Bugs", "Such as crashing, can't open app, inappropriate navigation or events happening, etc."),
        AboutText("Using the App", "How to find tutor, how to change password, how to edit session, how to rate others after session, etc."),
        AboutText("Data loss", "Session is completed but it is still shown in notifications, student is accepted or rejected but message is still shown in notifications, taken assessment and eligible as student/tutor but doesn't added in list of courses, etc.")
    )

    ScaffoldNoDrawer(
        text = "SELECT PROBLEM TOPIC",
        navController = navController
    ) { values ->
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(values)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(topics.size) {
                    Column(
                        modifier = Modifier.clickable { navController.navigate("${Routes.SUPPORT_CHAT}/$it") }
                    ) {
                        Text(
                            text = topics[it].title,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 25.dp)
                        )
                        Text(
                            text = topics[it].description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 25.dp)
                        )
                    }
                    HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(horizontal = 15.dp))
                }
            }
        }
    }
}