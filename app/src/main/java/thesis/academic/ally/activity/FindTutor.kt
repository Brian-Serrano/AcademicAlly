package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.custom_composables.CustomSearchBar
import thesis.academic.ally.custom_composables.DrawerAndScaffold
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.FilterDialog
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.RatingBar
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.viewmodel.FindTutorViewModel

@Composable
fun FindTutor(
    scope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    context: Context,
    findTutorViewModel: FindTutorViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        findTutorViewModel.getData()
    }

    val search by findTutorViewModel.searchInfo.collectAsState()
    val tutors by findTutorViewModel.findTutorData.collectAsState()
    val user by findTutorViewModel.drawerData.collectAsState()
    val process by findTutorViewModel.processState.collectAsState()
    val dialogOpen by findTutorViewModel.isFilterDialogOpen.collectAsState()
    val filterState by findTutorViewModel.filterState.collectAsState()
    val isRefreshLoading by findTutorViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { findTutorViewModel.refreshData() }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "SEARCH TUTORS",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "SEARCH TUTORS",
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
                topBarText = "SEARCH TUTORS",
                navController = navController,
                context = context,
                selected = Routes.DASHBOARD
            ) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(values)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                                    findTutorViewModel.updateMenu(filterState, it)
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
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = onRefresh,
                            refreshTriggerDistance = 50.dp
                        ) {
                            if (tutors.tutors.isNotEmpty()) {
                                LazyColumn {
                                    items(items = tutors.tutors) {
                                        CustomCard {
                                            Column {
                                                val avgRating = Utils.roundRating(it.coursesAndRatings.map { it.courseRating }.average())
                                                val performance = Utils.roundRating((if (it.performance.rateNumber > 0) it.performance.rating / it.performance.rateNumber else 0.0) * 5)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    BadgedBox(badge = {
                                                        Badge(
                                                            modifier = Modifier
                                                                .size(20.dp)
                                                                .offset((-30).dp, (65).dp),
                                                            containerColor = if (it.isAvailable) Color.Green else Color.Red
                                                        )
                                                    }) {
                                                        Image(
                                                            bitmap = Utils.convertToImage(it.image),
                                                            contentDescription = null,
                                                            modifier = Modifier
                                                                .padding(10.dp)
                                                                .size(80.dp)
                                                                .clip(RoundedCornerShape(40.dp))
                                                        )
                                                    }
                                                    Column {
                                                        Text(
                                                            text = "Name: ${it.tutorName}",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            modifier = Modifier.padding(10.dp)
                                                        )
                                                        Text(
                                                            text = "Courses: ${
                                                                it.coursesAndRatings.joinToString(
                                                                    limit = 5
                                                                ) { it.courseName }
                                                            }",
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
                                                        Text(
                                                            text = "${it.tutorName} is ${if (it.primaryPattern == user.primaryLearning) "also" else "not"} ${user.primaryLearning}.",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            modifier = Modifier.padding(10.dp),
                                                            color = if (it.primaryPattern == user.primaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                                                        )
                                                        Text(
                                                            text = "${it.tutorName} is ${if (it.secondaryPattern == user.secondaryLearning) "also" else "not"} ${user.secondaryLearning}.",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            modifier = Modifier.padding(10.dp),
                                                            color = if (it.secondaryPattern == user.secondaryLearning) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.error
                                                        )
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    RatingBar(
                                                        rating = Utils.roundRating((avgRating + performance) / 2).toFloat(),
                                                        modifier = Modifier
                                                            .padding(10.dp)
                                                            .height(20.dp)
                                                    )
                                                    BlackButton(
                                                        text = "VIEW TUTOR",
                                                        action = { navController.navigate("${Routes.ABOUT_TUTOR}/${it.tutorId}") },
                                                        style = MaterialTheme.typography.labelMedium,
                                                        modifier = Modifier.padding(10.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // To make swipe refresh work
                                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                    Spacer(modifier = Modifier.padding(100.dp))
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
                            findTutorViewModel = findTutorViewModel
                        )
                    }
                }
            }
        }
    }
}