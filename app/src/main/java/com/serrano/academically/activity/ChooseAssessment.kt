package com.serrano.academically.activity

import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.CustomSearchBar
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.TopBarNoDrawer
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SearchInfo
import com.serrano.academically.viewmodel.ChooseAssessmentViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.serrano.academically.custom_composables.TopBar
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
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            if (isDrawerShouldAvailable) {
                Drawer(
                    scope = scope,
                    drawerState = drawerState,
                    user = user,
                    navController = navController,
                    context = context
                ) {
                    Scaffold(
                        topBar = TopBar(
                            scope = scope,
                            drawerState = drawerState,
                            text = Strings.startAssess,
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
            }
            else {
                Scaffold(
                    topBar = TopBarNoDrawer(
                        text = Strings.startAssess,
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(padding)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomSearchBar(
                    placeHolder = "Choose Course",
                    searchInfo = search,
                    onQueryChange = { chooseAssessmentViewModel.updateSearch(search.copy(searchQuery = it)) },
                    onSearch = {
                        chooseAssessmentViewModel.updateSearch(search.copy(searchQuery = it, isActive = false))
                        chooseAssessmentViewModel.search(it, context)
                    },
                    onActiveChange = { chooseAssessmentViewModel.updateSearch(search.copy(isActive = it)) },
                    onTrailingIconClick = {
                        if (search.searchQuery.isEmpty()) {
                            chooseAssessmentViewModel.updateSearch(search.copy(isActive = false))
                        }
                        else {
                            chooseAssessmentViewModel.updateSearch(search.copy(searchQuery = ""))
                        }
                    }
                )
            }
            LazyColumn {
                items(items = courses) {
                    YellowCard(MaterialTheme.colorScheme.tertiary) {
                        Text_1(text = it[1])
                        Text_1(text = it[2])
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
}