package com.serrano.academically.datastore

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.collections.immutable.mutate

val Context.dataStore by dataStore("user-pref.json", UserPrefSerializer)

object UpdateUserPref {

    suspend fun updateDataByLoggingIn(
        context: Context,
        isRemember: Boolean,
        id: Int,
        email: String,
        password: String
    ) {
        context.dataStore.updateData {
            it.copy(
                isNotFirstTimeUser = true,
                isLoggedIn = true,
                isRemember = isRemember,
                id = id,
                email = email,
                password = password
            )
        }
    }

    suspend fun clearDataByLoggingOut(context: Context) {
        context.dataStore.updateData {
            it.copy(
                isLoggedIn = false,
                id = 0
            )
        }
    }

    suspend fun addSearchTutorHistory(context: Context, history: String) {
        context.dataStore.updateData { user ->
            user.copy(
                searchTutorHistory = user.searchTutorHistory.mutate { his ->
                    if (his.none { it == history }) {
                        his.add(history)
                    }
                }
            )
        }
    }

    suspend fun addSearchCourseHistory(context: Context, history: String) {
        context.dataStore.updateData { user ->
            user.copy(
                searchCourseHistory = user.searchCourseHistory.mutate { his ->
                    if (his.none { it == history }) {
                        his.add(history)
                    }
                }
            )
        }
    }

    suspend fun addSearchArchiveHistory(context: Context, history: String) {
        context.dataStore.updateData { user ->
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
        context: Context,
        eligibility: String,
        courseId: Int,
        score: Int,
        items: Int,
        evaluator: Double
    ) {
        context.dataStore.updateData {
            it.copy(
                eligibility = eligibility,
                courseId = courseId,
                score = score,
                items = items,
                evaluator = evaluator
            )
        }
    }

    suspend fun clearAssessmentResultData(context: Context) {
        context.dataStore.updateData {
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
        context: Context,
        assessmentType: String,
        assessmentItems: String
    ) {
        context.dataStore.updateData {
            it.copy(
                assessmentType = assessmentType,
                assessmentItems = assessmentItems
            )
        }
    }

    suspend fun clearAssessmentType(context: Context) {
        context.dataStore.updateData {
            it.copy(
                assessmentType = "",
                assessmentItems = ""
            )
        }
    }
}