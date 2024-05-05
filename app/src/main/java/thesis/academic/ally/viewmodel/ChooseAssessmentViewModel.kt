package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.Course2
import thesis.academic.ally.api.OptionalCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.SearchInfo
import thesis.academic.ally.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseAssessmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    private val _courses = MutableStateFlow<List<Course2>>(emptyList())
    val courses: StateFlow<List<Course2>> = _courses.asStateFlow()

    private val _coursesRender = MutableStateFlow<List<Course2>>(emptyList())
    val coursesRender: StateFlow<List<Course2>> = _coursesRender.asStateFlow()

    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun updateSearch(newSearch: SearchInfo) {
        _searchInfo.value = newSearch
    }

    private suspend fun updateHistory() {
        updateSearch(_searchInfo.value.copy(history = userCacheRepository.userDataStore.data.first().searchCourseHistory))
    }

    fun getData() {
        viewModelScope.launch {
            try {
                val coursesCache = ActivityCacheManager.chooseAssessment
                val currentUserCache = ActivityCacheManager.currentUser

                if (coursesCache != null && currentUserCache != null) {
                    _courses.value = coursesCache
                    mutableDrawerData.value = currentUserCache
                    _isAuthorized.value = Utils.checkToken(userCacheRepository.userDataStore.data.first().authToken)
                } else {
                    callApi()
                }

                _coursesRender.value = _courses.value

                updateHistory()

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

                _coursesRender.value = _courses.value

                updateHistory()

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi() {
        when (val courses = academicallyApi.getCourses()) {
            is OptionalCurrentUser.CurrentUserData -> {
                _courses.value = courses.data!!
                mutableDrawerData.value = courses.currentUser!!
                _isAuthorized.value = true

                ActivityCacheManager.chooseAssessment = courses.data
                ActivityCacheManager.currentUser = courses.currentUser
            }
            is OptionalCurrentUser.UserData -> {
                _courses.value = courses.data!!

                ActivityCacheManager.chooseAssessment = courses.data
                ActivityCacheManager.currentUser = mutableDrawerData.value
            }
            is OptionalCurrentUser.Error -> throw IllegalArgumentException(courses.error)
        }
    }

    fun search(searchQuery: String) {
        viewModelScope.launch {
            try {
                mutableProcessState.value = ProcessState.Loading

                if (searchQuery.isEmpty()) {
                    _coursesRender.value = _courses.value
                } else {
                    val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
                    userCacheRepository.addSearchCourseHistory(searchQuery)
                    _coursesRender.value = _courses.value.filter { regex.containsMatchIn(it.name) }
                }

                updateHistory()

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }
}