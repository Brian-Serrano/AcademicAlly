package com.serrano.academically.room

import com.serrano.academically.utils.MessageNotifications
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepository @Inject constructor(private val messageDao: MessageDao) :
    IMessageRepository {

    override suspend fun addMessage(message: Message) = messageDao.addMessage(message)

    override fun getMessage(messageId: Int): Flow<Message> = messageDao.getMessage(messageId)

    override fun getStudentMessages(
        status: String,
        studentId: Int
    ): Flow<List<MessageNotifications>> = messageDao.getStudentMessages(status, studentId)

    override fun getTutorMessages(status: String, tutorId: Int): Flow<List<MessageNotifications>> =
        messageDao.getTutorMessages(status, tutorId)

    override suspend fun updateMessageStatus(status: String, messageId: Int) =
        messageDao.updateMessageStatus(status, messageId)

    override suspend fun updateTutorView(messageId: Int) = messageDao.updateTutorView(messageId)

    override fun checkIfStudentAlreadyMessageThatTutor(
        studentId: Int,
        tutorId: Int
    ): Flow<List<Int>> = messageDao.checkIfStudentAlreadyMessageThatTutor(studentId, tutorId)
}