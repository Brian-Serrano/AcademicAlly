package com.serrano.academically.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.Leaderboard
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
class LeaderboardViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _leaderboardsData = MutableStateFlow<List<Leaderboard>>(emptyList())
    val leaderboardsData: StateFlow<List<Leaderboard>> = _leaderboardsData.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData() {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val leaderboardCache = ActivityCacheManager.leaderboard
                val currentUserCache = ActivityCacheManager.currentUser

                if (leaderboardCache != null && currentUserCache != null) {
                    _leaderboardsData.value = leaderboardCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi()
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

        val response = when (val leaderboardsData = academicallyApi.getLeaderboard()) {
            is WithCurrentUser.Success -> leaderboardsData
            is WithCurrentUser.Error -> throw IllegalArgumentException(leaderboardsData.error)
        }

        _leaderboardsData.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.leaderboard = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }
}