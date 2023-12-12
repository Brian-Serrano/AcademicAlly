package com.serrano.academically.room

import com.serrano.academically.utils.MyTypeConverter
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, Session::class, Message::class, CourseSkill::class, Assignment::class], version = 1, exportSchema = false)
@TypeConverters(value = [MyTypeConverter::class])
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun messageDao(): MessageDao
    abstract fun courseSkillDao(): CourseSkillDao
    abstract fun assignmentDao(): AssignmentDao
}