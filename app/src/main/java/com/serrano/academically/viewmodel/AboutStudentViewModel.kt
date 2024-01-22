package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.api.RejectStudentBody
import com.serrano.academically.api.Student
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutStudentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _message = MutableStateFlow(Student())
    val message: StateFlow<Student> = _message.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _rejectButtonEnabled = MutableStateFlow(true)
    val rejectButtonEnabled: StateFlow<Boolean> = _rejectButtonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(messageId: Int, context: Context) {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val messageCache = ActivityCacheManager.aboutStudent[messageId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (messageCache != null && currentUserCache != null) {
                    _message.value = messageCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(messageId, context)
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(messageId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(messageId, context)

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(messageId: Int, context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
            val response = when (val message = academicallyApi.getStudent(messageId)) {
                is WithCurrentUser.Success -> message
                is WithCurrentUser.Error -> throw IllegalArgumentException(message.error)
            }

            _message.value = response.data!!
            _drawerData.value = response.currentUser!!

            ActivityCacheManager.aboutStudent[messageId] = response.data
            ActivityCacheManager.currentUser = response.currentUser
        }
    }

    fun respond(studentId: Int, tutorId: Int, messageId: Int, context: Context, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                _rejectButtonEnabled.value = false

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                    val apiResponse = academicallyApi.rejectStudent(RejectStudentBody(messageId, studentId, tutorId))
                    Utils.showToast(
                        when (apiResponse) {
                            is NoCurrentUser.Success -> apiResponse.data!!
                            is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                        },
                        context
                    )

                    ActivityCacheManager.aboutStudent.remove(messageId)
                    ActivityCacheManager.notificationsMessages = null
                    ActivityCacheManager.archiveRejectedMessages = null
                }

                _rejectButtonEnabled.value = true

                navigate()
            } catch (e: Exception) {
                _rejectButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }
}