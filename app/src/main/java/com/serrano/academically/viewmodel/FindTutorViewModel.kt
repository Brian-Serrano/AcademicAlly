package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.FindTutor
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.FilterDialogStates
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.SearchInfo
import com.serrano.academically.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindTutorViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _findTutorData = MutableStateFlow(FindTutor())
    val findTutorData: StateFlow<FindTutor> = _findTutorData.asStateFlow()

    private val _filterState = MutableStateFlow<List<FilterDialogStates>>(emptyList())
    val filterState: StateFlow<List<FilterDialogStates>> = _filterState.asStateFlow()

    private val _isFilterDialogOpen = MutableStateFlow(false)
    val isFilterDialogOpen: StateFlow<Boolean> = _isFilterDialogOpen.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun updateSearch(newSearch: SearchInfo) {
        _searchInfo.value = newSearch
    }

    fun updateFilterState(newFilterState: List<FilterDialogStates>) {
        _filterState.value = newFilterState
    }

    private suspend fun updateHistory() {
        updateSearch(_searchInfo.value.copy(history = userCacheRepository.userDataStore.data.first().searchTutorHistory))
    }

    fun getData(context: Context) {
        viewModelScope.launch {
            try {
                val findTutorCache = ActivityCacheManager.findTutor
                val currentUserCache = ActivityCacheManager.currentUser

                if (findTutorCache != null && currentUserCache != null) {
                    _findTutorData.value = findTutorCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(context)
                }

                _filterState.value = _findTutorData.value.courses.map { course ->
                    FilterDialogStates(
                        course.id,
                        course.name,
                        _findTutorData.value.studentCourseIds.any { course.id == it }
                    )
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(context)

                _filterState.value = _findTutorData.value.courses.map { course ->
                    FilterDialogStates(
                        course.id,
                        course.name,
                        _findTutorData.value.studentCourseIds.any { course.id == it }
                    )
                }

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
            when (val findTutorData = academicallyApi.getTutors()) {
                is WithCurrentUser.Success -> {
                    _findTutorData.value = findTutorData.data!!
                    _drawerData.value = findTutorData.currentUser!!

                    ActivityCacheManager.findTutor = findTutorData.data
                    ActivityCacheManager.currentUser = findTutorData.currentUser
                }
                is WithCurrentUser.Error -> throw IllegalArgumentException(findTutorData.error)
            }
        }
    }

    fun updateMenu(filterDialogStates: List<FilterDialogStates>, searchQuery: String, context: Context) {
        viewModelScope.launch {
            try {
                _processState.value = ProcessState.Loading

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi) {
                    if (searchQuery.isNotEmpty()) {
                        userCacheRepository.addSearchTutorHistory(searchQuery)
                        updateHistory()
                    }

                    val apiResponse = academicallyApi.searchTutor(
                        filterDialogStates.filter { it.isEnabled }.map { it.id }.joinToString(separator = ","),
                        searchQuery
                    )

                    _findTutorData.value = _findTutorData.value.copy(
                        tutors = when (apiResponse) {
                            is NoCurrentUser.Success -> apiResponse.data!!
                            is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                        }
                    )

                    ActivityCacheManager.findTutor = _findTutorData.value
                }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }
}