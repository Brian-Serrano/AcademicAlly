package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.Course2
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.OptionalCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SearchInfo
import com.serrano.academically.utils.Utils
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
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

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
                    _drawerData.value = currentUserCache
                    _isAuthorized.value = Utils.checkToken(userCacheRepository.userDataStore.data.first().authToken)
                } else {
                    callApi()
                }

                _coursesRender.value = _courses.value

                updateHistory()

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi()

                _coursesRender.value = _courses.value

                updateHistory()

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi() {
        when (val courses = academicallyApi.getCourses()) {
            is OptionalCurrentUser.CurrentUserData -> {
                _courses.value = courses.data!!
                _drawerData.value = courses.currentUser!!
                _isAuthorized.value = true

                ActivityCacheManager.chooseAssessment = courses.data
                ActivityCacheManager.currentUser = courses.currentUser
            }
            is OptionalCurrentUser.UserData -> {
                _courses.value = courses.data!!

                ActivityCacheManager.chooseAssessment = courses.data
                ActivityCacheManager.currentUser = _drawerData.value
            }
            is OptionalCurrentUser.Error -> throw IllegalArgumentException(courses.error)
        }
    }

    fun search(searchQuery: String) {
        viewModelScope.launch {
            try {
                _processState.value = ProcessState.Loading

                if (searchQuery.isEmpty()) {
                    _coursesRender.value = _courses.value
                } else {
                    val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
                    userCacheRepository.addSearchCourseHistory(searchQuery)
                    _coursesRender.value = _courses.value.filter { regex.containsMatchIn(it.name) }
                }

                updateHistory()

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }
}