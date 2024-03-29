package com.serrano.academically.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.CourseRating
import com.serrano.academically.api.DrawerData
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
class CoursesMenuViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _courseSkills = MutableStateFlow<List<CourseRating>>(emptyList())
    val courseSkills: StateFlow<List<CourseRating>> = _courseSkills.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData() {
        viewModelScope.launch {
            try {
                val courseSkillsCache = ActivityCacheManager.coursesMenu
                val currentUserCache = ActivityCacheManager.currentUser

                if (courseSkillsCache != null && currentUserCache != null) {
                    _courseSkills.value = courseSkillsCache
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

        val response = when (val courseSkills = academicallyApi.getCourseEligibility()) {
            is WithCurrentUser.Success -> courseSkills
            is WithCurrentUser.Error -> throw IllegalArgumentException(courseSkills.error)
        }

        _courseSkills.value = response.data!!
        mutableDrawerData.value = response.currentUser!!

        ActivityCacheManager.coursesMenu = response.data
        ActivityCacheManager.currentUser = response.currentUser
    }
}