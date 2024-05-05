package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.api.SessionData
import thesis.academic.ally.api.UpdateSessionBody
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.RateDialogStates
import thesis.academic.ally.utils.SessionSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSessionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

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

    fun getData(sessionId: Int) {
        viewModelScope.launch {
            try {
                val sessionCache = ActivityCacheManager.editSession[sessionId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (sessionCache != null && currentUserCache != null) {
                    _sessionData.value = sessionCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(sessionId)
                }

                refreshSessionSettings()

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(sessionId: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(sessionId)

                refreshSessionSettings()

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
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
            location = _sessionData.value.location
        )
    }

    private suspend fun callApi(sessionId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        when (val sessionResponse = academicallyApi.getSessionSettings(sessionId)) {
            is WithCurrentUser.Success -> {
                _sessionData.value = sessionResponse.data!!
                mutableDrawerData.value = sessionResponse.currentUser!!

                ActivityCacheManager.editSession[sessionId] = sessionResponse.data
                ActivityCacheManager.currentUser = sessionResponse.currentUser
            }
            is WithCurrentUser.Error -> throw IllegalArgumentException(sessionResponse.error)
        }
    }

    fun updateSessionSettings(newSessionSettings: SessionSettings) {
        _sessionSettings.value = newSessionSettings
    }

    fun updateSession(settings: SessionSettings, sessionId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                _buttonEnabled.value = false

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

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