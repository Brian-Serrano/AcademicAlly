package thesis.academic.ally.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import thesis.academic.ally.utils.DropDownState

@Composable
fun DropDown(
    dropDownState: DropDownState,
    onArrowClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onItemSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .height(60.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onArrowClick)
        ) {
            Text(
                text = dropDownState.selected,
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.background,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(10.dp)
            )
        }
        DropdownMenu(
            expanded = dropDownState.expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
        ) {
            dropDownState.dropDownItems.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.padding(10.dp)
                        )
                    },
                    onClick = {
                        onItemSelect(it)
                    }
                )
            }
        }
    }
}