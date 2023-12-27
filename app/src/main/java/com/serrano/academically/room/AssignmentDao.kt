package com.serrano.academically.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {

    // Usage CreateAssignment
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAssignment(assignment: Assignment)

    // Usage Assignment
    @Query("SELECT * FROM Assignment WHERE assignmentId = :assignmentId")
    fun getAssignment(assignmentId: Int): Flow<Assignment>

    // Usage Notifications, Archive
    @Query("SELECT * FROM Assignment WHERE studentId = :studentId AND status = :status")
    fun getStudentAssignments(status: String, studentId: Int): Flow<List<Assignment>>

    // Usage Notifications, Archive
    @Query("SELECT * FROM Assignment WHERE tutorId = :tutorId AND status = :status")
    fun getTutorAssignments(status: String, tutorId: Int): Flow<List<Assignment>>

    // Usage Assignment
    @Query("UPDATE Assignment SET studentScore = :score, status = 'COMPLETED' WHERE assignmentId = :assignmentId")
    suspend fun completeAssignment(score: Int, assignmentId: Int)

    // Usage Assignment
    @Query("UPDATE Assignment SET studentViewed = 1 WHERE assignmentId = :assignmentId")
    suspend fun updateStudentView(assignmentId: Int)
}