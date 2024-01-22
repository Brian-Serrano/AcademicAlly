package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.Course
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.OptionalCurrentUser
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentOptionViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _course = MutableStateFlow(Course())
    val course: StateFlow<Course> = _course.asStateFlow()

    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    private val _startButtonEnabled = MutableStateFlow(true)
    val startButtonEnabled: StateFlow<Boolean> = _startButtonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(courseId: Int) {
        viewModelScope.launch {
            try {
                val courseCache = ActivityCacheManager.assessmentOption[courseId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (courseCache != null && currentUserCache != null) {
                    _course.value = courseCache
                    _drawerData.value = currentUserCache
                    _isAuthorized.value = Utils.checkToken(userCacheRepository.userDataStore.data.first().authToken)
                } else {
                    callApi(courseId)
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(courseId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(courseId)

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(courseId: Int) {
        when (val course = academicallyApi.getCourseNameAndDesc(courseId)) {
            is OptionalCurrentUser.CurrentUserData -> {
                _course.value = course.data!!
                _drawerData.value = course.currentUser!!
                _isAuthorized.value = true

                ActivityCacheManager.assessmentOption[courseId] = course.data
                ActivityCacheManager.currentUser = course.currentUser
            }
            is OptionalCurrentUser.UserData -> {
                _course.value = course.data!!

                ActivityCacheManager.assessmentOption[courseId] = course.data
                ActivityCacheManager.currentUser = _drawerData.value
            }
            is OptionalCurrentUser.Error -> throw IllegalArgumentException(course.error)
        }
    }

    fun saveAssessmentType(context: Context, navigate: (String, String) -> Unit) {
        viewModelScope.launch {
            try {
                _startButtonEnabled.value = false

                val data = userCacheRepository.userDataStore.data.first()

                if (data.assessmentItems.isNotEmpty() && data.assessmentType.isNotEmpty()) {
                    _startButtonEnabled.value = true

                    navigate(data.assessmentItems, data.assessmentType)
                } else {
                    val items = listOf("5", "10", "15").random()
                    val type = listOf("Multiple Choice", "Identification", "True or False").random()
                    userCacheRepository.saveAssessmentType(type, items)

                    _startButtonEnabled.value = true

                    navigate(items, type)
                }
            } catch (e: Exception) {
                _startButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }
}