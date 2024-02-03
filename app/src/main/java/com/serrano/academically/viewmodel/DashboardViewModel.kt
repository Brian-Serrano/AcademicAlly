package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.Dashboard
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.NoCurrentUser
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
class DashboardViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _dashboard = MutableStateFlow(Dashboard())
    val dashboard: StateFlow<Dashboard> = _dashboard.asStateFlow()

    private val _animationPlayed = MutableStateFlow(false)
    val animationPlayed: StateFlow<Boolean> = _animationPlayed.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(context: Context, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val dashboardCache = ActivityCacheManager.dashboard
                val currentUserCache = ActivityCacheManager.currentUser

                if (dashboardCache != null && currentUserCache != null) {
                    _dashboard.value = dashboardCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(context)
                }

                if (_drawerData.value.primaryLearning == "NA" || _drawerData.value.secondaryLearning == "NA") {
                    navigate()
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(context: Context) {
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

    private suspend fun callApi(context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

        val response = when (val dashboard = academicallyApi.getDashboardData()) {
            is WithCurrentUser.Success -> dashboard
            is WithCurrentUser.Error -> throw IllegalArgumentException(dashboard.error)
        }

        _dashboard.value = response.data!!
        _drawerData.value = response.currentUser!!

        ActivityCacheManager.dashboard = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun playAnimation() {
        _animationPlayed.value = true
    }
}