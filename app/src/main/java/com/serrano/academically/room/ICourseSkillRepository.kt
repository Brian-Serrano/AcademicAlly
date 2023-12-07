package com.serrano.academically.room

import kotlinx.coroutines.flow.Flow

interface ICourseSkillRepository {

    suspend fun addCourseSkill(courseSkill: CourseSkill)

    fun getCourseSkillsOfUser(userId: Int, role: String): Flow<List<CourseSkill>>

    fun getThreeCourseSkillsOfUserNoRating(userId: Int, role: String): Flow<List<Int>>

    fun getCourseSkillsOfUserNoRating(userId: Int, role: String): Flow<List<Int>>

    fun getCourseSkillsForTutorByCourse(courseId: Int): Flow<List<CourseSkill>>

    fun getCourseSkill(courseId: Int, userId: Int): Flow<List<CourseSkill>>

    suspend fun updateCourseSkill(role: String, taken: Int, score: Int, items: Int, eval: Float, courseSkillId: Int)

}