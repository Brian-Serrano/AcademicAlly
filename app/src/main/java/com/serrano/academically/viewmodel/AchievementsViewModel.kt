package com.serrano.academically.viewmodel

import android.content.Context
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetAchievements
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfoAndCredentials
import com.serrano.academically.utils.emptyUserDrawerData
import com.serrano.academically.utils.emptyUserInfoAndCredentials
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _userData = MutableStateFlow(emptyUserDrawerData())
    val userData: StateFlow<UserDrawerData> = _userData.asStateFlow()

    private val _achievements = MutableStateFlow<List<List<String>>>(emptyList())
    val achievements: StateFlow<List<List<String>>> = _achievements.asStateFlow()

    private val _achievementsProgress = MutableStateFlow<List<Double>>(emptyList())
    val achievementsProgress: StateFlow<List<Double>> = _achievementsProgress.asStateFlow()

    fun getData(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                // Fetch drawer data
                _userData.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch achievements and progress base on user role
                when (userData.value.role) {
                    "STUDENT" -> {
                        _achievements.value = GetAchievements.getAchievements(1, context)
                        _achievementsProgress.value = userRepository.getBadgeProgressAsStudent(id).first().achievement
                    }
                    "TUTOR" -> {
                        _achievements.value = GetAchievements.getAchievements(0, context)
                        _achievementsProgress.value = userRepository.getBadgeProgressAsTutor(id).first().achievement
                    }
                }
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

}