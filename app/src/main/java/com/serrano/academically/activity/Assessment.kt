package com.serrano.academically.activity

import android.app.Application
import com.serrano.academically.custom_composables.Drawer
import com.serrano.academically.custom_composables.ErrorComposable
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.custom_composables.GreenButton
import com.serrano.academically.custom_composables.Loading
import com.serrano.academically.custom_composables.LoginTextField
import com.serrano.academically.custom_composables.SimpleProgressIndicatorWithAnim
import com.serrano.academically.custom_composables.TopBarNoDrawer
import com.serrano.academically.custom_composables.YellowCard
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.viewmodel.AssessmentViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serrano.academically.custom_composables.TopBar
import kotlinx.coroutines.CoroutineScope

@Composable
fun Assessment(
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context,
    id: Int,
    courseId: Int,
    items: String,
    type: String,
    navController: NavController,
    assessmentViewModel: AssessmentViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        assessmentViewModel.getData(id, courseId, items.toInt(), type, context)
    }

    val user by assessmentViewModel.drawerData.collectAsState()
    val process by assessmentViewModel.processState.collectAsState()
    val course by assessmentViewModel.courseName.collectAsState()
    val assessmentData by assessmentViewModel.assessmentData.collectAsState()
    val assessmentAnswers by assessmentViewModel.assessmentAnswers.collectAsState()
    val item by assessmentViewModel.item.collectAsState()
    val isDrawerShouldAvailable by assessmentViewModel.isDrawerShouldAvailable.collectAsState()

    val onBackButtonClick = {
        if(item > 0) {
            assessmentViewModel.moveItem(false)
        }
    }
    val onNextButtonClick = {
        if(item < assessmentData.size - 1) {
            assessmentViewModel.moveItem(true)
        }
        else {
            val result = assessmentViewModel.evaluateAnswers(type)
            val eligibility = if (result.score.toFloat() / result.items >= result.evaluator) "TUTOR" else "STUDENT"
            navController.navigate("AssessmentResult/$id/$courseId/${result.score}/${result.items}/${result.evaluator}/$eligibility")
        }
    }

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
                            text = Strings.assess,
                            navController = navController
                        )
                    ) {
                        AssessmentMenu(
                            items = items,
                            type = type,
                            course = course,
                            item = item,
                            assessmentData = assessmentData,
                            assessmentAnswers = assessmentAnswers,
                            onBackButtonClick = onBackButtonClick,
                            onNextButtonClick = onNextButtonClick,
                            padding = it,
                            assessmentViewModel = assessmentViewModel
                        )
                    }
                }
            }
            else {
                Scaffold(
                    topBar = TopBarNoDrawer(
                        text = Strings.assess,
                        navController = navController
                    )
                ) {
                    AssessmentMenu(
                        items = items,
                        type = type,
                        course = course,
                        item = item,
                        assessmentData = assessmentData,
                        assessmentAnswers = assessmentAnswers,
                        onBackButtonClick = onBackButtonClick,
                        onNextButtonClick = onNextButtonClick,
                        padding = it,
                        assessmentViewModel = assessmentViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AssessmentMenu(
    items: String,
    type: String,
    course: String,
    item: Int,
    assessmentData: List<List<String>>,
    assessmentAnswers: List<String>,
    onBackButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit,
    padding: PaddingValues,
    assessmentViewModel: AssessmentViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow {
                item { QuizInfo(name = "Course", value = course) }
                item { QuizInfo(name = "Items", value = items) }
                item { QuizInfo(name = "Type", value = type) }
                item { QuizInfo(name = "Module", value = assessmentData[item][0]) }
            }
            SimpleProgressIndicatorWithAnim(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .height(10.dp), cornerRadius = 35.dp, thumbRadius = 1.dp, thumbOffset = 1.5.dp,
                progress = item / items.toFloat(),
                progressBarColor = Color.Cyan
            )
            Text(
                text = Strings.progress,
                style = MaterialTheme.typography.labelMedium
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                when (type) {
                    "Multiple Choice" -> MultipleChoice(
                        itemContent = assessmentData[item],
                        item = item,
                        answer = assessmentAnswers[item],
                        assessmentViewModel = assessmentViewModel
                    )
                    "Identification" -> Identification(
                        itemContent = assessmentData[item],
                        item = item,
                        answer = assessmentAnswers[item],
                        assessmentViewModel = assessmentViewModel
                    )
                    "True or False" -> TrueOrFalse(
                        itemContent = assessmentData[item],
                        item = item,
                        answer = assessmentAnswers[item],
                        assessmentViewModel = assessmentViewModel
                    )
                }
                Row {
                    GreenButton(
                        action = onBackButtonClick,
                        text = "Back"
                    )
                    GreenButton(
                        action = onNextButtonClick,
                        text = "Next"
                    )
                }
            }
        }
    }
}

@Composable
fun MultipleChoice(
    itemContent: List<String>,
    item: Int,
    answer: String,
    assessmentViewModel: AssessmentViewModel,
    choices: List<String> = listOf("A", "B", "C", "D")
) {
    YellowCard(MaterialTheme.colorScheme.tertiary) {
        Text(
            text = "${item + 1}.) ${itemContent[1]}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        for (idx in choices.indices) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        assessmentViewModel.addAnswer(choices[idx], item)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = choices[idx],
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(all = 10.dp)
                )
                Text(
                    text = itemContent[idx + 2],
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .weight(1f)
                )
                if (choices[idx] == answer) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(color = Color(0xFF177424))
                            .padding(7.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Identification(
    itemContent: List<String>,
    item: Int,
    answer: String,
    assessmentViewModel: AssessmentViewModel
) {
    YellowCard(MaterialTheme.colorScheme.tertiary) {
        Text(
            text = "${item + 1}.) ${itemContent[1]}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        LoginTextField(
            inputName = "Answer",
            input = answer,
            onInputChange = { assessmentViewModel.addAnswer(it, item) },
            modifier = Modifier.padding(20.dp),
            supportingText = "Answer length: ${itemContent[2].length}"
        )
    }
}

@Composable
fun TrueOrFalse(
    itemContent: List<String>,
    item: Int,
    answer: String,
    assessmentViewModel: AssessmentViewModel,
    choices: List<String> = listOf("TRUE", "FALSE")
) {
    YellowCard(MaterialTheme.colorScheme.tertiary) {
        Text(
            text = "${item + 1}.) ${itemContent[1]}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        for (idx in choices.indices) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        assessmentViewModel.addAnswer(choices[idx], item)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = choices[idx],
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .weight(1f)
                )
                if (choices[idx] == answer) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(color = Color(0xFF177424))
                            .padding(7.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuizInfo(name: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        modifier = Modifier
            .padding(all = 10.dp)
            .width(175.dp)
            .height(100.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp)
            )
        }
    }
}