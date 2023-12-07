package com.serrano.academically.room

import com.serrano.academically.utils.SessionNotifications
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    @Query("SELECT sessionId, courseId, startTime, endTime FROM Session WHERE tutorId = :tutorId AND isComplete = 0")
    fun getTutorSessions(tutorId: Int): Flow<List<SessionNotifications>>

    // Usage Notifications
    @Query("SELECT sessionId, courseId, startTime, endTime FROM Session WHERE studentId = :studentId AND isComplete = 0")
    fun getStudentSessions(studentId: Int): Flow<List<SessionNotifications>>

    // Usage EditSession
    @Query("UPDATE Session SET startTime = :startTime, endTime = :endTime, location = :location WHERE sessionId = :sessionId")
    suspend fun updateSession(startTime: LocalDateTime, endTime: LocalDateTime, location: String, sessionId: Int)

    // Usage EditSession
    @Query("UPDATE Session SET isComplete = 1 WHERE sessionId = :sessionId")
    suspend fun completeSession(sessionId: Int)
}