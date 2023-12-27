package com.serrano.academically.custom_composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ScaffoldNoDrawer(
    text: String,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    SelectionContainer {
        Scaffold(
            topBar = TopBarNoDrawer(
                text = text,
                navController = navController
            ),
            content = content
        )
    }
}