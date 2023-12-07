package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AssessmentResult
import com.serrano.academically.utils.GetAssessments
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
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

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun addAnswer(answer: String, index: Int) {
        _assessmentAnswers.value = assessmentAnswers.value.mapIndexed { idx, ans -> if (idx == index) answer else ans }
    }

    fun getData(userId: Int, courseId: Int, items: Int, type: String, context: Context) {
        viewModelScope.launch {
            try {
                _courseName.value = GetCourses.getCourseNameById(courseId, context)
                _assessmentData.value = GetAssessments.getAssessments(courseId, items, type, context)
                _assessmentAnswers.value = List(items) { "" }
                if (userId != 0) {
                    _drawerData.value = userRepository.getUserDataForDrawer(userId).first()
                    _isDrawerShouldAvailable.value = true
                }
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun evaluateAnswers(type: String): AssessmentResult {
        var totalScore = 0
        val items = assessmentData.value.size
        for (idx in assessmentData.value.indices) {
            val answerIdx = if (type == "Multiple Choice") 6 else 2
            if (assessmentData.value[idx][answerIdx].lowercase() == assessmentAnswers.value[idx].lowercase()) {
                totalScore += 1
            }
        }
        return AssessmentResult(
            score = totalScore,
            items = items,
            evaluator = when (type) {
                "Multiple Choice" -> 0.75F
                "Identification" -> 0.6F
                else -> 0.9F
            }
        )
    }
}