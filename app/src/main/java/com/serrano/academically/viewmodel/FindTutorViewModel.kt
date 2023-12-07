package com.serrano.academically.viewmodel

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
import com.serrano.academically.utils.emptyUserDrawerData
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindTutorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val courseSkillRepository: CourseSkillRepository
): ViewModel() {

    private val _searchInfo = MutableStateFlow(SearchInfo("", false, emptyList()))
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(emptyUserDrawerData())
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

    fun getData(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                val userCourses = courseSkillRepository
                    .getCourseSkillsOfUserNoRating(id, drawerData.value.role)
                    .first()
                updateSearch(searchInfo.value.copy(history = context.dataStore.data.first().searchTutorHistory))
                _drawerData.value = userRepository.getUserDataForDrawer(id).first()
                _filterState.value = GetCourses
                    .getAllCourses(context)
                    .map { course ->
                        FilterDialogStates(
                            course[0].toInt(),
                            course[1],
                            userCourses.any { it == course[0].toInt() }
                        )
                    }
                updateTutorMenu(filterState.value, searchInfo.value.searchQuery, context)
                _processState.value = ProcessState.Success
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    fun updateTutorMenu(filterDialogStates: List<FilterDialogStates>, searchQuery: String, context: Context) {
        viewModelScope.launch {
            try {
                _findTutorData.value = search(
                    data = filterDialogStates
                        .filter { it.isEnabled }
                        .map { it.id }
                        .flatMap { courseSkillRepository.getCourseSkillsForTutorByCourse(it).first() }
                        .groupBy { it.userId }
                        .map { course -> FindTutorData(course.key, userRepository.getUserName(course.key).first(), course.value.map { GetCourses.getCourseNameById(it.courseId, context) }, course.value.map { (it.courseAssessmentScore.toFloat() / it.courseAssessmentItemsTotal) * 5 }) },
                    searchQuery = searchQuery,
                    context = context
                )
            }
            catch (e: Exception) {
                _processState.value = ProcessState.Error
            }
        }
    }

    private suspend fun search(data: List<FindTutorData>, searchQuery: String, context: Context): List<FindTutorData> {
        return if (searchQuery.isEmpty()) {
            data
        } else {
            UpdateUserPref.addSearchTutorHistory(context, searchQuery)
            val regex = Regex(searchQuery, RegexOption.IGNORE_CASE)
            data.filter { regex.containsMatchIn(it.tutorName) }
        }
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }
}