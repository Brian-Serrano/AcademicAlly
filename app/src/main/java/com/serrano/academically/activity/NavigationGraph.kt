package com.serrano.academically.activity

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavigationGraph(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context
) {
    NavHost(navController = navController, startDestination = "Splash") {
        composable(route = "Splash") {
            Splash(navController, context)
        }
        composable(route = "Main") {
            Main(navController)
        }
        composable(route = "About") {
            About(navController)
        }
        composable(
            route = "Signup/{user}",
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) {
            Signup(navController, it.arguments?.getString("user") ?: "STUDENT", context)
        }
        composable(
            route = "Login/{user}",
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) {
            Login(navController, it.arguments?.getString("user") ?: "STUDENT", context)
        }
        composable(
            route = "Assessment/{courseId}/{items}/{type}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.IntType },
                navArgument("items") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) {
            Assessment(
                scope = scope,
                drawerState = drawerState,
                context = context,
                courseId = it.arguments?.getInt("courseId") ?: 0,
                items = it.arguments?.getString("items") ?: "5",
                type = it.arguments?.getString("type") ?: "Multiple Choice",
                navController = navController
            )
        }
        composable(route = "ChooseAssessment") {
            ChooseAssessment(scope, drawerState, context, navController)
        }
        composable(
            route = "AssessmentOption/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) {
            AssessmentOption(scope, drawerState, context, it.arguments?.getInt("courseId") ?: 0, navController)
        }
        composable(
            route = "AssessmentResult/{score}/{items}/{eligibility}/{isAuthorized}",
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("items") { type = NavType.IntType },
                navArgument("eligibility") { type = NavType.StringType },
                navArgument("isAuthorized") { type = NavType.BoolType }
            )
        ) {
            AssessmentResult(
                isAuthorized = it.arguments?.getBoolean("isAuthorized") ?: false,
                score = it.arguments?.getInt("score") ?: 0,
                items = it.arguments?.getInt("items") ?: 0,
                eligibility = it.arguments?.getString("eligibility") ?: "STUDENT",
                navController = navController
            )
        }
        composable(route = "Dashboard") {
            Dashboard(scope, drawerState, navController, context)
        }
        composable(route = "CoursesMenu") {
            CoursesMenu(scope, drawerState, context, navController)
        }
        composable(route = "FindTutor") {
            FindTutor(scope, drawerState, navController, context)
        }
        composable(route = "Notifications") {
            Notifications(scope, drawerState, navController, context)
        }
        composable(
            route = "Profile/{otherId}",
            arguments = listOf(navArgument("otherId") { type = NavType.IntType })
        ) {
            Profile(scope, drawerState, it.arguments?.getInt("otherId") ?: 0, navController, context)
        }
        composable(route = "Leaderboard") {
            Leaderboard(scope, drawerState, navController, context)
        }
        composable(route = "Analytics") {
            Analytics(scope, drawerState, navController, context)
        }
        composable(
            route = "MessageTutor/{tutorId}",
            arguments = listOf(navArgument("tutorId") { type = NavType.IntType })
        ) {
            MessageTutor(scope, drawerState, context, navController, it.arguments?.getInt("tutorId") ?: 0)
        }
        composable(
            route = "CreateSession/{messageId}",
            arguments = listOf(navArgument("messageId") { type = NavType.IntType })
        ) {
            CreateSession(scope, drawerState, context, navController, it.arguments?.getInt("messageId") ?: 0)
        }
        composable(
            route = "EditSession/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
        ) {
            EditSession(scope, drawerState, context, navController, it.arguments?.getInt("sessionId") ?: 0)
        }
        composable(
            route = "AboutSession/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
        ) {
            AboutSession(scope, drawerState, it.arguments?.getInt("sessionId") ?: 0, navController, context)
        }
        composable(
            route = "AboutStudent/{messageId}",
            arguments = listOf(navArgument("messageId") { type = NavType.IntType })
        ) {
            AboutStudent(scope, drawerState, it.arguments?.getInt("messageId") ?: 0, navController, context)
        }
        composable(
            route = "AboutTutor/{tutorId}",
            arguments = listOf(navArgument("tutorId") { type = NavType.IntType })
        ) {
            AboutTutor(scope, drawerState, it.arguments?.getInt("tutorId") ?: 0, navController, context
            )
        }
        composable(route = "Account") {
            Account(scope, drawerState, navController, context)
        }
        composable(route = "Achievements") {
            Achievements(scope, drawerState, navController, context)
        }
        composable(
            route = "AssignmentOption/{sessionId}/{rate}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.IntType },
                navArgument("rate") { type = NavType.IntType }
            )
        ) {
            AssignmentOption(
                scope = scope,
                drawerState = drawerState,
                navController = navController,
                sessionId = it.arguments?.getInt("sessionId") ?: 0,
                rate = it.arguments?.getInt("rate") ?: 0,
                context = context
            )
        }
        composable(
            route = "CreateAssignment/{sessionId}/{items}/{type}/{deadline}/{rate}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.IntType },
                navArgument("items") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType },
                navArgument("deadline") { type = NavType.StringType },
                navArgument("rate") { type = NavType.IntType }
            )
        ) {
            CreateAssignment(
                scope = scope,
                drawerState = drawerState,
                navController = navController,
                context = context,
                sessionId = it.arguments?.getInt("sessionId") ?: 0,
                items = it.arguments?.getString("items") ?: "5",
                type = it.arguments?.getString("type") ?: "Multiple Choice",
                deadline = it.arguments?.getString("deadline") ?: "",
                rate = it.arguments?.getInt("rate") ?: 0
            )
        }
        composable(
            route = "Assignment/{assignmentId}",
            arguments = listOf(navArgument("assignmentId") { type = NavType.IntType })
        ) {
            Assignment(scope, drawerState, context, navController, it.arguments?.getInt("assignmentId") ?: 0)
        }
        composable(route = "Archive") {
            Archive(scope, drawerState, context, navController)
        }
        composable(route = "PatternAssessment") {
            PatternAssessment(scope, drawerState, context, navController)
        }
    }
}