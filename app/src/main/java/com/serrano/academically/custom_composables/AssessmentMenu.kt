package com.serrano.academically.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    onAddAnswer: (String) -> Unit,
    nextButtonEnabled: Boolean
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
            val creatorIdx = if (type == "Multiple Choice") 7 else 3
            LazyRow {
                item { QuizInfo(name = "Course", value = course) }
                item { QuizInfo(name = "Items", value = items) }
                item { QuizInfo(name = "Type", value = type) }
                item { QuizInfo(name = "Module", value = assessmentData[item][0]) }
                item { QuizInfo(name = "Creator", value = assessmentData[item][creatorIdx]) }
            }
            SimpleProgressIndicatorWithAnim(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth()
                    .height(10.dp),
                cornerRadius = 35.dp,
                thumbRadius = 1.dp,
                thumbOffset = 1.5.dp,
                progress = item / items.toFloat(),
                progressBarColor = Color.Cyan
            )
            Text(
                text = "Item",
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
                        onAddAnswer = onAddAnswer
                    )

                    "Identification" -> Identification(
                        itemContent = assessmentData[item],
                        item = item,
                        answer = assessmentAnswers[item],
                        onAddAnswer = onAddAnswer
                    )

                    "True or False" -> TrueOrFalse(
                        itemContent = assessmentData[item],
                        item = item,
                        answer = assessmentAnswers[item],
                        onAddAnswer = onAddAnswer
                    )
                }
                Row {
                    GreenButton(
                        action = onBackButtonClick,
                        text = "Back"
                    )
                    GreenButton(
                        action = onNextButtonClick,
                        text = "Next",
                        enabled = nextButtonEnabled
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
    onAddAnswer: (String) -> Unit,
    choices: List<String> = listOf("A", "B", "C", "D")
) {
    YellowCard {
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
                    .clickable { onAddAnswer(choices[idx]) },
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
    onAddAnswer: (String) -> Unit
) {
    YellowCard {
        Text(
            text = "${item + 1}.) ${itemContent[1]}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(all = 20.dp)
        )
        LoginTextField(
            inputName = "Answer",
            input = answer,
            onInputChange = { onAddAnswer(it) },
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
    onAddAnswer: (String) -> Unit,
    choices: List<String> = listOf("TRUE", "FALSE")
) {
    YellowCard {
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
                    .clickable { onAddAnswer(choices[idx]) },
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
            .height(100.dp),
        shape = MaterialTheme.shapes.extraSmall
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