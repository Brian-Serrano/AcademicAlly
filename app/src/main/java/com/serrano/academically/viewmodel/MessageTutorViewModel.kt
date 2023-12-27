package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.Message
import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MessageTutorViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

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

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _tutorName = MutableStateFlow("")
    val tutorName: StateFlow<String> = _tutorName.asStateFlow()

    private val _requestButtonEnabled = MutableStateFlow(true)
    val requestButtonEnabled: StateFlow<Boolean> = _requestButtonEnabled.asStateFlow()

    fun getData(userId: Int, tutorId: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch all courses and modules
                courses = GetCourses.getAllCourses(context).map { it[1] }
                modules = GetModules.getAllModules(context)

                // Fetch drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(userId).first()

                // Fetch tutor name
                _tutorName.value = userRepository.getUserName(tutorId).first()

                // Fetch tutor courses and place on dropdown
                val courseNames =
                    courseSkillRepository.getCourseSkillsOfUserNoRating(tutorId, "TUTOR").first()
                        .map { courses[it - 1] }
                val selectedCourse = courseNames[0]
                _coursesDropdown.value = DropDownState(courseNames, selectedCourse, false)

                // Fetch the modules base on selected course and place in dropdown
                val moduleNames = modules[0].drop(1)
                val selectedModule = moduleNames[0]
                _modulesDropdown.value = DropDownState(moduleNames, selectedModule, false)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
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

    fun sendRequest(
        course: DropDownState,
        module: DropDownState,
        studentId: Int,
        tutorId: Int,
        message: String,
        navigate: (String) -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                // Disable request button temporarily
                _requestButtonEnabled.value = false

                if (messageRepository.checkIfStudentAlreadyMessageThatTutor(studentId, tutorId)
                        .first().isEmpty()
                ) {
                    // Save message
                    messageRepository.addMessage(
                        Message(
                            courseId = courses.indexOf(course.selected) + 1,
                            moduleId = module.dropDownItems.indexOf(module.selected) + 1,
                            studentId = studentId,
                            tutorId = tutorId,
                            studentMessage = message,
                            expireDate = LocalDateTime.now().plusDays(28)
                        )
                    )

                    // Update student and tutor sent request data and points
                    userRepository.updateStudentRequests(0.1, studentId)
                    userRepository.updateTutorRequests(0.1, tutorId)

                    // Update points and sent request achievement of student
                    val achievementProgressStudent =
                        userRepository.getBadgeProgressAsStudent(studentId).first().achievement
                    val computedProgressStudent = HelperFunctions.computeAchievementProgress(
                        userRepository.getStudentPoints(studentId).first(),
                        listOf(10, 25, 50, 100, 200),
                        listOf(7, 8, 9, 10, 11),
                        HelperFunctions.computeAchievementProgress(
                            userRepository.getStudentSentRequests(studentId).first().toDouble(),
                            listOf(1, 5, 10, 20),
                            listOf(0, 1, 2, 3),
                            achievementProgressStudent
                        )
                    )
                    userRepository.updateStudentBadgeProgress(computedProgressStudent, studentId)

                    // Show toast message if an achievement is completed
                    HelperFunctions.checkCompletedAchievements(
                        achievementProgressStudent, computedProgressStudent
                    ) {
                        Toast.makeText(
                            context,
                            GetAchievements.getAchievements(1, context)[it][0],
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Update points achievement of tutor
                    val achievementProgressTutor =
                        userRepository.getBadgeProgressAsTutor(tutorId).first().achievement
                    val computedProgressTutor = HelperFunctions.computeAchievementProgress(
                        userRepository.getTutorPoints(tutorId).first(),
                        listOf(10, 25, 50, 100, 200),
                        listOf(7, 8, 9, 10, 11),
                        achievementProgressTutor
                    )
                    userRepository.updateTutorBadgeProgress(computedProgressTutor, tutorId)

                    // Enable request button again
                    _requestButtonEnabled.value = true

                    navigate("Message Sent!")
                } else {
                    // Enable request button again
                    _requestButtonEnabled.value = true

                    navigate("Tutor can only be message once.")
                }
            } catch (e: Exception) {
                _requestButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }
}