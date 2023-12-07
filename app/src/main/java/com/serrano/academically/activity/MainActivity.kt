package com.serrano.academically.activity

import com.serrano.academically.datastore.UserPrefSerializer
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.serrano.academically.ui.theme.AcademicAllyPrototypeTheme
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.datastore.dataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.LocalDate

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