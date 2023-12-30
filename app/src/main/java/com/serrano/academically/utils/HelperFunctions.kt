package com.serrano.academically.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.serrano.academically.room.CourseSkill
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.round
import kotlin.random.Random

object HelperFunctions {

    suspend fun updateUserAssessmentsAchievements(
        eligibility: Boolean,
        userRepository: UserRepository,
        context: Context,
        courseSkillRepository: CourseSkillRepository,
        score: Int,
        userId: Int
    ) {
        // Update student or tutor assessments and points
        if (eligibility) {
            userRepository.updateTutorAssessments(0.1 * score, userId)
        } else {
            userRepository.updateStudentAssessments(0.1 * score, userId)
        }

        // Update student or tutor points and assessment achievement
        val achievementProgress = if (eligibility) userRepository.getBadgeProgressAsTutor(userId)
            .first().achievement else userRepository.getBadgeProgressAsStudent(userId)
            .first().achievement
        val computedProgress = computeAchievementProgress(
            if (eligibility) userRepository.getTutorPoints(userId)
                .first() else userRepository.getStudentPoints(userId).first(),
            listOf(10, 25, 50, 100, 200),
            listOf(7, 8, 9, 10, 11),
            computeAchievementProgress(
                courseSkillRepository.getCourseSkillsOfUserNoRating(
                    userId,
                    if (eligibility) "TUTOR" else "STUDENT"
                ).first().count().toDouble(),
                listOf(1, 3, 5, 10),
                listOf(15, 16, 17, 18),
                achievementProgress
            )
        )
        if (eligibility) {
            userRepository.updateTutorBadgeProgress(computedProgress, userId)
        } else {
            userRepository.updateStudentBadgeProgress(computedProgress, userId)
        }

        // Show toast message if an achievement is completed
        checkCompletedAchievements(
            achievementProgress, computedProgress
        ) {
            Toast.makeText(
                context,
                GetAchievements.getAchievements(if (eligibility) 0 else 1, context)[it][0],
                Toast.LENGTH_LONG
            ).show()
        }
    }

    suspend fun updateCourseSkillAndAchievement(
        courseSkillRepository: CourseSkillRepository,
        userRepository: UserRepository,
        context: Context,
        courseId: Int,
        userId: Int,
        score: Int,
        items: Int,
        evaluator: Double
    ) {
        // Check if course of user exists
        val courseSkill = courseSkillRepository.getCourseSkill(courseId, userId).first()
        val rating = eligibilityComputingAlgorithm(score, items, evaluator)
        val eligibility = rating >= 0.5

        if (courseSkill.isEmpty()) {
            // If no, add the course
            courseSkillRepository.addCourseSkill(
                CourseSkill(
                    courseId = courseId,
                    userId = userId,
                    role = if (eligibility) "TUTOR" else "STUDENT",
                    assessmentTaken = 1,
                    assessmentRating = rating
                )
            )
        } else {
            // If yes, update the course
            val newRating = courseSkill[0].assessmentRating + rating
            val newTaken = courseSkill[0].assessmentTaken + 1
            val newRole = if (newRating / newTaken >= 0.5) "TUTOR" else "STUDENT"
            courseSkillRepository.updateCourseSkill(
                role = newRole,
                taken = newTaken,
                rating = newRating,
                courseSkillId = courseSkill[0].courseSkillId
            )
        }

        // Update student or tutor points and assessment achievement
        updateUserAssessmentsAchievements(
            eligibility,
            userRepository,
            context,
            courseSkillRepository,
            score,
            userId
        )
    }

    fun roundRating(x: Double): Double = round(x * 10.0) / 10.0

    @SuppressLint("SimpleDateFormat")
    fun toMilitaryTime(h: Int, m: Int): String {
        return SimpleDateFormat("hh:mm a").format(
            SimpleDateFormat("HH:mm").parse("${h}:${m}") ?: "01:00 AM"
        )
    }

    fun formatTime(t1: LocalDateTime, t2: LocalDateTime): String {
        return "${toMilitaryTime(t1.hour, t1.minute)} - ${toMilitaryTime(t2.hour, t2.minute)}"
    }

    fun formatDate(date: LocalDateTime): String {
        return "${date.month} ${date.dayOfMonth}, ${date.year}"
    }

    fun formatDate(date: LocalDate): String {
        return "${date.month} ${date.dayOfMonth}, ${date.year}"
    }

    fun eligibilityComputingAlgorithm(score: Int, items: Int, eval: Double): Double {
        val percentage = score.toDouble() / items
        return if (percentage >= eval) {
            val multiplier = (eval - 0.5) / (1.0 - eval)
            percentage - ((1.0 - percentage) * multiplier)
        } else {
            val multiplier = (0.5 - eval) / eval
            percentage + (percentage * multiplier)
        }
    }

    fun evaluateAnswer(
        assessmentData: List<List<String>>,
        assessmentAnswers: List<String>,
        type: String
    ): Int {
        var totalScore = 0
        val answerIdx = if (type == "Multiple Choice") 6 else 2
        for (idx in assessmentData.indices) {
            if (assessmentData[idx][answerIdx].lowercase() == assessmentAnswers[idx].lowercase()) {
                totalScore += 1
            }
        }
        return totalScore
    }

    fun generateRandomColor(seed: Int): Color {
        val rand = Random(seed)
        return Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), 255)
    }

    fun computeAchievementProgress(
        data: Double,
        achievementsGoal: List<Int>,
        achievementsIndex: List<Int>,
        achievementProgress: List<Double>
    ): List<Double> {
        return achievementProgress.mapIndexed { idx, progress ->
            if (achievementsIndex.indexOf(idx) != -1) (100 * (data / achievementsGoal[achievementsIndex.indexOf(
                idx
            )])).coerceAtMost(100.0) else progress
        }
    }

    fun checkCompletedAchievements(
        currentProgress: List<Double>,
        computedProgress: List<Double>,
        showToast: (Int) -> Unit
    ) {
        for (idx in currentProgress.indices) {
            if (currentProgress[idx] != computedProgress[idx] && computedProgress[idx] >= 100.0) {
                showToast(idx)
            }
        }
    }
}