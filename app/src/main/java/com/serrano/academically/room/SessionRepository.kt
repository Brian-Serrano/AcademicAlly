package com.serrano.academically.room

import com.serrano.academically.utils.SessionNotifications
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class SessionRepository @Inject constructor(private val sessionDao: SessionDao) :
    ISessionRepository {

    override suspend fun addSession(session: Session) = sessionDao.addSession(session)

    override fun getSession(sessionId: Int): Flow<Session> = sessionDao.getSession(sessionId)

    override fun getTutorSessions(status: String, tutorId: Int): Flow<List<SessionNotifications>> =
        sessionDao.getTutorSessions(status, tutorId)

    override fun getStudentSessions(
        status: String,
        studentId: Int
    ): Flow<List<SessionNotifications>> = sessionDao.getStudentSessions(status, studentId)

    override fun getTutorArchiveSessions(status: String, tutorId: Int): Flow<List<Session>> =
        sessionDao.getTutorArchiveSessions(status, tutorId)

    override fun getStudentArchiveSessions(status: String, studentId: Int): Flow<List<Session>> =
        sessionDao.getStudentArchiveSessions(status, studentId)

    override suspend fun updateSession(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        location: String,
        expireDate: LocalDateTime,
        sessionId: Int
    ) = sessionDao.updateSession(startTime, endTime, location, expireDate, sessionId)

    override suspend fun completeSession(sessionId: Int) = sessionDao.completeSession(sessionId)

    override suspend fun updateStudentRate(sessionId: Int) = sessionDao.updateStudentRate(sessionId)

    override suspend fun updateTutorRate(sessionId: Int) = sessionDao.updateTutorRate(sessionId)

    override suspend fun updateStudentView(sessionId: Int) = sessionDao.updateStudentView(sessionId)
}