package com.serrano.academically.viewmodel

import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionSettings
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _sessionSettings = MutableStateFlow(SessionSettings("", "", "", "", ""))
    val sessionSettings: StateFlow<SessionSettings> = _sessionSettings.asStateFlow()

    fun getData(userId: Int, sessionId: Int) {
        viewModelScope.launch {
            try {
                _drawerData.value = userRepository.getUserDataForDrawer(userId).first()
                val session = sessionRepository.getSession(sessionId).first()
                _sessionSettings.value = SessionSettings(
                    date = String.format("%02d/%02d/%04d", session.startTime.dayOfMonth, session.startTime.monthValue, session.startTime.year),
                    startTime = String.format("%02d:%02d", session.startTime.hour, session.startTime.minute),
                    endTime = String.format("%02d:%02d", session.endTime.hour, session.endTime.minute),
                    location = session.location,
                    error = ""
                )
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
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
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                sessionRepository.updateSession(
                    startTime = LocalDateTime.parse("${settings.date} ${settings.startTime}", formatter),
                    endTime = LocalDateTime.parse("${settings.date} ${settings.endTime}", formatter),
                    location = settings.location,
                    sessionId = sessionId
                )
                navigate()
            }
            catch (e: Exception) {
                updateSessionSettings(sessionSettings.value.copy(error = "Invalid Time"))
            }
        }
    }

    fun completeSession(sessionId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                sessionRepository.completeSession(sessionId)
                navigate()
            }
            catch (e: Exception) {
                updateSessionSettings(sessionSettings.value.copy(error = "Something Went Wrong"))
            }
        }
    }
}