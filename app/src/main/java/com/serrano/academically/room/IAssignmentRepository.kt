package com.serrano.academically.room

import kotlinx.coroutines.flow.Flow

interface IAssignmentRepository {

    suspend fun addAssignment(assignment: Assignment)

    fun getAssignment(assignmentId: Int): Flow<Assignment>

    fun getStudentAssignments(studentId: Int): Flow<List<Assignment>>

    fun getTutorAssignments(tutorId: Int): Flow<List<Assignment>>

    suspend fun completeAssignment(score: Int, assignmentId: Int)
}