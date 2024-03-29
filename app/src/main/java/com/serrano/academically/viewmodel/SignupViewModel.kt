package com.serrano.academically.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.SignupBody
import com.serrano.academically.api.AuthenticationResponse
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.SignupInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _signupInput = MutableStateFlow(SignupInput())
    val signupInput: StateFlow<SignupInput> = _signupInput.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    fun signup(role: String, si: SignupInput, navigate: () -> Unit, error: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _buttonEnabled.value = false

                val assessment = userCacheRepository.userDataStore.data.first()
                val rating = Utils.eligibilityComputingAlgorithm(
                    assessment.score,
                    assessment.items,
                    assessment.evaluator
                )
                val response = academicallyApi.signup(
                    SignupBody(
                        si.name,
                        role,
                        si.email,
                        si.password,
                        si.confirmPassword,
                        assessment.eligibility,
                        assessment.courseId,
                        if (rating.isNaN()) 0.0 else rating,
                        assessment.score,
                        assessment.items,
                        assessment.evaluator
                    )
                )
                when (response) {
                    is AuthenticationResponse.SuccessResponse -> {
                        Utils.showToast(response.achievements, getApplication())
                        userCacheRepository.clearAssessmentResultData()
                        userCacheRepository.updateDataByLoggingIn(false, response.token, si.email, si.password, role)
                        updateInput(si.copy(name = "", email = "", password = "", confirmPassword = "", error = ""))
                        ActivityCacheManager.clearCache()
                        _buttonEnabled.value = true
                        navigate()
                    }
                    is AuthenticationResponse.SuccessNoAssessment -> {
                        userCacheRepository.updateDataByLoggingIn(false, response.token, si.email, si.password, role)
                        updateInput(si.copy(name = "", email = "", password = "", confirmPassword = "", error = ""))
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

    fun updateInput(newSignupInput: SignupInput) {
        _signupInput.value = newSignupInput
    }
}