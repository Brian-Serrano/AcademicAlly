package com.serrano.academically.room

import com.serrano.academically.utils.MessageNotifications
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepository @Inject constructor(private val messageDao: MessageDao): IMessageRepository {

    override suspend fun addMessage(message: Message) = messageDao.addMessage(message)

    override fun getMessage(messageId: Int): Flow<Message> = messageDao.getMessage(messageId)

    override fun getStudentMessages(studentId: Int): Flow<List<MessageNotifications>> = messageDao.getStudentMessages(studentId)

    override fun getTutorMessages(tutorId: Int): Flow<List<MessageNotifications>> = messageDao.getTutorMessages(tutorId)

    override suspend fun updateMessageStatus(status: String, messageId: Int) = messageDao.updateMessageStatus(status, messageId)
}