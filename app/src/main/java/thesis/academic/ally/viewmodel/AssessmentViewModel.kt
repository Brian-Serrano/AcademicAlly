package thesis.academic.ally.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import thesis.academic.ally.api.AcademicallyApi
import thesis.academic.ally.api.Assessment
import thesis.academic.ally.api.CourseEligibilityBody
import thesis.academic.ally.api.NoCurrentUser
import thesis.academic.ally.api.OptionalCurrentUser
import thesis.academic.ally.datastore.UserCacheRepository
import thesis.academic.ally.utils.AssessmentResult
import thesis.academic.ally.utils.ProcessState
import thesis.academic.ally.utils.Utils
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val academicallyApi: AcademicallyApi,
    private val userCacheRepository: UserCacheRepository,
    application: Application
) : BaseViewModel(application) {

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

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshData(courseId: Int, items: Int, type: String) {
        viewModelScope.launch {
            try {
                _isRefreshLoading.value = true

                callApi(courseId, items, type)

                _assessmentAnswers.value = List(items) { "" }

                _isRefreshLoading.value = false

                mutableProcessState.value = ProcessState.Success
            } catch (e: Exception) {
                _isRefreshLoading.value = false
                Toast.makeText(getApplication(), "Failed to refresh data.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun callApi(courseId: Int, items: Int, type: String) {
        when (val assessment = academicallyApi.getAssessment(courseId, items, type)) {
            is OptionalCurrentUser.CurrentUserData -> {
                _assessment.value = assessment.data!!
                mutableDrawerData.value = assessment.currentUser!!
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

    fun saveResultToPreferences(result: AssessmentResult, navigate: (Int, Int, String) -> Unit) {
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
                Toast.makeText(getApplication(), "Something went wrong saving your assessment.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateCourseSkill(
        result: AssessmentResult,
        navigate: (Int, Int, String) -> Unit
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
                    getApplication()
                )

                userCacheRepository.clearAssessmentType()

                _nextButtonEnabled.value = true

                navigate(result.score, result.items, result.eligibility)
            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(getApplication(), "Something went wrong saving your assessment.", Toast.LENGTH_LONG).show()
            }
        }
    }
}