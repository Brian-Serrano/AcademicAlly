package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.api.SessionForAssignment
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.DeadlineField
import thesis.academic.ally.utils.DropDownState
import thesis.academic.ally.utils.Utils
import thesis.academic.ally.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentOptionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

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

    fun getData(sessionId: Int) {
        viewModelScope.launch {
            try {
                val sessionCache = ActivityCacheManager.assignmentOption[sessionId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (sessionCache != null && currentUserCache != null) {
                    _session.value = sessionCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(sessionId)
                }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(sessionId: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(sessionId)

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

        val response = when (val session = academicallyApi.getSessionForAssignment(sessionId)) {
            is WithCurrentUser.Success -> session
            is WithCurrentUser.Error -> throw IllegalArgumentException(session.error)
        }

        _session.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

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