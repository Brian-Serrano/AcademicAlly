package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
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
import thesis.academic.ally.custom_composables.CoursesList
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.CustomTab
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.RatingCard
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.ProfileViewModel

@Composable
fun Profile(
    scope: CoroutineScope,
    drawerState: DrawerState,
    otherId: Int,
    navController: NavController,
    context: Context,
    tabs: List<String> = listOf("AS STUDENT", "AS TUTOR"),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        profileViewModel.getData(otherId)
    }

    val user by profileViewModel.drawerData.collectAsState()
    val profile by profileViewModel.userData.collectAsState()
    val process by profileViewModel.processState.collectAsState()
    val tabIndex by profileViewModel.tabIndex.collectAsState()
    val isRefreshLoading by profileViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { profileViewModel.refreshData(otherId) }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "",
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
                topBarText = "",
                navController = navController,
                context = context,
                selected = Routes.DASHBOARD
            ) { values ->
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = onRefresh,
                    refreshTriggerDistance = 50.dp,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxSize()
                        .padding(values)
                ) {
                    LazyColumn {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val isTutorAvailable = Utils.isTutorAvailable(profile.user.freeTutoringTime)
                                Image(
                                    bitmap = Utils.convertToImage(profile.user.image),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                )
                                Text(
                                    text = profile.user.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    text = if (isTutorAvailable) "Available" else "Not Available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isTutorAvailable) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = profile.user.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    text = "Current Role: ${profile.user.role}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    text = "Program: ${profile.user.degree}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    text = "Age: ${profile.user.age}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    text = "Address: ${profile.user.address}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    text = "Contact Number: ${profile.user.contactNumber}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    text = "${profile.user.name} is ${if (profile.primaryLearning == user.primaryLearning) "also" else "not"} ${user.primaryLearning}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (profile.primaryLearning == user.primaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "${profile.user.name} is ${if (profile.secondaryLearning == user.secondaryLearning) "also" else "not"} ${user.secondaryLearning}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (profile.secondaryLearning == user.secondaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        item {
                            CustomTab(
                                tabIndex = tabIndex,
                                tabs = tabs,
                                onTabClick = { profileViewModel.updateTabIndex(it) }
                            )
                        }
                        item {
                            CustomCard {
                                Text(
                                    text = "Summary: ${profile.user.summary}",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                                Text(
                                    text = "Educational Background: ${profile.user.educationalBackground}",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                                Text(
                                    text = "Free Tutoring Time:\n${Utils.formatTutoringAvailability(profile.user.freeTutoringTime)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                        item {
                            CustomCard {
                                RatingCard(
                                    text = "Performance Rating",
                                    rating = profile.rating.map { Utils.roundRating((if (it.rateNumber > 0) it.rating / it.rateNumber else 0.0) * 5) }[tabIndex]
                                )
                            }
                        }
                        item {
                            CustomCard {
                                CoursesList(profile.courses[tabIndex])
                            }
                        }
                        if (tabIndex == 1 && user.id != otherId && profile.courses[tabIndex].isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    BlackButton(
                                        text = "CONTACT TUTOR",
                                        action = { navController.navigate("${Routes.MESSAGE_TUTOR}/$otherId") },
                                        modifier = Modifier.padding(20.dp),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}