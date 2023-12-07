package com.serrano.academically.activity

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate

@Composable
fun NavigationGraph(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context
) {
    NavHost(navController = navController, startDestination = "Splash") {
        composable("Splash") {
            Splash(navController, context)
        }
        composable("Main") {
            Main(navController)
        }
        composable("About") {
            About(navController)
        }
        composable(
            route = "Signup/{user}/{courseId}/{score}/{items}/{eval}",
            arguments = listOf(
                navArgument("user") { type = NavType.StringType },
                navArgument("courseId") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("items") { type = NavType.IntType },
                navArgument("eval") { type = NavType.FloatType }
            )
        ) {
            Signup(
                navController = navController,
                user = it.arguments?.getString("user") ?: "STUDENT",
                context = context,
                courseId = it.arguments?.getInt("courseId") ?: 0,
                score = it.arguments?.getInt("score") ?: 0,
                items = it.arguments?.getInt("items") ?: 5,
                eval = it.arguments?.getFloat("eval") ?: 0F
            )
        }
        composable(
            route = "Login/{user}",
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) {
            Login(
                navController = navController,
                user = it.arguments?.getString("user") ?: "STUDENT",
                context = context
            )
        }
        composable(
            route = "Assessment/{id}/{courseId}/{items}/{type}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("courseId") { type = NavType.IntType },
                navArgument("items") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) {
            Assessment(
                scope = scope,
                drawerState = drawerState,
                context = context,
                id = it.arguments?.getInt("id") ?: 0,
                courseId = it.arguments?.getInt("courseId") ?: 0,
                items = it.arguments?.getString("items") ?: "5",
                type = it.arguments?.getString("type") ?: "Multiple Choice",
                navController = navController
            )
        }
        composable(
            route = "ChooseAssessment/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            ChooseAssessment(
                scope = scope,
                drawerState = drawerState,
                context = context,
                id = it.arguments?.getInt("id") ?: 0,
                navController = navController
            )
        }
        composable(
            route = "AssessmentOption/{id}/{courseId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("courseId") { type = NavType.IntType }
            )
        ) {
            AssessmentOption(
                scope = scope,
                drawerState = drawerState,
                context = context,
                id = it.arguments?.getInt("id") ?: 0,
                courseId = it.arguments?.getInt("courseId") ?: 0,
                navController = navController
            )
        }
        composable(
            route = "AssessmentResult/{id}/{courseId}/{score}/{items}/{eval}/{eligibility}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("courseId") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("items") { type = NavType.IntType },
                navArgument("eval") { type = NavType.FloatType },
                navArgument("eligibility") { type = NavType.StringType }
            )
        ) {
            AssessmentResult(
                id = it.arguments?.getInt("id") ?: 0,
                courseId = it.arguments?.getInt("courseId") ?: 0,
                score = it.arguments?.getInt("score") ?: 0,
                items = it.arguments?.getInt("items") ?: 5,
                eval = it.arguments?.getFloat("eval") ?: 0F,
                eligibility = it.arguments?.getString("eligibility") ?: "STUDENT",
                navController = navController
            )
        }
        composable(
            route = "Dashboard/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) {
            Dashboard(
                scope = scope,
                drawerState = drawerState,
                navController = navController,
                userId = it.arguments?.getInt("id") ?: 0,
                context = context
            )
        }
        composable(
            route = "CoursesMenu/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) {
            CoursesMenu(
                scope = scope,
                drawerState = drawerState,
                context = context,
                id = it.arguments?.getInt("id") ?: 0,
                navController = navController)
        }
        composable(
            route = "FindTutor/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            FindTutor(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "Notifications/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            Notifications(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "Profile/{id}/{otherId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("otherId") { type = NavType.IntType },
            )
        ) {
            Profile(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                otherId = it.arguments?.getInt("otherId") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "Leaderboard/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            Leaderboard(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "Analytics/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            Analytics(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "MessageTutor/{id}/{tutorId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("tutorId") { type = NavType.IntType }
            )
        ) {
            MessageTutor(
                scope = scope,
                drawerState = drawerState,
                context = context,
                navController = navController,
                userId = it.arguments?.getInt("id") ?: 0,
                tutorId = it.arguments?.getInt("tutorId") ?: 0
            )
        }
        composable(
            route = "CreateSession/{id}/{studentId}/{courseId}/{moduleId}/{messageId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("studentId") { type = NavType.IntType },
                navArgument("courseId") { type = NavType.IntType },
                navArgument("moduleId") { type = NavType.IntType },
                navArgument("messageId") { type = NavType.IntType }
            )
        ) {
            CreateSession(
                scope = scope,
                drawerState = drawerState,
                context = context,
                navController = navController,
                userId = it.arguments?.getInt("id") ?: 0,
                studentId = it.arguments?.getInt("studentId") ?: 0,
                courseId = it.arguments?.getInt("courseId") ?: 0,
                moduleId = it.arguments?.getInt("moduleId") ?: 0,
                messageId = it.arguments?.getInt("messageId") ?: 0
            )
        }
        composable(
            route = "EditSession/{id}/{sessionId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("sessionId") { type = NavType.IntType }
            )
        ) {
            EditSession(
                scope = scope,
                drawerState = drawerState,
                context = context,
                navController = navController,
                userId = it.arguments?.getInt("id") ?: 0,
                sessionId = it.arguments?.getInt("sessionId") ?: 0
            )
        }
        composable(
            route = "AboutSession/{id}/{sessionId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("sessionId") { type = NavType.IntType }
            )
        ) {
            AboutSession(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                sessionId = it.arguments?.getInt("sessionId") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "AboutStudent/{id}/{messageId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("messageId") { type = NavType.IntType }
            )
        ) {
            AboutStudent(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                messageId = it.arguments?.getInt("messageId") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "AboutTutor/{id}/{tutorId}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("tutorId") { type = NavType.IntType }
            )
        ) {
            AboutTutor(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                tutorId = it.arguments?.getInt("tutorId") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "Account/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            Account(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                navController = navController,
                context = context
            )
        }
        composable(
            route = "Achievements/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            Achievements(
                scope = scope,
                drawerState = drawerState,
                userId = it.arguments?.getInt("id") ?: 0,
                navController = navController,
                context = context
            )
        }
    }
}