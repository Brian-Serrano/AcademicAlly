package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SessionInfo
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutSessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _sessionDetails = MutableStateFlow(Pair(Session(), SessionInfo()))
    val sessionDetails: StateFlow<Pair<Session, SessionInfo>> = _sessionDetails.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(UserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    fun getData(userId: Int, sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch session
                val session = sessionRepository.getSession(sessionId).first()
                _sessionDetails.value = Pair(
                    session,
                    SessionInfo(
                        courseName = GetCourses.getCourseNameById(session.courseId, context),
                        tutorName = userRepository.getUserName(session.tutorId).first(),
                        studentName = userRepository.getUserName(session.studentId).first(),
                        moduleName = GetModules.getModuleByCourseAndModuleId(
                            session.courseId,
                            session.moduleId,
                            context
                        )
                    )
                )

                // Mark session as seen by student
                sessionRepository.updateStudentView(sessionId)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}