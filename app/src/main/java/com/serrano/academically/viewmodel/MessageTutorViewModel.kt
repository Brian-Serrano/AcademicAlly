package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.Message
import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageTutorViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
): ViewModel() {

    private var courses = emptyList<String>()
    private var modules = emptyList<List<String>>()

    private val _coursesDropdown = MutableStateFlow(DropDownState(emptyList(), "", false))
    val coursesDropdown: StateFlow<DropDownState> = _coursesDropdown.asStateFlow()

    private val _modulesDropdown = MutableStateFlow(DropDownState(emptyList(), "", false))
    val modulesDropdown: StateFlow<DropDownState> = _modulesDropdown.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _tutorName = MutableStateFlow("")
    val tutorName: StateFlow<String> = _tutorName.asStateFlow()

    fun getData(userId: Int, tutorId: Int, context: Context) {
        viewModelScope.launch {
            try {
                courses = GetCourses.getAllCourses(context).map { it[1] }
                modules = GetModules.getAllModules(context)
                _drawerData.value = userRepository.getUserDataForDrawer(userId).first()
                _tutorName.value = userRepository.getUserName(tutorId).first()
                val courseNames = courseSkillRepository.getCourseSkillsOfUserNoRating(tutorId, "TUTOR").first().map { courses[it - 1] }
                val selectedCourse = courseNames[0]
                _coursesDropdown.value = DropDownState(courseNames, selectedCourse, false)
                val moduleNames = modules[0].drop(1)
                val selectedModule = moduleNames[0]
                _modulesDropdown.value = DropDownState(moduleNames, selectedModule, false)
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }

    fun updateCoursesDropdown(newDropdown: DropDownState) {
        _coursesDropdown.value = newDropdown
        updateModulesDropdownList(newDropdown.selected)
    }

    fun updateModulesDropdown(newDropdown: DropDownState) {
        _modulesDropdown.value = newDropdown
    }

    private fun updateModulesDropdownList(selected: String) {
        val idx = courses.indexOf(selected)
        val moduleNames = modules[idx].drop(1)
        val selectedModule = moduleNames[0]
        updateModulesDropdown(DropDownState(moduleNames, selectedModule, false))
    }

    fun sendRequest(course: DropDownState, module: DropDownState, studentId: Int, tutorId: Int, message: String, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                messageRepository.addMessage(
                    Message(
                        courseId = courses.indexOf(course.selected) + 1,
                        moduleId = module.dropDownItems.indexOf(module.selected) + 1,
                        studentId = studentId,
                        tutorId = tutorId,
                        studentMessage = message
                    )
                )
                navigate()
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}