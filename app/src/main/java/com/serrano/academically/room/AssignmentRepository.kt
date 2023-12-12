package com.serrano.academically.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssignmentRepository @Inject constructor(private val assignmentDao: AssignmentDao): IAssignmentRepository {

    override suspend fun addAssignment(assignment: Assignment) = assignmentDao.addAssignment(assignment)

    override fun getAssignment(assignmentId: Int): Flow<Assignment> = assignmentDao.getAssignment(assignmentId)

    override fun getStudentAssignments(studentId: Int): Flow<List<Assignment>> = assignmentDao.getStudentAssignments(studentId)

    override fun getTutorAssignments(tutorId: Int): Flow<List<Assignment>> = assignmentDao.getTutorAssignments(tutorId)

    override suspend fun completeAssignment(score: Int, assignmentId: Int) = assignmentDao.completeAssignment(score, assignmentId)
}