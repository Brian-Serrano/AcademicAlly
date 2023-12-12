package com.serrano.academically.custom_composables

import android.util.Log
import com.serrano.academically.ui.theme.Strings
import com.serrano.academically.utils.SearchInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    placeHolder: String,
    searchInfo: SearchInfo,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onTrailingIconClick: () -> Unit,
    onFilterClick: () -> Unit = {},
    isFilterButtonEnabled: Boolean = false
) {
    SearchBar(
        query = searchInfo.searchQuery,
        onQueryChange = onQueryChange,
        active = searchInfo.isActive,
        onSearch = onSearch,
        onActiveChange = onActiveChange,
        placeholder = {
            Text(
                text = placeHolder,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = Color.DarkGray
            )
        },
        trailingIcon = {
            Row {
                if (searchInfo.isActive) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.clickable(onClick = onTrailingIconClick)
                    )
                }
                if (isFilterButtonEnabled) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.clickable(onClick = onFilterClick)
                    )
                }
            }
        },
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
        colors = SearchBarDefaults.colors(
            containerColor = Color.White,
            dividerColor = Color.Gray,
            inputFieldColors = InputFieldColors()
        )
    ) {
        LazyColumn {
            items(items = searchInfo.history) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            onSearch(it)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}