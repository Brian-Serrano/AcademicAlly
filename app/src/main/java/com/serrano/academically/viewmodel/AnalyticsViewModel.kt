package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.Analytics
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.ChartState
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(Analytics())
    val userData: StateFlow<Analytics> = _userData.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _animationPlayed = MutableStateFlow(false)
    val animationPlayed: StateFlow<Boolean> = _animationPlayed.asStateFlow()

    private val _chartTabIndex = MutableStateFlow(0)
    val chartTabIndex: StateFlow<Int> = _chartTabIndex.asStateFlow()

    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState> = _chartState.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(context: Context) {
        viewModelScope.launch {
            try {
                val analyticsCache = ActivityCacheManager.analytics

                if (analyticsCache != null) {
                    _userData.value = analyticsCache
                } else {
                    callApi(context)
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

        val response = when (val userData = academicallyApi.getAnalytics()) {
            is NoCurrentUser.Success -> userData
            is NoCurrentUser.Error -> throw IllegalArgumentException(userData.error)
        }
        _userData.value = response.data!!
        ActivityCacheManager.analytics = response.data
    }

    fun updateChartState(newState: ChartState) {
        _chartState.value = newState
    }

    fun toggleAnimation(bool: Boolean) {
        _animationPlayed.value = bool
    }

    fun updateChartTab(newIdx: Int) {
        toggleAnimation(false)
        _chartTabIndex.value = newIdx
    }
}