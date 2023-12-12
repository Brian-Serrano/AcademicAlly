package com.serrano.academically.utils

import com.serrano.academically.room.Assignment
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
    return Session(courseId = 0, sessionId = 0, tutorId = 0, studentId = 0, moduleId = 0, startTime = LocalDateTime.MIN, endTime = LocalDateTime.MIN, location = "Laoag", expireDate = LocalDateTime.MIN)
}

fun emptyUserDrawerData(): UserDrawerData {
    return UserDrawerData(id = 0, name = "Test", role = "STUDENT", email = "test@gmail.com", degree = "BSCS")
}

fun emptySessionInfo(): SessionInfo {
    return SessionInfo(courseName = "Computer Programming 1", tutorName = "TestTutor", studentName = "TestStudent", moduleName = "Basics of Programming")
}

fun emptyMessage(): Message {
    return Message(courseId = 0, moduleId = 0, studentId = 0, tutorId = 0, studentMessage = "NA", expireDate = LocalDateTime.MIN)
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
        studentPoints = 0.0,
        studentAssessmentPoints = 0.0,
        studentRequestPoints = 0.0,
        studentSessionPoints = 0.0,
        sessionsCompletedAsStudent = 0,
        requestsSent = 0,
        deniedRequests = 0,
        acceptedRequests = 0,
        assignmentsTaken = 0,
        assessmentsTakenAsStudent = 0,
        badgeProgressAsStudent = List(19) { 0.0 },
        tutorPoints = 0.0,
        tutorAssessmentPoints = 0.0,
        tutorRequestPoints = 0.0,
        tutorSessionPoints = 0.0,
        sessionsCompletedAsTutor = 0,
        requestsAccepted = 0,
        requestsDenied = 0,
        requestsReceived = 0,
        assignmentsCreated = 0,
        assessmentsTakenAsTutor = 0,
        badgeProgressAsTutor = List(19) { 0.0 }
    )
}

fun emptyAssignment(): Assignment {
    return Assignment(studentId = 0, tutorId = 0, courseId = 0, moduleId = 0, assessmentIds = emptyList(), type = "Multiple Choice", deadLine = LocalDateTime.MIN)
}