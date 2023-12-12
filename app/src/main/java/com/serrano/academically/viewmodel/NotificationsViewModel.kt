package com.serrano.academically.viewmodel

import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.MessageNotifications
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionNotifications
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Assignment
import com.serrano.academically.room.AssignmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val sessionRepository: SessionRepository,
    private val assignmentRepository: AssignmentRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(emptyUserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _message = MutableStateFlow<List<MessageNotifications>>(emptyList())
    val message: StateFlow<List<MessageNotifications>> = _message.asStateFlow()

    private val _session = MutableStateFlow<List<SessionNotifications>>(emptyList())
    val session: StateFlow<List<SessionNotifications>> = _session.asStateFlow()

    private val _messageUsers = MutableStateFlow<List<String>>(emptyList())
    val messageUsers: StateFlow<List<String>> = _messageUsers.asStateFlow()

    private val _assignment = MutableStateFlow<List<Assignment>>(emptyList())
    val assignment: StateFlow<List<Assignment>> = _assignment.asStateFlow()

    fun getData(id: Int) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch messages and sessions base on user role
                when (userData.value.role) {
                    "STUDENT" -> {
                        _session.value = sessionRepository.getStudentSessions(id).first()
                        _message.value = messageRepository.getStudentMessages(id).first()
                        _assignment.value = assignmentRepository.getStudentAssignments(id).first()
                    }
                    "TUTOR" -> {
                        _session.value = sessionRepository.getTutorSessions(id).first()
                        _message.value = messageRepository.getTutorMessages(id).first()
                        _assignment.value = assignmentRepository.getTutorAssignments(id).first()
                    }
                }
                // Fetch users who send/receive messages
                _messageUsers.value = message.value.map { userRepository.getUserName(if (userData.value.role == "STUDENT") it.tutorId else it.studentId).first() }

                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }
}