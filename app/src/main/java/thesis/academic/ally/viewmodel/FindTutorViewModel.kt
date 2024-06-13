package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.FindTutor
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.WithCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.ActivityCacheManager
import thesis.academic.ally.utils.FilterDialogStates
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.SearchInfo
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class FindTutorViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

    private val _searchInfo = MutableStateFlow(SearchInfo())
    val searchInfo: StateFlow<SearchInfo> = _searchInfo.asStateFlow()

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

    fun getData() {
        viewModelScope.launch {
            try {
                val findTutorCache = ActivityCacheManager.findTutor
                val currentUserCache = ActivityCacheManager.currentUser

                if (findTutorCache != null && currentUserCache != null) {
                    _findTutorData.value = findTutorCache
                    mutableDrawerData.value = currentUserCache
                } else {
                    callApi()
                }

                _filterState.value = _findTutorData.value.courses.map { course ->
                    FilterDialogStates(
                        course.id,
                        course.name,
                        _findTutorData.value.studentCourseIds.any { course.id == it }
                    )
                }

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi()

                _filterState.value = _findTutorData.value.courses.map { course ->
                    FilterDialogStates(
                        course.id,
                        course.name,
                        _findTutorData.value.studentCourseIds.any { course.id == it }
                    )
                }

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi() {
        Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

        when (val findTutorData = academicallyApi.getTutors()) {
            is WithCurrentUser.Success -> {
                _findTutorData.value = findTutorData.data!!
                mutableDrawerData.value = findTutorData.currentUser!!

                ActivityCacheManager.findTutor = findTutorData.data
                ActivityCacheManager.currentUser = findTutorData.currentUser
            }
            is WithCurrentUser.Error -> throw IllegalArgumentException(findTutorData.error)
        }
    }

    fun updateMenu(filterDialogStates: List<FilterDialogStates>, searchQuery: String) {
        viewModelScope.launch {
            try {
                mutableProcessState.value = ProcessState.Loading

                Utils.checkAuthentication(getApplication(), userCacheRepository, academicallyApi)

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

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun toggleDialog(bool: Boolean) {
        _isFilterDialogOpen.value = bool
    }
}