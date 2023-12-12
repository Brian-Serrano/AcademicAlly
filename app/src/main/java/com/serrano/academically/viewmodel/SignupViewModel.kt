package com.serrano.academically.viewmodel

import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.User
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.SignupInput
import com.serrano.academically.utils.ValidationMessage
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.utils.updateUserAssessmentsAchievements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
): ViewModel() {

    private val _signupInput = MutableStateFlow(SignupInput("", "", "", "", ""))
    val signupInput: StateFlow<SignupInput> = _signupInput.asStateFlow()

    fun validateUserSignUpAsynchronously(
        context: Context,
        role: String,
        si: SignupInput,
        courseId: Int,
        score: Int,
        items: Int,
        eval: Double,
        navigate: (String) -> Unit,
        error: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val vm = validateUserSignUp(role, si, userRepository.getUserNames().first(), userRepository.getUserEmails().first())
                if (vm.isValid) {
                    if (courseId != 0) {
                        courseSkillRepository.addCourseSkill(
                            CourseSkill(
                                courseId = courseId,
                                userId = vm.message.toInt(),
                                role = role,
                                courseAssessmentTaken = 1,
                                courseAssessmentScore = score,
                                courseAssessmentItemsTotal = items,
                                courseAssessmentEvaluator = eval
                            )
                        )
                        updateUserAssessmentsAchievements(score.toFloat() / items >= eval, userRepository, context, courseSkillRepository, score, vm.message.toInt())
                    }
                    UpdateUserPref.updateDataByLoggingIn(context, false, vm.message.toInt(), si.email, si.password)
                    updateInput(signupInput.value.copy(name = "", email = "", password = "", confirmPassword = "", error = ""))
                    navigate("Dashboard/${vm.message}")
                }
                else error(vm.message)
            }
            catch (e: Exception) {
                error("Something went wrong processing your credentials.")
            }
        }
    }

    private suspend fun validateUserSignUp(role: String, si: SignupInput, name: List<String>, email: List<String>): ValidationMessage {
        return when {
            si.name.isEmpty() ||
                    si.email.isEmpty() ||
                    si.password.isEmpty() ||
                    si.confirmPassword.isEmpty() -> ValidationMessage(false, "Fill up all empty fields")
            si.name.length < 5 || si.name.length > 20 ||
                    si.email.length < 15 || si.email.length > 40 ||
                    si.password.length < 8 || si.password.length > 20 -> ValidationMessage(false, "Fill up fields with specified length")
            si.password != si.confirmPassword -> ValidationMessage(false, "Passwords do not match")
            !Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").containsMatchIn(si.email) -> ValidationMessage(false, "Invalid Email")
            !Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$").containsMatchIn(si.password) -> ValidationMessage(false, "Invalid Password")
            name.any { it == si.name } -> ValidationMessage(false, "Username already exist")
            email.any { it == si.email } -> ValidationMessage(false, "Email already exist")
            else -> {
                userRepository.addUser(
                    User(
                        name = si.name,
                        role = role,
                        email = si.email,
                        password = si.password
                    )
                )
                ValidationMessage(
                    isValid = true,
                    message = (userRepository.getUserId(si.email, si.password, role).first()).toString()
                )
            }
        }
    }

    fun updateInput(newSignupInput: SignupInput) {
        _signupInput.value = newSignupInput
    }
}