package com.serrano.academically.api

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass


annotation class Unauthorized

sealed class WithCurrentUser<T>(val data: T?, val currentUser: DrawerData?, val error: String?) {
    class Success<T>(data: T, currentUser: DrawerData): WithCurrentUser<T>(data, currentUser, null)
    class Error<T>(error: String): WithCurrentUser<T>(null, null, error)
}

sealed class OptionalCurrentUser<T>(val data: T?, val currentUser: DrawerData?, val error: String?) {
    class CurrentUserData<T>(data: T, currentUser: DrawerData): OptionalCurrentUser<T>(data, currentUser, null)
    class UserData<T>(data: T): OptionalCurrentUser<T>(data, null, null)
    class Error<T>(error: String): OptionalCurrentUser<T>(null, null, error)
}

sealed class NoCurrentUser<T>(val data: T?, val error: String?) {
    class Success<T>(data: T): NoCurrentUser<T>(data, null)
    class Error<T>(error: String): NoCurrentUser<T>(null, error)
}

sealed class MessageResponse {
    data class AchievementResponse(val achievements: List<String>): MessageResponse()
    data class DuplicateMessageResponse(val message: String): MessageResponse()
    data class ErrorResponse(val error: String): MessageResponse()
}

sealed class AuthenticationResponse {
    data class SuccessResponse(val token: String, val achievements: List<String>): AuthenticationResponse()
    data class SuccessNoAssessment(val token: String): AuthenticationResponse()
    data class ValidationError(val isValid: Boolean, val message: String): AuthenticationResponse()
    data class ErrorResponse(val error: String): AuthenticationResponse()
}

data class Session(
    val sessionId: Int = 0,
    val courseName: String = "Computer Programming 1",
    val tutorId: Int = 0,
    val tutorName: String = "TestTutor",
    val studentId: Int = 0,
    val studentName: String = "TestStudent",
    val moduleName: String = "Basics of Programming",
    val startTime: String = "2000-01-01 00:00:00.000000",
    val endTime: String = "2000-01-01 00:00:00.000000",
    val location: String = "USA"
)

data class SessionForAssignment(
    val sessionId: Int = 0,
    val courseName: String = "Computer Programming 1",
    val moduleName: String = "Basics of Programming",
    val startTime: String = "2000-01-01 00:00:00.000000",
    val endTime: String = "2000-01-01 00:00:00.000000",
    val location: String = "USA",
    val studentId: Int = 0,
    val tutorId: Int = 0,
    val courseId: Int = 0,
    val moduleId: Int = 0
)

data class Student(
    val messageId: Int = 0,
    val studentId: Int = 0,
    val tutorId: Int = 0,
    val courseName: String = "Computer Programming 1",
    val moduleName: String = "Basics of Programming",
    val studentMessage: String = "Test Message",
    val userId: Int = 0,
    val name: String = "TestUser",
    val degree: String = "BSCS",
    val age: Int = 0,
    val address: String = "USA",
    val contactNumber: String = "NA",
    val summary: String = "NA",
    val educationalBackground: String = "NA",
    val image: String = "",
    val primaryLearning: String = "NA",
    val secondaryLearning: String = "NA"
)

data class RejectStudentBody(
    val messageId: Int,
    val studentId: Int,
    val tutorId: Int
)

data class Tutor(
    val performanceRating: Double = 0.0,
    val numberOfRates: Int = 0,
    val tutorCourses: List<CourseRating> = emptyList(),
    val userId: Int = 0,
    val name: String = "TestUser",
    val degree: String = "BSCS",
    val age: Int = 0,
    val address: String = "USA",
    val contactNumber: String = "NA",
    val summary: String = "NA",
    val educationalBackground: String = "NA",
    val image: String = "",
    val freeTutoringTime: String = "NA",
    val primaryLearning: String = "NA",
    val secondaryLearning: String = "NA"
)

data class CourseRating(
    val courseName: String,
    val courseDescription: String,
    val assessmentRating: Double,
    val assessmentTaken: Int
)

data class Info(
    val id: Int = 0,
    val name: String = "TestUser",
    val role: String = "STUDENT",
    val email: String = "test@gmail.com",
    val degree: String = "BSCS",
    val age: Int = 0,
    val address: String = "USA",
    val contactNumber: String = "NA",
    val summary: String = "NA",
    val educationalBackground: String = "NA",
    val image: String = "",
    val freeTutoringTime: String = "NA"
)

data class InfoBody(
    val name: String,
    val age: Int,
    val degree: String,
    val address: String,
    val contactNumber: String,
    val summary: String,
    val educationalBackground: String,
    val freeTutoringTime: String
)

data class Validation(
    val isValid: Boolean,
    val message: String
)

data class Success(
    val message: String
)

