package com.serrano.academically.viewmodel

import android.app.Application
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
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _dashboard = MutableStateFlow(Dashboard())
    val dashboard: StateFlow<Dashboard> = _dashboard.asStateFlow()

    private val _animationPlayed = MutableStateFlow(false)
    val animationPlayed: StateFlow<Boolean> = _animationPlayed.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val dashboardCache = ActivityCacheManager.dashboard
                val currentUserCache = ActivityCacheManager.currentUser

                if (dashboardCache != null && currentUserCache != null) {
                    _dashboard.value = dashboardCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi()
                }

                if (mutableDrawerData.value.primaryLearning == "NA" || mutableDrawerData.value.secondaryLearning == "NA") {
                    navigate()
                }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData() {
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

    private suspend fun callApi() {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        val response = when (val dashboard = academicallyApi.getDashboardData()) {
            is WithCurrentUser.Success -> dashboard
            is WithCurrentUser.Error -> throw IllegalArgumentException(dashboard.error)
        }

        _dashboard.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.dashboard = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun playAnimation() {
        _animationPlayed.value = true
    }
}