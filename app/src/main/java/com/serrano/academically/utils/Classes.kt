package com.serrano.academically.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime

data class AboutText(
    var title: String,
    var description: String
)

data class DashboardIcons(
    var route: String,
    var name: String,
    var icon: ImageVector
)

data class LoginInput(
    var email: String,
    var password: String,
    var error: String,
    var remember: Boolean
)

data class ValidationMessage(
    val isValid: Boolean,
    val message: String
)

data class SignupInput(
    var name: String,
    var email: String,
    var password: String,
    var confirmPassword: String,
    var error: String
)

data class SearchInfo(
    var searchQuery: String,
    var isActive: Boolean,
    var history: List<String>
)

data class AssessmentResult(
    var score: Int,
    var items: Int,
    var evaluator: Float
)

data class DropDownState(
    var dropDownItems: List<String>,
    var selected: String,
    var expanded: Boolean
)

data class SessionSettings(
    var date: String,
    var startTime: String,
    var endTime: String,
    var location: String,
    var error: String
)

data class ManageAccountFields(
    val name: String,
    var degree: String,
    var age: String,
    var address: String,
    var contactNumber: String,
    var summary: String,
    var educationalBackground: String,
    var error: String,
    var errorColor: Color
)

data class PasswordFields(
    var currentPassword: String,
    var newPassword: String,
    var confirmPassword: String,
    var error: String,
    var errorColor: Color
)

data class FindTutorData(
    var tutorId: Int,
    var tutorName: String,
    var courses: List<String>,
    var rating: List<Float>
)

data class FilterDialogStates(
    var id: Int,
    var courseName: String,
    var isEnabled: Boolean
)

data class RoomIsDumb(
    val id: Int,
    val achievement: List<Float>
)

data class UserDrawerData(
    val id: Int,
    val name: String,
    val role : String,
    val email: String,
    val degree: String
)

data class UserInfo(
    val id: Int,
    val name: String,
    val degree: String,
    val age: Int,
    val address: String,
    val contactNumber: String,
    val summary: String,
    val educationalBackground: String
)

data class UserInfoAndCredentials(
    val id: Int,
    val name: String,
    val role : String,
    val email: String,
    val password: String,
    val imagePath: String,
    val degree: String,
    val age: Int,
    val address: String,
    val contactNumber: String,
    val summary: String,
    val educationalBackground: String
)

data class AnalyticsData(
    val id: Int,

    val name: String,
    val role : String,
    val email: String,
    val degree: String,

    val studentPoints: Float,
    val studentAssessmentPoints: Float,
    val studentBadgePoints: Float,
    val studentRequestPoints: Float,
    val studentSessionPoints: Float,
    val sessionsCompletedAsStudent: Int,
    val requestsSent: Int,
    val deniedRequests: Int,
    val acceptedRequests: Int,
    val assessmentsTakenAsStudent: Int,
    val badgeProgressAsStudent: List<Float>,

    val tutorPoints: Float,
    val tutorAssessmentPoints: Float,
    val tutorBadgePoints: Float,
    val tutorRequestPoints: Float,
    val tutorSessionPoints: Float,
    val sessionsCompletedAsTutor: Int,
    val requestsAccepted: Int,
    val requestsDenied: Int,
    val requestsReceived: Int,
    val assessmentsTakenAsTutor: Int,
    val badgeProgressAsTutor: List<Float>
)

data class LeaderboardData(
    val id: Int,
    val name: String,
    val points: Float
)

data class SessionInfo(
    val courseName: String,
    val tutorName: String,
    val studentName: String,
    val moduleName: String
)

data class SessionNotifications(
    val sessionId: Int,
    val courseId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)

data class MessageCourse(
    val courseName: String,
    val moduleName: String
)

data class MessageNotifications(
    val messageId: Int,
    val studentId: Int,
    val tutorId: Int,
    val courseId: Int
)

sealed class ProcessState {
    object Loading : ProcessState()
    object Success : ProcessState()
    object Error : ProcessState()
}