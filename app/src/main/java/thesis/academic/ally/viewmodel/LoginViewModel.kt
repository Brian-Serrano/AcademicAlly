package thesis.academic.ally.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.AuthenticationResponse
import thesis.academic.ally.api.EmailBody
import thesis.academic.ally.api.LoginBody
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.utils.LoginInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _loginInput = MutableStateFlow(LoginInput())
    val loginInput: StateFlow<LoginInput> = _loginInput.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    private val _forgotClickable = MutableStateFlow(true)
    val forgotClickable: StateFlow<Boolean> = _forgotClickable.asStateFlow()

    fun updateInput(newLoginInput: LoginInput) {
        _loginInput.value = newLoginInput
    }

    fun login(role: String, li: LoginInput, navigate: () -> Unit, error: (String) -> Unit) {
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
                        assessment.eligibility,
                        Firebase.messaging.token.await()
                    )
                )
                when (response) {
                    is AuthenticationResponse.SuccessResponse -> {
                        Utils.showToast(response.achievements, getApplication())
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
                    !Utils.emailPattern.containsMatchIn(email) -> {
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