package com.serrano.academically.room

import com.serrano.academically.utils.SessionNotifications
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class SessionRepository @Inject constructor(private val sessionDao: SessionDao): ISessionRepository {

    override suspend fun addSession(session: Session) = sessionDao.addSession(session)

    override fun getSession(sessionId: Int): Flow<Session> = sessionDao.getSession(sessionId)

    override fun getTutorSessions(tutorId: Int): Flow<List<SessionNotifications>> = sessionDao.getTutorSessions(tutorId)

    override fun getStudentSessions(studentId: Int): Flow<List<SessionNotifications>> = sessionDao.getStudentSessions(studentId)

    override suspend fun updateSession(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        location: String,
        sessionId: Int
    ) = sessionDao.updateSession(startTime, endTime, location, sessionId)

    override suspend fun completeSession(sessionId: Int) = sessionDao.completeSession(sessionId)
}