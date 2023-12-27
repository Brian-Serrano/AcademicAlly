package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.HelperFunctions
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
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _user = MutableStateFlow(UserDrawerData())
    val user: StateFlow<UserDrawerData> = _user.asStateFlow()

    private val _courseSkills = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val courseSkills: StateFlow<List<Pair<String, String>>> = _courseSkills.asStateFlow()

    private val _ratings = MutableStateFlow(Pair(0.0, 0.0))
    val ratings: StateFlow<Pair<Double, Double>> = _ratings.asStateFlow()

    private val _animationPlayed = MutableStateFlow(false)
    val animationPlayed: StateFlow<Boolean> = _animationPlayed.asStateFlow()

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch user drawer data
                _user.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch user courses and ratings base on role
                val role = _user.value.role
                val userCourses = courseSkillRepository.getCourseSkillsOfUser(id, role).first()
                val rating = if (role == "STUDENT") userRepository.getStudentRating(id)
                    .first() else userRepository.getTutorRating(id).first()
                _courseSkills.value =
                    userCourses.map { GetCourses.getCourseAndDescription(it.courseId, context) }
                _ratings.value = Pair(
                    HelperFunctions.roundRating(if (userCourses.isNotEmpty()) userCourses.map { it.assessmentRating / it.assessmentTaken }
                        .average() else 0.0),
                    HelperFunctions.roundRating(if (rating.number > 0) rating.rating / rating.number else 0.0)
                )

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun playAnimation() {
        _animationPlayed.value = true
    }
}