package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.Message
import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.Session
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.MessageCourse
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfo
import com.serrano.academically.utils.emptyMessage
import com.serrano.academically.utils.emptyMessageCourse
import com.serrano.academically.utils.emptySession
import com.serrano.academically.utils.emptySessionInfo
import com.serrano.academically.utils.emptyUserDrawerData
import com.serrano.academically.utils.emptyUserInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutStudentViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _messageDetails = MutableStateFlow(emptyMessage())
    val messageDetails: StateFlow<Message> = _messageDetails.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(emptyUserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _messageInfo = MutableStateFlow(emptyMessageCourse())
    val messageInfo: StateFlow<MessageCourse> = _messageInfo.asStateFlow()

    private val _userInfo = MutableStateFlow(emptyUserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo.asStateFlow()

    fun getData(userId: Int, messageId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _messageDetails.value = messageRepository.getMessage(messageId).first()
                _messageInfo.value = MessageCourse(
                    courseName = GetCourses.getCourseNameById(messageDetails.value.courseId, context),
                    moduleName = GetModules.getModuleByCourseAndModuleId(messageDetails.value.courseId, messageDetails.value.moduleId, context)
                )
                _userData.value = userRepository.getUserDataForDrawer(userId).first()
                _userInfo.value = userRepository.getUserInfo(if (userData.value.role == "STUDENT") messageDetails.value.tutorId else messageDetails.value.studentId).first()
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun respond(status: String, messageId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                messageRepository.updateMessageStatus(status, messageId)
                navigate()
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}