package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Message
import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
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
class CreateSessionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _sessionSettings = MutableStateFlow(SessionSettings())
    val sessionSettings: StateFlow<SessionSettings> = _sessionSettings.asStateFlow()

    private val _messageInfo = MutableStateFlow(Message())
    val messageInfo: StateFlow<Message> = _messageInfo.asStateFlow()

    private val _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled: StateFlow<Boolean> = _buttonEnabled.asStateFlow()

    fun getData(id: Int, messageId: Int) {
        viewModelScope.launch {
            try {
                // Fetch user drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch message information
                _messageInfo.value = messageRepository.getMessage(messageId).first()

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateSessionSettings(newSessionSettings: SessionSettings) {
        _sessionSettings.value = newSessionSettings
    }

    fun createSession(
        settings: SessionSettings,
        courseId: Int,
        moduleId: Int,
        tutorId: Int,
        studentId: Int,
        messageId: Int,
        navigate: () -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                // Disable button temporarily
                _buttonEnabled.value = false

                // Save session
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
                sessionRepository.addSession(
                    Session(
                        courseId = courseId,
                        tutorId = tutorId,
                        studentId = studentId,
                        moduleId = moduleId,
                        startTime = LocalDateTime.parse(
                            "${settings.date} ${settings.startTime}",
                            formatter
                        ),
                        endTime = LocalDateTime.parse(
                            "${settings.date} ${settings.endTime}",
                            formatter
                        ),
                        location = settings.location,
                        expireDate = LocalDateTime.now().plusDays(28)
                    )
                )

                // Update message as ACCEPTED
                messageRepository.updateMessageStatus("ACCEPT", messageId)

                // Update student and tutor accepted requests and points
                userRepository.updateStudentAcceptedRequests(0.2, studentId)
                userRepository.updateTutorAcceptedRequests(0.2, tutorId)

                // Update student points and accepted requests achievement
                val achievementProgressStudent =
                    userRepository.getBadgeProgressAsStudent(studentId).first().achievement
                val computedProgressStudent = HelperFunctions.computeAchievementProgress(
                    userRepository.getStudentPoints(studentId).first(),
                    listOf(10, 25, 50, 100, 200),
                    listOf(7, 8, 9, 10, 11),
                    HelperFunctions.computeAchievementProgress(
                        userRepository.getStudentAcceptedRequests(studentId).first().toDouble(),
                        listOf(1, 3, 10),
                        listOf(4, 5, 6),
                        achievementProgressStudent
                    )
                )
                userRepository.updateStudentBadgeProgress(computedProgressStudent, studentId)

                // Update tutor points and accepted requests achievement
                val achievementProgressTutor =
                    userRepository.getBadgeProgressAsTutor(tutorId).first().achievement
                val computedProgressTutor = HelperFunctions.computeAchievementProgress(
                    userRepository.getTutorPoints(tutorId).first(),
                    listOf(10, 25, 50, 100, 200),
                    listOf(7, 8, 9, 10, 11),
                    HelperFunctions.computeAchievementProgress(
                        userRepository.getTutorAcceptedRequests(tutorId).first().toDouble(),
                        listOf(1, 5, 10, 20),
                        listOf(0, 1, 2, 3),
                        achievementProgressTutor
                    )
                )
                userRepository.updateTutorBadgeProgress(computedProgressTutor, tutorId)

                // Show toast message if an achievement is completed
                HelperFunctions.checkCompletedAchievements(
                    achievementProgressTutor, computedProgressTutor
                ) {
                    Toast.makeText(
                        context,
                        GetAchievements.getAchievements(0, context)[it][0],
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Enable button again
                _buttonEnabled.value = true

                navigate()
            } catch (e: Exception) {
                _buttonEnabled.value = true
                updateSessionSettings(sessionSettings.value.copy(error = "Invalid Time"))
            }
        }
    }
}