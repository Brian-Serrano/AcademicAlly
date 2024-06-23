package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.AssessmentBody
import thesis.academic.ally.api.Assignment
import thesis.academic.ally.api.AssignmentBody
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _assignment = MutableStateFlow(Assignment())
    val assignment: StateFlow<Assignment> = _assignment.asStateFlow()

    private val _assessmentData = MutableStateFlow<List<List<String>>>(emptyList())
    val assessmentData: StateFlow<List<List<String>>> = _assessmentData.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _assessmentAnswers = MutableStateFlow<List<String>>(emptyList())
    val assessmentAnswers: StateFlow<List<String>> = _assessmentAnswers.asStateFlow()

    private val _nextButtonEnabled = MutableStateFlow(true)
    val nextButtonEnabled: StateFlow<Boolean> = _nextButtonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    private val _dialogOpen = MutableStateFlow(false)
    val dialogOpen: StateFlow<Boolean> = _dialogOpen.asStateFlow()

    private val _assessmentResults = MutableStateFlow<List<Boolean>>(emptyList())
    val assessmentResults: StateFlow<List<Boolean>> = _assessmentResults.asStateFlow()

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun toggleDialog() {
        _dialogOpen.value = !_dialogOpen.value
    }

    fun addAnswer(answer: String, index: Int) {
        _assessmentAnswers.value = _assessmentAnswers.value.mapIndexed { idx, ans -> if (idx == index) answer else ans }
    }

    fun getData(assignmentId: Int) {
        viewModelScope.launch {
            try {
                val assignmentCache = ActivityCacheManager.assignment[assignmentId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (assignmentCache != null && currentUserCache != null) {
                    _assignment.value = assignmentCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(assignmentId)
                }

                _assessmentData.value = mapAssignmentData(_assignment.value.data, _assignment.value.type)

                _assessmentAnswers.value = List(_assessmentData.value.size) { "" }
                _assessmentResults.value = List(_assessmentData.value.size) { false }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(assignmentId: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(assignmentId)

                _assessmentData.value = mapAssignmentData(_assignment.value.data, _assignment.value.type)

                _assessmentAnswers.value = List(_assessmentData.value.size) { "" }
                _assessmentResults.value = List(_assessmentData.value.size) { false }

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(assignmentId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        val response = when (val assignment = academicallyApi.getAssignment(assignmentId)) {
            is WithCurrentUser.Success -> assignment
            is WithCurrentUser.Error -> throw IllegalArgumentException(assignment.error)
        }

        _assignment.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.assignment[assignmentId] = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun completeAssignment(score: Pair<Int, List<Boolean>>, assignmentId: Int, openDialog: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                _nextButtonEnabled.value = false

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val apiResponse = academicallyApi.completeAssignment(AssignmentBody(assignmentId, score.first))
                Utils.showToast(
                    when (apiResponse) {
                        is NoCurrentUser.Success -> apiResponse.data!!
                        is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                    },
                    getApplication()
                )

                _assessmentResults.value = score.second

                ActivityCacheManager.assignment.remove(assignmentId)
                ActivityCacheManager.notificationsAssignments = null
                ActivityCacheManager.archiveCompletedTasks = null

                _nextButtonEnabled.value = true

                openDialog(score.first)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(getApplication(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mapAssignmentData(assessment: List<AssessmentBody>, type: String): List<List<String>> {
        return when (type) {
            "Multiple Choice" -> assessment.map {
                listOf(it.module, it.question, it.letterA!!, it.letterB!!, it.letterC!!, it.letterD!!, it.answer, it.creator)
            }
            "Identification", "True or False" -> assessment.map {
                listOf(it.module, it.question, it.answer, it.creator)
            }
            else -> throw IllegalArgumentException()
        }
    }
}