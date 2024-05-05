package thesis.academic.ally.activity

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
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import thesis.academic.ally.api.Course2
import thesis.academic.ally.custom_composables.BlackButton
import thesis.academic.ally.custom_composables.CustomSearchBar
import thesis.academic.ally.custom_composables.Drawer
import thesis.academic.ally.custom_composables.ErrorComposable
import thesis.academic.ally.custom_composables.Loading
import thesis.academic.ally.custom_composables.ScaffoldNoDrawer
import thesis.academic.ally.custom_composables.TopBar
import thesis.academic.ally.custom_composables.CustomCard
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Routes
import thesis.academic.ally.utils.SearchInfo
import thesis.academic.ally.viewmodel.ChooseAssessmentViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun ChooseAssessment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    navController: NavController,
    chooseAssessmentViewModel: ChooseAssessmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        chooseAssessmentViewModel.getData()
    }

    val search by chooseAssessmentViewModel.searchInfo.collectAsState()
    val process by chooseAssessmentViewModel.processState.collectAsState()
    val user by chooseAssessmentViewModel.drawerData.collectAsState()
    val courses by chooseAssessmentViewModel.coursesRender.collectAsState()
    val isAuthorized by chooseAssessmentViewModel.isAuthorized.collectAsState()
    val isRefreshLoading by chooseAssessmentViewModel.isRefreshLoading.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshLoading)
    val onRefresh = { chooseAssessmentViewModel.refreshData() }

    when (val p = process) {
        is ProcessState.Error -> {
            ScaffoldNoDrawer(
                text = "START ASSESSMENT",
                navController = navController
            ) {
                ErrorComposable(navController, it, p.message, swipeRefreshState, onRefresh)
            }
        }

        is ProcessState.Loading -> {
            ScaffoldNoDrawer(
                text = "START ASSESSMENT",
                navController = navController
            ) {
                Loading(it)
            }
        }

        is ProcessState.Success -> {
            if (isAuthorized) {
                Drawer(
                    scope = scope,
                    drawerState = drawerState,
                    user = user,
                    navController = navController,
                    context = context,
                    selected = Routes.ASSESSMENT
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
    navController: NavController,
    search: SearchInfo,
    courses: List<Course2>,
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
                    chooseAssessmentViewModel.search(it)
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
                        text = it.name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                    BlackButton(
                        text = "SELECT",
                        action = { navController.navigate("${Routes.ASSESSMENT_OPTION}/${it.id}") },
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}