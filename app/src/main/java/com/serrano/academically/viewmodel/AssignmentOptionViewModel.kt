package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.SessionForAssignment
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.DeadlineField
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentOptionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _itemsDropdown = MutableStateFlow(DropDownState(listOf("5", "10", "15"), "5", false))
    val itemsDropdown: StateFlow<DropDownState> = _itemsDropdown.asStateFlow()

    private val _typeDropdown = MutableStateFlow(DropDownState(listOf("Multiple Choice", "Identification", "True or False"), "Multiple Choice", false))
    val typeDropdown: StateFlow<DropDownState> = _typeDropdown.asStateFlow()

    private val _deadlineField = MutableStateFlow(DeadlineField())
    val deadlineField: StateFlow<DeadlineField> = _deadlineField.asStateFlow()

    private val _session = MutableStateFlow(SessionForAssignment())
    val session: StateFlow<SessionForAssignment> = _session.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val sessionCache = ActivityCacheManager.assignmentOption[sessionId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (sessionCache != null && currentUserCache != null) {
                    _session.value = sessionCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(sessionId, context)
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(sessionId, context)

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(sessionId: Int, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

        val response = when (val session = academicallyApi.getSessionForAssignment(sessionId)) {
            is WithCurrentUser.Success -> session
            is WithCurrentUser.Error -> throw IllegalArgumentException(session.error)
        }

        _session.value = response.data!!
        _drawerData.value = response.currentUser!!

        ActivityCacheManager.assignmentOption[sessionId] = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun updateItemsDropdown(newDropDownState: DropDownState) {
        _itemsDropdown.value = newDropDownState
    }

    fun updateTypeDropdown(newDropDownState: DropDownState) {
        _typeDropdown.value = newDropDownState
    }

    fun updateDeadline(newDeadlineField: DeadlineField) {
        _deadlineField.value = newDeadlineField
    }

    fun validateDeadlineFormatAndNavigate(
        deadlineField: DeadlineField,
        navigate: (String) -> Unit
    ) {
        try {
            navigate(Utils.validateDateAndNavigate("${deadlineField.date} ${deadlineField.time}"))
        } catch (e: Exception) {
            updateDeadline(deadlineField.copy(error = "Invalid time"))
        }
    }
}