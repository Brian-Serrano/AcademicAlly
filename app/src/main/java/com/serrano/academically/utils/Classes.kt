package com.serrano.academically.utils

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime

sealed class ProcessState {
    data object Loading : ProcessState()
    data object Success : ProcessState()
    data class Error(val message: String) : ProcessState()
}

sealed class AssessmentType {
    data class MultipleChoiceFields(
        var id: Int,
        var question: String,
        var choices: List<String>,
        var answer: DropDownState
    ) : AssessmentType()

    data class IdentificationFields(
        var id: Int,
        var question: String,
        var answer: String
    ) : AssessmentType()

    data class TrueOrFalseFields(
        var id: Int,
        var question: String,
        var answer: DropDownState
    ) : AssessmentType()
}

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

data class ManageAccountFields(
    var name: String = "",
    var degree: String = "",
    var age: String = "",
    var address: String = "",
    var contactNumber: String = "",
    var summary: String = "",
    var educationalBackground: String = "",
    var freeTutoringTime: String = "",
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