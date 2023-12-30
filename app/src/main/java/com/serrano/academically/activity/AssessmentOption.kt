package com.serrano.academically.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.ScaffoldNoDrawer
import com.serrano.academically.custom_composables.TopBar
import com.serrano.academically.custom_composables.CustomCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AssessmentOptionViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AssessmentOption(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    id: Int,
    courseId: Int,
    navController: NavController,
    assessmentOptionViewModel: AssessmentOptionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assessmentOptionViewModel.getData(id, courseId, context)
    }

    val user by assessmentOptionViewModel.drawerData.collectAsState()
    val process by assessmentOptionViewModel.processState.collectAsState()
    val course by assessmentOptionViewModel.course.collectAsState()
    val isDrawerShouldAvailable by assessmentOptionViewModel.isDrawerShouldAvailable.collectAsState()
    val startEnabled by assessmentOptionViewModel.startButtonEnabled.collectAsState()

    val onClick = {
        assessmentOptionViewModel.saveAssessmentType(
            context = context,
            navigate = { item, type -> navController.navigate("Assessment/$id/$courseId/$item/$type") }
        )
    }

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
                        AssessmentOptionMenu(
                            course = course,
                            padding = it,
                            enabled = startEnabled,
                            onClick = onClick
                        )
                    }
                }
            } else {
                ScaffoldNoDrawer(
                    text = "START ASSESSMENT",
                    navController = navController
                ) {
                    AssessmentOptionMenu(
                        course = course,
                        padding = it,
                        enabled = startEnabled,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
fun AssessmentOptionMenu(
    course: Pair<String, String>,
    padding: PaddingValues,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        CustomCard {
            Text(
                text = "Course: ${course.first}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(10.dp)
            )
            HorizontalDivider(thickness = 2.dp)
            Text(
                text = "Description: ${course.second}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(10.dp)
            )
            HorizontalDivider(thickness = 2.dp)
            Row {
                GreenButton(
                    action = onClick,
                    text = "Start Assessment",
                    enabled = enabled
                )
            }
        }
    }
}