package com.serrano.academically.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import kotlin.math.round

suspend fun updateUserAssessmentsAchievements (
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
    }
    else {
        userRepository.updateStudentAssessments(0.1 * score, userId)
    }

    // Update student or tutor points and assessment achievement
    val achievementProgress = if (eligibility) userRepository.getBadgeProgressAsTutor(userId).first().achievement else userRepository.getBadgeProgressAsStudent(userId).first().achievement
    val computedProgress = AchievementProgress.computeAchievementProgress(
        if (eligibility) userRepository.getTutorPoints(userId).first() else userRepository.getStudentPoints(userId).first(),
        listOf(10, 25, 50, 100, 200),
        listOf(7, 8, 9, 10, 11),
        AchievementProgress.computeAchievementProgress(
            courseSkillRepository.getCourseSkillsOfUserNoRating(userId, if (eligibility) "TUTOR" else "STUDENT").first().count().toDouble(),
            listOf(1, 3, 5, 10),
            listOf(15, 16, 17, 18),
            achievementProgress
        )
    )
    if (eligibility) {
        userRepository.updateTutorBadgeProgress(computedProgress, userId)
    }
    else {
        userRepository.updateStudentBadgeProgress(computedProgress, userId)
    }

    // Show toast message if an achievement is completed
    AchievementProgress.checkCompletedAchievements(
        achievementProgress, computedProgress
    ) {
        Toast.makeText(
            context,
            GetAchievements.getAchievements(if (eligibility) 0 else 1, context)[it][0],
            Toast.LENGTH_LONG
        ).show()
    }
}

suspend fun completeSessions(
    sessionRepository: SessionRepository,
    userRepository: UserRepository,
    studentId: Int,
    tutorId: Int,
    sessionId: Int,
    navigate: () -> Unit,
    context: Context
) {
    // Mark session as complete
    sessionRepository.completeSession(sessionId)

    // Update student and tutor completed sessions and points
    userRepository.updateStudentCompletedSessions(0.5, studentId)
    userRepository.updateTutorCompletedSessions(0.5, tutorId)

    // Update student points and completed sessions achievement
    val achievementProgressStudent = userRepository.getBadgeProgressAsStudent(studentId).first().achievement
    val computedProgressStudent = AchievementProgress.computeAchievementProgress(
        userRepository.getStudentPoints(studentId).first(),
        listOf(10, 25, 50, 100, 200),
        listOf(7, 8, 9, 10, 11),
        AchievementProgress.computeAchievementProgress(
            userRepository.getStudentCompletedSessions(studentId).first().toDouble(),
            listOf(1, 5, 10),
            listOf(12, 13, 14),
            achievementProgressStudent
        )
    )
    userRepository.updateStudentBadgeProgress(computedProgressStudent, studentId)

    // Update tutor points and completed sessions achievement
    val achievementProgressTutor = userRepository.getBadgeProgressAsTutor(tutorId).first().achievement
    val computedProgressTutor = AchievementProgress.computeAchievementProgress(
        userRepository.getTutorPoints(tutorId).first(),
        listOf(10, 25, 50, 100, 200),
        listOf(7, 8, 9, 10, 11),
        AchievementProgress.computeAchievementProgress(
            userRepository.getTutorCompletedSessions(tutorId).first().toDouble(),
            listOf(1, 5, 10),
            listOf(12, 13, 14),
            achievementProgressTutor
        )
    )
    userRepository.updateTutorBadgeProgress(computedProgressTutor, tutorId)

    // Show toast message if an achievement is completed
    AchievementProgress.checkCompletedAchievements(
        achievementProgressTutor, computedProgressTutor
    ) {
        Toast.makeText(
            context,
            GetAchievements.getAchievements(0, context)[it][0],
            Toast.LENGTH_LONG
        ).show()
    }

    navigate()
}

fun roundRating(x: Double): Double = round(x * 10.0) / 10.0

@SuppressLint("SimpleDateFormat")
fun toMilitaryTime(x: List<Int>): String {
    return SimpleDateFormat("hh:mm a").format(SimpleDateFormat("HH:mm").parse("${x[0]}:${x[1]}") ?: "01:00 AM")
}