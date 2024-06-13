package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.custom_composables.SimpleProgressIndicatorWithAnim
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.AchievementsViewModel

@Composable
fun Achievements(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    achievementsViewModel: AchievementsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        achievementsViewModel.getData()
    }

    val user by achievementsViewModel.drawerData.collectAsState()
    val process by achievementsViewModel.processState.collectAsState()
    val achievements by achievementsViewModel.achievements.collectAsState()
    val isRefreshLoading by achievementsViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { achievementsViewModel.refreshData() }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "Achievements",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "Achievements",
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
                topBarText = "Achievements",
                navController = navController,
                context = context,
                selected = Routes.ACHIEVEMENTS
            ) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = onRefresh,
                        refreshTriggerDistance = 50.dp
                    ) {
                        LazyColumn {
                            items(items = achievements.achievements) {
                                Row(
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(75.dp)
                                            .padding(5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            bitmap = Utils.convertToImage(achievements.badge),
                                            contentDescription = null,
                                            modifier = Modifier.matchParentSize()
                                        )
                                        Image(
                                            bitmap = Utils.convertToImage(it.icons),
                                            contentDescription = null,
                                            modifier = Modifier.size(25.dp)
                                        )
                                        if (it.progress >= 100) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .offset(20.dp, (-20).dp)
                                                    .size(30.dp)
                                                    .clip(MaterialTheme.shapes.medium)
                                                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                                                    .padding(7.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = it.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                                        )
                                        Text(
                                            text = it.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                                        )
                                        SimpleProgressIndicatorWithAnim(
                                            modifier = Modifier
                                                .padding(vertical = 15.dp)
                                                .fillMaxWidth()
                                                .height(10.dp),
                                            cornerRadius = 35.dp,
                                            thumbRadius = 1.dp,
                                            thumbOffset = 1.5.dp,
                                            progress = Utils.roundRating(it.progress / 100).toFloat(),
                                            progressBarColor = MaterialTheme.colorScheme.surfaceVariant
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
}