package thesis.academic.ally.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.AuthenticationResponse
import thesis.academic.ally.api.SignupBody
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.SignupInput
import thesis.academic.ally.utils.Utils
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
                        assessment.evaluator,
                        Firebase.messaging.token.await()
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