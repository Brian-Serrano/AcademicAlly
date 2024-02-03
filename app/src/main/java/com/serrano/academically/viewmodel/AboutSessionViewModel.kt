package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.Session
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
class AboutSessionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _session = MutableStateFlow(Session())
    val session: StateFlow<Session> = _session.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val sessionCache = ActivityCacheManager.aboutSession[sessionId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (sessionCache != null && currentUserCache != null) {
                    _session.value = sessionCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(sessionId, context)
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(sessionId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(sessionId, context)

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(sessionId: Int, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

        val response = when (val session = academicallyApi.getSession(sessionId)) {
            is WithCurrentUser.Success -> session
            is WithCurrentUser.Error -> throw IllegalArgumentException(session.error)
        }

        _session.value = response.data!!
        _drawerData.value = response.currentUser!!

        ActivityCacheManager.aboutSession[sessionId] = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }
}