package com.serrano.academically.room

import com.serrano.academically.utils.MessageNotifications
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // Usage MessageTutor
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMessage(message: Message)

    // Usage AboutStudent
    @Query("SELECT * FROM Message WHERE messageId = :messageId")
    fun getMessage(messageId: Int): Flow<Message>

    // Usage Notifications
    @Query("SELECT messageId, studentId, tutorId, courseId FROM Message WHERE studentId = :studentId AND status = 'WAITING'")
    fun getStudentMessages(studentId: Int): Flow<List<MessageNotifications>>

    // Usage Notifications
    @Query("SELECT messageId, studentId, tutorId, courseId FROM Message WHERE tutorId = :tutorId AND status = 'WAITING'")
    fun getTutorMessages(tutorId: Int): Flow<List<MessageNotifications>>

    // Usage AboutStudent, CreateSession
    @Query("UPDATE Message SET status = :status WHERE messageId = :messageId")
    suspend fun updateMessageStatus(status: String, messageId: Int)
}