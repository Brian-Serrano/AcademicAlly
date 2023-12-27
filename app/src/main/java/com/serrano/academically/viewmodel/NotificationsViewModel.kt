package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Assignment
import com.serrano.academically.room.AssignmentRepository
import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.MessageNotifications
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionNotifications
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val sessionRepository: SessionRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(UserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _message =
        MutableStateFlow<List<Triple<MessageNotifications, String, String>>>(emptyList())
    val message: StateFlow<List<Triple<MessageNotifications, String, String>>> =
        _message.asStateFlow()

    private val _session = MutableStateFlow<List<Pair<SessionNotifications, String>>>(emptyList())
    val session: StateFlow<List<Pair<SessionNotifications, String>>> = _session.asStateFlow()

    private val _assignment =
        MutableStateFlow<List<Triple<Assignment, String, String>>>(emptyList())
    val assignment: StateFlow<List<Triple<Assignment, String, String>>> = _assignment.asStateFlow()

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch messages and sessions base on user role
                when (_userData.value.role) {
                    "STUDENT" -> {
                        _message.value = messageRepository
                            .getStudentMessages("WAITING", id)
                            .first()
                            .map {
                                Triple(
                                    it,
                                    userRepository.getUserName(it.tutorId).first(),
                                    GetCourses.getCourseNameById(it.courseId, context)
                                )
                            }
                        _session.value = sessionRepository
                            .getStudentSessions("UPCOMING", id)
                            .first()
                            .map { Pair(it, GetCourses.getCourseNameById(it.courseId, context)) }
                        _assignment.value = assignmentRepository
                            .getStudentAssignments("UNCOMPLETED", id)
                            .first()
                            .map {
                                Triple(
                                    it,
                                    GetCourses.getCourseNameById(it.courseId, context),
                                    GetModules.getModuleByCourseAndModuleId(
                                        it.courseId,
                                        it.moduleId,
                                        context
                                    )
                                )
                            }
                    }

                    "TUTOR" -> {
                        _message.value = messageRepository
                            .getTutorMessages("WAITING", id)
                            .first()
                            .map {
                                Triple(
                                    it,
                                    userRepository.getUserName(it.studentId).first(),
                                    GetCourses.getCourseNameById(it.courseId, context)
                                )
                            }
                        _session.value = sessionRepository
                            .getTutorSessions("UPCOMING", id)
                            .first()
                            .map { Pair(it, GetCourses.getCourseNameById(it.courseId, context)) }
                        _assignment.value = assignmentRepository
                            .getTutorAssignments("UNCOMPLETED", id)
                            .first()
                            .map {
                                Triple(
                                    it,
                                    GetCourses.getCourseNameById(it.courseId, context),
                                    GetModules.getModuleByCourseAndModuleId(
                                        it.courseId,
                                        it.moduleId,
                                        context
                                    )
                                )
                            }
                    }
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }
}