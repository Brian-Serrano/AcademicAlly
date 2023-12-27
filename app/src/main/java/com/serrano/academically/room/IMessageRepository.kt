package com.serrano.academically.room

import com.serrano.academically.utils.MessageNotifications
import kotlinx.coroutines.flow.Flow

interface IMessageRepository {

    suspend fun addMessage(message: Message)

    fun getMessage(messageId: Int): Flow<Message>

    fun getStudentMessages(status: String, studentId: Int): Flow<List<MessageNotifications>>

    fun getTutorMessages(status: String, tutorId: Int): Flow<List<MessageNotifications>>

    suspend fun updateMessageStatus(status: String, messageId: Int)

    suspend fun updateTutorView(messageId: Int)

    fun checkIfStudentAlreadyMessageThatTutor(studentId: Int, tutorId: Int): Flow<List<Int>>
}