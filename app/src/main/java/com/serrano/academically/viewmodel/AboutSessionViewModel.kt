package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionInfo
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptySession
import com.serrano.academically.utils.emptySessionInfo
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutSessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _sessionDetails = MutableStateFlow(emptySession())
    val sessionDetails: StateFlow<Session> = _sessionDetails.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(emptyUserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _sessionInfo = MutableStateFlow(emptySessionInfo())
    val sessionInfo: StateFlow<SessionInfo> = _sessionInfo.asStateFlow()

    fun getData(userId: Int, sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch session
                _sessionDetails.value = sessionRepository.getSession(sessionId).first()

                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch names for session ids
                _sessionInfo.value = SessionInfo(
                    courseName = GetCourses.getCourseNameById(sessionDetails.value.courseId, context),
                    tutorName = userRepository.getUserName(sessionDetails.value.tutorId).first(),
                    studentName = userRepository.getUserName(sessionDetails.value.studentId).first(),
                    moduleName = GetModules.getModuleByCourseAndModuleId(sessionDetails.value.courseId, sessionDetails.value.moduleId, context)
                )
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}