package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.Assignment
import com.serrano.academically.api.AssignmentBody
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

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

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun addAnswer(answer: String, index: Int) {
        _assessmentAnswers.value = _assessmentAnswers.value.mapIndexed { idx, ans -> if (idx == index) answer else ans }
    }

    fun getData(assignmentId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val assignmentCache = ActivityCacheManager.assignment[assignmentId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (assignmentCache != null && currentUserCache != null) {
                    _assignment.value = assignmentCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(assignmentId, context)
                }

                _assessmentData.value = deserializeAssignmentData(_assignment.value.data)

                _assessmentAnswers.value = List(_assessmentData.value.size) { "" }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(assignmentId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(assignmentId, context)

                _assessmentData.value = deserializeAssignmentData(_assignment.value.data)

                _assessmentAnswers.value = List(_assessmentData.value.size) { "" }

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(assignmentId: Int, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
            val response = when (val assignment = academicallyApi.getAssignment(assignmentId)) {
                is WithCurrentUser.Success -> assignment
                is WithCurrentUser.Error -> throw IllegalArgumentException(assignment.error)
            }

            _assignment.value = response.data!!
            _drawerData.value = response.currentUser!!

            ActivityCacheManager.assignment[assignmentId] = response.data
            ActivityCacheManager.currentUser = response.currentUser
        }
    }

    fun completeAssignment(
        score: Int,
        assignmentId: Int,
        context: Context,
        navigate: (Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _nextButtonEnabled.value = false

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                    val apiResponse = academicallyApi.completeAssignment(AssignmentBody(assignmentId, score))
                    Utils.showToast(
                        when (apiResponse) {
                            is NoCurrentUser.Success -> apiResponse.data!!
                            is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                        },
                        context
                    )

                    ActivityCacheManager.assignment.remove(assignmentId)
                    ActivityCacheManager.notificationsAssignments = null
                    ActivityCacheManager.archiveCompletedTasks = null
                }

                _nextButtonEnabled.value = true

                navigate(score)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deserializeAssignmentData(assessment: String): List<List<String>> {
        return Json.decodeFromString<List<List<String>>>(assessment)
    }
}