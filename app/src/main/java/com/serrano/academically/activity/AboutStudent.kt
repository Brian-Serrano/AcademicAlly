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
import com.serrano.academically.viewmodel.AboutStudentViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import kotlinx.coroutines.CoroutineScope

@Composable
fun AboutStudent(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    messageId: Int,
    navController: NavController,
    context: Context,
    aboutStudentViewModel: AboutStudentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        aboutStudentViewModel.getData(userId, messageId, context)
    }

    val message by aboutStudentViewModel.messageDetails.collectAsState()
    val message2 by aboutStudentViewModel.messageInfo.collectAsState()
    val user by aboutStudentViewModel.userData.collectAsState()
    val userInfo by aboutStudentViewModel.userInfo.collectAsState()
    val process by aboutStudentViewModel.processState.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "ABOUT ${userInfo.name}",
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(80.dp)
                                )
                                Column {
                                    Box(modifier = Modifier.clickable { navController.navigate("Profile/${user.id}/${userInfo.id}") }) {
                                        Text_1(text = userInfo.name)
                                    }
                                    Text_1(text = message.studentMessage)
                                }
                            }
                            if (user.role == "TUTOR") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BlackButton(
                                        text = Strings.accept,
                                        action = {
                                            navController.navigate("CreateSession/$userId/${userInfo.id}/${message.courseId}/${message.moduleId}/$messageId")
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(65.dp)
                                            .padding(10.dp)
                                    )
                                    BlackButton(
                                        text = Strings.reject,
                                        action = {
                                            aboutStudentViewModel.respond("REJECT", messageId) { navController.navigateUp() }
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(65.dp)
                                            .padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                    InfoCard(title = "COURSE", description = message2.courseName)
                    InfoCard(title = "MODULE", description = message2.moduleName)
                    InfoCard(title = "AGE", description = userInfo.age.toString())
                    InfoCard(title = "DEGREE", description = userInfo.degree)
                    InfoCard(title = "ADDRESS", description = userInfo.address)
                    InfoCard(title = "CONTACT NUMBER", description = userInfo.contactNumber)
                    InfoCard(title = "SUMMARY", description = userInfo.summary)
                    InfoCard(title = "EDUCATIONAL BACKGROUND", description = userInfo.educationalBackground)
                }
            }
        }
    }
}