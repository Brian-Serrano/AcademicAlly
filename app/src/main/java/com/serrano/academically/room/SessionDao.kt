package com.serrano.academically.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.serrano.academically.utils.SessionNotifications
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface SessionDao {

    // Usage CreateSession
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSession(session: Session)

    // Usage AboutSession
    @Query("SELECT * FROM Session WHERE sessionId = :sessionId")
    fun getSession(sessionId: Int): Flow<Session>

    // Usage Notifications
    @Query("SELECT sessionId, courseId, startTime, endTime, status, studentViewed FROM Session WHERE tutorId = :tutorId AND status = :status")
    fun getTutorSessions(status: String, tutorId: Int): Flow<List<SessionNotifications>>

    // Usage Notifications
    @Query("SELECT sessionId, courseId, startTime, endTime, status, studentViewed FROM Session WHERE studentId = :studentId AND status = :status")
    fun getStudentSessions(status: String, studentId: Int): Flow<List<SessionNotifications>>

    // Usage Archive
    @Query("SELECT * FROM Session WHERE tutorId = :tutorId AND status = :status")
    fun getTutorArchiveSessions(status: String, tutorId: Int): Flow<List<Session>>

    // Usage Archive
    @Query("SELECT * FROM Session WHERE studentId = :studentId AND status = :status")
    fun getStudentArchiveSessions(status: String, studentId: Int): Flow<List<Session>>

    // Usage EditSession
    @Query("UPDATE Session SET startTime = :startTime, endTime = :endTime, location = :location, expireDate = :expireDate, studentViewed = 0 WHERE sessionId = :sessionId")
    suspend fun updateSession(
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        location: String,
        expireDate: LocalDateTime,
        sessionId: Int
    )

    // Usage EditSession
    @Query("UPDATE Session SET status = 'COMPLETED' WHERE sessionId = :sessionId")
    suspend fun completeSession(sessionId: Int)

    // Usage Archive
    @Query("UPDATE Session SET studentRate = 1 WHERE sessionId = :sessionId")
    suspend fun updateStudentRate(sessionId: Int)

    // Usage Archive
    @Query("UPDATE Session SET tutorRate = 1 WHERE sessionId = :sessionId")
    suspend fun updateTutorRate(sessionId: Int)

    // Usage AboutSession
    @Query("UPDATE Session SET studentViewed = 1 WHERE sessionId = :sessionId")
    suspend fun updateStudentView(sessionId: Int)
}