data class PasswordBody(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

data class AchievementWrapper(
    val achievements: List<Achievement> = emptyList(),
    val badge: String = ""
)

data class Achievement(
    val title: String,
    val description: String,
    val progress: Double,
    val icons: String
)

data class Analytics(
    val points: Double = 0.0,
    val assessmentPoints: Double = 0.0,
    val requestPoints: Double = 0.0,
    val sessionPoints: Double = 0.0,
    val assignmentPoints: Double = 0.0,
    val sessionsCompleted: Int = 0,
    val requestsSentReceived: Int = 0,
    val requestsAccepted: Int = 0,
    val requestsDenied: Int = 0,
    val assignments: Int = 0,
    val assessments: Int = 0,
    val rateNumber: Int = 0,
    val ratedUsers: Int = 0,
    val badgesCompleted: Int = 0,
    val rating: Double = 0.0,
    val courses: List<CourseRating> = emptyList()
)

data class Course(
    val name: String = "NA",
    val description: String = "NA"
)

data class Assessment(
    val name: String = "NA",
    val description: String = "NA",
    val assessmentData: List<List<String>> = emptyList()
)

data class CourseEligibilityBody(
    val courseId: Int,
    val rating: Double,
    val score: Int
)

data class LoginBody(
    val courseId: Int,
    val rating: Double,
    val score: Int,
    val email: String,
    val password: String,
    val role: String,
    val eligibility: String
)

data class Assignment(
    val name: String = "",
    val description: String = "",
    val type: String = "Multiple Choice",
    val data: List<AssessmentBody> = emptyList()
)

data class AssignmentBody(
    val assignmentId: Int,
    val score: Int
)

data class Course2(
    val id: Int,
    val name: String,
    val description: String
)

data class CreateAssignmentBody(
    val sessionId: Int,
    val studentId: Int,
    val tutorId: Int,
    val courseId: Int,
    val moduleId: Int,
    val data: List<AssessmentBody>,
    val type: String,
    val deadLine: String,
    val rate: Int
)

data class AssessmentBody(
    val question: String,
    val answer: String,
    val letterA: String? = null,
    val letterB: String? = null,
    val letterC: String? = null,
    val letterD: String? = null,
    val module: String,
    val creator: String
)

data class Message(
    val messageId: Int = 0,
    val courseId: Int = 0,
    val moduleId: Int = 0,
    val studentId: Int = 0,
    val tutorId: Int = 0,
    val studentMessage: String = "Test Message"
)

data class CreateSessionBody(
    val startTime: String,
    val endTime: String,
    val messageId: Int,
    val studentId: Int,
    val tutorId: Int,
    val courseId: Int,
    val moduleId: Int,
    val location: String
)

data class Dashboard(
    val rateNumber: Int = 0,
    val rating: Double = 0.0,
    val courses: List<CourseRating> = emptyList(),
    val image: String = "NA"
)

data class SessionData(
    val startTime: String = "2000-01-01 00:00:00.000000",
    val endTime: String = "2000-01-01 00:00:00.000000",
    val location: String = "USA"
)

data class UpdateSessionBody(
    val sessionId: Int,
    val startTime: String,
    val endTime: String,
    val location: String
)

data class Leaderboard(
    val id: Int,
    val name: String,
    val rating: Double,
    val rateNumber: Int,
    val image: String
)

data class CourseModule(
    val courseId: Int,
    val courseName: String,
    val modules: List<String>
)

data class TutorCourses(
    val tutorName: String = "TestTutor",
    val tutorCourses: List<CourseModule> = emptyList()
)

data class TutorRequestBody(
    val studentId: Int,
    val tutorId: Int,
    val courseId: Int,
    val moduleId: Int,
    val studentMessage: String
)

data class MessageNotifications(
    val messageId: Int,
    val name: String,
    val courseName: String,
    val status: String,
    val tutorViewed: Boolean,
    val image: String
)

data class SessionNotifications(
    val sessionId: Int,
    val courseName: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val studentViewed: Boolean
)

data class AssignmentNotifications(
    val assignmentId: Int,
    val courseName: String,
    val moduleName: String,
    val type: String,
    val deadLine: String,
    val status: String,
    val studentScore: Int,
    val studentViewed: Boolean
)

data class Profile(
    val user: Info = Info(),
    val courses: List<List<Course>> = emptyList(),
    val rating: List<PerformanceRating> = emptyList(),
    val primaryLearning: String = "NA",
    val secondaryLearning: String = "NA"
)

data class SignupBody(
    val name: String,
    val role: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val eligibility: String,
    val courseId: Int,
    val rating: Double,
    val score: Int,
    val items: Int,
    val evaluator: Double
)

data class DrawerData(
    val id: Int = 0,
    val name: String = "TestUser",
    val role: String = "STUDENT",
    val email: String = "test@gmail.com",
    val degree: String = "BSCS",
    val primaryLearning: String = "NA",
    val secondaryLearning: String = "NA"
)

data class CourseRating2(
    val courseName: String,
    val courseRating: Double
)

data class PerformanceRating(
    val rating: Double,
    val rateNumber: Int
)

data class FindTutor(
    val studentCourseIds: List<Int> = emptyList(),
    val courses: List<Course3> = emptyList(),
    val tutors: List<FindTutorData> = emptyList()
)

data class FindTutorData(
    val tutorId: Int,
    val tutorName: String,
    val coursesAndRatings: List<CourseRating2>,
    val performance: PerformanceRating,
    val primaryPattern: String,
    val secondaryPattern: String,
    val image: String
)

data class SessionArchive(
    val sessionId: Int,
    val studentId: Int,
    val tutorId: Int,
    val courseName: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val status: String,
    val studentViewed: Boolean,
    val studentRate: Boolean,
    val tutorRate: Boolean
)

data class RateBody(
    val sessionId: Int,
    val otherId: Int,
    val rate: Int
)

data class PatternAssessment(
    val question: String,
    val choices: List<Choice>
)

data class Choice(
    val choice: String,
    val type: String
)

data class PatternAssessmentBody(
    val collaborative: Int,
    val independent: Int,
    val experiential: Int,
    val dependent: Int
)

data class Course3(
    val id: Int,
    val name: String
)

data class EmailBody(
    val email: String
)

data class SupportMessage(
    val chatId: Int,
    val message: String,
    val fromId: Int,
    val toId: Int,
    val date: String
)

data class SupportBody(
    val message: String,
    val fromId: Int,
    val toId: Int
)