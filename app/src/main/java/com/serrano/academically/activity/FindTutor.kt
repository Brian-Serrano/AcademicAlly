package com.serrano.academically.activity

import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.BlackButton
import com.serrano.academically.custom_composables.CustomSearchBar
import com.serrano.academically.custom_composables.DrawerAndScaffold
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.FilterDialog
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.RatingBar
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.FindTutorViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.utils.roundRating
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
        ProcessState.Error -> ErrorComposable(navController)
        ProcessState.Loading -> Loading()
        ProcessState.Success -> {
            DrawerAndScaffold(
                scope = scope,
                drawerState = drawerState,
                user = user,
                topBarText = Strings.searchTutor,
                navController = navController,
                context = context
            ) { values ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(values)
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
                                placeHolder = "Search Tutor",
                                searchInfo = search,
                                onQueryChange = { findTutorViewModel.updateSearch(search.copy(searchQuery = it)) },
                                onSearch = {
                                    findTutorViewModel.updateSearch(search.copy(searchQuery = it, isActive = false))
                                    findTutorViewModel.updateTutorMenu(filterState, it, context)
                                },
                                onActiveChange = { findTutorViewModel.updateSearch(search.copy(isActive = it)) },
                                onTrailingIconClick = {
                                    if (search.searchQuery.isEmpty()) {
                                        findTutorViewModel.updateSearch(search.copy(isActive = false))
                                    }
                                    else {
                                        findTutorViewModel.updateSearch(search.copy(searchQuery = ""))
                                    }
                                },
                                onFilterClick = { findTutorViewModel.toggleDialog(true) },
                                isFilterButtonEnabled = true
                            )
                        }
                        LazyColumn {
                            items(items = tutors) {
                                YellowCard(MaterialTheme.colorScheme.tertiary) {
                                    Column {
                                        val avgRating = roundRating(it.rating.average())
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.AccountCircle,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .size(80.dp)
                                            )
                                            Column {
                                                Text_1(text = "Name: ${it.tutorName}")
                                                Text_1(text = "Courses: ${it.courses.joinToString(limit = 5)}")
                                                Text_1(text = "Rating: $avgRating")
                                            }
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RatingBar(rating = avgRating.toFloat(), modifier = Modifier
                                                .padding(10.dp)
                                                .height(20.dp))
                                            BlackButton(
                                                text = Strings.viewTutor,
                                                action = { navController.navigate("AboutTutor/${user.id}/${it.tutorId}") },
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .height(65.dp)
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
                            modifier = Modifier.fillMaxSize().background(Color(0x55000000))
                        )
                        FilterDialog(
                            courseNames = filterState,
                            searchQuery = search.searchQuery,
                            context = context,
                            findTutorViewModel = findTutorViewModel
                        )
                    }
                }
            }
        }
    }
}