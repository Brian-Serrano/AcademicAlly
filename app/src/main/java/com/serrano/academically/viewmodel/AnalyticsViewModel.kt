package com.serrano.academically.viewmodel

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AnalyticsData
import com.serrano.academically.utils.ChartState
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

    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

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

    fun updateChartState(newState: ChartState) {
        _chartState.value = newState
    }

    fun toggleAnimation(bool: Boolean) {
        _animationPlayed.value = bool
    }

    fun updateChartTab(newIdx: Int) {
        toggleAnimation(false)
        _chartTabIndex.value = newIdx
    }
}