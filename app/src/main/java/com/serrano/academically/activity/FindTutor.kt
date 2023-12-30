package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.CustomSearchBar
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.FilterDialog
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingBar
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.FindTutorViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun FindTutor(
    scope: CoroutineScope,
    drawerState: DrawerState,
    userId: Int,
    navController: NavController,
    context: Context,
    findTutorViewModel: FindTutorViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        findTutorViewModel.getData(context, userId)
    }

    val search by findTutorViewModel.searchInfo.collectAsState()
    val tutors by findTutorViewModel.findTutorData.collectAsState()
    val user by findTutorViewModel.drawerData.collectAsState()
    val process by findTutorViewModel.processState.collectAsState()
    val dialogOpen by findTutorViewModel.isFilterDialogOpen.collectAsState()
    val filterState by findTutorViewModel.filterState.collectAsState()

    when (process) {
        ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "SEARCH TUTORS",
                navController = navController
            ) {
                ErrorComposable(navController, it)
            }
        }

        ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "SEARCH TUTORS",
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
                topBarText = "SEARCH TUTORS",
                navController = navController,
                context = context,
                selected = "FindTutor"
            ) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomSearchBar(
                                placeHolder = "Search Tutor",
                                searchInfo = search,
                                onQueryChange = {
                                    findTutorViewModel.updateSearch(
                                        search.copy(
                                            searchQuery = it
                                        )
                                    )
                                },
                                onSearch = {
                                    findTutorViewModel.updateSearch(
                                        search.copy(
                                            searchQuery = it,
                                            isActive = false
                                        )
                                    )
                                    findTutorViewModel.updateMenu(filterState, it, context, userId)
                                },
                                onActiveChange = {
                                    findTutorViewModel.updateSearch(
                                        search.copy(
                                            isActive = it
                                        )
                                    )
                                },
                                onTrailingIconClick = {
                                    if (search.searchQuery.isEmpty()) {
                                        findTutorViewModel.updateSearch(search.copy(isActive = false))
                                    } else {
                                        findTutorViewModel.updateSearch(search.copy(searchQuery = ""))
                                    }
                                },
                                onFilterClick = { findTutorViewModel.toggleDialog(true) },
                                isFilterButtonEnabled = true
                            )
                        }
                        LazyColumn {
                            items(items = tutors) {
                                CustomCard {
                                    Column {
                                        val avgRating =
                                            HelperFunctions.roundRating(it.rating.average())
                                        val performance =
                                            HelperFunctions.roundRating((if (it.performance.number > 0) it.performance.rating / it.performance.number else 0.0) * 5)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.AccountCircle,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .size(80.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "Name: ${it.tutorName}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                                Text(
                                                    text = "Courses: ${it.courses.joinToString(limit = 5)}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                                Text(
                                                    text = "Course Rating: $avgRating",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                                Text(
                                                    text = "Performance Rating: $performance",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                            }
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RatingBar(
                                                rating = HelperFunctions.roundRating((avgRating + performance) / 2)
                                                    .toFloat(),
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .height(20.dp)
                                            )
                                            BlackButton(
                                                text = "VIEW TUTOR",
                                                action = { navController.navigate("AboutTutor/${user.id}/${it.tutorId}") },
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .padding(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (dialogOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x55000000))
                        )
                        FilterDialog(
                            courseNames = filterState,
                            search = search,
                            context = context,
                            id = userId,
                            findTutorViewModel = findTutorViewModel
                        )
                    }
                }
            }
        }
    }
}