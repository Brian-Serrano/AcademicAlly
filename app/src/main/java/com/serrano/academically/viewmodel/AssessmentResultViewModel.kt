package com.serrano.academically.viewmodel

import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentResultViewModel @Inject constructor(
    private val courseSkillRepository: CourseSkillRepository
): ViewModel() {

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    fun updateCourseSkill(userId: Int, courseId: Int, score: Int, items: Int, evaluator: Float, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                val courseSkill = courseSkillRepository.getCourseSkill(courseId, userId).first()
                if (courseSkill.isEmpty()) {
                    courseSkillRepository.addCourseSkill(
                        CourseSkill(
                            courseId = courseId,
                            userId = userId,
                            role = if (score / items >= evaluator) "TUTOR" else "STUDENT",
                            courseAssessmentTaken = 1,
                            courseAssessmentScore = score,
                            courseAssessmentItemsTotal = items,
                            courseAssessmentEvaluator = evaluator
                        )
                    )
                }
                else {
                    val newScore = courseSkill[0].courseAssessmentScore + score
                    val newItems = courseSkill[0].courseAssessmentItemsTotal + items
                    val newTaken = courseSkill[0].courseAssessmentTaken + 1
                    val newEval = courseSkill[0].courseAssessmentEvaluator + evaluator
                    val newRole = if (newScore.toFloat() / newItems >= newEval / newTaken) "TUTOR" else "STUDENT"
                    courseSkillRepository.updateCourseSkill(
                        role = newRole,
                        taken = newTaken,
                        score = newScore,
                        items = newItems,
                        eval = newEval,
                        courseSkillId = courseSkill[0].courseSkillId
                    )
                }
                navigate()
            }
            catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Something went wrong saving your assessment."
            }
        }
    }
}