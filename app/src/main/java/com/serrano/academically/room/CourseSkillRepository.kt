package com.serrano.academically.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CourseSkillRepository @Inject constructor(private val courseSkillDao: CourseSkillDao) :
    ICourseSkillRepository {

    override suspend fun addCourseSkill(courseSkill: CourseSkill) =
        courseSkillDao.addCourseSkill(courseSkill)

    override fun getCourseSkillsOfUser(userId: Int, role: String): Flow<List<CourseSkill>> =
        courseSkillDao.getCourseSkillsOfUser(userId, role)

    override fun getThreeCourseSkillsOfUserNoRating(userId: Int, role: String): Flow<List<Int>> =
        courseSkillDao.getThreeCourseSkillsOfUserNoRating(userId, role)

    override fun getCourseSkillsOfUserNoRating(userId: Int, role: String): Flow<List<Int>> =
        courseSkillDao.getCourseSkillsOfUserNoRating(userId, role)

    override fun getCourseSkillsForTutorByCourse(courseId: Int): Flow<List<CourseSkill>> =
        courseSkillDao.getCourseSkillsForTutorByCourse(courseId)

    override fun getCourseSkill(courseId: Int, userId: Int): Flow<List<CourseSkill>> =
        courseSkillDao.getCourseSkill(courseId, userId)

    override suspend fun updateCourseSkill(
        role: String,
        taken: Int,
        rating: Double,
        courseSkillId: Int
    ) = courseSkillDao.updateCourseSkill(role, taken, rating, courseSkillId)
}