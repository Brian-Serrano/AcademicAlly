package thesis.academic.ally.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import thesis.academic.ally.utils.FilterDialogStates
import thesis.academic.ally.utils.SearchInfo
import thesis.academic.ally.viewmodel.FindTutorViewModel

@Composable
fun FilterDialog(
    courseNames: List<FilterDialogStates>,
    search: SearchInfo,
    findTutorViewModel: FindTutorViewModel
) {
    Dialog(onDismissRequest = { findTutorViewModel.toggleDialog(false) }) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .size(300.dp, 500.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.onBackground),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Filter Tutor by Course",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(10.dp),
                    color = MaterialTheme.colorScheme.background
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    items(items = courseNames) { filter ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
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
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
                BlackButton(
                    text = "APPLY",
                    action = {
                        findTutorViewModel.toggleDialog(false)
                        findTutorViewModel.updateSearch(search.copy(isActive = false))
                        findTutorViewModel.updateMenu(courseNames, search.searchQuery)
                    },
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}