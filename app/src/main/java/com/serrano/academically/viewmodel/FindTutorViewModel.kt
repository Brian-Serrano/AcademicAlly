package com.serrano.academically.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.datastore.UpdateUserPref
import com.serrano.academically.datastore.dataStore
import com.serrano.academically.room.CourseSkillRepository
import com.serrano.academically.room.UserRepository
import com.serrano.academically.utils.FilterDialogStates
import com.serrano.academically.utils.FindTutorData
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
class FindTutorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
) : ViewModel() {

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(UserDrawerData())
    val drawerData: StateFlow<UserDrawerData> = _drawerData.asStateFlow()

    private val _findTutorData = MutableStateFlow<List<FindTutorData>>(emptyList())
    val findTutorData: StateFlow<List<FindTutorData>> = _findTutorData.asStateFlow()

    private val _filterState = MutableStateFlow<List<FilterDialogStates>>(emptyList())
    val filterState: StateFlow<List<FilterDialogStates>> = _filterState.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen: StateFlow<Boolean> = _isFilterDialogOpen.asStateFlow()

    fun updateSearch(newSearch: SearchInfo) {
        _searchInfo.value = newSearch
    }

    fun updateFilterState(newFilterState: List<FilterDialogStates>) {
        _filterState.value = newFilterState
    }

    private suspend fun updateHistory(context: Context) {
        updateSearch(_searchInfo.value.copy(history = context.dataStore.data.first().searchTutorHistory))
    }

    fun getData(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                // Fetch user drawer data
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()

                // Fetch user/student courses id
                val userCourses = courseSkillRepository
                    .getCourseSkillsOfUserNoRating(id, _drawerData.value.role)
                    .first()

                // Fetch search history from preferences
                updateHistory(context)

                // Enable checkboxes for user/student courses in filter dialog
                _filterState.value = GetCourses
                    .getAllCourses(context)
                    .map { course ->
                        FilterDialogStates(
                            course[0].toInt(),
                            course[1],
                            userCourses.any { it == course[0].toInt() }
                        )
                    }

                // Filter tutors base on filter options and search query
                updateTutorMenu(_filterState.value, _searchInfo.value.searchQuery, context, id)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateMenu(
        filterDialogStates: List<FilterDialogStates>,
        searchQuery: String,
        context: Context,
        id: Int
    ) {
        viewModelScope.launch {
            try {
                _processState.value = ProcessState.Loading

                // Filter tutors base on filter options and search query
                updateTutorMenu(filterDialogStates, searchQuery, context, id)

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    private suspend fun updateTutorMenu(
        filterDialogStates: List<FilterDialogStates>,
        searchQuery: String,
        context: Context,
        id: Int
    ) {
        // Filter tutors base on filter options and search query
        _findTutorData.value = search(
            data = filterDialogStates
                .filter { it.isEnabled }
                .map { it.id }
                .flatMap { courseSkillRepository.getCourseSkillsForTutorByCourse(it).first() }
                .groupBy { it.userId }
                .map { course ->
                    FindTutorData(
                        tutorId = course.key,
                        tutorName = userRepository.getUserName(course.key).first(),
                        courses = course.value.map {
                            GetCourses.getCourseNameById(
                                it.courseId,
                                context
                            )
                        },
                        rating = course.value.map { (it.assessmentRating / it.assessmentTaken) * 5 },
                        performance = userRepository.getTutorRating(course.key).first()
                    )
                }
                .filter { id != it.tutorId },
            searchQuery = searchQuery,
            context = context
        )
    }

    private suspend fun search(
        data: List<FindTutorData>,
        searchQuery: String,
        context: Context
    ): List<FindTutorData> {
        // Filter the tutors base on search query and add the search query in the preferences and re-fetch
        return if (searchQuery.isEmpty()) {
            data
        } else {
            UpdateUserPref.addSearchTutorHistory(context, searchQuery)
            updateHistory(context)
            val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
            data.filter { regex.containsMatchIn(it.tutorName) }
        }
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }
}