package com.serrano.academically.viewmodel

import android.content.Context
import android.util.Log
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.User
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUser
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
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _user = MutableStateFlow(emptyUserDrawerData())
    val user: StateFlow<UserDrawerData> = _user.asStateFlow()

    private val _courseSkills = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val courseSkills: StateFlow<List<Pair<String, String>>> = _courseSkills.asStateFlow()

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch user drawer data
                _user.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch user courses base on role
                _courseSkills.value = courseSkillRepository.getThreeCourseSkillsOfUserNoRating(id, user.value.role).first().map { GetCourses.getCourseAndDescription(it, context) }

                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                e.printStackTrace()
                _processState.value = ProcessState.Error
            }
        }
    }
}