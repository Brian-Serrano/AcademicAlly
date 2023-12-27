package com.serrano.academically.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.serrano.academically.utils.MessageNotifications
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // Usage MessageTutor
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMessage(message: Message)

    // Usage AboutStudent
    @Query("SELECT * FROM Message WHERE messageId = :messageId")
    fun getMessage(messageId: Int): Flow<Message>

    // Usage Notifications, Archive
    @Query("SELECT messageId, studentId, tutorId, courseId, status, tutorViewed FROM Message WHERE studentId = :studentId AND status = :status")
    fun getStudentMessages(status: String, studentId: Int): Flow<List<MessageNotifications>>

    // Usage Notifications, Archive
    @Query("SELECT messageId, studentId, tutorId, courseId, status, tutorViewed FROM Message WHERE tutorId = :tutorId AND status = :status")
    fun getTutorMessages(status: String, tutorId: Int): Flow<List<MessageNotifications>>

    // Usage AboutStudent, CreateSession
    @Query("UPDATE Message SET status = :status WHERE messageId = :messageId")
    suspend fun updateMessageStatus(status: String, messageId: Int)

    // Usage AboutStudent
    @Query("UPDATE Message SET tutorViewed = 1 WHERE messageId = :messageId")
    suspend fun updateTutorView(messageId: Int)

    // Usage MessageTutor
    @Query("SELECT messageId FROM Message WHERE studentId = :studentId AND tutorId = :tutorId AND status = 'WAITING'")
    fun checkIfStudentAlreadyMessageThatTutor(studentId: Int, tutorId: Int): Flow<List<Int>>
}