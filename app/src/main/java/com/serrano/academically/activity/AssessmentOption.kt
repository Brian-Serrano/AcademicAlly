package com.serrano.academically.activity

import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.Divider
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.DropDown
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.Text_1
import com.serrano.academically.custom_composables.TopBarNoDrawer
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AssessmentOptionViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serrano.academically.custom_composables.TopBar
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
    val itemsDropdown by assessmentOptionViewModel.itemsDropdown.collectAsState()
    val typeDropdown by assessmentOptionViewModel.typeDropdown.collectAsState()

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
                        AssessmentOptionMenu(
                            id = id,
                            courseId = courseId,
                            navController = navController,
                            course = course,
                            itemsDropdown = itemsDropdown,
                            typeDropdown = typeDropdown,
                            padding = it,
                            assessmentOptionViewModel = assessmentOptionViewModel
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
                    AssessmentOptionMenu(
                        id = id,
                        courseId = courseId,
                        navController = navController,
                        course = course,
                        itemsDropdown = itemsDropdown,
                        typeDropdown = typeDropdown,
                        padding = it,
                        assessmentOptionViewModel = assessmentOptionViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AssessmentOptionMenu(
    id: Int,
    courseId: Int,
    navController: NavController,
    course: Pair<String, String>,
    itemsDropdown: DropDownState,
    typeDropdown: DropDownState,
    padding: PaddingValues,
    assessmentOptionViewModel: AssessmentOptionViewModel
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
        YellowCard(MaterialTheme.colorScheme.tertiary) {
            Text_1(text = "Course: ${course.first}")
            Divider()
            Text_1(text = "Description: ${course.second}")
            Divider()
            Text_1(text = "Items")
            DropDown(
                dropDownState = itemsDropdown,
                onArrowClick = { assessmentOptionViewModel.updateItemsDropdown(itemsDropdown.copy(expanded = true)) },
                onDismissRequest = { assessmentOptionViewModel.updateItemsDropdown(itemsDropdown.copy(expanded = false)) },
                onItemSelect = { assessmentOptionViewModel.updateItemsDropdown(itemsDropdown.copy(selected = it, expanded = false)) }
            )
            Divider()
            Text_1(text = "Type")
            DropDown(
                dropDownState = typeDropdown,
                onArrowClick = { assessmentOptionViewModel.updateTypeDropdown(typeDropdown.copy(expanded = true)) },
                onDismissRequest = { assessmentOptionViewModel.updateTypeDropdown(typeDropdown.copy(expanded = false)) },
                onItemSelect = { assessmentOptionViewModel.updateTypeDropdown(typeDropdown.copy(selected = it, expanded = false)) }
            )
            Divider()
            Row {
                GreenButton(
                    action = { navController.navigate("Assessment/$id/$courseId/${itemsDropdown.selected}/${typeDropdown.selected}") },
                    text = "Start Assessment"
                )
            }
        }
    }
}