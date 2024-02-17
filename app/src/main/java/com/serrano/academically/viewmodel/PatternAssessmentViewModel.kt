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
import com.serrano.academically.utils.AboutText
import com.serrano.academically.utils.ActivityCacheManager
import com.serrano.academically.utils.PatternAssessmentState
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

    private val _patternAssessment = MutableStateFlow(PatternAssessmentState())
    val patternAssessment: StateFlow<PatternAssessmentState> = _patternAssessment.asStateFlow()

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

    fun sendAnswer(context: Context) {
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

                val collaborativeCount = result.count { it == "Collaborative" }
                val independentCount = result.count { it == "Independent" }
                val experientialCount = result.count { it == "Experiential" }
                val dependentCount = result.count { it == "Dependent" }

                academicallyApi.completeLearningPatternAssessment(
                    PatternAssessmentBody(collaborativeCount, independentCount, experientialCount, dependentCount)
                )

                ActivityCacheManager.patternAssessment = null
                ActivityCacheManager.currentUser = null

                _nextButtonEnabled.value = true

                val learningPattern = listOf(
                    AboutText("Collaborative", "Individuals with a collaborative learning style prefer to work closely with others. They find value in group interactions, discussions, and shared insights. Collaborative learners often seek input from peers, share ideas, and actively engage in group activities. They thrive in environments that promote teamwork and cooperative learning.") to collaborativeCount,
                    AboutText("Independent", "Independent learners prefer to take charge of their learning process. They are self-reliant, organized, and often excel in studying and working alone. Independent learners are comfortable setting their study routines, breaking down problems individually, and managing their time autonomously. They value in-depth understanding and often seek to master concepts on their own.") to independentCount,
                    AboutText("Experiential", "Experiential learners thrive on hands-on experiences and active engagement with the subject matter. They prefer learning by doing, experimenting, and applying concepts in practical scenarios. Experiential learners may jump into tasks without extensive pre-planning, relying on trial and error to understand and master skills. They often value real-world applications and immediate feedback.") to experientialCount,
                    AboutText("Dependent", "Individuals with a dependent learning style prefer structured guidance and support from external sources. They may seek detailed instructions, predefined study materials, and clear step-by-step processes. Dependent learners often feel comfortable following established procedures and relying on the expertise of others. They may prefer explicit guidelines and find security in well-defined learning frameworks.") to dependentCount
                ).sortedByDescending { it.second }

                _patternAssessment.value = _patternAssessment.value.copy(
                    dialogOpen = true,
                    primaryPattern = learningPattern[0].first,
                    secondaryPattern = learningPattern[1].first
                )

            } catch (e: Exception) {
                _nextButtonEnabled.value = true
                Toast.makeText(context, "Something went wrong saving your assessment.", Toast.LENGTH_LONG).show()
            }
        }
    }
}