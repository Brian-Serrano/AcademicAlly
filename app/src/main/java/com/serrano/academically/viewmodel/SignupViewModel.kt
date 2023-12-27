package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.User
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.SignupInput
import com.serrano.academically.utils.ValidationMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _signupInput = MutableStateFlow(SignupInput())
    val signupInput: StateFlow<SignupInput> = _signupInput.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    fun validateUserSignUpAsynchronously(
        context: Context,
        role: String,
        si: SignupInput,
        navigate: (ValidationMessage) -> Unit,
        error: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Disable button temporarily
                _buttonEnabled.value = false

                val vm = validateUserSignUp(
                    role,
                    si,
                    userRepository.getUserNames().first(),
                    userRepository.getUserEmails().first()
                )
                if (vm.isValid) {
                    // Check if the user take assessment recently then save it
                    val resultData = context.dataStore.data.first()
                    if (resultData.eligibility.isNotEmpty()) {
                        courseSkillRepository.addCourseSkill(
                            CourseSkill(
                                courseId = resultData.courseId,
                                userId = vm.id,
                                role = resultData.eligibility,
                                assessmentTaken = 1,
                                assessmentRating = HelperFunctions.eligibilityComputingAlgorithm(
                                    resultData.score,
                                    resultData.items,
                                    resultData.evaluator
                                )
                            )
                        )
                        // Update student or tutor points and assessment achievement
                        HelperFunctions.updateUserAssessmentsAchievements(
                            resultData.score.toFloat() / resultData.items >= resultData.evaluator,
                            userRepository,
                            context,
                            courseSkillRepository,
                            resultData.score,
                            vm.id
                        )

                        // Clear assessment result data preferences before navigating to dashboard
                        UpdateUserPref.clearAssessmentResultData(context)
                    }

                    // Save data to preferences for auto login
                    UpdateUserPref.updateDataByLoggingIn(
                        context,
                        false,
                        vm.id,
                        si.email,
                        si.password
                    )

                    // Clear the signup fields
                    updateInput(
                        si.copy(
                            name = "",
                            email = "",
                            password = "",
                            confirmPassword = "",
                            error = ""
                        )
                    )

                    // Enable button again
                    _buttonEnabled.value = true

                    navigate(vm)
                } else {
                    // Enable button again
                    _buttonEnabled.value = true

                    error(vm.message)
                }
            } catch (e: Exception) {
                _buttonEnabled.value = true
                error("Something went wrong processing your credentials.")
            }
        }
    }

    private suspend fun validateUserSignUp(
        role: String,
        si: SignupInput,
        name: List<String>,
        email: List<String>
    ): ValidationMessage {
        return when {
            si.name.isEmpty() ||
                    si.email.isEmpty() ||
                    si.password.isEmpty() ||
                    si.confirmPassword.isEmpty() -> ValidationMessage(
                false,
                "Fill up all empty fields",
                0
            )

            si.name.length < 5 || si.name.length > 20 ||
                    si.email.length < 15 || si.email.length > 40 ||
                    si.password.length < 8 || si.password.length > 20 -> ValidationMessage(
                false,
                "Fill up fields with specified length",
                0
            )

            si.password != si.confirmPassword -> ValidationMessage(
                false,
                "Passwords do not match",
                0
            )

            !Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").containsMatchIn(si.email) -> ValidationMessage(
                false,
                "Invalid Email",
                0
            )

            !Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$").containsMatchIn(si.password) -> ValidationMessage(
                false,
                "Invalid Password",
                0
            )

            name.any { it == si.name } -> ValidationMessage(false, "Username already exist", 0)
            email.any { it == si.email } -> ValidationMessage(false, "Email already exist", 0)
            else -> {
                // Save user
                userRepository.addUser(
                    User(
                        name = si.name,
                        role = role,
                        email = si.email,
                        password = si.password
                    )
                )
                // Return successful result
                ValidationMessage(
                    isValid = true,
                    message = "User Signed Up!",
                    id = userRepository.getUserId(si.email, si.password, role).first()
                )
            }
        }
    }

    fun updateInput(newSignupInput: SignupInput) {
        _signupInput.value = newSignupInput
    }
}