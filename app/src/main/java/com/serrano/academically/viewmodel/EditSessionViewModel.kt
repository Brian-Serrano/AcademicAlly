package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.SessionData
import com.serrano.academically.api.UpdateSessionBody
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.RateDialogStates
import com.serrano.academically.utils.SessionSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSessionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _sessionData = MutableStateFlow(SessionData())
    val sessionData: StateFlow<SessionData> = _sessionData.asStateFlow()

    private val _sessionSettings = MutableStateFlow(SessionSettings())
    val sessionSettings: StateFlow<SessionSettings> = _sessionSettings.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen: StateFlow<Boolean> = _isFilterDialogOpen.asStateFlow()

    private val _isRateDialogOpen = MutableStateFlow(false)
    val isRateDialogOpen: StateFlow<Boolean> = _isRateDialogOpen.asStateFlow()

    private val _rateDialogStates = MutableStateFlow(RateDialogStates())
    val rateDialogStates: StateFlow<RateDialogStates> = _rateDialogStates.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val sessionCache = ActivityCacheManager.editSession[sessionId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (sessionCache != null && currentUserCache != null) {
                    _sessionData.value = sessionCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(sessionId, context)
                }

                refreshSessionSettings()

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(sessionId, context)

                refreshSessionSettings()

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshSessionSettings() {
        val startTime = Utils.convertToDate(_sessionData.value.startTime)
        val endTime = Utils.convertToDate(_sessionData.value.endTime)

        _sessionSettings.value = SessionSettings(
            date = String.format(
                "%02d/%02d/%04d",
                startTime.dayOfMonth,
                startTime.monthValue,
                startTime.year
            ),
            startTime = Utils.toMilitaryTime(
                startTime.hour,
                startTime.minute
            ),
            endTime = Utils.toMilitaryTime(
                endTime.hour,
                endTime.minute
            ),
            location = _sessionData.value.location,
            error = ""
        )
    }

    private suspend fun callApi(sessionId: Int, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
            when (val sessionResponse = academicallyApi.getSessionSettings(sessionId)) {
                is WithCurrentUser.Success -> {
                    _sessionData.value = sessionResponse.data!!
                    _drawerData.value = sessionResponse.currentUser!!

                    ActivityCacheManager.editSession[sessionId] = sessionResponse.data
                    ActivityCacheManager.currentUser = sessionResponse.currentUser
                }
                is WithCurrentUser.Error -> throw IllegalArgumentException(sessionResponse.error)
            }
        }
    }

    fun updateSessionSettings(newSessionSettings: SessionSettings) {
        _sessionSettings.value = newSessionSettings
    }

    fun updateSession(context: Context, settings: SessionSettings, sessionId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                _buttonEnabled.value = false

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                    academicallyApi.updateSession(
                        UpdateSessionBody(
                            sessionId,
                            Utils.validateDate("${settings.date} ${settings.startTime}"),
                            Utils.validateDate("${settings.date} ${settings.endTime}"),
                            settings.location
                        )
                    )

                    ActivityCacheManager.editSession.remove(sessionId)
                    ActivityCacheManager.notificationsSessions = null
                }

                _buttonEnabled.value = true

                navigate()
            } catch (e: Exception) {
                _buttonEnabled.value = true
                updateSessionSettings(sessionSettings.value.copy(error = "Invalid Time"))
            }
        }
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }

    fun toggleRateDialog(bool: Boolean) {
        _isRateDialogOpen.value = bool
    }

    fun updateRateDialogStates(newRateDialogStates: RateDialogStates) {
        _rateDialogStates.value = newRateDialogStates
    }
}