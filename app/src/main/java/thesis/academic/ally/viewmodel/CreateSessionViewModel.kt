package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.CreateSessionBody
import thesis.academic.ally.api.Message
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.SessionSettings
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class CreateSessionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _sessionSettings = MutableStateFlow(SessionSettings())
    val sessionSettings: StateFlow<SessionSettings> = _sessionSettings.asStateFlow()

    private val _message = MutableStateFlow(Message())
    val message: StateFlow<Message> = _message.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(messageId: Int) {
        viewModelScope.launch {
            try {
                val messageCache = ActivityCacheManager.createSession[messageId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (messageCache != null && currentUserCache != null) {
                    _message.value = messageCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(messageId)
                }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(messageId: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(messageId)

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(messageId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        when (val message = academicallyApi.getMessage(messageId)) {
            is WithCurrentUser.Success -> {
                _message.value = message.data!!
                mutableDrawerData.value = message.currentUser!!

                ActivityCacheManager.createSession[messageId] = message.data
                ActivityCacheManager.currentUser = message.currentUser
            }
            is WithCurrentUser.Error -> throw IllegalArgumentException(message.error)
        }
    }

    fun updateSessionSettings(newSessionSettings: SessionSettings) {
        _sessionSettings.value = newSessionSettings
    }

    fun createSession(settings: SessionSettings, message: Message, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                _buttonEnabled.value = false

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val apiResponse = academicallyApi.createSession(
                    CreateSessionBody(
                        Utils.validateDate("${settings.date} ${settings.startTime}"),
                        Utils.validateDate("${settings.date} ${settings.endTime}"),
                        message.messageId,
                        message.studentId,
                        message.tutorId,
                        message.courseId,
                        message.moduleId,
                        settings.location
                    )
                )
                Utils.showToast(
                    when (apiResponse) {
                        is NoCurrentUser.Success -> apiResponse.data!!
                        is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                    },
                    getApplication()
                )

                ActivityCacheManager.createSession.remove(message.messageId)
                ActivityCacheManager.aboutStudent.remove(message.messageId)
                ActivityCacheManager.notificationsSessions = null
                ActivityCacheManager.notificationsMessages = null
                ActivityCacheManager.archiveAcceptedMessages = null

                _buttonEnabled.value = true

                navigate()
            } catch (e: Exception) {
                _buttonEnabled.value = true
                updateSessionSettings(sessionSettings.value.copy(error = "Invalid Time"))
            }
        }
    }
}