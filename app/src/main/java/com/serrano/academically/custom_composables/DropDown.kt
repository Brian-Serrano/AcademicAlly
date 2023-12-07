package com.serrano.academically.custom_composables

import com.serrano.academically.utils.DropDownState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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
            .background(Color.White)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = dropDownState.selected,
                modifier = Modifier.weight(1f).padding(10.dp),
                overflow= TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                color = Color.DarkGray,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier
                    .padding(10.dp)
                    .clickable(onClick = onArrowClick)
            )
        }
        DropdownMenu(
            expanded = dropDownState.expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(Color.White)
        ) {
            dropDownState.dropDownItems.forEach {
                DropdownMenuItem(
                    text = { Text_1(text = it) },
                    onClick = {
                        onItemSelect(it)
                    }
                )
            }
        }
    }
}