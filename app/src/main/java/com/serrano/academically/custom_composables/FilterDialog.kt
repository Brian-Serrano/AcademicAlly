package com.serrano.academically.custom_composables

import com.serrano.academically.utils.FilterDialogStates
import com.serrano.academically.viewmodel.FindTutorViewModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import com.serrano.academically.ui.theme.Strings

@Composable
fun FilterDialog(
    courseNames: List<FilterDialogStates>,
    searchQuery: String,
    context: Context,
    findTutorViewModel: FindTutorViewModel
) {
    Dialog(onDismissRequest = { findTutorViewModel.toggleDialog(false) }) {
        LazyColumn(
            modifier = Modifier
                .size(300.dp, 500.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color = Color.White)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "Filter Tutor by Course",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                }

            }
            items(items = courseNames) { filter ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
                    Checkbox(
                        checked = filter.isEnabled,
                        onCheckedChange = {
                            findTutorViewModel.updateFilterState(
                                courseNames.mapIndexed { idx, fds ->
                                    if (idx + 1 == filter.id) fds.copy(isEnabled = it) else fds
                                }
                            )
                        }
                    )
                    Text(
                        text = filter.courseName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    BlackButton(
                        text = "APPLY",
                        action = {
                            findTutorViewModel.toggleDialog(false)
                            findTutorViewModel.updateTutorMenu(courseNames, searchQuery, context)
                        },
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }

}