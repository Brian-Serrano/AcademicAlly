package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Assignment
import com.serrano.academically.room.AssignmentRepository
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AssessmentType
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.IdentificationFields
import com.serrano.academically.utils.MessageCourse
import com.serrano.academically.utils.MultipleChoiceFields
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.TrueOrFalseFields
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateAssignmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _sessionInfo = MutableStateFlow(Pair(Session(), MessageCourse()))
    val sessionInfo: StateFlow<Pair<Session, MessageCourse>> = _sessionInfo.asStateFlow()

    private val _quizFields = MutableStateFlow<List<AssessmentType>>(emptyList())
    val quizFields: StateFlow<List<AssessmentType>> = _quizFields.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen: StateFlow<Boolean> = _isFilterDialogOpen.asStateFlow()

    fun getData(id: Int, sessionId: Int, items: Int, type: String, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch session information
                val session = sessionRepository.getSession(sessionId).first()
                _sessionInfo.value = Pair(
                    session,
                    MessageCourse(
                        courseName = GetCourses.getCourseNameById(session.courseId, context),
                        moduleName = GetModules.getModuleByCourseAndModuleId(
                            session.courseId,
                            session.moduleId,
                            context
                        )
                    )
                )

                // Generate states for all questions and answers fields
                _quizFields.value = when (type) {
                    "Multiple Choice" -> List(items) {
                        MultipleChoiceFields(
                            id = it,
                            question = "",
                            choices = listOf("", "", "", ""),
                            answer = DropDownState(listOf("A", "B", "C", "D"), "A", false)
                        )
                    }

                    "Identification" -> List(items) {
                        IdentificationFields(
                            id = it,
                            question = "",
                            answer = ""
                        )
                    }

                    "True or False" -> List(items) {
                        TrueOrFalseFields(
                            id = it,
                            question = "",
                            answer = DropDownState(listOf("TRUE", "FALSE"), "TRUE", false)
                        )
                    }

                    else -> throw IllegalArgumentException()
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun updateFields(newFields: List<AssessmentType>) {
        _quizFields.value = newFields
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }

    fun completeSessionAndSaveAssignment(
        studentId: Int,
        tutorId: Int,
        sessionId: Int,
        rate: Int,
        courseId: Int,
        moduleId: Int,
        type: String,
        deadLine: LocalDateTime,
        navigate: () -> Unit,
        context: Context,
        assessment: List<AssessmentType>,
        name: String
    ) {
        viewModelScope.launch {
            try {
                val validationResult = validateAssignment(assessment)
                if (validationResult.all { it }) {
                    // Mark session as complete
                    sessionRepository.completeSession(sessionId)

                    // Save assignment
                    assignmentRepository.addAssignment(
                        Assignment(
                            studentId = studentId,
                            tutorId = tutorId,
                            courseId = courseId,
                            moduleId = moduleId,
                            data = serializeAssignmentData(
                                assessment,
                                GetModules.getModuleByCourseAndModuleId(
                                    courseId,
                                    moduleId,
                                    context
                                ),
                                name
                            ),
                            type = type,
                            deadLine = deadLine
                        )
                    )

                    // Update student and tutor completed sessions and points
                    userRepository.updateStudentCompletedSessions(0.5, studentId)
                    userRepository.updateTutorCompletedSessions(0.5, tutorId)

                    // Update tutor created assignments and points
                    userRepository.updateTutorAssignments(0.5, tutorId)

                    if (rate > 0) {
                        // Update session student rate
                        sessionRepository.updateTutorRate(sessionId)

                        // Update studentsRated of tutor and numberOfRatesAsStudent and totalRatingAsStudent of student
                        userRepository.updateTutorRates(tutorId)
                        userRepository.updateStudentRating(rate / 5.0, studentId)
                    }

                    // Update student points, student rated and completed sessions achievement
                    val achievementProgressStudent =
                        userRepository.getBadgeProgressAsStudent(studentId).first().achievement
                    val computedProgressStudent = HelperFunctions.computeAchievementProgress(
                        userRepository.getStudentPoints(studentId).first(),
                        listOf(10, 25, 50, 100, 200),
                        listOf(7, 8, 9, 10, 11),
                        HelperFunctions.computeAchievementProgress(
                            userRepository.getStudentCompletedSessions(studentId).first()
                                .toDouble(),
                            listOf(1, 5, 10),
                            listOf(12, 13, 14),
                            if (rate > 0) HelperFunctions.computeAchievementProgress(
                                userRepository.getStudentRatingNumber(studentId).first().toDouble(),
                                listOf(1, 5, 10),
                                listOf(25, 26, 27),
                                achievementProgressStudent
                            ) else achievementProgressStudent
                        )
                    )
                    userRepository.updateStudentBadgeProgress(computedProgressStudent, studentId)

                    // Update tutor points, create assignment, tutor rates and completed sessions achievement
                    val achievementProgressTutor =
                        userRepository.getBadgeProgressAsTutor(tutorId).first().achievement
                    val computedProgressTutor = HelperFunctions.computeAchievementProgress(
                        userRepository.getTutorPoints(tutorId).first(),
                        listOf(10, 25, 50, 100, 200),
                        listOf(7, 8, 9, 10, 11),
                        HelperFunctions.computeAchievementProgress(
                            userRepository.getTutorCompletedSessions(tutorId).first().toDouble(),
                            listOf(1, 5, 10),
                            listOf(12, 13, 14),
                            HelperFunctions.computeAchievementProgress(
                                userRepository.getTutorAssignments(tutorId).first().toDouble(),
                                listOf(1, 5, 10),
                                listOf(19, 20, 21),
                                if (rate > 0) HelperFunctions.computeAchievementProgress(
                                    userRepository.getTutorRates(tutorId).first().toDouble(),
                                    listOf(1, 5, 10),
                                    listOf(22, 23, 24),
                                    achievementProgressTutor
                                ) else achievementProgressTutor
                            )
                        )
                    )
                    userRepository.updateTutorBadgeProgress(computedProgressTutor, tutorId)

                    // Show toast message if an achievement is completed
                    HelperFunctions.checkCompletedAchievements(
                        achievementProgressTutor, computedProgressTutor
                    ) {
                        Toast.makeText(
                            context,
                            GetAchievements.getAchievements(0, context)[it][0],
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    navigate()
                } else {
                    val itemsInvalid = assessment.map { it.id + 1 }
                        .filterIndexed { idx, _ -> !validationResult[idx] }
                    Toast.makeText(
                        context,
                        "Invalid input in ${itemsInvalid.joinToString(", ")}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateAssignment(assessment: List<AssessmentType>): List<Boolean> {
        return assessment.map {
            when (it) {
                is MultipleChoiceFields -> {
                    it.question.isNotEmpty() && it.choices.all { ch -> ch.isNotEmpty() } && it.question.length >= 15
                }

                is IdentificationFields -> {
                    it.question.isNotEmpty() && it.answer.isNotEmpty() && it.question.length >= 15
                }

                is TrueOrFalseFields -> {
                    it.question.isNotEmpty() && it.question.length >= 15
                }

                else -> throw IllegalArgumentException()
            }
        }
    }

    private fun serializeAssignmentData(
        assessment: List<AssessmentType>,
        moduleName: String,
        creator: String
    ): String {
        val serializedData = assessment.map {
            when (it) {
                is MultipleChoiceFields -> {
                    listOf(
                        moduleName,
                        it.question,
                        it.choices[0],
                        it.choices[1],
                        it.choices[2],
                        it.choices[3],
                        it.answer.selected,
                        creator
                    )
                }

                is IdentificationFields -> {
                    listOf(moduleName, it.question, it.answer, creator)
                }

                is TrueOrFalseFields -> {
                    listOf(moduleName, it.question, it.answer.selected, creator)
                }

                else -> throw IllegalArgumentException()
            }
        }
        return Json.encodeToString(serializedData)
    }
}