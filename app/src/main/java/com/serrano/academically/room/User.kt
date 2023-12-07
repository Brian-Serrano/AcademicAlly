package com.serrano.academically.room

import com.serrano.academically.ui.theme.Strings
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // Credentials
    val name: String,
    val role: String,
    val email: String,
    val password: String,
    // Info
    val imagePath: String = "NA",
    val degree: String = "NA",
    val age: Int = 0,
    val address: String = "NA",
    val contactNumber: String = "NA",
    val summary: String = Strings.loremIpsum,
    val educationalBackground: String = Strings.loremIpsum,
    // Student
    val studentPoints: Float = 0F,
    val studentAssessmentPoints: Float = 0F,
    val studentBadgePoints: Float = 0F,
    val studentRequestPoints: Float = 0F,
    val studentSessionPoints: Float = 0F,
    val sessionsCompletedAsStudent: Int = 0,
    val requestsSent: Int = 0,
    val deniedRequests: Int = 0,
    val acceptedRequests: Int = 0,
    val assessmentsTakenAsStudent: Int = 0,
    val badgeProgressAsStudent: List<Float> = List(20) { 0F },
    // Tutor
    val tutorPoints: Float = 0F,
    val tutorAssessmentPoints: Float = 0F,
    val tutorBadgePoints: Float = 0F,
    val tutorRequestPoints: Float = 0F,
    val tutorSessionPoints: Float = 0F,
    val sessionsCompletedAsTutor: Int = 0,
    val requestsAccepted: Int = 0,
    val requestsDenied: Int = 0,
    val requestsReceived: Int = 0,
    val assessmentsTakenAsTutor: Int = 0,
    val badgeProgressAsTutor: List<Float> = List(22) { 0F }
)