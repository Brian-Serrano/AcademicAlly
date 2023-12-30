package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.room.Assignment
import com.serrano.academically.room.AssignmentRepository
import com.serrano.academically.room.MessageRepository
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.GetModules
import com.serrano.academically.utils.HelperFunctions
import com.serrano.academically.utils.MessageNotifications
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.RateDialogStates
import com.serrano.academically.utils.SearchInfo
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
class ArchiveViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val sessionRepository: SessionRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _rejectedMessages =
        MutableStateFlow<List<Triple<MessageNotifications, String, String>>>(emptyList())
    val rejectedMessages: StateFlow<List<Triple<MessageNotifications, String, String>>> =
        _rejectedMessages.asStateFlow()

    private val _acceptedMessages =
        MutableStateFlow<List<Triple<MessageNotifications, String, String>>>(emptyList())
    val acceptedMessages: StateFlow<List<Triple<MessageNotifications, String, String>>> =
        _acceptedMessages.asStateFlow()

    private val _cancelledSessions =
        MutableStateFlow<List<Triple<Session, String, String>>>(emptyList())
    val cancelledSessions: StateFlow<List<Triple<Session, String, String>>> =
        _cancelledSessions.asStateFlow()

    private val _completedSessions =
        MutableStateFlow<List<Triple<Session, String, String>>>(emptyList())
    val completedSessions: StateFlow<List<Triple<Session, String, String>>> =
        _completedSessions.asStateFlow()

    private val _deadlinedTasks =
        MutableStateFlow<List<Triple<Assignment, String, String>>>(emptyList())
    val deadlinedTasks: StateFlow<List<Triple<Assignment, String, String>>> =
        _deadlinedTasks.asStateFlow()

    private val _completedTasks =
        MutableStateFlow<List<Triple<Assignment, String, String>>>(emptyList())
    val completedTasks: StateFlow<List<Triple<Assignment, String, String>>> =
        _completedTasks.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    private val _navBarIndex = MutableStateFlow(0)
    val navBarIndex: StateFlow<Int> = _navBarIndex.asStateFlow()

    private val _rating = MutableStateFlow(RateDialogStates())
    val rating: StateFlow<RateDialogStates> = _rating.asStateFlow()

    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen.asStateFlow()

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    fun updateSearch(newSearch: SearchInfo) {
        _searchInfo.value = newSearch
    }

    private suspend fun updateHistory(context: Context) {
        updateSearch(_searchInfo.value.copy(history = context.dataStore.data.first().searchArchiveHistory))
    }

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()
                val role = _drawerData.value.role

                // Fetch search history from preferences
                updateHistory(context)

                // Fetch all messages, sessions and assignments that is cancelled, complete, reject or accepted
                _rejectedMessages.value = mapMessage(role, context, getMessage(role, id, "REJECT"))
                _acceptedMessages.value = mapMessage(role, context, getMessage(role, id, "ACCEPT"))
                _cancelledSessions.value =
                    mapSession(role, context, getSession(role, id, "CANCELLED"))
                _completedSessions.value =
                    mapSession(role, context, getSession(role, id, "COMPLETED"))
                _deadlinedTasks.value = mapAssignment(context, getAssignment(role, id, "DEADLINED"))
                _completedTasks.value = mapAssignment(context, getAssignment(role, id, "COMPLETED"))

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun search(searchQuery: String, context: Context, id: Int, role: String) {
        viewModelScope.launch {
            try {
                _processState.value = ProcessState.Loading

                // Fetch the archive data filtered base on search query
                when (_tabIndex.value) {
                    0 -> when (_navBarIndex.value) {
                        0 -> _acceptedMessages.value = filterMessage(
                            context,
                            searchQuery,
                            mapMessage(role, context, getMessage(role, id, "ACCEPT"))
                        )

                        1 -> _rejectedMessages.value = filterMessage(
                            context,
                            searchQuery,
                            mapMessage(role, context, getMessage(role, id, "REJECT"))
                        )
                    }

                    1 -> when (_navBarIndex.value) {
                        0 -> _completedSessions.value = filterSession(
                            searchQuery,
                            context,
                            mapSession(role, context, getSession(role, id, "COMPLETED"))
                        )

                        1 -> _cancelledSessions.value = filterSession(
                            searchQuery,
                            context,
                            mapSession(role, context, getSession(role, id, "CANCELLED"))
                        )
                    }

                    2 -> when (_navBarIndex.value) {
                        0 -> _completedTasks.value = filterAssignment(
                            context,
                            searchQuery,
                            mapAssignment(context, getAssignment(role, id, "COMPLETED"))
                        )

                        1 -> _deadlinedTasks.value = filterAssignment(
                            context,
                            searchQuery,
                            mapAssignment(context, getAssignment(role, id, "DEADLINED"))
                        )
                    }
                }

                // Fetch search history from preferences
                updateHistory(context)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    private fun matchDate(regex: Regex, date: LocalDateTime): Boolean {
        return regex.containsMatchIn(date.month.toString()) ||
                regex.containsMatchIn(date.dayOfMonth.toString()) ||
                regex.containsMatchIn(date.year.toString())
    }

    private fun matchModule(regex: Regex, courseId: Int, moduleId: Int, context: Context): Boolean {
        return regex.containsMatchIn(
            GetModules.getModuleByCourseAndModuleId(
                courseId,
                moduleId,
                context
            )
        )
    }

    private suspend fun filterMessage(
        context: Context,
        searchQuery: String,
        messages: List<Triple<MessageNotifications, String, String>>
    ): List<Triple<MessageNotifications, String, String>> {
        return if (searchQuery.isEmpty()) {
            messages
        } else {
            val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
            UpdateUserPref.addSearchArchiveHistory(context, searchQuery)
            messages.filter { regex.containsMatchIn(it.second) || regex.containsMatchIn(it.third) }
        }
    }

    private suspend fun mapMessage(
        role: String,
        context: Context,
        messages: List<MessageNotifications>
    ): List<Triple<MessageNotifications, String, String>> {
        return messages.map {
            Triple(
                it,
                userRepository.getUserName(if (role == "STUDENT") it.tutorId else it.studentId)
                    .first(),
                GetCourses.getCourseNameById(it.courseId, context)
            )
        }.reversed()
    }

    private suspend fun getMessage(
        role: String,
        id: Int,
        status: String
    ): List<MessageNotifications> {
        return if (role == "STUDENT") {
            messageRepository.getStudentMessages(status, id).first()
        } else {
            messageRepository.getTutorMessages(status, id).first()
        }
    }

    private suspend fun filterSession(
        searchQuery: String,
        context: Context,
        sessions: List<Triple<Session, String, String>>
    ): List<Triple<Session, String, String>> {
        return if (searchQuery.isEmpty()) {
            sessions
        } else {
            val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
            UpdateUserPref.addSearchArchiveHistory(context, searchQuery)
            sessions.filter {
                regex.containsMatchIn(it.second) ||
                        regex.containsMatchIn(it.third) ||
                        regex.containsMatchIn(it.first.location) ||
                        matchDate(regex, it.first.startTime) ||
                        matchDate(regex, it.first.endTime) ||
                        matchModule(regex, it.first.courseId, it.first.moduleId, context)
            }
        }
    }

    private suspend fun mapSession(
        role: String,
        context: Context,
        sessions: List<Session>
    ): List<Triple<Session, String, String>> {
        return sessions.map {
            Triple(
                it,
                userRepository.getUserName(if (role == "STUDENT") it.tutorId else it.studentId)
                    .first(),
                GetCourses.getCourseNameById(it.courseId, context)
            )
        }.reversed()
    }

    private suspend fun getSession(
        role: String,
        id: Int,
        status: String
    ): List<Session> {
        return if (role == "STUDENT") {
            sessionRepository.getStudentArchiveSessions(status, id).first()
        } else {
            sessionRepository.getTutorArchiveSessions(status, id).first()
        }
    }

    private suspend fun filterAssignment(
        context: Context,
        searchQuery: String,
        assignments: List<Triple<Assignment, String, String>>
    ): List<Triple<Assignment, String, String>> {
        return if (searchQuery.isEmpty()) {
            assignments
        } else {
            val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
            UpdateUserPref.addSearchArchiveHistory(context, searchQuery)
            assignments.filter {
                regex.containsMatchIn(it.second) ||
                        regex.containsMatchIn(it.third) ||
                        regex.containsMatchIn(it.first.type) ||
                        matchDate(regex, it.first.deadLine)
            }
        }
    }

    private fun mapAssignment(
        context: Context,
        assignments: List<Assignment>
    ): List<Triple<Assignment, String, String>> {
        return assignments.map {
            Triple(
                it,
                GetCourses.getCourseNameById(it.courseId, context),
                GetModules.getModuleByCourseAndModuleId(it.courseId, it.moduleId, context)
            )
        }.reversed()
    }

    private suspend fun getAssignment(
        role: String,
        id: Int,
        status: String
    ): List<Assignment> {
        return if (role == "STUDENT") {
            assignmentRepository.getStudentAssignments(status, id).first()
        } else {
            assignmentRepository.getTutorAssignments(role, id).first()
        }
    }

    fun rateUser(rating: RateDialogStates, id: Int, role: String, context: Context) {
        viewModelScope.launch {
            try {
                when (role) {
                    "STUDENT" -> {
                        // Update session student rate
                        sessionRepository.updateStudentRate(rating.sessionId)

                        // Update tutorsRated of student and numberOfRatesAsTutor and totalRatingAsTutor of tutor
                        userRepository.updateStudentRates(id)
                        userRepository.updateTutorRating(rating.star / 5.0, rating.userId)

                        // Update student rates achievement
                        val achievementProgressStudent =
                            userRepository.getBadgeProgressAsStudent(id).first().achievement
                        val computedProgressStudent = HelperFunctions.computeAchievementProgress(
                            userRepository.getStudentRates(id).first().toDouble(),
                            listOf(1, 5, 10),
                            listOf(22, 23, 24),
                            achievementProgressStudent
                        )
                        userRepository.updateStudentBadgeProgress(computedProgressStudent, id)

                        // Update tutor rated achievement
                        val achievementProgressTutor =
                            userRepository.getBadgeProgressAsTutor(rating.userId)
                                .first().achievement
                        val computedProgressTutor = HelperFunctions.computeAchievementProgress(
                            userRepository.getTutorRatingNumber(rating.userId).first().toDouble(),
                            listOf(1, 5, 10),
                            listOf(25, 26, 27),
                            achievementProgressTutor
                        )
                        userRepository.updateTutorBadgeProgress(
                            computedProgressTutor,
                            rating.userId
                        )

                        // Show toast message if an achievement is completed
                        HelperFunctions.checkCompletedAchievements(
                            achievementProgressStudent, computedProgressStudent
                        ) {
                            Toast.makeText(
                                context,
                                GetAchievements.getAchievements(0, context)[it][0],
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    "TUTOR" -> {
                        // Update session student rate
                        sessionRepository.updateTutorRate(rating.sessionId)

                        // Update studentsRated of tutor and numberOfRatesAsStudent and totalRatingAsStudent of student
                        userRepository.updateTutorRates(id)
                        userRepository.updateStudentRating(rating.star / 5.0, rating.userId)

                        // Update tutor rates achievement
                        val achievementProgressTutor =
                            userRepository.getBadgeProgressAsTutor(id).first().achievement
                        val computedProgressTutor = HelperFunctions.computeAchievementProgress(
                            userRepository.getTutorRates(id).first().toDouble(),
                            listOf(1, 5, 10),
                            listOf(22, 23, 24),
                            achievementProgressTutor
                        )
                        userRepository.updateTutorBadgeProgress(computedProgressTutor, id)

                        // Update student rated achievement
                        val achievementProgressStudent =
                            userRepository.getBadgeProgressAsStudent(rating.userId)
                                .first().achievement
                        val computedProgressStudent = HelperFunctions.computeAchievementProgress(
                            userRepository.getStudentRatingNumber(rating.userId).first().toDouble(),
                            listOf(1, 5, 10),
                            listOf(25, 26, 27),
                            achievementProgressStudent
                        )
                        userRepository.updateStudentBadgeProgress(
                            computedProgressStudent,
                            rating.userId
                        )

                        // Show toast message if an achievement is completed
                        HelperFunctions.checkCompletedAchievements(
                            achievementProgressTutor, computedProgressTutor
                        ) {
                            Toast.makeText(
                                context,
                                GetAchievements.getAchievements(0, context)[it][0],
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                Toast.makeText(context, "${rating.name} Rated Successfully!", Toast.LENGTH_LONG)
                    .show()

                // Refresh archived sessions
                refreshSessionAfterRate(role, context, id)
            } catch (e: Exception) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun refreshSessionAfterRate(role: String, context: Context, id: Int) {
        try {
            _processState.value = ProcessState.Loading

            // Refresh archived sessions
            _completedSessions.value = mapSession(role, context, getSession(role, id, "COMPLETED"))

            _processState.value = ProcessState.Success
        } catch (e: Exception) {
            _processState.value = ProcessState.Error
        }
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }

    fun updateNavBarIndex(newIdx: Int) {
        _navBarIndex.value = newIdx
    }

    fun updateRatingDialog(newRatingDialogStates: RateDialogStates) {
        _rating.value = newRatingDialogStates
    }

    fun toggleDialog(bool: Boolean) {
        _isDialogOpen.value = bool
    }
}