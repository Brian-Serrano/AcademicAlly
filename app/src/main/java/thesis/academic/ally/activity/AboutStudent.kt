package thesis.academic.ally.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.InfoCard
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.AboutStudentViewModel

@Composable
fun AboutStudent(
    scope: CoroutineScope,
    drawerState: DrawerState,
    messageId: Int,
    navController: NavController,
    context: Context,
    aboutStudentViewModel: AboutStudentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        aboutStudentViewModel.getData(messageId)
    }

    val message by aboutStudentViewModel.message.collectAsState()
    val user by aboutStudentViewModel.drawerData.collectAsState()
    val process by aboutStudentViewModel.processState.collectAsState()
    val rejectEnabled by aboutStudentViewModel.rejectButtonEnabled.collectAsState()
    val isRefreshLoading by aboutStudentViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { aboutStudentViewModel.refreshData(messageId) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "ABOUT STUDENT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "ABOUT STUDENT",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = "ABOUT ${message.name}",
                navController = navController,
                context = context,
                selected = Routes.NOTIFICATIONS
            ) {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = onRefresh,
                    refreshTriggerDistance = 50.dp,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        CustomCard {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        bitmap = Utils.convertToImage(message.image),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(40.dp))
                                    )
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .clickable { navController.navigate("${Routes.PROFILE}/${message.userId}") }
                                        ) {
                                            Text(
                                                text = message.name,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                        }
                                        Text(
                                            text = message.studentMessage,
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
                                                navController.navigate("${Routes.CREATE_SESSION}/$messageId")
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
                                                    studentId = message.studentId,
                                                    tutorId = message.tutorId,
                                                    messageId = messageId,
                                                    navigate = {
                                                        Toast.makeText(
                                                            context,
                                                            "Student Rejected!",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        navController.popBackStack()
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

                        CustomCard {
                            Text(
                                text = "Learning Pattern",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                            HorizontalDivider(thickness = 2.dp)
                            Text(
                                text = "${message.name} is ${if (message.primaryLearning == user.primaryLearning) "also" else "not"} ${user.primaryLearning}.",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp),
                                color = if (message.primaryLearning == user.primaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "${message.name} is ${if (message.secondaryLearning == user.secondaryLearning) "also" else "not"} ${user.secondaryLearning}.",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(10.dp),
                                color = if (message.secondaryLearning == user.secondaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                            )
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
                            message.courseName,
                            message.moduleName,
                            message.age.toString(),
                            message.degree,
                            message.address,
                            message.contactNumber,
                            message.summary,
                            message.educationalBackground
                        )

                        titles.forEachIndexed { index, title ->
                            InfoCard(title = title, description = descriptions[index])
                        }
                    }
                }
            }
        }
    }
}