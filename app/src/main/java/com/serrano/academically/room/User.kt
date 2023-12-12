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
    val studentPoints: Double = 0.0,
    val studentAssessmentPoints: Double = 0.0,
    val studentRequestPoints: Double = 0.0,
    val studentSessionPoints: Double = 0.0,
    val sessionsCompletedAsStudent: Int = 0,
    val requestsSent: Int = 0,
    val deniedRequests: Int = 0,
    val acceptedRequests: Int = 0,
    val assignmentsTaken: Int = 0,
    val assessmentsTakenAsStudent: Int = 0,
    val badgeProgressAsStudent: List<Double> = List(19) { 0.0 },
    // Tutor
    val tutorPoints: Double = 0.0,
    val tutorAssessmentPoints: Double = 0.0,
    val tutorRequestPoints: Double = 0.0,
    val tutorSessionPoints: Double = 0.0,
    val sessionsCompletedAsTutor: Int = 0,
    val requestsAccepted: Int = 0,
    val requestsDenied: Int = 0,
    val requestsReceived: Int = 0,
    val assignmentsCreated: Int = 0,
    val assessmentsTakenAsTutor: Int = 0,
    val badgeProgressAsTutor: List<Double> = List(19) { 0.0 }
)