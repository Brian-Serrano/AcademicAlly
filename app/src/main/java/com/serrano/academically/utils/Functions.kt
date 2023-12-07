package com.serrano.academically.utils

import com.serrano.academically.room.Message
import com.serrano.academically.room.Session
import com.serrano.academically.room.User
import com.serrano.academically.ui.theme.Strings
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

fun emptyUser(): User {
    return User(name = "Test", role = "STUDENT", email = "test@gmail.com", password = "test123")
}

fun emptySession(): Session {
    return Session(courseId = 0, sessionId = 0, tutorId = 0, studentId = 0, moduleId = 0, startTime = LocalDateTime.MIN, endTime = LocalDateTime.MIN, location = "Laoag")
}

fun emptyUserDrawerData(): UserDrawerData {
    return UserDrawerData(id = 0, name = "Test", role = "STUDENT", email = "test@gmail.com", degree = "BSCS")
}

fun emptySessionInfo(): SessionInfo {
    return SessionInfo(courseName = "Computer Programming 1", tutorName = "TestTutor", studentName = "TestStudent", moduleName = "Basics of Programming")
}

fun emptyMessage(): Message {
    return Message(courseId = 0, moduleId = 0, studentId = 0, tutorId = 0, studentMessage = "NA")
}

fun emptyMessageCourse(): MessageCourse {
    return MessageCourse(courseName = "Computer Programming 1", moduleName = "Basics of Programming")
}

fun emptyUserInfo(): UserInfo {
    return UserInfo(id = 0, name = "Test", degree = "BSCS", age = 0, address = "NA", contactNumber = "NA", summary = Strings.loremIpsum, educationalBackground = Strings.loremIpsum)
}

fun emptyUserInfoAndCredentials(): UserInfoAndCredentials {
    return UserInfoAndCredentials(id = 0, name = "Test", role = "STUDENT", email = "test@gmail.com", password = "test123", imagePath = "NA", age = 0, degree = "BSCS", address = "NA", contactNumber = "NA", summary = Strings.loremIpsum, educationalBackground = Strings.loremIpsum)
}

fun emptyAnalyticsData(): AnalyticsData {
    return AnalyticsData(
        id = 0,
        name = "Test",
        role = "STUDENT",
        email = "test@gmail.com",
        degree = "BSCS",
        studentPoints = 0F,
        studentAssessmentPoints = 0F,
        studentBadgePoints = 0F,
        studentRequestPoints = 0F,
        studentSessionPoints = 0F,
        sessionsCompletedAsStudent = 0,
        requestsSent = 0,
        deniedRequests = 0,
        acceptedRequests = 0,
        assessmentsTakenAsStudent = 0,
        badgeProgressAsStudent = List(20) { 0F },
        tutorPoints = 0F,
        tutorAssessmentPoints = 0F,
        tutorBadgePoints = 0F,
        tutorRequestPoints = 0F,
        tutorSessionPoints = 0F,
        sessionsCompletedAsTutor = 0,
        requestsAccepted = 0,
        requestsDenied = 0,
        requestsReceived = 0,
        assessmentsTakenAsTutor = 0,
        badgeProgressAsTutor = List(22) { 0F }
    )
}