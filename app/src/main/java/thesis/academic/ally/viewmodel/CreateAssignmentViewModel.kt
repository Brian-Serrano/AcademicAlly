package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.AssessmentBody
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.api.CreateAssignmentBody
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.SessionForAssignment
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.AssessmentType
import thesis.academic.ally.utils.DropDownState
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CreateAssignmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

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

    fun getData(sessionId: Int, items: Int, type: String) {
        viewModelScope.launch {
            try {
                val sessionCache = ActivityCacheManager.createAssignment[sessionId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (sessionCache != null && currentUserCache != null) {
                    _session.value = sessionCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(sessionId)
                }

                refreshQuizFields(items, type)

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(sessionId: Int, items: Int, type: String) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(sessionId)

                refreshQuizFields(items, type)

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(sessionId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        when (val session = academicallyApi.getSessionForAssignment(sessionId)) {
            is WithCurrentUser.Success -> {
                _session.value = session.data!!
                mutableDrawerData.value = session.currentUser!!

                ActivityCacheManager.createAssignment[sessionId] = session.data
                ActivityCacheManager.currentUser = session.currentUser
            }
            is WithCurrentUser.Error -> throw IllegalArgumentException(session.error)
        }
    }

    private fun refreshQuizFields(items: Int, type: String) {
        _quizFields.value = when (type) {
            "Multiple Choice" -> List(items) {
                AssessmentType.MultipleChoiceFields(
                    id = it,
                    question = "",
                    choices = listOf("", "", "", ""),
                    answer = DropDownState(listOf("A", "B", "C", "D"), "A", false)
                )
            }

            "Identification" -> List(items) {
                AssessmentType.IdentificationFields(
                    id = it,
                    question = "",
                    answer = ""
                )
            }

            "True or False" -> List(items) {
                AssessmentType.TrueOrFalseFields(
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
        assessment: List<AssessmentType>,
        name: String
    ) {
        viewModelScope.launch {
            try {
                val validationResult = validateAssignment(assessment)
                if (validationResult.all { it }) {

                    Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                    val apiResponse = academicallyApi.completeSessionAndCreateAssignment(
                        CreateAssignmentBody(
                            session.sessionId,
                            session.studentId,
                            session.tutorId,
                            session.courseId,
                            session.moduleId,
                            mapAssignment(assessment, session.moduleName, name),
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
                        getApplication()
                    )

                    ActivityCacheManager.createAssignment.remove(session.sessionId)
                    ActivityCacheManager.assignmentOption.remove(session.sessionId)
                    ActivityCacheManager.aboutSession.remove(session.sessionId)
                    ActivityCacheManager.notificationsAssignments = null
                    ActivityCacheManager.notificationsSessions = null
                    ActivityCacheManager.archiveCompletedSessions = null

                    navigate()
                } else {
                    val itemsInvalid = assessment.map {
                        Utils.getFieldId(it) + 1
                    }
                        .filterIndexed { idx, _ -> !validationResult[idx] }
                    Toast.makeText(
                        getApplication(),
                        "Invalid input in ${itemsInvalid.joinToString(", ")}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateAssignment(assessment: List<AssessmentType>): List<Boolean> {
        return assessment.map {
            when (it) {
                is AssessmentType.MultipleChoiceFields -> {
                    it.question.isNotEmpty() && it.choices.all { ch -> ch.isNotEmpty() } && it.question.length >= 15
                }

                is AssessmentType.IdentificationFields -> {
                    it.question.isNotEmpty() && it.answer.isNotEmpty() && it.question.length >= 15
                }

                is AssessmentType.TrueOrFalseFields -> {
                    it.question.isNotEmpty() && it.question.length >= 15
                }
            }
        }
    }

    private fun mapAssignment(assessment: List<AssessmentType>, moduleName: String, creator: String): List<AssessmentBody> {
        return assessment.map {
            when (it) {
                is AssessmentType.MultipleChoiceFields -> AssessmentBody(
                    question = it.question,
                    answer = it.answer.selected,
                    letterA = it.choices[0],
                    letterB = it.choices[1],
                    letterC = it.choices[2],
                    letterD = it.choices[3],
                    module = moduleName,
                    creator = creator
                )
                is AssessmentType.IdentificationFields -> AssessmentBody(
                    question = it.question,
                    answer = it.answer,
                    module = moduleName,
                    creator = creator
                )
                is AssessmentType.TrueOrFalseFields -> AssessmentBody(
                    question = it.question,
                    answer = it.answer.selected,
                    module = moduleName,
                    creator = creator
                )
            }
        }
    }
}