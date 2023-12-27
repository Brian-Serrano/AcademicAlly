package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AssessmentResult
import com.serrano.academically.utils.GetAssessments
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
class AssessmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _courseName = MutableStateFlow("")
    val courseName: StateFlow<String> = _courseName.asStateFlow()

    private val _isDrawerShouldAvailable = MutableStateFlow(false)
    val isDrawerShouldAvailable: StateFlow<Boolean> = _isDrawerShouldAvailable.asStateFlow()

    private val _assessmentData = MutableStateFlow<List<List<String>>>(emptyList())
    val assessmentData: StateFlow<List<List<String>>> = _assessmentData.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _assessmentAnswers = MutableStateFlow<List<String>>(emptyList())
    val assessmentAnswers: StateFlow<List<String>> = _assessmentAnswers.asStateFlow()

    private val _nextButtonEnabled = MutableStateFlow(true)
    val nextButtonEnabled: StateFlow<Boolean> = _nextButtonEnabled.asStateFlow()

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun addAnswer(answer: String, index: Int) {
        _assessmentAnswers.value =
            assessmentAnswers.value.mapIndexed { idx, ans -> if (idx == index) answer else ans }
    }

    fun getData(userId: Int, courseId: Int, items: Int, type: String, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch course chosen
                _courseName.value = GetCourses.getCourseNameById(courseId, context)

                // Fetch assessments base on users chosen course and option
                _assessmentData.value =
                    GetAssessments.getAssessments(courseId, items, type, context)
                _assessmentAnswers.value = List(items) { "" }

                // Fetch and enable drawer data base on user login state
                if (userId != 0) {
                    _drawerData.value = userRepository.getUserDataForDrawer(userId).first()
                    _isDrawerShouldAvailable.value = true
                }
                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun evaluateAnswers(
        assessmentData: List<List<String>>,
        assessmentAnswers: List<String>,
        type: String,
        courseId: Int,
    ): AssessmentResult {

        // Get the score, items and evaluator
        val score = HelperFunctions.evaluateAnswer(assessmentData, assessmentAnswers, type)
        val items = assessmentData.size
        val evaluator = when (type) {
            "Multiple Choice" -> 0.75
            "Identification" -> 0.6
            else -> 0.9
        }

        // Pack them to AssessmentResult object
        return AssessmentResult(
            score = score,
            items = items,
            evaluator = evaluator,
            courseId = courseId,
            eligibility = if (score.toDouble() / items >= evaluator) "TUTOR" else "STUDENT"
        )
    }

    fun saveResultToPreferences(
        result: AssessmentResult,
        context: Context,
        navigate: (Int, Int, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Disable next button temporarily
                _nextButtonEnabled.value = false

                // Save result to preferences to save and retain data until login or signup
                UpdateUserPref.saveAssessmentResultData(
                    context,
                    result.eligibility,
                    result.courseId,
                    result.score,
                    result.items,
                    result.evaluator
                )

                // Clear the assessment option randomly generated in preferences
                UpdateUserPref.clearAssessmentType(context)

                // Enable next button again
                _nextButtonEnabled.value = true

                navigate(result.score, result.items, result.eligibility)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(
                    context,
                    "Something went wrong saving your assessment.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun updateCourseSkill(
        userId: Int,
        result: AssessmentResult,
        navigate: (Int, Int, String) -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                // Disable next button temporarily
                _nextButtonEnabled.value = false

                // Save or update course skill and their points and achievements
                HelperFunctions.updateCourseSkillAndAchievement(
                    courseSkillRepository = courseSkillRepository,
                    userRepository = userRepository,
                    context = context,
                    courseId = result.courseId,
                    userId = userId,
                    score = result.score,
                    items = result.items,
                    evaluator = result.evaluator
                )

                // Clear the assessment option randomly generated in preferences
                UpdateUserPref.clearAssessmentType(context)

                // Enable next button again
                _nextButtonEnabled.value = true

                navigate(result.score, result.items, result.eligibility)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(
                    context,
                    "Something went wrong saving your assessment.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}