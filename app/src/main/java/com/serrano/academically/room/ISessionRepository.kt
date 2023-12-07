package com.serrano.academically.room

import com.serrano.academically.utils.SessionNotifications
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ISessionRepository {

    suspend fun addSession(session: Session)

    fun getSession(sessionId: Int): Flow<Session>

    fun getTutorSessions(tutorId: Int): Flow<List<SessionNotifications>>

    fun getStudentSessions(studentId: Int): Flow<List<SessionNotifications>>

    suspend fun updateSession(startTime: LocalDateTime, endTime: LocalDateTime, location: String, sessionId: Int)

    suspend fun completeSession(sessionId: Int)
}