package com.serrano.academically.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.serrano.academically.ui.theme.AcademicAllyPrototypeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AcademicAllyPrototypeTheme {
                NavigationGraph(
                    navController = rememberNavController(),
                    scope = rememberCoroutineScope(),
                    drawerState = rememberDrawerState(DrawerValue.Closed),
                    context = this
                )
            }
        }
    }
}