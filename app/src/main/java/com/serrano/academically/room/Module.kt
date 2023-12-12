package com.serrano.academically.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun provideUserDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        Database::class.java,
        "database"
    ).build()

    @Provides
    fun provideUserDao(
        database: Database
    ) = database.userDao()

    @Provides
    fun provideUserRepository(
        userDao: UserDao
    ): IUserRepository = UserRepository(userDao)

    @Provides
    fun provideSessionDao(
        database: Database
    ) = database.sessionDao()

    @Provides
    fun provideSessionRepository(
        sessionDao: SessionDao
    ): ISessionRepository = SessionRepository(sessionDao)

    @Provides
    fun provideMessageDao(
        database: Database
    ) = database.messageDao()

    @Provides
    fun provideMessageRepository(
        messageDao: MessageDao
    ): IMessageRepository = MessageRepository(messageDao)

    @Provides
    fun provideCourseSkillDao(
        database: Database
    ) = database.courseSkillDao()

    @Provides
    fun provideCourseSkillRepository(
        courseSkillDao: CourseSkillDao
    ): ICourseSkillRepository = CourseSkillRepository(courseSkillDao)

    @Provides
    fun provideAssignmentDao(
        database: Database
    ) = database.assignmentDao()

    @Provides
    fun provideAssignmentRepository(
        assignmentDao: AssignmentDao
    ): IAssignmentRepository = AssignmentRepository(assignmentDao)
}