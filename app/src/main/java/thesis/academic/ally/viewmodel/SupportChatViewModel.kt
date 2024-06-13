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
import thesis.academic.ally.api.SupportBody
import thesis.academic.ally.api.SupportMessage
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class SupportChatViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
): BaseViewModel(application) {

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

    fun getData() {
        viewModelScope.launch {
            try {
                val supportCache = ActivityCacheManager.supportChat
                val currentUserCache = ActivityCacheManager.currentUser

                if (supportCache != null && currentUserCache != null) {
                    _chats.value = supportCache
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

        val response = when (val support = academicallyApi.getSupportMessages()) {
            is WithCurrentUser.Success -> support
            is WithCurrentUser.Error -> throw IllegalArgumentException(support.error)
        }

        _chats.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.supportChat = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun sendSupportMessage(message: String, id: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                academicallyApi.sendSupportMessage(
                    SupportBody(message, id, 0)
                )

                callApi()

                _hasSentMessage.value = true
                _isRefreshLoading.value = false
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to send message.", Toast.LENGTH_LONG).show()
            }
        }
    }
}