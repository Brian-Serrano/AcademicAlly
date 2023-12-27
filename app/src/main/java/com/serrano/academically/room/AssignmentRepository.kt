package com.serrano.academically.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssignmentRepository @Inject constructor(private val assignmentDao: AssignmentDao) :
    IAssignmentRepository {

    override suspend fun addAssignment(assignment: Assignment) =
        assignmentDao.addAssignment(assignment)

    override fun getAssignment(assignmentId: Int): Flow<Assignment> =
        assignmentDao.getAssignment(assignmentId)

    override fun getStudentAssignments(status: String, studentId: Int): Flow<List<Assignment>> =
        assignmentDao.getStudentAssignments(status, studentId)

    override fun getTutorAssignments(status: String, tutorId: Int): Flow<List<Assignment>> =
        assignmentDao.getTutorAssignments(status, tutorId)

    override suspend fun completeAssignment(score: Int, assignmentId: Int) =
        assignmentDao.completeAssignment(score, assignmentId)

    override suspend fun updateStudentView(assignmentId: Int) =
        assignmentDao.updateStudentView(assignmentId)
}