package com.serrano.academically.room

import com.serrano.academically.utils.MessageNotifications
import kotlinx.coroutines.flow.Flow

interface IMessageRepository {

    suspend fun addMessage(message: Message)

    fun getMessage(messageId: Int): Flow<Message>

    fun getStudentMessages(studentId: Int): Flow<List<MessageNotifications>>

    fun getTutorMessages(tutorId: Int): Flow<List<MessageNotifications>>

    suspend fun updateMessageStatus(status: String, messageId: Int)
}