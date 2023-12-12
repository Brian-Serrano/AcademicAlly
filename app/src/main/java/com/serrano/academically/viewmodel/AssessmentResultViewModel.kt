package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AchievementProgress
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.updateUserAssessmentsAchievements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentResultViewModel @Inject constructor(
    private val courseSkillRepository: CourseSkillRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    fun updateCourseSkill(userId: Int, courseId: Int, score: Int, items: Int, evaluator: Double, navigate: () -> Unit, context: Context) {
        viewModelScope.launch {
            try {
                // Check if course of user exists
                val courseSkill = courseSkillRepository.getCourseSkill(courseId, userId).first()
                val eligibility = score.toDouble() / items >= evaluator

                if (courseSkill.isEmpty()) {
                    // If no, add the course
                    courseSkillRepository.addCourseSkill(
                        CourseSkill(
                            courseId = courseId,
                            userId = userId,
                            role = if (eligibility) "TUTOR" else "STUDENT",
                            courseAssessmentTaken = 1,
                            courseAssessmentScore = score,
                            courseAssessmentItemsTotal = items,
                            courseAssessmentEvaluator = evaluator
                        )
                    )
                }
                else {
                    // If yes, update the course
                    val newScore = courseSkill[0].courseAssessmentScore + score
                    val newItems = courseSkill[0].courseAssessmentItemsTotal + items
                    val newTaken = courseSkill[0].courseAssessmentTaken + 1
                    val newEval = courseSkill[0].courseAssessmentEvaluator + evaluator
                    val newRole = if (newScore.toDouble() / newItems >= newEval / newTaken) "TUTOR" else "STUDENT"
                    courseSkillRepository.updateCourseSkill(
                        role = newRole,
                        taken = newTaken,
                        score = newScore,
                        items = newItems,
                        eval = newEval,
                        courseSkillId = courseSkill[0].courseSkillId
                    )
                }

                updateUserAssessmentsAchievements(eligibility, userRepository, context, courseSkillRepository, score, userId)

                navigate()
            }
            catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Something went wrong saving your assessment."
            }
        }
    }
}