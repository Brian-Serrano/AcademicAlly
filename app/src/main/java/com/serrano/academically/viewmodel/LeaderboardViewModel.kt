package com.serrano.academically.viewmodel

import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.LeaderboardData
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.emptyUser
import com.serrano.academically.utils.emptyUserDrawerData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userDrawer = MutableStateFlow(emptyUserDrawerData())
    val userDrawer: StateFlow<UserDrawerData> = _userDrawer.asStateFlow()

    private val _leaderboardsData = MutableStateFlow<List<List<LeaderboardData>>>(emptyList())
    val leaderboardsData: StateFlow<List<List<LeaderboardData>>> = _leaderboardsData.asStateFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex.asStateFlow()

    fun getData(id: Int) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userDrawer.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch the users that have top scores base on user role
                _leaderboardsData.value = when (userDrawer.value.role) {
                    "STUDENT" -> {
                        listOf(
                            userRepository.getLeaderboardStudentPoints().first(),
                            userRepository.getLeaderboardStudentAssessmentPoints().first(),
                            userRepository.getLeaderboardStudentRequestPoints().first(),
                            userRepository.getLeaderboardStudentSessionPoints().first()
                        )
                    }
                    "TUTOR" -> {
                        listOf(
                            userRepository.getLeaderboardTutorPoints().first(),
                            userRepository.getLeaderboardTutorAssessmentPoints().first(),
                            userRepository.getLeaderboardTutorRequestPoints().first(),
                            userRepository.getLeaderboardTutorSessionPoints().first()
                        )
                    }
                    else -> emptyList()
                }
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateTabIndex(newIdx: Int) {
        _tabIndex.value = newIdx
    }
}