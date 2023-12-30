package com.serrano.academically.utils

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
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
    var email: String = "",
    var password: String = "",
    var error: String = "",
    var remember: Boolean = false
)

data class SignupInput(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var error: String = ""
)

data class SearchInfo(
    var searchQuery: String = "",
    var isActive: Boolean = false,
    var history: List<String> = emptyList()
)

data class AssessmentResult(
    var score: Int,
    var items: Int,
    var evaluator: Double,
    var courseId: Int,
    var eligibility: String
)

data class DropDownState(
    var dropDownItems: List<String>,
    var selected: String,
    var expanded: Boolean
)

data class SessionSettings(
    var date: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var location: String = "",
    var error: String = ""
)

data class DeadlineField(
    var date: String = "",
    var time: String = "",
    var error: String = ""
)

data class RateDialogStates(
    var userId: Int = 0,
    var sessionId: Int = 0,
    var name: String = "",
    var star: Int = 0
)

interface AssessmentType {
    var id: Int
    var question: String
}

data class MultipleChoiceFields(
    override var id: Int,
    override var question: String,
    var choices: List<String>,
    var answer: DropDownState
) : AssessmentType

data class IdentificationFields(
    override var id: Int,
    override var question: String,
    var answer: String
) : AssessmentType

data class TrueOrFalseFields(
    override var id: Int,
    override var question: String,
    var answer: DropDownState
) : AssessmentType

data class ManageAccountFields(
    var name: String = "",
    var degree: String = "",
    var age: String = "",
    var address: String = "",
    var contactNumber: String = "",
    var summary: String = "",
    var educationalBackground: String = "",
    var errorMessage: String = "",
    var isError: Boolean = false
)

data class PasswordFields(
    var currentPassword: String = "",
    var newPassword: String = "",
    var confirmPassword: String = "",
    var errorMessage: String = "",
    var isError: Boolean = false
)

data class FindTutorData(
    var tutorId: Int,
    var tutorName: String,
    var courses: List<String>,
    var rating: List<Double>,
    var performance: Rating
)

data class FilterDialogStates(
    var id: Int,
    var courseName: String,
    var isEnabled: Boolean
)

data class ChartState(
    val camera: Offset = Offset.Zero,
    val scale: Float = 1f,
    val size: Offset = Offset.Zero,
    val viewSize: Offset = Offset.Zero
)

data class ChartData(
    val text: String,
    val value: State<Float>,
    val color: Color
)

data class ValidationMessage(
    val isValid: Boolean = false,
    val message: String = "",
    val id: Int = 0
)

data class RoomIsDumb(
    val id: Int,
    val achievement: List<Double>
)

data class UserDrawerData(
    val id: Int = 0,
    val name: String = "Test",
    val role: String = "STUDENT",
    val email: String = "test@gmail.com",
    val degree: String = "BSCS"
)

data class UserInfo(
    val id: Int = 0,
    val name: String = "Test",
    val degree: String = "BSCS",
    val age: Int = 0,
    val address: String = "NA",
    val contactNumber: String = "NA",
    val summary: String = "This user has no summary provided",
    val educationalBackground: String = "This user has no educational background provided"
)

data class UserInfoAndCredentials(
    val id: Int = 0,
    val name: String = "Test",
    val role: String = "STUDENT",
    val email: String = "test@gmail.com",
    val password: String = "test123",
    val imagePath: String = "NA",
    val degree: String = "BSCS",
    val age: Int = 0,
    val address: String = "NA",
    val contactNumber: String = "NA",
    val summary: String = "This user has no summary provided",
    val educationalBackground: String = "This user has no educational background provided"
)

data class AnalyticsData(
    val id: Int = 0,
    val name: String = "Test",
    val role: String = "STUDENT",
    val email: String = "test@gmail.com",
    val degree: String = "BSCS",
    val studentPoints: Double = 0.0,
    val studentAssessmentPoints: Double = 0.0,
    val studentRequestPoints: Double = 0.0,
    val studentSessionPoints: Double = 0.0,
    val studentAssignmentPoints: Double = 0.0,
    val sessionsCompletedAsStudent: Int = 0,
    val requestsSent: Int = 0,
    val deniedRequests: Int = 0,
    val acceptedRequests: Int = 0,
    val assignmentsTaken: Int = 0,
    val assessmentsTakenAsStudent: Int = 0,
    val badgeProgressAsStudent: List<Double> = List(28) { 0.0 },
    val numberOfRatesAsStudent: Int = 0,
    val totalRatingAsStudent: Double = 0.0,
    val tutorsRated: Int = 0,
    val tutorPoints: Double = 0.0,
    val tutorAssessmentPoints: Double = 0.0,
    val tutorRequestPoints: Double = 0.0,
    val tutorSessionPoints: Double = 0.0,
    val tutorAssignmentPoints: Double = 0.0,
    val sessionsCompletedAsTutor: Int = 0,
    val requestsAccepted: Int = 0,
    val requestsDenied: Int = 0,
    val requestsReceived: Int = 0,
    val assignmentsCreated: Int = 0,
    val assessmentsTakenAsTutor: Int = 0,
    val badgeProgressAsTutor: List<Double> = List(28) { 0.0 },
    val numberOfRatesAsTutor: Int = 0,
    val totalRatingAsTutor: Double = 0.0,
    val studentsRated: Int = 0
)

data class LeaderboardData(
    val id: Int,
    val name: String,
    val rating: Double,
    val number: Int
)

data class Rating(
    val rating: Double = 0.0,
    val number: Int = 0,
)

data class SessionInfo(
    val courseName: String = "Computer Programming 1",
    val tutorName: String = "TestTutor",
    val studentName: String = "TestStudent",
    val moduleName: String = "Basics of Programming"
)

data class SessionNotifications(
    val sessionId: Int,
    val courseId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val status: String,
    val studentViewed: Boolean
)

data class MessageCourse(
    val courseName: String = "Computer Programming 1",
    val moduleName: String = "Basics of Programming"
)

data class MessageNotifications(
    val messageId: Int,
    val studentId: Int,
    val tutorId: Int,
    val courseId: Int,
    val status: String,
    val tutorViewed: Boolean
)

sealed class ProcessState {
    data object Loading : ProcessState()
    data object Success : ProcessState()
    data object Error : ProcessState()
}