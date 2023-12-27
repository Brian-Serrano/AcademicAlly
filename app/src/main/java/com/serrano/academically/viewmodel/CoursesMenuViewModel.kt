package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesMenuViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _user = MutableStateFlow(UserDrawerData())
    val user: StateFlow<UserDrawerData> = _user.asStateFlow()

    private val _courseSkills =
        MutableStateFlow<List<Pair<CourseSkill, Pair<String, String>>>>(emptyList())
    val courseSkills: StateFlow<List<Pair<CourseSkill, Pair<String, String>>>> =
        _courseSkills.asStateFlow()

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch user drawer data
                _user.value = userRepository.getUserDataForDrawer(id).first()
                val role = _user.value.role

                // Fetch user courses base on role
                _courseSkills.value = courseSkillRepository
                    .getCourseSkillsOfUser(id, role)
                    .first()
                    .map { Pair(it, GetCourses.getCourseAndDescription(it.courseId, context)) }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}