package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.serrano.academically.custom_composables.CustomSearchBar
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.TopBar
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SearchInfo
import com.serrano.academically.viewmodel.ChooseAssessmentViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun ChooseAssessment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    id: Int,
    navController: NavController,
    chooseAssessmentViewModel: ChooseAssessmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        chooseAssessmentViewModel.getData(context, id)
    }

    val search by chooseAssessmentViewModel.searchInfo.collectAsState()
    val process by chooseAssessmentViewModel.processState.collectAsState()
    val user by chooseAssessmentViewModel.drawerData.collectAsState()
    val courses by chooseAssessmentViewModel.courses.collectAsState()
    val isDrawerShouldAvailable by chooseAssessmentViewModel.isDrawerShouldAvailable.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "START ASSESSMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "START ASSESSMENT",
                navController = navController
            ) {
                Loading(it)
            }
        }

        ProcessState.Success -> {
            if (isDrawerShouldAvailable) {
                Drawer(
                    scope = scope,
                    drawerState = drawerState,
                    user = user,
                    navController = navController,
                    context = context,
                    selected = "Assessment"
                ) {
                    Scaffold(
                        topBar = TopBar(
                            scope = scope,
                            drawerState = drawerState,
                            text = "START ASSESSMENT",
                            navController = navController
                        )
                    ) {
                        ChooseAssessmentMenu(
                            id = id,
                            context = context,
                            navController = navController,
                            search = search,
                            courses = courses,
                            padding = it,
                            chooseAssessmentViewModel = chooseAssessmentViewModel
                        )
                    }
                }
            } else {
                ScaffoldNoDrawer(
                    text = "START ASSESSMENT",
                    navController = navController
                ) {
                    ChooseAssessmentMenu(
                        id = id,
                        context = context,
                        navController = navController,
                        search = search,
                        courses = courses,
                        padding = it,
                        chooseAssessmentViewModel = chooseAssessmentViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ChooseAssessmentMenu(
    id: Int,
    context: Context,
    navController: NavController,
    search: SearchInfo,
    courses: List<List<String>>,
    padding: PaddingValues,
    chooseAssessmentViewModel: ChooseAssessmentViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(padding)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            CustomSearchBar(
                placeHolder = "Choose Course",
                searchInfo = search,
                onQueryChange = { chooseAssessmentViewModel.updateSearch(search.copy(searchQuery = it)) },
                onSearch = {
                    chooseAssessmentViewModel.updateSearch(
                        search.copy(
                            searchQuery = it,
                            isActive = false
                        )
                    )
                    chooseAssessmentViewModel.search(it, context)
                },
                onActiveChange = { chooseAssessmentViewModel.updateSearch(search.copy(isActive = it)) },
                onTrailingIconClick = {
                    if (search.searchQuery.isEmpty()) {
                        chooseAssessmentViewModel.updateSearch(search.copy(isActive = false))
                    } else {
                        chooseAssessmentViewModel.updateSearch(search.copy(searchQuery = ""))
                    }
                }
            )
        }
        LazyColumn {
            items(items = courses) {
                CustomCard {
                    Text(
                        text = it[1],
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = it[2],
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                    BlackButton(
                        text = "SELECT",
                        action = { navController.navigate("AssessmentOption/$id/${it[0]}") },
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}