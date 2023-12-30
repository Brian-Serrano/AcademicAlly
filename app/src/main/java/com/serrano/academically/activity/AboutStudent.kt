package com.serrano.academically.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.InfoCard
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AboutStudentViewModel
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
    val user by aboutStudentViewModel.userData.collectAsState()
    val process by aboutStudentViewModel.processState.collectAsState()
    val rejectEnabled by aboutStudentViewModel.rejectButtonEnabled.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ABOUT STUDENT",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ABOUT STUDENT",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "ABOUT ${message.third.name}",
                navController = navController,
                context = context,
                selected = "Notifications"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(it)
                        .verticalScroll(rememberScrollState())
                ) {
                    CustomCard {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(80.dp)
                                )
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .clickable { navController.navigate("Profile/${user.id}/${message.third.id}") }
                                    ) {
                                        Text(
                                            text = message.third.name,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                    Text(
                                        text = message.first.studentMessage,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                            if (user.role == "TUTOR") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BlackButton(
                                        text = "ACCEPT",
                                        action = {
                                            navController.navigate("CreateSession/$userId/$messageId")
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(10.dp)
                                    )
                                    BlackButton(
                                        text = "REJECT",
                                        action = {
                                            aboutStudentViewModel.respond(
                                                studentId = message.first.studentId,
                                                tutorId = message.first.tutorId,
                                                status = "REJECT",
                                                messageId = messageId,
                                                context = context,
                                                navigate = {
                                                    Toast.makeText(
                                                        context,
                                                        "Student Rejected!",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    navController.navigateUp()
                                                }
                                            )
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(10.dp),
                                        enabled = rejectEnabled
                                    )
                                }
                            }
                        }
                    }

                    val titles = listOf(
                        "COURSE",
                        "MODULE",
                        "AGE",
                        "PROGRAM",
                        "ADDRESS",
                        "CONTACT NUMBER",
                        "SUMMARY",
                        "EDUCATIONAL BACKGROUND"
                    )
                    val descriptions = listOf(
                        message.second.courseName,
                        message.second.moduleName,
                        message.third.age.toString(),
                        message.third.degree,
                        message.third.address,
                        message.third.contactNumber,
                        message.third.summary,
                        message.third.educationalBackground
                    )

                    titles.forEachIndexed { index, title ->
                        InfoCard(title = title, description = descriptions[index])
                    }
                }
            }
        }
    }
}