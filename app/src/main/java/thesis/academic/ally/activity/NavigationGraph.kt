package thesis.academic.ally.activity

import android.content.Context
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import thesis.academic.ally.utils.Routes

@Composable
fun NavigationGraph(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    context: Context
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(route = Routes.SPLASH) {
            Splash(navController, context)
        }
        composable(route = Routes.MAIN) {
            Main(navController)
        }
        composable(route = Routes.ABOUT) {
            About(navController)
        }
        composable(
            route = "${Routes.SIGNUP}/{user}",
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) {
            Signup(navController, it.arguments?.getString("user") ?: "STUDENT", context)
        }
        composable(
            route = "${Routes.LOGIN}/{user}",
            arguments = listOf(navArgument("user") { type = NavType.StringType })
        ) {
            Login(navController, it.arguments?.getString("user") ?: "STUDENT", context)
        }
        composable(
            route = "${Routes.ASSESSMENT}/{courseId}/{items}/{type}",
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
        composable(route = Routes.CHOOSE_ASSESSMENT) {
            ChooseAssessment(scope, drawerState, context, navController)
        }
        composable(
            route = "${Routes.ASSESSMENT_OPTION}/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.IntType })
        ) {
            AssessmentOption(scope, drawerState, context, it.arguments?.getInt("courseId") ?: 0, navController)
        }
        composable(
            route = "${Routes.ASSESSMENT_RESULT}/{score}/{items}/{eligibility}/{isAuthorized}",
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
        composable(route = Routes.DASHBOARD) {
            Dashboard(scope, drawerState, navController, context)
        }
        composable(route = Routes.COURSES_MENU) {
            CoursesMenu(scope, drawerState, context, navController)
        }
        composable(route = Routes.FIND_TUTOR) {
            FindTutor(scope, drawerState, navController, context)
        }
        composable(route = Routes.NOTIFICATIONS) {
            Notifications(scope, drawerState, navController, context)
        }
        composable(
            route = "${Routes.PROFILE}/{otherId}",
            arguments = listOf(navArgument("otherId") { type = NavType.IntType })
        ) {
            Profile(scope, drawerState, it.arguments?.getInt("otherId") ?: 0, navController, context)
        }
        composable(route = Routes.LEADERBOARD) {
            Leaderboard(scope, drawerState, navController, context)
        }
        composable(route = Routes.ANALYTICS) {
            Analytics(scope, drawerState, navController, context)
        }
        composable(
            route = "${Routes.MESSAGE_TUTOR}/{tutorId}",
            arguments = listOf(navArgument("tutorId") { type = NavType.IntType })
        ) {
            MessageTutor(scope, drawerState, context, navController, it.arguments?.getInt("tutorId") ?: 0)
        }
        composable(
            route = "${Routes.CREATE_SESSION}/{messageId}",
            arguments = listOf(navArgument("messageId") { type = NavType.IntType })
        ) {
            CreateSession(scope, drawerState, context, navController, it.arguments?.getInt("messageId") ?: 0)
        }
        composable(
            route = "${Routes.EDIT_SESSION}/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
        ) {
            EditSession(scope, drawerState, context, navController, it.arguments?.getInt("sessionId") ?: 0)
        }
        composable(
            route = "${Routes.ABOUT_SESSION}/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
        ) {
            AboutSession(scope, drawerState, it.arguments?.getInt("sessionId") ?: 0, navController, context)
        }
        composable(
            route = "${Routes.ABOUT_STUDENT}/{messageId}",
            arguments = listOf(navArgument("messageId") { type = NavType.IntType })
        ) {
            AboutStudent(scope, drawerState, it.arguments?.getInt("messageId") ?: 0, navController, context)
        }
        composable(
            route = "${Routes.ABOUT_TUTOR}/{tutorId}",
            arguments = listOf(navArgument("tutorId") { type = NavType.IntType })
        ) {
            AboutTutor(scope, drawerState, it.arguments?.getInt("tutorId") ?: 0, navController, context)
        }
        composable(route = Routes.ACCOUNT) {
            Account(scope, drawerState, navController, context)
        }
        composable(route = Routes.ACHIEVEMENTS) {
            Achievements(scope, drawerState, navController, context)
        }
        composable(
            route = "${Routes.ASSIGNMENT_OPTION}/{sessionId}/{rate}",
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
            route = "${Routes.CREATE_ASSIGNMENT}/{sessionId}/{items}/{type}/{deadline}/{rate}",
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
            route = "${Routes.ASSIGNMENT}/{assignmentId}",
            arguments = listOf(navArgument("assignmentId") { type = NavType.IntType })
        ) {
            Assignment(scope, drawerState, context, navController, it.arguments?.getInt("assignmentId") ?: 0)
        }
        composable(route = Routes.ARCHIVE) {
            Archive(scope, drawerState, context, navController)
        }
        composable(route = Routes.PATTERN_ASSESSMENT) {
            PatternAssessment(scope, drawerState, context, navController)
        }
        composable(route = Routes.SUPPORT) {
            Support(navController)
        }
        composable(
            route = "${Routes.SUPPORT_CHAT}/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.IntType })
        ) {
            SupportChat(scope, drawerState, navController, context, it.arguments?.getInt("topicId") ?: 0)
        }
    }
}