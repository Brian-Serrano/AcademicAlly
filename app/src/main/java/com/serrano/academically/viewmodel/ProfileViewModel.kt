package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.User
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _userData = MutableStateFlow(User())
    val userData: StateFlow<User> = _userData.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _statisticsData = MutableStateFlow<List<List<String>>>(emptyList())
    val statisticsData: StateFlow<List<List<String>>> = _statisticsData.asStateFlow()

    private val _courses = MutableStateFlow<List<List<Pair<String, String>>>>(emptyList())
    val courses: StateFlow<List<List<Pair<String, String>>>> = _courses.asStateFlow()

    fun getData(userId: Int, otherId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch user drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch the other user data the user visited
                _userData.value = userRepository.getUser(otherId).first()

                // Fetch the other users courses
                _courses.value = listOf(
                    courseSkillRepository.getThreeCourseSkillsOfUserNoRating(otherId, "STUDENT")
                        .first().map { GetCourses.getCourseAndDescription(it, context) },
                    courseSkillRepository.getThreeCourseSkillsOfUserNoRating(otherId, "TUTOR")
                        .first().map { GetCourses.getCourseAndDescription(it, context) }
                )
                val u = userData.value

                // Organize other user data
                _statisticsData.value = listOf(
                    listOf(
                        HelperFunctions.roundRating(u.studentPoints),
                        HelperFunctions.roundRating(u.studentAssessmentPoints),
                        HelperFunctions.roundRating(u.studentRequestPoints),
                        HelperFunctions.roundRating(u.studentSessionPoints),
                        HelperFunctions.roundRating(u.studentAssignmentPoints),
                        u.sessionsCompletedAsStudent,
                        u.requestsSent,
                        u.deniedRequests,
                        u.acceptedRequests,
                        u.assignmentsTaken,
                        u.assessmentsTakenAsStudent,
                        u.badgeProgressAsStudent.count { it >= 100.0 },
                        u.numberOfRatesAsStudent,
                        u.tutorsRated
                    ).map { it.toString() },
                    listOf(
                        HelperFunctions.roundRating(u.tutorPoints),
                        HelperFunctions.roundRating(u.tutorAssessmentPoints),
                        HelperFunctions.roundRating(u.tutorRequestPoints),
                        HelperFunctions.roundRating(u.tutorSessionPoints),
                        HelperFunctions.roundRating(u.tutorAssignmentPoints),
                        u.sessionsCompletedAsTutor,
                        u.requestsReceived,
                        u.requestsDenied,
                        u.requestsAccepted,
                        u.assignmentsCreated,
                        u.assessmentsTakenAsTutor,
                        u.badgeProgressAsTutor.count { it >= 100.0 },
                        u.numberOfRatesAsTutor,
                        u.studentsRated
                    ).map { it.toString() }
                )
                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }
}