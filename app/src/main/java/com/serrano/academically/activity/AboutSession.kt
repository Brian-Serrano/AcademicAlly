package com.serrano.academically.activity

import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.InfoCard
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AboutSessionViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.utils.toMilitaryTime
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutSession(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    sessionId: Int,
    navController: NavController,
    context: Context,
    aboutSessionViewModel: AboutSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        aboutSessionViewModel.getData(userId, sessionId, context)
    }

    val session by aboutSessionViewModel.sessionDetails.collectAsState()
    val session2 by aboutSessionViewModel.sessionInfo.collectAsState()
    val user by aboutSessionViewModel.userData.collectAsState()
    val process by aboutSessionViewModel.processState.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = Strings.aboutSession,
                navController = navController,
                context = context
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(it)
                        .verticalScroll(rememberScrollState())
                ) {
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Text_1(text = session2.courseName)
                        Box(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("Profile/${user.id}/${ if (user.role == "STUDENT") session.tutorId else session.studentId }")
                                }
                        ) {
                            Text_1(
                                text = if (user.role == "STUDENT") "Tutor: ${session2.tutorName}" else "Student: ${session2.studentName}"
                            )
                        }
                        Text_1(text = "Module: ${session2.moduleName}")
                    }
                    InfoCard(
                        title = "SCHEDULE",
                        description = "Date: ${session.startTime.month} ${session.startTime.dayOfMonth}, ${session.startTime.year}\nTime: ${toMilitaryTime(listOf(session.startTime.hour, session.startTime.minute))} - ${toMilitaryTime(listOf(session.endTime.hour, session.endTime.minute))}"
                    )
                    InfoCard(title = "LOCATION", description = session.location)

                    if (user.role == "TUTOR") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            BlackButton(text = "EDIT", action = { navController.navigate("EditSession/$userId/$sessionId") }, modifier = Modifier.padding(15.dp))
                        }
                    }
                }
            }
        }
    }
}