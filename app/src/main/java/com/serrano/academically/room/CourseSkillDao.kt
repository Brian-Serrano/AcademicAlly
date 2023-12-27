package com.serrano.academically.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseSkillDao {

    // Usage Signup
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCourseSkill(courseSkill: CourseSkill)

    // Usage CoursesMenu, Analytics, Dashboard
    @Query("SELECT * FROM CourseSkill WHERE userId = :userId AND role = :role")
    fun getCourseSkillsOfUser(userId: Int, role: String): Flow<List<CourseSkill>>

    // Usage Profile
    @Query("SELECT courseId FROM CourseSkill WHERE userId = :userId AND role = :role LIMIT 3")
    fun getThreeCourseSkillsOfUserNoRating(userId: Int, role: String): Flow<List<Int>>

    // Usage MessageTutor, FindTutor, Signup, Assessment
    @Query("SELECT courseId FROM CourseSkill WHERE userId = :userId AND role = :role")
    fun getCourseSkillsOfUserNoRating(userId: Int, role: String): Flow<List<Int>>

    // Usage FindTutor
    @Query("SELECT * FROM CourseSkill WHERE courseId = :courseId AND role = 'TUTOR'")
    fun getCourseSkillsForTutorByCourse(courseId: Int): Flow<List<CourseSkill>>

    // Usage Assessment
    @Query("SELECT * FROM CourseSkill WHERE courseId = :courseId AND userId = :userId")
    fun getCourseSkill(courseId: Int, userId: Int): Flow<List<CourseSkill>>

    // Usage Assessment
    @Query("UPDATE CourseSkill SET role = :role, assessmentTaken = :taken, assessmentRating = :rating WHERE courseSkillId = :courseSkillId")
    suspend fun updateCourseSkill(role: String, taken: Int, rating: Double, courseSkillId: Int)
}