package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Rating
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
class AboutTutorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(UserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _tutorInfo = MutableStateFlow(UserInfo())
    val tutorInfo: StateFlow<UserInfo> = _tutorInfo.asStateFlow()

    private val _tutorRating = MutableStateFlow(Rating())
    val tutorRating: StateFlow<Rating> = _tutorRating.asStateFlow()

    private val _tutorCourses = MutableStateFlow<List<Pair<CourseSkill, String>>>(emptyList())
    val tutorCourses: StateFlow<List<Pair<CourseSkill, String>>> = _tutorCourses.asStateFlow()

    fun getData(userId: Int, tutorId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch tutor information
                _tutorInfo.value = userRepository.getUserInfo(tutorId).first()

                // Fetch tutor performance rating
                _tutorRating.value = userRepository.getTutorRating(tutorId).first()

                // Fetch tutor courses
                _tutorCourses.value = courseSkillRepository
                    .getCourseSkillsOfUser(tutorId, "TUTOR")
                    .first()
                    .map { Pair(it, GetCourses.getCourseNameById(it.courseId, context)) }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}