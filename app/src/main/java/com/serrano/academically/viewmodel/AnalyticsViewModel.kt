package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AnalyticsData
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(AnalyticsData())
    val userData: StateFlow<AnalyticsData> = _userData.asStateFlow()

    private val _userCourses = MutableStateFlow<List<Pair<CourseSkill, String>>>(emptyList())
    val userCourses: StateFlow<List<Pair<CourseSkill, String>>> = _userCourses.asStateFlow()

    private val _animationPlayed = MutableStateFlow(false)
    val animationPlayed: StateFlow<Boolean> = _animationPlayed.asStateFlow()

    private val _chartTabIndex = MutableStateFlow(0)
    val chartTabIndex: StateFlow<Int> = _chartTabIndex.asStateFlow()

    private val _chartCamera = MutableStateFlow(0f)
    val chartCamera: StateFlow<Float> = _chartCamera.asStateFlow()

    private val _chartViewSize = MutableStateFlow(0f)
    val chartViewSize: StateFlow<Float> = _chartViewSize.asStateFlow()

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch user statistics
                _userData.value = userRepository.getAnalyticsData(id).first()

                // Fetch user courses base on role
                _userCourses.value = courseSkillRepository
                    .getCourseSkillsOfUser(id, _userData.value.role)
                    .first()
                    .map { Pair(it, GetCourses.getCourseNameById(it.courseId, context)) }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun toggleAnimation(bool: Boolean) {
        _animationPlayed.value = bool
    }

    fun updateCamera(delta: Float) {
        _chartCamera.value = delta
    }

    fun updateChartSize(actualSize: Float) {
        _chartViewSize.value = actualSize
    }

    fun updateChartTab(newIdx: Int) {
        updateCamera(0f)
        toggleAnimation(false)
        _chartTabIndex.value = newIdx
    }
}