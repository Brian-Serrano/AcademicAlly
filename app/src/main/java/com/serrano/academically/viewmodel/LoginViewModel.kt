package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.LoginInput
import com.serrano.academically.utils.ValidationMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _loginInput = MutableStateFlow(LoginInput())
    val loginInput: StateFlow<LoginInput> = _loginInput.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    fun updateInput(newLoginInput: LoginInput) {
        _loginInput.value = newLoginInput
    }

    fun validateUserLoginAsynchronously(
        context: Context,
        role: String,
        li: LoginInput,
        navigate: (ValidationMessage) -> Unit,
        error: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Disable button temporarily
                _buttonEnabled.value = false

                // Validate and login user
                val vm = validateUserLogin(role, li)
                if (vm.isValid) {

                    // Check if the user take assessment recently then save it
                    val resultData = context.dataStore.data.first()
                    if (resultData.eligibility.isNotEmpty()) {

                        // Save or update course skill and their points and achievements
                        HelperFunctions.updateCourseSkillAndAchievement(
                            courseSkillRepository = courseSkillRepository,
                            userRepository = userRepository,
                            context = context,
                            courseId = resultData.courseId,
                            userId = vm.id,
                            score = resultData.score,
                            items = resultData.items,
                            evaluator = resultData.evaluator
                        )

                        // Clear assessment result data preferences before navigating to dashboard
                        UpdateUserPref.clearAssessmentResultData(context)
                    }

                    // Save to preferences for auto login
                    UpdateUserPref.updateDataByLoggingIn(
                        context,
                        li.remember,
                        vm.id,
                        li.email,
                        li.password
                    )

                    // Clear the login fields
                    updateInput(loginInput.value.copy(email = "", password = "", error = ""))

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
                e.printStackTrace()
                error("Something went wrong processing your credentials.")
            }
        }
    }

    private suspend fun validateUserLogin(role: String, li: LoginInput): ValidationMessage {
        val id = userRepository.getUserId(li.email, li.password, role).firstOrNull() ?: 0
        return when {
            li.email.isEmpty() || li.password.isEmpty() -> ValidationMessage(
                false,
                "Fill up all empty fields",
                0
            )

            li.email.length < 15 || li.email.length > 40 || li.password.length < 8 || li.password.length > 20 -> ValidationMessage(
                false,
                "Fill up fields with specified length",
                0
            )

            id != 0 -> ValidationMessage(true, "User Logged In", id)
            else -> ValidationMessage(false, "User not found", 0)
        }
    }
}