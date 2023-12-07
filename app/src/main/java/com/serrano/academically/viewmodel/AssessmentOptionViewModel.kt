package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentOptionViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _course = MutableStateFlow(Pair("", ""))
    val course: StateFlow<Pair<String, String>> = _course.asStateFlow()

    private val _isDrawerShouldAvailable = MutableStateFlow(false)
    val isDrawerShouldAvailable: StateFlow<Boolean> = _isDrawerShouldAvailable.asStateFlow()

    private val _itemsDropdown = MutableStateFlow(DropDownState(listOf("5", "10", "15"), "5", false))
    val itemsDropdown: StateFlow<DropDownState> = _itemsDropdown.asStateFlow()

    private val _typeDropdown = MutableStateFlow(DropDownState(listOf("Multiple Choice", "Identification", "True or False"), "Multiple Choice", false))
    val typeDropdown: StateFlow<DropDownState> = _typeDropdown.asStateFlow()

    fun updateItemsDropdown(newState: DropDownState) {
        _itemsDropdown.value = newState
    }

    fun updateTypeDropdown(newState: DropDownState) {
        _typeDropdown.value = newState
    }

    fun getData(userId: Int, courseId: Int, context: Context) {
        viewModelScope.launch {
            try {
                _course.value = GetCourses.getCourseAndDescription(courseId, context)
                if (userId != 0) {
                    _drawerData.value = userRepository.getUserDataForDrawer(userId).first()
                    _isDrawerShouldAvailable.value = true
                }
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}