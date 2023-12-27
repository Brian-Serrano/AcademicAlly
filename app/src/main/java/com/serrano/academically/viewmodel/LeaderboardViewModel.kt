package com.serrano.academically.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.LeaderboardData
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
class LeaderboardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userDrawer = MutableStateFlow(UserDrawerData())
    val userDrawer: StateFlow<UserDrawerData> = _userDrawer.asStateFlow()

    private val _leaderboardsData = MutableStateFlow<List<LeaderboardData>>(emptyList())
    val leaderboardsData: StateFlow<List<LeaderboardData>> = _leaderboardsData.asStateFlow()

    fun getData(id: Int) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userDrawer.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch the users that have top scores base on user role
                _leaderboardsData.value = when (_userDrawer.value.role) {
                    "STUDENT" -> userRepository.getStudentLeaderboard().first()
                    else -> userRepository.getTutorLeaderboard().first()
                }
                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}