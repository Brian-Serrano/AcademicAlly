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
import thesis.academic.ally.api.Profile
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _userData = MutableStateFlow(Profile())
    val userData: StateFlow<Profile> = _userData.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(otherId: Int) {
        viewModelScope.launch {
            try {
                val profileCache = ActivityCacheManager.profile
                val currentUserCache = ActivityCacheManager.currentUser

                if (profileCache != null && currentUserCache != null) {
                    _userData.value = profileCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(otherId)
                }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(otherId: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(otherId)

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(otherId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        val response = when (val userData = academicallyApi.getProfile(otherId)) {
            is WithCurrentUser.Success -> userData
            is WithCurrentUser.Error -> throw IllegalArgumentException(userData.error)
        }

        _userData.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.profile = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }
}