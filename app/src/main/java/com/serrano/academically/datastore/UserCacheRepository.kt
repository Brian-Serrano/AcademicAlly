package com.serrano.academically.datastore

import androidx.datastore.core.DataStore
import kotlinx.collections.immutable.mutate
import javax.inject.Inject

class UserCacheRepository @Inject constructor(
    val userDataStore: DataStore<UserCache>
) {

    suspend fun updateDataByLoggingIn(
        isRemember: Boolean,
        authToken: String,
        email: String,
        password: String,
        role: String
    ) {
        userDataStore.updateData {
            it.copy(
                isNotFirstTimeUser = true,
                isRemember = isRemember,
                authToken = authToken,
                email = email,
                password = password,
                role = role
            )
        }
    }

    suspend fun updateAuthToken(authToken: String) {
        userDataStore.updateData {
            it.copy(authToken = authToken)
        }
    }

    suspend fun updatePassword(password: String) {
        userDataStore.updateData {
            it.copy(password = password)
        }
    }

    suspend fun updateRole(role: String) {
        userDataStore.updateData {
            it.copy(role = role)
        }
    }

    suspend fun addSearchTutorHistory(history: String) {
        userDataStore.updateData { user ->
            user.copy(
                searchTutorHistory = user.searchTutorHistory.mutate { his ->
                    if (his.none { it == history }) {
                        his.add(history)
                    }
                }
            )
        }
    }

    suspend fun addSearchCourseHistory(history: String) {
        userDataStore.updateData { user ->
            user.copy(
                searchCourseHistory = user.searchCourseHistory.mutate { his ->
                    if (his.none { it == history }) {
                        his.add(history)
                    }
                }
            )
        }
    }

    suspend fun addSearchArchiveHistory(history: String) {
        userDataStore.updateData { user ->
            user.copy(
                searchArchiveHistory = user.searchArchiveHistory.mutate { his ->
                    if (his.none { it == history }) {
                        his.add(history)
                    }
                }
            )
        }
    }

    suspend fun saveAssessmentResultData(
        eligibility: String,
        courseId: Int,
        score: Int,
        items: Int,
        evaluator: Double
    ) {
        userDataStore.updateData {
            it.copy(
                eligibility = eligibility,
                courseId = courseId,
                score = score,
                items = items,
                evaluator = evaluator
            )
        }
    }

    suspend fun clearAssessmentResultData() {
        userDataStore.updateData {
            it.copy(
                eligibility = "",
                courseId = 0,
                score = 0,
                items = 0,
                evaluator = 0.0
            )
        }
    }

    suspend fun saveAssessmentType(
        assessmentType: String,
        assessmentItems: String
    ) {
        userDataStore.updateData {
            it.copy(
                assessmentType = assessmentType,
                assessmentItems = assessmentItems
            )
        }
    }

    suspend fun clearAssessmentType() {
        userDataStore.updateData {
            it.copy(
                assessmentType = "",
                assessmentItems = ""
            )
        }
    }
}