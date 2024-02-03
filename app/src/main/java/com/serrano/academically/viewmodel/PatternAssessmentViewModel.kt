package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.WithCurrentUser
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.PatternAssessment
import com.serrano.academically.api.PatternAssessmentBody
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.ProcessState
import com.serrano.academically.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatternAssessmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
): ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _assessmentAnswers = MutableStateFlow<List<String>>(emptyList())
    val assessmentAnswers: StateFlow<List<String>> = _assessmentAnswers.asStateFlow()

    private val _assessmentData = MutableStateFlow<List<PatternAssessment>>(emptyList())
    val assessmentData: StateFlow<List<PatternAssessment>> = _assessmentData.asStateFlow()

    private val _assessmentState = MutableStateFlow<List<List<String>>>(emptyList())
    val assessmentState: StateFlow<List<List<String>>> = _assessmentState.asStateFlow()

    private val _nextButtonEnabled = MutableStateFlow(true)
    val nextButtonEnabled: StateFlow<Boolean> = _nextButtonEnabled.asStateFlow()

    private val _isRefreshLoading = MutableStateFlow(false)
    val isRefreshLoading: StateFlow<Boolean> = _isRefreshLoading.asStateFlow()

    fun moveItem(isAdd: Boolean) {
        _item.value = item.value + if (isAdd) 1 else -1
    }

    fun addAnswer(answer: String, index: Int) {
        _assessmentAnswers.value = assessmentAnswers.value.mapIndexed { idx, ans -> if (idx == index) answer else ans }
    }

    fun getData(context: Context) {
        viewModelScope.launch {
            try {
                val patternAssessmentCache = ActivityCacheManager.patternAssessment
                val currentUserCache = ActivityCacheManager.currentUser

                if (patternAssessmentCache != null && currentUserCache != null) {
                    _assessmentData.value = patternAssessmentCache
                    _drawerData.value = currentUserCache
                } else {
                    callApi(context)
                }

                refreshFields()

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

                refreshFields()

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshFields() {
        _assessmentState.value = _assessmentData.value
            .map { listOf("???", it.question, it.choices[0].choice, it.choices[1].choice, it.choices[2].choice, it.choices[3].choice, "A", "default") }

        _assessmentAnswers.value = List(_assessmentState.value.size) { "" }
    }

    private suspend fun callApi(context: Context) {
        Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

        when (val assessmentData = academicallyApi.getLearningPatternAssessment()) {
            is WithCurrentUser.Success -> {
                _assessmentData.value = assessmentData.data!!
                _drawerData.value = assessmentData.currentUser!!

                ActivityCacheManager.patternAssessment = assessmentData.data
                ActivityCacheManager.currentUser = assessmentData.currentUser
            }
            is WithCurrentUser.Error -> throw IllegalArgumentException(assessmentData.error)
        }
    }

    fun sendAnswer(context: Context, navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                _nextButtonEnabled.value = false

                Utils.checkAuthentication(context, userCacheRepository, academicallyApi)

                val result = _assessmentAnswers.value.mapIndexed { idx, ans ->
                    when (ans) {
                        "A" -> _assessmentData.value[idx].choices[0].type
                        "B" -> _assessmentData.value[idx].choices[1].type
                        "C" -> _assessmentData.value[idx].choices[2].type
                        "D" -> _assessmentData.value[idx].choices[3].type
                        else -> ""
                    }
                }

                academicallyApi.completeLearningPatternAssessment(
                    PatternAssessmentBody(
                        result.count { it == "Collaborative" },
                        result.count { it == "Independent" },
                        result.count { it == "Experiential" },
                        result.count { it == "Dependent" }
                    )
                )

                ActivityCacheManager.patternAssessment = null
                ActivityCacheManager.currentUser = null

                _nextButtonEnabled.value = true

                navigate()
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong saving your assessment.", Toast.LENGTH_LONG).show()
            }
        }
    }
}