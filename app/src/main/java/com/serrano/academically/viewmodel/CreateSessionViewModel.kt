package com.serrano.academically.viewmodel

import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.Session
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
class CreateSessionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val messageRepository: MessageRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _sessionSettings = MutableStateFlow(SessionSettings("", "", "", "", ""))
    val sessionSettings: StateFlow<SessionSettings> = _sessionSettings.asStateFlow()

    fun getData(id: Int) {
        viewModelScope.launch {
            try {
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()
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

    fun createSession(settings: SessionSettings, courseId: Int, moduleId: Int, tutorId: Int, studentId: Int, messageId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                sessionRepository.addSession(
                    Session(
                        courseId = courseId,
                        tutorId = tutorId,
                        studentId = studentId,
                        moduleId = moduleId,
                        startTime = LocalDateTime.parse("${settings.date} ${settings.startTime}", formatter),
                        endTime = LocalDateTime.parse("${settings.date} ${settings.endTime}", formatter),
                        location = settings.location
                    )
                )
                messageRepository.updateMessageStatus("ACCEPT", messageId)
                navigate()
            }
            catch (e: Exception) {
                updateSessionSettings(sessionSettings.value.copy(error = "Invalid Time"))
            }
        }
    }
}