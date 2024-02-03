package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.CreateSessionBody
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.Message
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSessionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _sessionSettings = MutableStateFlow(SessionSettings())
    val sessionSettings: StateFlow<SessionSettings> = _sessionSettings.asStateFlow()

    private val _message = MutableStateFlow(Message())
    val message: StateFlow<Message> = _message.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(messageId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val messageCache = ActivityCacheManager.createSession[messageId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (messageCache != null && currentUserCache != null) {
                    _message.value = messageCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(messageId, context)
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(messageId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(messageId, context)

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(messageId: Int, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

        when (val message = academicallyApi.getMessage(messageId)) {
            is WithCurrentUser.Success -> {
                _message.value = message.data!!
                _drawerData.value = message.currentUser!!

                ActivityCacheManager.createSession[messageId] = message.data
                ActivityCacheManager.currentUser = message.currentUser
            }
            is WithCurrentUser.Error -> throw IllegalArgumentException(message.error)
        }
    }

    fun updateSessionSettings(newSessionSettings: SessionSettings) {
        _sessionSettings.value = newSessionSettings
    }

    fun createSession(
        settings: SessionSettings,
        message: Message,
        navigate: () -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                _buttonEnabled.value = false

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

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
                    context
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