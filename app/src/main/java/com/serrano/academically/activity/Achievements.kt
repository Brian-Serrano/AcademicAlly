package com.serrano.academically.activity

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.R
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.SimpleProgressIndicatorWithAnim
import com.serrano.academically.ui.theme.AcademicAllyPrototypeTheme
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AchievementsViewModel
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
    val achievementImages by achievementsViewModel.achievementImages.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "Achievements",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "Achievements",
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
                topBarText = "Achievements",
                navController = navController,
                context = context,
                selected = "Badge"
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
                                Box(
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(5.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.badge),
                                        contentDescription = null,
                                        modifier = Modifier.matchParentSize()
                                    )
                                    Image(
                                        painter = painterResource(id = achievementImages[it]),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                    if (achievementProgress[it] >= 100) {
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
                                        text = achievementData[it][0],
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(start = 5.dp, top = 5.dp)
                                    )
                                    Text(
                                        text = achievementData[it][1],
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
                                        progress = HelperFunctions.roundRating(achievementProgress[it] / 100)
                                            .toFloat(),
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

@Preview(showBackground = true)
@Composable
fun PrevTest() {
    AcademicAllyPrototypeTheme {
        LazyColumn {
            items((1..15).toList()) {
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.badge),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize()
                    )
                    Image(
                        painter = painterResource(id = R.drawable.yes),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
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
        }
    }
}