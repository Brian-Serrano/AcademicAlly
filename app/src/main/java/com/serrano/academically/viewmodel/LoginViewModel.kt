package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.AuthenticationResponse
import com.serrano.academically.api.EmailBody
import com.serrano.academically.api.LoginBody
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.LoginInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _loginInput = MutableStateFlow(LoginInput())
    val loginInput: StateFlow<LoginInput> = _loginInput.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    private val _forgotClickable = MutableStateFlow(true)
    val forgotClickable: StateFlow<Boolean> = _forgotClickable.asStateFlow()

    fun updateInput(newLoginInput: LoginInput) {
        _loginInput.value = newLoginInput
    }

    fun login(
        context: Context,
        role: String,
        li: LoginInput,
        navigate: () -> Unit,
        error: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _buttonEnabled.value = false

                val assessment = userCacheRepository.userDataStore.data.first()
                val rating = Utils.eligibilityComputingAlgorithm(
                    assessment.score,
                    assessment.items,
                    assessment.evaluator
                )
                val response = academicallyApi.login(
                    LoginBody(
                        assessment.courseId,
                        if (rating.isNaN()) 0.0 else rating,
                        assessment.score,
                        li.email,
                        li.password,
                        role,
                        assessment.eligibility
                    )
                )
                when (response) {
                    is AuthenticationResponse.SuccessResponse -> {
                        Utils.showToast(response.achievements, context)
                        userCacheRepository.clearAssessmentResultData()
                        userCacheRepository.updateDataByLoggingIn(li.remember, response.token, li.email, li.password, role)
                        updateInput(loginInput.value.copy(email = "", password = "", error = ""))
                        ActivityCacheManager.clearCache()
                        _buttonEnabled.value = true
                        navigate()
                    }
                    is AuthenticationResponse.SuccessNoAssessment -> {
                        userCacheRepository.updateDataByLoggingIn(li.remember, response.token, li.email, li.password, role)
                        updateInput(loginInput.value.copy(email = "", password = "", error = ""))
                        ActivityCacheManager.clearCache()
                        _buttonEnabled.value = true
                        navigate()
                    }
                    is AuthenticationResponse.ValidationError -> {
                        _buttonEnabled.value = true
                        error(response.message)
                    }
                    is AuthenticationResponse.ErrorResponse -> {
                        throw IllegalArgumentException(response.error)
                    }
                }
            } catch (e: Exception) {
                _buttonEnabled.value = true
                error("Something went wrong processing your credentials.")
            }
        }
    }

    fun forgotPassword(email: String, error: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _forgotClickable.value = false

                when {
                    email.isEmpty() -> {
                        error("Provide the email field.")
                    }
                    !Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").containsMatchIn(email) -> {
                        error("Email is invalid.")
                    }
                    else -> {
                        val message = when (val apiResponse = academicallyApi.forgotPassword(EmailBody(email))) {
                            is NoCurrentUser.Success -> apiResponse.data!!.message
                            is NoCurrentUser.Error -> apiResponse.error!!
                        }

                        error(message)
                    }
                }

                _forgotClickable.value = true
            } catch (e: Exception) {
                _forgotClickable.value = true
                error("Something went wrong processing your credentials.")
            }
        }
    }
}