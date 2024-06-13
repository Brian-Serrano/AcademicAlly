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
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.RejectStudentBody
import thesis.academic.ally.api.Student
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class AboutStudentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _message = MutableStateFlow(Student())
    val message: StateFlow<Student> = _message.asStateFlow()

    private val _rejectButtonEnabled = MutableStateFlow(true)
    val rejectButtonEnabled: StateFlow<Boolean> = _rejectButtonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(messageId: Int) {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val messageCache = ActivityCacheManager.aboutStudent[messageId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (messageCache != null && currentUserCache != null) {
                    _message.value = messageCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(messageId)
                }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(messageId: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(messageId)

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(messageId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        val response = when (val message = academicallyApi.getStudent(messageId)) {
            is WithCurrentUser.Success -> message
            is WithCurrentUser.Error -> throw IllegalArgumentException(message.error)
        }

        _message.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.aboutStudent[messageId] = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }

    fun respond(studentId: Int, tutorId: Int, messageId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                _rejectButtonEnabled.value = false

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val apiResponse = academicallyApi.rejectStudent(RejectStudentBody(messageId, studentId, tutorId))
                Utils.showToast(
                    when (apiResponse) {
                        is NoCurrentUser.Success -> apiResponse.data!!
                        is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                    },
                    getApplication()
                )

                ActivityCacheManager.aboutStudent.remove(messageId)
                ActivityCacheManager.notificationsMessages = null
                ActivityCacheManager.archiveRejectedMessages = null

                _rejectButtonEnabled.value = true

                navigate()
            } catch (e: Exception) {
                _rejectButtonEnabled.value = true
                Toast.makeText(getApplication(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }
}