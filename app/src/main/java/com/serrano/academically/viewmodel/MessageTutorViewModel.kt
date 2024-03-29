package com.serrano.academically.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.MessageResponse
import com.serrano.academically.api.TutorCourses
import com.serrano.academically.api.TutorRequestBody
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageTutorViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _coursesDropdown = MutableStateFlow(DropDownState(emptyList(), "", false))
    val coursesDropdown: StateFlow<DropDownState> = _coursesDropdown.asStateFlow()

    private val _modulesDropdown = MutableStateFlow(DropDownState(emptyList(), "", false))
    val modulesDropdown: StateFlow<DropDownState> = _modulesDropdown.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _tutorCourses = MutableStateFlow(TutorCourses())
    val tutorCourses: StateFlow<TutorCourses> = _tutorCourses.asStateFlow()

    private val _requestButtonEnabled = MutableStateFlow(true)
    val requestButtonEnabled: StateFlow<Boolean> = _requestButtonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun getData(tutorId: Int) {
        viewModelScope.launch {
            try {
                val tutorCoursesCache = ActivityCacheManager.messageTutor[tutorId]
                val currentUserCache = ActivityCacheManager.currentUser

                if (tutorCoursesCache != null && currentUserCache != null) {
                    _tutorCourses.value = tutorCoursesCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi(tutorId)
                }

                refreshDropdowns()

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

                refreshDropdowns()

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshDropdowns() {
        val courses = _tutorCourses.value.tutorCourses.map { it.courseName }
        _coursesDropdown.value = DropDownState(courses, courses[0], false)

        val modules = _tutorCourses.value.tutorCourses.map { it.modules }[0]
        _modulesDropdown.value = DropDownState(modules, modules[0], false)
    }

    private suspend fun callApi(tutorId: Int) {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        when (val tutorCourses = academicallyApi.getTutorEligibleCourses(tutorId)) {
            is WithCurrentUser.Success -> {
                _tutorCourses.value = tutorCourses.data!!
                mutableDrawerData.value = tutorCourses.currentUser!!

                ActivityCacheManager.messageTutor[tutorId] = tutorCourses.data
                ActivityCacheManager.currentUser = tutorCourses.currentUser
            }
            is WithCurrentUser.Error -> throw IllegalArgumentException(tutorCourses.error)
        }
    }

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun updateCoursesDropdown(newDropdown: DropDownState) {
        _coursesDropdown.value = newDropdown
        updateModulesDropdownList(_tutorCourses.value.tutorCourses.indexOfFirst { it.courseName == newDropdown.selected })
    }

    fun updateModulesDropdown(newDropdown: DropDownState) {
        _modulesDropdown.value = newDropdown
    }

    private fun updateModulesDropdownList(idx: Int) {
        val modules = _tutorCourses.value.tutorCourses.map { it.modules }[idx]
        updateModulesDropdown(DropDownState(modules, modules[0], false))
    }

    fun sendRequest(
        course: DropDownState,
        module: DropDownState,
        studentId: Int,
        tutorId: Int,
        message: String,
        navigate: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _requestButtonEnabled.value = false

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

                val courseObj = _tutorCourses.value.tutorCourses.first { course.selected == it.courseName }

                when (val messageResponse = academicallyApi.sendTutorRequest(TutorRequestBody(studentId, tutorId, courseObj.courseId, courseObj.modules.indexOf(module.selected), message))) {
                    is MessageResponse.AchievementResponse -> {
                        Utils.showToast(messageResponse.achievements, getApplication())
                        ActivityCacheManager.messageTutor.remove(tutorId)
                        ActivityCacheManager.notificationsMessages = null
                        _requestButtonEnabled.value = true
                        navigate("Message Sent!")
                    }
                    is MessageResponse.DuplicateMessageResponse -> {
                        ActivityCacheManager.messageTutor.remove(tutorId)
                        ActivityCacheManager.notificationsMessages = null
                        _requestButtonEnabled.value = true
                        navigate(messageResponse.message)
                    }
                    is MessageResponse.ErrorResponse -> {
                        throw IllegalArgumentException(messageResponse.error)
                    }
                }
            } catch (e: Exception) {
                _requestButtonEnabled.value = true
                Toast.makeText(getApplication(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }
}