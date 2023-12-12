package com.serrano.academically.activity

import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.SimpleProgressIndicator
import com.serrano.academically.custom_composables.SimpleProgressIndicatorWithAnim
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AchievementsViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.utils.roundRating
import kotlinx.coroutines.CoroutineScope

@Composable
fun Achievements(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    navController: NavController,
    context: Context,
    achievementsViewModel: AchievementsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        achievementsViewModel.getData(userId, context)
    }

    val user by achievementsViewModel.userData.collectAsState()
    val process by achievementsViewModel.processState.collectAsState()
    val achievementData by achievementsViewModel.achievements.collectAsState()
    val achievementProgress by achievementsViewModel.achievementsProgress.collectAsState()

    when (process) {
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = Strings.achievements,
                navController = navController,
                context = context
            ) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    LazyColumn {
                        items(achievementProgress.size) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(60.dp).padding(10.dp)
                                )
                                Column {
                                    Text(
                                        text = achievementData[it][0],
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Black,
                                        modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                                    )
                                    Text(
                                        text = achievementData[it][1],
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Black,
                                        modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)
                                    )
                                    SimpleProgressIndicatorWithAnim(
                                        modifier = Modifier
                                            .padding(vertical = 15.dp)
                                            .fillMaxWidth()
                                            .height(10.dp), cornerRadius = 35.dp, thumbRadius = 1.dp, thumbOffset = 1.5.dp,
                                        progress = roundRating(achievementProgress[it] / 100).toFloat(),
                                        progressBarColor = Color.Cyan
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