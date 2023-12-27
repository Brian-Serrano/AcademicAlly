package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.GetCourses
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SearchInfo
import com.serrano.academically.utils.UserDrawerData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseAssessmentViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _courses = MutableStateFlow<List<List<String>>>(emptyList())
    val courses: StateFlow<List<List<String>>> = _courses.asStateFlow()

    private val _isDrawerShouldAvailable = MutableStateFlow(false)
    val isDrawerShouldAvailable: StateFlow<Boolean> = _isDrawerShouldAvailable.asStateFlow()

    fun updateSearch(newSearch: SearchInfo) {
        _searchInfo.value = newSearch
    }

    private suspend fun updateHistory(context: Context) {
        updateSearch(_searchInfo.value.copy(history = context.dataStore.data.first().searchCourseHistory))
    }

    fun getData(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                // Fetch all courses
                _courses.value = GetCourses.getAllCourses(context)

                // Fetch search history from preferences
                updateHistory(context)

                // Fetch and enable drawer data base on login state of user
                if (id != 0) {
                    _drawerData.value = userRepository.getUserDataForDrawer(id).first()
                    _isDrawerShouldAvailable.value = true
                }
                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun search(searchQuery: String, context: Context) {
        viewModelScope.launch {
            try {
                _processState.value = ProcessState.Loading

                // Filter courses base on search query and add the search query to preferences and re-fetch
                if (searchQuery.isEmpty()) {
                    _courses.value = GetCourses.getAllCourses(context)
                } else {
                    val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
                    UpdateUserPref.addSearchCourseHistory(context, searchQuery)
                    _courses.value =
                        GetCourses.getAllCourses(context).filter { regex.containsMatchIn(it[1]) }
                }

                // Fetch search history from preferences
                updateHistory(context)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }
}