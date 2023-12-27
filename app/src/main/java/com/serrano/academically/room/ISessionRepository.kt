package com.serrano.academically.room

import com.serrano.academically.utils.SessionNotifications
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ISessionRepository {

    suspend fun addSession(session: Session)

    fun getSession(sessionId: Int): Flow<Session>

    fun getTutorSessions(status: String, tutorId: Int): Flow<List<SessionNotifications>>

    fun getStudentSessions(status: String, studentId: Int): Flow<List<SessionNotifications>>

    fun getTutorArchiveSessions(status: String, tutorId: Int): Flow<List<Session>>

    fun getStudentArchiveSessions(status: String, studentId: Int): Flow<List<Session>>

    suspend fun updateSession(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        location: String,
        expireDate: LocalDateTime,
        sessionId: Int
    )

    suspend fun completeSession(sessionId: Int)

    suspend fun updateStudentRate(sessionId: Int)

    suspend fun updateTutorRate(sessionId: Int)

    suspend fun updateStudentView(sessionId: Int)
}