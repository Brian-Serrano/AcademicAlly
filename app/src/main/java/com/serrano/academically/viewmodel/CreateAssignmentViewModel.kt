package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.AssignmentRepository
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.AssessmentType
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.IdentificationFields
import com.serrano.academically.utils.MultipleChoiceFields
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.TrueOrFalseFields
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.completeSessions
import com.serrano.academically.utils.emptySession
import com.serrano.academically.utils.emptyUserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAssignmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val assignmentRepository: AssignmentRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _sessionInfo = MutableStateFlow(emptySession())
    val sessionInfo: StateFlow<Session> = _sessionInfo.asStateFlow()

    private val _quizFields = MutableStateFlow<List<AssessmentType>>(emptyList())
    val quizFields: StateFlow<List<AssessmentType>> = _quizFields.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen: StateFlow<Boolean> = _isFilterDialogOpen.asStateFlow()

    fun getData(id: Int, sessionId: Int, items: Int, type: String) {
        viewModelScope.launch {
            try {
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()

                _sessionInfo.value = sessionRepository.getSession(sessionId).first()

                _quizFields.value = when (type) {
                    "Multiple Choice" -> List(items) { MultipleChoiceFields(it, "", listOf("", "", "", ""), DropDownState(
                        listOf("A", "B", "C", "D"), "A", false
                    )) }
                    "Identification" -> List(items) { IdentificationFields(it, "", "") }
                    "True or False" -> List(items) { TrueOrFalseFields(it, "", DropDownState(listOf("TRUE", "FALSE"), "TRUE", false)) }
                    else -> throw IllegalArgumentException()
                }

                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun updateFields(newFields: List<AssessmentType>) {
        _quizFields.value = newFields
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }

    fun completeSessionAndSaveAssignment(
        studentId: Int,
        tutorId: Int,
        sessionId: Int,
        navigate: () -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                completeSessions(
                    sessionRepository = sessionRepository,
                    userRepository = userRepository,
                    studentId = studentId,
                    tutorId = tutorId,
                    sessionId = sessionId,
                    navigate = navigate,
                    context = context
                )

                // TODO save assessment data to excel, update statistics data, update achievement progress and save assignment
            }
            catch (e: Exception) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }
    }
}