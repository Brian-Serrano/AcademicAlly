package com.serrano.academically.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.academically.api.AcademicallyApi
import com.serrano.academically.api.Assessment
import com.serrano.academically.api.CourseEligibilityBody
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.OptionalCurrentUser
import com.serrano.academically.api.NoCurrentUser
import com.serrano.academically.datastore.UserCacheRepository
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.AssessmentResult
import com.serrano.academically.utils.Utils
import com.serrano.academically.utils.ProcessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _drawerData = MutableStateFlow(DrawerData())
    val drawerData: StateFlow<DrawerData> = _drawerData.asStateFlow()

    private val _isAuthorized = MutableStateFlow(false)
    val isAuthorized: StateFlow<Boolean> = _isAuthorized.asStateFlow()

    private val _assessment = MutableStateFlow(Assessment())
    val assessment: StateFlow<Assessment> = _assessment.asStateFlow()

    private val _item = MutableStateFlow(0)
    val item: StateFlow<Int> = _item.asStateFlow()

    private val _assessmentAnswers = MutableStateFlow<List<String>>(emptyList())
    val assessmentAnswers: StateFlow<List<String>> = _assessmentAnswers.asStateFlow()

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

    fun getData(courseId: Int, items: Int, type: String) {
        viewModelScope.launch {
            try {
                callApi(courseId, items, type)

                _assessmentAnswers.value = List(items) { "" }

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(courseId: Int, items: Int, type: String, context: Context) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(courseId, items, type)

                _assessmentAnswers.value = List(items) { "" }

                _isRefreshLoading.value = false

                _processState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(context, "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(courseId: Int, items: Int, type: String) {
        when (val assessment = academicallyApi.getAssessment(courseId, items, type)) {
            is OptionalCurrentUser.CurrentUserData -> {
                _assessment.value = assessment.data!!
                _drawerData.value = assessment.currentUser!!
                _isAuthorized.value = true
            }
            is OptionalCurrentUser.UserData -> {
                _assessment.value = assessment.data!!
            }
            is OptionalCurrentUser.Error -> throw IllegalArgumentException(assessment.error)
        }
    }

    fun evaluateAnswers(
        assessmentData: List<List<String>>,
        assessmentAnswers: List<String>,
        type: String,
        courseId: Int,
    ): AssessmentResult {

        val score = Utils.evaluateAnswer(assessmentData, assessmentAnswers, type)
        val items = assessmentData.size
        val evaluator = when (type) {
            "Multiple Choice" -> 0.75
            "Identification" -> 0.6
            else -> 0.9
        }

        return AssessmentResult(
            score = score,
            items = items,
            evaluator = evaluator,
            courseId = courseId,
            eligibility = if (score.toDouble() / items >= evaluator) "TUTOR" else "STUDENT"
        )
    }

    fun saveResultToPreferences(result: AssessmentResult, context: Context, navigate: (Int, Int, String) -> Unit) {
        viewModelScope.launch {
            try {
                _nextButtonEnabled.value = false

                userCacheRepository.saveAssessmentResultData(
                    result.eligibility,
                    result.courseId,
                    result.score,
                    result.items,
                    result.evaluator
                )

                userCacheRepository.clearAssessmentType()

                _nextButtonEnabled.value = true

                navigate(result.score, result.items, result.eligibility)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong saving your assessment.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateCourseSkill(
        result: AssessmentResult,
        navigate: (Int, Int, String) -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                _nextButtonEnabled.value = false

                val apiResponse = academicallyApi.completeAssessment(
                    CourseEligibilityBody(
                        result.courseId,
                        Utils.eligibilityComputingAlgorithm(result.score, result.items, result.evaluator),
                        result.score
                    )
                )
                Utils.showToast(
                    when (apiResponse) {
                        is NoCurrentUser.Success -> apiResponse.data!!
                        is NoCurrentUser.Error -> throw IllegalArgumentException(apiResponse.error)
                    },
                    context
                )

                userCacheRepository.clearAssessmentType()

                _nextButtonEnabled.value = true

                navigate(result.score, result.items, result.eligibility)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong saving your assessment.", Toast.LENGTH_LONG).show()
            }
        }
    }
}