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
import thesis.academic.ally.api.Session
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class AboutSessionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _session = MutableStateFlow(Session())
    val session: StateFlow<Session> = _session.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(sessionId: Int) {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val sessionCache = ActivityCacheManager.aboutSession[sessionId]
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

        val response = when (val session = academicallyApi.getSession(sessionId)) {
            is WithCurrentUser.Success -> session
            is WithCurrentUser.Error -> throw IllegalArgumentException(session.error)
        }

        _session.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.aboutSession[sessionId] = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }
}