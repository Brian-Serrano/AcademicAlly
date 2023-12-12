package com.serrano.academically.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAssignment(assignment: Assignment)

    @Query("SELECT * FROM Assignment WHERE assignmentId = :assignmentId")
    fun getAssignment(assignmentId: Int): Flow<Assignment>

    @Query("SELECT * FROM Assignment WHERE studentId = :studentId AND status = 'UNCOMPLETED'")
    fun getStudentAssignments(studentId: Int): Flow<List<Assignment>>

    @Query("SELECT * FROM Assignment WHERE tutorId = :tutorId AND status <> 'DEADLINE REACHED'")
    fun getTutorAssignments(tutorId: Int): Flow<List<Assignment>>

    @Query("UPDATE Assignment SET studentScore = :score, status = 'COMPLETED' WHERE assignmentId = :assignmentId")
    suspend fun completeAssignment(score: Int, assignmentId: Int)
}