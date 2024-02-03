package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.SupportBody
import com.serrano.academically.api.SupportMessage
import com.serrano.academically.api.WithCurrentUser
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
class SupportChatViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _chats = MutableStateFlow<List<SupportMessage>>(emptyList())
    val chats: StateFlow<List<SupportMessage>> = _chats.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _hasSentMessage = MutableStateFlow(false)
    val hasSentMessage: StateFlow<Boolean> = _hasSentMessage.asStateFlow()

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun getData(context: Context) {
        viewModelScope.launch {
            try {
                val supportCache = ActivityCacheManager.supportChat
                val currentUserCache = ActivityCacheManager.currentUser

                if (supportCache != null && currentUserCache != null) {
                    _chats.value = supportCache
                    _drawerData.value = currentUserCache
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

        val response = when (val support = academicallyApi.getSupportMessages()) {
            is WithCurrentUser.Success -> support
            is WithCurrentUser.Error -> throw IllegalArgumentException(support.error)
        }

        _chats.value = response.data!!
        _drawerData.value = response.currentUser!!

        ActivityCacheManager.supportChat = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun sendSupportMessage(message: String, id: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

                academicallyApi.sendSupportMessage(
                    SupportBody(message, id, 1)
                )

                callApi(context)

                _hasSentMessage.value = true
                _isRefreshLoading.value = false
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to send message.", Toast.LENGTH_LONG).show()
            }
        }
    }
}