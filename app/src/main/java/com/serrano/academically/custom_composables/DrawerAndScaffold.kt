package com.serrano.academically.custom_composables

import com.serrano.academically.room.User
import com.serrano.academically.utils.UserDrawerData
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun DrawerAndScaffold(
    scope: CoroutineScope,
    drawerState: DrawerState,
    user: UserDrawerData,
    topBarText: String,
    navController: NavController,
    context: Context,
    content: @Composable (PaddingValues) -> Unit
) {
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
                text = topBarText,
                navController = navController
            ),
            content = content
        )
    }
}