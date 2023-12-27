package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Assignment
import com.serrano.academically.room.AssignmentRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetAchievements
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _assignment = MutableStateFlow(Assignment())
    val assignment: StateFlow<Assignment> = _assignment.asStateFlow()

    private val _assessmentData = MutableStateFlow<List<List<String>>>(emptyList())
    val assessmentData: StateFlow<List<List<String>>> = _assessmentData.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _assessmentAnswers = MutableStateFlow<List<String>>(emptyList())
    val assessmentAnswers: StateFlow<List<String>> = _assessmentAnswers.asStateFlow()

    private val _nextButtonEnabled = MutableStateFlow(true)
    val nextButtonEnabled: StateFlow<Boolean> = _nextButtonEnabled.asStateFlow()

    private val _courseName = MutableStateFlow("")
    val courseName: StateFlow<String> = _courseName.asStateFlow()

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun addAnswer(answer: String, index: Int) {
        _assessmentAnswers.value =
            _assessmentAnswers.value.mapIndexed { idx, ans -> if (idx == index) answer else ans }
    }

    fun getData(id: Int, assignmentId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch assignment information
                _assignment.value = assignmentRepository.getAssignment(assignmentId).first()

                // Fetch assessments by assignment information
                _assessmentData.value = deserializeAssignmentData(_assignment.value.data)

                // Generate assessment answer spaces
                _assessmentAnswers.value = List(_assessmentData.value.size) { "" }

                // Get course name
                _courseName.value =
                    GetCourses.getCourseNameById(_assignment.value.courseId, context)

                // Mark assignment as seen by student
                assignmentRepository.updateStudentView(assignmentId)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun completeAssignment(
        id: Int,
        score: Int,
        assignmentId: Int,
        context: Context,
        navigate: (Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Disable next button temporarily
                _nextButtonEnabled.value = false

                // Complete assignment and save score
                assignmentRepository.completeAssignment(score, assignmentId)

                // Update student assessment taken and points
                userRepository.updateStudentAssignments(score * 0.1, id)

                // Update student points and taken assignments achievement
                val achievementProgress =
                    userRepository.getBadgeProgressAsStudent(id).first().achievement
                val computedProgress = HelperFunctions.computeAchievementProgress(
                    userRepository.getStudentPoints(id).first(),
                    listOf(10, 25, 50, 100, 200),
                    listOf(7, 8, 9, 10, 11),
                    HelperFunctions.computeAchievementProgress(
                        userRepository.getStudentAssignments(id).first().toDouble(),
                        listOf(1, 5, 10),
                        listOf(19, 20, 21),
                        achievementProgress
                    )
                )
                userRepository.updateStudentBadgeProgress(computedProgress, id)

                // Show toast message if an achievement is completed
                HelperFunctions.checkCompletedAchievements(
                    achievementProgress, computedProgress
                ) {
                    Toast.makeText(
                        context,
                        GetAchievements.getAchievements(0, context)[it][0],
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Enable next button again
                _nextButtonEnabled.value = true

                navigate(score)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deserializeAssignmentData(assessment: String): List<List<String>> {
        return Json.decodeFromString<List<List<String>>>(assessment)
    }
}