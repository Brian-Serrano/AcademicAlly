package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.activity.userDataStore
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.api.AssignmentNotifications
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.MessageNotifications
import com.serrano.academically.api.RateBody
import com.serrano.academically.api.SessionArchive
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.RateDialogStates
import com.serrano.academically.utils.SearchInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _rejectedMessages = MutableStateFlow<List<MessageNotifications>>(emptyList())
    val rejectedMessages: StateFlow<List<MessageNotifications>> = _rejectedMessages.asStateFlow()

    private val _acceptedMessages = MutableStateFlow<List<MessageNotifications>>(emptyList())
    val acceptedMessages: StateFlow<List<MessageNotifications>> = _acceptedMessages.asStateFlow()

    private val _cancelledSessions = MutableStateFlow<List<SessionArchive>>(emptyList())
    val cancelledSessions: StateFlow<List<SessionArchive>> = _cancelledSessions.asStateFlow()

    private val _completedSessions = MutableStateFlow<List<SessionArchive>>(emptyList())
    val completedSessions: StateFlow<List<SessionArchive>> = _completedSessions.asStateFlow()

    private val _deadlinedTasks = MutableStateFlow<List<AssignmentNotifications>>(emptyList())
    val deadlinedTasks: StateFlow<List<AssignmentNotifications>> = _deadlinedTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<AssignmentNotifications>>(emptyList())
    val completedTasks: StateFlow<List<AssignmentNotifications>> = _completedTasks.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _navBarIndex = MutableStateFlow(0)
    val navBarIndex: StateFlow<Int> = _navBarIndex.asStateFlow()

    private val _rating = MutableStateFlow(RateDialogStates())
    val rating: StateFlow<RateDialogStates> = _rating.asStateFlow()

    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen.asStateFlow()

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun updateSearch(newSearch: SearchInfo) {
        _searchInfo.value = newSearch
    }

    private suspend fun updateHistory() {
        updateSearch(_searchInfo.value.copy(history = userCacheRepository.userDataStore.data.first().searchArchiveHistory))
    }

    fun getData(context: Context) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                    val currentUserCache = ActivityCacheManager.currentUser

                    if (currentUserCache != null) {
                        _drawerData.value = currentUserCache
                    } else {
                        getCurrentUser()
                    }

                    val acceptedMessagesCache = ActivityCacheManager.archiveAcceptedMessages

                    if (acceptedMessagesCache != null) {
                        _acceptedMessages.value = acceptedMessagesCache
                    } else {
                        getAcceptedMessagesFromApi("")
                    }

                    val rejectedMessagesCache = ActivityCacheManager.archiveRejectedMessages

                    if (rejectedMessagesCache != null) {
                        _rejectedMessages.value = rejectedMessagesCache
                    } else {
                        getRejectedMessagesFromApi("")
                    }

                    val completedSessionsCache = ActivityCacheManager.archiveCompletedSessions

                    if (completedSessionsCache != null) {
                        _completedSessions.value = completedSessionsCache
                    } else {
                        getCompletedSessionsFromApi("")
                    }

                    val cancelledSessionsCache = ActivityCacheManager.archiveCancelledSessions

                    if (cancelledSessionsCache != null) {
                        _cancelledSessions.value = cancelledSessionsCache
                    } else {
                        getCancelledSessionsFromApi("")
                    }

                    val completedTasksCache = ActivityCacheManager.archiveCompletedTasks

                    if (completedTasksCache != null) {
                        _completedTasks.value = completedTasksCache
                    } else {
                        getCompletedTasksFromApi("")
                    }

                    val deadlinedTasksCache = ActivityCacheManager.archiveDeadlinedTasks

                    if (deadlinedTasksCache != null) {
                        _deadlinedTasks.value = deadlinedTasksCache
                    } else {
                        getDeadlinedTasksFromApi("")
                    }

                    updateHistory()
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun search(searchQuery: String, context: Context) {
        viewModelScope.launch {
            try {
                _processState.value = ProcessState.Loading

                if (searchQuery.isNotEmpty()) {
                    callApi(searchQuery, context)
                    userCacheRepository.addSearchArchiveHistory(searchQuery)
                    updateHistory()
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshPage(searchQuery: String, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(searchQuery, context)

                updateHistory()

                _isRefreshLoading.value = false
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

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                    getAcceptedMessagesFromApi("")
                    getRejectedMessagesFromApi("")
                    getCompletedSessionsFromApi("")
                    getCancelledSessionsFromApi("")
                    getCompletedTasksFromApi("")
                    getDeadlinedTasksFromApi("")
                }

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(searchQuery: String, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
            when (_tabIndex.value) {
                0 -> when (_navBarIndex.value) {
                    0 -> getAcceptedMessagesFromApi(searchQuery)
                    1 -> getRejectedMessagesFromApi(searchQuery)
                }

                1 -> when (_navBarIndex.value) {
                    0 -> getCompletedSessionsFromApi(searchQuery)
                    1 -> getCancelledSessionsFromApi(searchQuery)
                }

                2 -> when (_navBarIndex.value) {
                    0 -> getCompletedTasksFromApi(searchQuery)
                    1 -> getDeadlinedTasksFromApi(searchQuery)
                }
            }
        }
    }

    fun rateUser(rating: RateDialogStates, context: Context) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                    val apiResponse = academicallyApi.rateUser(RateBody(rating.sessionId, rating.userId))
                    Utils.showToast(
                        when (apiResponse) {
                            is NoCurrentUser.Success -> apiResponse.data!!
                            is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                        },
                        context
                    )

                    Toast.makeText(context, "${rating.name} Rated Successfully!", Toast.LENGTH_LONG).show()

                    refreshSessionAfterRate()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun refreshSessionAfterRate() {
        try {
            _processState.value = ProcessState.Loading

            getCompletedSessionsFromApi("")

            _processState.value = ProcessState.Success
        } catch (e: Exception) {
            _processState.value = ProcessState.Error(e.message ?: "")
        }
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }

    fun updateNavBarIndex(newIdx: Int) {
        _navBarIndex.value = newIdx
    }

    fun updateRatingDialog(newRatingDialogStates: RateDialogStates) {
        _rating.value = newRatingDialogStates
    }

    fun toggleDialog(bool: Boolean) {
        _isDialogOpen.value = bool
    }

    private suspend fun getAcceptedMessagesFromApi(searchQuery: String) {
        val acceptedMessages = academicallyApi.searchMessageArchives("ACCEPT", searchQuery)
        _acceptedMessages.value = when (acceptedMessages) {
            is NoCurrentUser.Success -> acceptedMessages.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(acceptedMessages.error)
        }
        ActivityCacheManager.archiveAcceptedMessages = _acceptedMessages.value
    }

    private suspend fun getRejectedMessagesFromApi(searchQuery: String) {
        val rejectedMessages = academicallyApi.searchMessageArchives("REJECT", searchQuery)
        _rejectedMessages.value = when (rejectedMessages) {
            is NoCurrentUser.Success -> rejectedMessages.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(rejectedMessages.error)
        }
        ActivityCacheManager.archiveRejectedMessages = _rejectedMessages.value
    }

    private suspend fun getCompletedSessionsFromApi(searchQuery: String) {
        val completedSessions = academicallyApi.searchSessionArchives("COMPLETED", searchQuery)
        _completedSessions.value = when (completedSessions) {
            is NoCurrentUser.Success -> completedSessions.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(completedSessions.error)
        }
        ActivityCacheManager.archiveCompletedSessions = _completedSessions.value
    }

    private suspend fun getCancelledSessionsFromApi(searchQuery: String) {
        val cancelledSessions = academicallyApi.searchSessionArchives("CANCELLED", searchQuery)
        _cancelledSessions.value = when (cancelledSessions) {
            is NoCurrentUser.Success -> cancelledSessions.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(cancelledSessions.error)
        }
        ActivityCacheManager.archiveCancelledSessions = _cancelledSessions.value
    }

    private suspend fun getCompletedTasksFromApi(searchQuery: String) {
        val completedTasks = academicallyApi.searchAssignmentArchives("COMPLETED", searchQuery)
        _completedTasks.value = when (completedTasks) {
            is NoCurrentUser.Success -> completedTasks.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(completedTasks.error)
        }
        ActivityCacheManager.archiveCompletedTasks = _completedTasks.value
    }

    private suspend fun getDeadlinedTasksFromApi(searchQuery: String) {
        val deadlinedTasks = academicallyApi.searchAssignmentArchives("DEADLINED", searchQuery)
        _deadlinedTasks.value = when (deadlinedTasks) {
            is NoCurrentUser.Success -> deadlinedTasks.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(deadlinedTasks.error)
        }
        ActivityCacheManager.archiveDeadlinedTasks = _deadlinedTasks.value
    }

    private suspend fun getCurrentUser() {
        val drawerData = academicallyApi.getCurrentUser()
        _drawerData.value = when (drawerData) {
            is NoCurrentUser.Success -> drawerData.data!!
            is NoCurrentUser.Error -> throw IllegalArgumentException(drawerData.error)
        }
        ActivityCacheManager.currentUser = _drawerData.value
    }
}