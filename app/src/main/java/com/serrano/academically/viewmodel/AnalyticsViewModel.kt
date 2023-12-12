package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AnalyticsData
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.emptyAnalyticsData
import com.serrano.academically.utils.emptyUserDrawerData
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
class AnalyticsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(emptyAnalyticsData())
    val userData: StateFlow<AnalyticsData> = _userData.asStateFlow()

    private val _userCourses = MutableStateFlow<List<CourseSkill>>(emptyList())
    val userCourses: StateFlow<List<CourseSkill>> = _userCourses.asStateFlow()

    private val _courseName = MutableStateFlow<List<String>>(emptyList())
    val courseName: StateFlow<List<String>> = _courseName.asStateFlow()

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch user statistics
                _userData.value = userRepository.getAnalyticsData(id).first()

                // Fetch user courses base on role
                val course = courseSkillRepository.getCourseSkillsOfUser(id, userData.value.role).first()
                _userCourses.value = course
                _courseName.value = course.map { GetCourses.getCourseNameById(it.courseId, context) }

                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}