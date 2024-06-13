package thesis.academic.ally.utils

import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import java.time.LocalTime

sealed class ProcessState {
    data object Loading : ProcessState()
    data object Success : ProcessState()
    data class Error(val message: String) : ProcessState()
}

sealed class AssessmentType {
    data class MultipleChoiceFields(
        val id: Int,
        val question: String,
        val choices: List<String>,
        val answer: DropDownState
    ) : AssessmentType()

    data class IdentificationFields(
        val id: Int,
        val question: String,
        val answer: String
    ) : AssessmentType()

    data class TrueOrFalseFields(
        val id: Int,
        val question: String,
        val answer: DropDownState
    ) : AssessmentType()
}

data class AboutText(
    val title: String,
    val description: String
)

data class DashboardIcons(
    val route: String,
    val name: String,
    val icon: ImageVector
)

data class LoginInput(
    val email: String = "",
    val password: String = "",
    val error: String = "",
    val remember: Boolean = false,
    val passwordVisibility: Boolean = false
)

data class SignupInput(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val error: String = "",
    val passwordVisibility: Boolean = false,
    val confirmPasswordVisibility: Boolean = false
)

data class PatternAssessmentState(
    val dialogOpen: Boolean = false,
    val primaryPattern: AboutText = AboutText("", ""),
    val secondaryPattern: AboutText = AboutText("", "")
)

data class SearchInfo(
    val searchQuery: String = "",
    val isActive: Boolean = false,
    val history: List<String> = emptyList()
)

data class AssessmentResult(
    val score: Int,
    val items: Int,
    val evaluator: Double,
    val courseId: Int,
    val eligibility: String
)

data class DropDownState(
    val dropDownItems: List<String>,
    val selected: String,
    val expanded: Boolean
)

data class SessionSettings(
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val error: String = "",
    val datePickerEnabled: Boolean = false,
    val timePickerEnabled: Boolean = false,
    val dialogDate: LocalDate = LocalDate.now(),
    val dialogTime: LocalTime = LocalTime.now(),
    val isStartTime: Boolean = true
)

data class DeadlineField(
    val date: String = "",
    val time: String = "",
    val error: String = "",
    val datePickerEnabled: Boolean = false,
    val timePickerEnabled: Boolean = false,
    val dialogDate: LocalDate = LocalDate.now(),
    val dialogTime: LocalTime = LocalTime.now()
)

data class RateDialogStates(
    val userId: Int = 0,
    val sessionId: Int = 0,
    val name: String = "",
    val star: Int = 0
)

data class ManageAccountFields(
    val name: String = "",
    val degree: String = "",
    val age: String = "",
    val address: String = "",
    val contactNumber: String = "",
    val summary: String = "",
    val educationalBackground: String = "",
    val freeTutoringTime: List<TutorAvailabilityData> = emptyList(),
    val errorMessage: String = "",
    val isError: Boolean = false
)

data class PasswordFields(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val errorMessage: String = "",
    val isError: Boolean = false
)

data class FilterDialogStates(
    val id: Int,
    val courseName: String,
    val isEnabled: Boolean
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

data class DrawerItem(
    val name: String,
    val icon: ImageVector,
    val action: suspend () -> Unit
)

data class TutorAvailabilityData(
    val day: String = "Sunday",
    val from: LocalTime = LocalTime.now(),
    val to: LocalTime = LocalTime.now()
)

data class AccountDialogState(
    val time: LocalTime = LocalTime.now(),
    val day: Int = 0,
    val threshold: Int = 0,
    val dialogOpen: Boolean = false
)