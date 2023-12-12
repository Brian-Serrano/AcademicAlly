package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfo
import com.serrano.academically.utils.emptyUserDrawerData
import com.serrano.academically.utils.emptyUserInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutTutorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(emptyUserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _tutorInfo = MutableStateFlow(emptyUserInfo())
    val tutorInfo: StateFlow<UserInfo> = _tutorInfo.asStateFlow()

    private val _tutorCourses = MutableStateFlow<List<CourseSkill>>(emptyList())
    val tutorCourses: StateFlow<List<CourseSkill>> = _tutorCourses.asStateFlow()

    private val _courseNames = MutableStateFlow<List<String>>(emptyList())
    val courseNames: StateFlow<List<String>> = _courseNames.asStateFlow()

    fun getData(userId: Int, tutorId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch tutor information
                _tutorInfo.value = userRepository.getUserInfo(tutorId).first()

                // Fetch tutor courses
                val courses = courseSkillRepository.getCourseSkillsOfUser(tutorId, "TUTOR").first()
                _tutorCourses.value = courses
                _courseNames.value = courses.map { GetCourses.getCourseNameById(it.courseId, context) }

                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}