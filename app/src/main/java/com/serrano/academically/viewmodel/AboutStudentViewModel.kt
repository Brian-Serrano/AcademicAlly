package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Message
import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.MessageCourse
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutStudentViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _messageDetails = MutableStateFlow(Triple(Message(), MessageCourse(), UserInfo()))
    val messageDetails: StateFlow<Triple<Message, MessageCourse, UserInfo>> =
        _messageDetails.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(UserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _rejectButtonEnabled = MutableStateFlow(true)
    val rejectButtonEnabled: StateFlow<Boolean> = _rejectButtonEnabled.asStateFlow()

    fun getData(userId: Int, messageId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(userId).first()
                val role = _userData.value.role

                // Fetch message
                val message = messageRepository.getMessage(messageId).first()
                _messageDetails.value = Triple(
                    message,
                    MessageCourse(
                        courseName = GetCourses.getCourseNameById(message.courseId, context),
                        moduleName = GetModules.getModuleByCourseAndModuleId(
                            message.courseId,
                            message.moduleId,
                            context
                        )
                    ),
                    userRepository.getUserInfo(if (role == "STUDENT") message.tutorId else message.studentId)
                        .first()
                )

                // Mark message as seen by tutor
                messageRepository.updateTutorView(messageId)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun respond(
        studentId: Int,
        tutorId: Int,
        status: String,
        messageId: Int,
        context: Context,
        navigate: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Disable reject button temporarily
                _rejectButtonEnabled.value = false

                // Update message to be REJECTED
                messageRepository.updateMessageStatus(status, messageId)

                // Update student and tutor denied requests
                userRepository.updateStudentDeniedRequests(studentId)
                userRepository.updateTutorDeniedRequests(tutorId)

                // Update tutor denied requests achievement
                val achievementProgressTutor =
                    userRepository.getBadgeProgressAsTutor(tutorId).first().achievement
                val computedProgressTutor = HelperFunctions.computeAchievementProgress(
                    userRepository.getTutorDeniedRequests(tutorId).first().toDouble(),
                    listOf(1, 3, 10),
                    listOf(4, 5, 6),
                    achievementProgressTutor
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

                // Enable reject button again
                _rejectButtonEnabled.value = true

                navigate()
            } catch (e: Exception) {
                _rejectButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }
}