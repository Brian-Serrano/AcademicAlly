package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionSettings
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Session
import com.serrano.academically.utils.AchievementProgress
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.completeSessions
import com.serrano.academically.utils.emptySession
import com.serrano.academically.utils.toMilitaryTime
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

    private val _session = MutableStateFlow(emptySession())
    val session: StateFlow<Session> = _session.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _sessionSettings = MutableStateFlow(SessionSettings("", "", "", "", ""))
    val sessionSettings: StateFlow<SessionSettings> = _sessionSettings.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen: StateFlow<Boolean> = _isFilterDialogOpen.asStateFlow()

    fun getData(userId: Int, sessionId: Int) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch session
                val s = sessionRepository.getSession(sessionId).first()
                _session.value = s

                // Place session info in text fields
                _sessionSettings.value = SessionSettings(
                    date = String.format("%02d/%02d/%04d", s.startTime.dayOfMonth, s.startTime.monthValue, s.startTime.year),
                    startTime = toMilitaryTime(listOf(s.startTime.hour, s.startTime.minute)),
                    endTime = toMilitaryTime(listOf(s.endTime.hour, s.endTime.minute)),
                    location = s.location,
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
                // Update Session
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
                sessionRepository.updateSession(
                    startTime = LocalDateTime.parse("${settings.date} ${settings.startTime}", formatter),
                    endTime = LocalDateTime.parse("${settings.date} ${settings.endTime}", formatter),
                    location = settings.location,
                    expireDate = LocalDateTime.now().plusDays(28),
                    sessionId = sessionId
                )

                navigate()
            }
            catch (e: Exception) {
                updateSessionSettings(sessionSettings.value.copy(error = "Invalid Time"))
            }
        }
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }
}