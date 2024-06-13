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
import thesis.academic.ally.api.AssignmentNotifications
import thesis.academic.ally.api.MessageNotifications
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.SessionNotifications
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

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

    fun getData() {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val currentUserCache = ActivityCacheManager.currentUser

                if (currentUserCache != null) {
                    mutableDrawerData.value = currentUserCache
                } else {
                    mutableDrawerData.value = when (val drawerData = academicallyApi.getCurrentUser()) {
                        is NoCurrentUser.Success -> drawerData.data!!
                        is NoCurrentUser.Error -> throw IllegalArgumentException(drawerData.error)
                    }
                    ActivityCacheManager.currentUser = mutableDrawerData.value
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

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshPage() {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi()

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                getMessagesFromApi()
                getSessionsFromApi()
                getAssignmentsFromApi()

                _isRefreshLoading.value = false
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi() {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

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