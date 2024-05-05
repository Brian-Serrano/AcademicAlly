package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.api.Tutor
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutTutorViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _tutor = MutableStateFlow(Tutor())
    val tutor: StateFlow<Tutor> = _tutor.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(tutorId: Int) {
        viewModelScope.launch {
            try {
                ActivityCacheManager.profile = null

                val tutorCache = ActivityCacheManager.aboutTutor[tutorId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (tutorCache != null && currentUserCache != null) {
                    _tutor.value = tutorCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(tutorId)
                }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(tutorId: Int) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(tutorId)

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(tutorId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        val response = when (val tutor = academicallyApi.getTutor(tutorId)) {
            is WithCurrentUser.Success -> tutor
            is WithCurrentUser.Error -> throw IllegalArgumentException(tutor.error)
        }

        _tutor.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.aboutTutor[tutorId] = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }
}