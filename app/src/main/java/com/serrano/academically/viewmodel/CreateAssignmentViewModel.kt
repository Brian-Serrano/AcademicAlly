package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.CreateAssignmentBody
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.api.SessionForAssignment
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.AssessmentType
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.IdentificationFields
import com.serrano.academically.utils.MultipleChoiceFields
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.TrueOrFalseFields
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CreateAssignmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _session = MutableStateFlow(SessionForAssignment())
    val session: StateFlow<SessionForAssignment> = _session.asStateFlow()

    private val _quizFields = MutableStateFlow<List<AssessmentType>>(emptyList())
    val quizFields: StateFlow<List<AssessmentType>> = _quizFields.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen: StateFlow<Boolean> = _isFilterDialogOpen.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(sessionId: Int, items: Int, type: String, context: Context) {
        viewModelScope.launch {
            try {
                val sessionCache = ActivityCacheManager.createAssignment[sessionId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (sessionCache != null && currentUserCache != null) {
                    _session.value = sessionCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(sessionId, context)
                }

                refreshQuizFields(items, type)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(sessionId: Int, items: Int, type: String, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(sessionId, context)

                refreshQuizFields(items, type)

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(sessionId: Int, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
            when (val session = academicallyApi.getSessionForAssignment(sessionId)) {
                is WithCurrentUser.Success -> {
                    _session.value = session.data!!
                    _drawerData.value = session.currentUser!!

                    ActivityCacheManager.createAssignment[sessionId] = session.data
                    ActivityCacheManager.currentUser = session.currentUser
                }
                is WithCurrentUser.Error -> throw IllegalArgumentException(session.error)
            }
        }
    }

    private fun refreshQuizFields(items: Int, type: String) {
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
        session: SessionForAssignment,
        rate: Int,
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

                    Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                        val apiResponse = academicallyApi.completeSessionAndCreateAssignment(
                            CreateAssignmentBody(
                                session.sessionId,
                                session.studentId,
                                session.tutorId,
                                session.courseId,
                                session.moduleId,
                                serializeAssignmentData(
                                    assessment,
                                    session.moduleName,
                                    name
                                ),
                                type,
                                deadLine.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")),
                                rate
                            )
                        )
                        Utils.showToast(
                            when (apiResponse) {
                                is NoCurrentUser.Success -> apiResponse.data!!
                                is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                            },
                            context
                        )

                        ActivityCacheManager.createAssignment.remove(session.sessionId)
                        ActivityCacheManager.assignmentOption.remove(session.sessionId)
                        ActivityCacheManager.aboutSession.remove(session.sessionId)
                        ActivityCacheManager.notificationsAssignments = null
                        ActivityCacheManager.notificationsSessions = null
                        ActivityCacheManager.archiveCompletedSessions = null
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