package com.serrano.academically.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.RateDialogStates
import com.serrano.academically.utils.SessionSettings
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class EditSessionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _session = MutableStateFlow(Session())
    val session: StateFlow<Session> = _session.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

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

    fun getData(userId: Int, sessionId: Int) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch session
                val session = sessionRepository.getSession(sessionId).first()
                _session.value = session

                // Place session info in text fields
                _sessionSettings.value = SessionSettings(
                    date = String.format(
                        "%02d/%02d/%04d",
                        session.startTime.dayOfMonth,
                        session.startTime.monthValue,
                        session.startTime.year
                    ),
                    startTime = HelperFunctions.toMilitaryTime(
                        session.startTime.hour,
                        session.startTime.minute
                    ),
                    endTime = HelperFunctions.toMilitaryTime(
                        session.endTime.hour,
                        session.endTime.minute
                    ),
                    location = session.location,
                    error = ""
                )
                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateSessionSettings(newSessionSettings: SessionSettings) {
        _sessionSettings.value = newSessionSettings
    }

    fun updateSession(settings: SessionSettings, sessionId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                // Disable button temporarily
                _buttonEnabled.value = false

                // Update Session
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
                sessionRepository.updateSession(
                    startTime = LocalDateTime.parse(
                        "${settings.date} ${settings.startTime}",
                        formatter
                    ),
                    endTime = LocalDateTime.parse(
                        "${settings.date} ${settings.endTime}",
                        formatter
                    ),
                    location = settings.location,
                    expireDate = LocalDateTime.now().plusDays(28),
                    sessionId = sessionId
                )

                // Enable button again
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