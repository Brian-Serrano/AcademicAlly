package com.serrano.academically.activity

import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.FiveButtons
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.LeaderboardViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import kotlinx.coroutines.CoroutineScope

@Composable
fun Leaderboard(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    navController: NavController,
    context: Context,
    leaderboardViewModel: LeaderboardViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        leaderboardViewModel.getData(userId)
    }

    val user by leaderboardViewModel.userDrawer.collectAsState()
    val leaderboard by leaderboardViewModel.leaderboardsData.collectAsState()
    val tab by leaderboardViewModel.tabIndex.collectAsState()
    val process by leaderboardViewModel.processState.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = Strings.leaderboard,
                navController = navController,
                context = context
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(it)
                ) {
                    YellowCard(MaterialTheme.colorScheme.secondary) {
                        FiveButtons(
                            texts = listOf("ALL", "ASSESSMENTS", "BADGES", "REQUESTS", "SESSIONS"),
                            actions = listOf(
                                { leaderboardViewModel.updateTabIndex(0) },
                                { leaderboardViewModel.updateTabIndex(1) },
                                { leaderboardViewModel.updateTabIndex(2) },
                                { leaderboardViewModel.updateTabIndex(3) },
                                { leaderboardViewModel.updateTabIndex(4) }
                            )
                        )
                    }
                    YellowCard(MaterialTheme.colorScheme.secondary) {
                        LazyColumn {
                            items(leaderboard[tab].size) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)
                                        .clickable { navController.navigate("Profile/${user.id}/${leaderboard[tab][it].id}") },
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = (it + 1).toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .width(30.dp)
                                            .fillMaxHeight()
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.AccountCircle,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .size(40.dp),
                                    )
                                    Text(
                                        text = leaderboard[tab][it].name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = leaderboard[tab][it].points.toString(),
                                        style = MaterialTheme.typography.bodyMedium
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