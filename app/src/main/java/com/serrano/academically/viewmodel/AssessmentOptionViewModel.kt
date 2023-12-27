package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentOptionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _course = MutableStateFlow(Pair("", ""))
    val course: StateFlow<Pair<String, String>> = _course.asStateFlow()

    private val _isDrawerShouldAvailable = MutableStateFlow(false)
    val isDrawerShouldAvailable: StateFlow<Boolean> = _isDrawerShouldAvailable.asStateFlow()

    private val _startButtonEnabled = MutableStateFlow(true)
    val startButtonEnabled: StateFlow<Boolean> = _startButtonEnabled.asStateFlow()

    fun getData(userId: Int, courseId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch chosen course
                _course.value = GetCourses.getCourseAndDescription(courseId, context)

                // Fetch and enable drawer data base on user login state
                if (userId != 0) {
                    _drawerData.value = userRepository.getUserDataForDrawer(userId).first()
                    _isDrawerShouldAvailable.value = true
                }
                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun saveAssessmentType(context: Context, navigate: (String, String) -> Unit) {
        viewModelScope.launch {
            try {
                // Disable start button temporarily
                _startButtonEnabled.value = false

                // Fetch assessment type preferences
                val data = context.dataStore.data.first()

                // Check if there is preference
                if (data.assessmentItems.isNotEmpty() && data.assessmentType.isNotEmpty()) {
                    // Enable start button again
                    _startButtonEnabled.value = true

                    // If there is, navigate with that data
                    navigate(data.assessmentItems, data.assessmentType)
                } else {
                    // If there no, generate random, save it in preferences and navigate with the random data
                    val items = listOf("5", "10", "15").random()
                    val type = listOf("Multiple Choice", "Identification", "True or False").random()
                    UpdateUserPref.saveAssessmentType(context, type, items)

                    // Enable start button again
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