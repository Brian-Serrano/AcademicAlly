package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.api.AssignmentNotifications
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.MessageNotifications
import com.serrano.academically.api.SessionNotifications
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _message = MutableStateFlow<List<MessageNotifications>>(emptyList())
    val message: StateFlow<List<MessageNotifications>> = _message.asStateFlow()

    private val _session = MutableStateFlow<List<SessionNotifications>>(emptyList())
    val session: StateFlow<List<SessionNotifications>> = _session.asStateFlow()

    private val _assignment = MutableStateFlow<List<AssignmentNotifications>>(emptyList())
    val assignment: StateFlow<List<AssignmentNotifications>> = _assignment.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(context: Context) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

                val currentUserCache = ActivityCacheManager.currentUser

                if (currentUserCache != null) {
                    _drawerData.value = currentUserCache
                } else {
                    _drawerData.value = when (val drawerData = academicallyApi.getCurrentUser()) {
                        is NoCurrentUser.Success -> drawerData.data!!
                        is NoCurrentUser.Error -> throw IllegalArgumentException(drawerData.error)
                    }
                    ActivityCacheManager.currentUser = _drawerData.value
                }

                val messagesCache = ActivityCacheManager.notificationsMessages

                if (messagesCache != null) {
                    _message.value = messagesCache
                } else {
                    getMessagesFromApi()
                }

                val sessionsCache = ActivityCacheManager.notificationsSessions

                if (sessionsCache != null) {
                    _session.value = sessionsCache
                } else {
                    getSessionsFromApi()
                }

                val assignmentsCache = ActivityCacheManager.notificationsAssignments

                if (assignmentsCache != null) {
                    _assignment.value = assignmentsCache
                } else {
                    getAssignmentsFromApi()
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshPage(context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(context)

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun refreshData(context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

                getMessagesFromApi()
                getSessionsFromApi()
                getAssignmentsFromApi()

                _isRefreshLoading.value = false
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

        when (_tabIndex.value) {
            0 -> getMessagesFromApi()
            1 -> getSessionsFromApi()
            2 -> getAssignmentsFromApi()
        }
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }

    private suspend fun getMessagesFromApi() {
        val message = academicallyApi.getMessageNotifications()
        _message.value = when (message) {
            is NoCurrentUser.Success -> message.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(message.error)
        }
        ActivityCacheManager.notificationsMessages = _message.value
    }

    private suspend fun getSessionsFromApi() {
        val session = academicallyApi.getSessionNotifications()
        _session.value = when (session) {
            is NoCurrentUser.Success -> session.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(session.error)
        }
        ActivityCacheManager.notificationsSessions = _session.value
    }

    private suspend fun getAssignmentsFromApi() {
        val assignment = academicallyApi.getAssignmentNotifications()
        _assignment.value = when (assignment) {
            is NoCurrentUser.Success -> assignment.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(assignment.error)
        }
        ActivityCacheManager.notificationsAssignments = _assignment.value
    }
}