package com.serrano.academically.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.Session
import com.serrano.academically.room.SessionRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.DeadlineField
import com.serrano.academically.utils.DropDownState
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptySession
import com.serrano.academically.utils.emptyUserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AssignmentOptionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _itemsDropdown = MutableStateFlow(DropDownState(listOf("5", "10", "15"), "5", false))
    val itemsDropdown: StateFlow<DropDownState> = _itemsDropdown.asStateFlow()

    private val _typeDropdown = MutableStateFlow(DropDownState(listOf("Multiple Choice", "Identification", "True or False"), "Multiple Choice", false))
    val typeDropdown: StateFlow<DropDownState> = _typeDropdown.asStateFlow()

    private val _deadlineField = MutableStateFlow(DeadlineField("", "", ""))
    val deadlineField: StateFlow<DeadlineField> = _deadlineField.asStateFlow()

    private val _sessionInfo = MutableStateFlow(emptySession())
    val sessionInfo: StateFlow<Session> = _sessionInfo.asStateFlow()

    fun getData(id: Int, sessionId: Int) {
        viewModelScope.launch {
            try {
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()

                _sessionInfo.value = sessionRepository.getSession(sessionId).first()

                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateItemsDropdown(newDropDownState: DropDownState) {
        _itemsDropdown.value = newDropDownState
    }

    fun updateTypeDropdown(newDropDownState: DropDownState) {
        _typeDropdown.value = newDropDownState
    }

    fun updateDeadline(newDeadlineField: DeadlineField) {
        _deadlineField.value = newDeadlineField
    }

    fun validateDeadlineFormatAndNavigate(deadlineField: DeadlineField, navigate: (String) -> Unit) {
        try {
            val dateString = LocalDateTime.parse("${deadlineField.date} ${deadlineField.time}", DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")).toString()
            navigate(dateString)
        }
        catch (e: Exception) {
            updateDeadline(deadlineField.copy(error = "Invalid time"))
        }
    }
}