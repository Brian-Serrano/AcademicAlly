package com.serrano.academically.datastore

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class UserPref(
    val isNotFirstTimeUser: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isRemember: Boolean = false,
    val id: Int = 0,
    val email: String = "",
    val password: String = "",

    @Serializable(with = PersistentListSerializer::class)
    val searchCourseHistory: PersistentList<String> = persistentListOf(),

    @Serializable(with = PersistentListSerializer::class)
    val searchTutorHistory: PersistentList<String> = persistentListOf(),

    @Serializable(with = PersistentListSerializer::class)
    val searchArchiveHistory: PersistentList<String> = persistentListOf(),

    val eligibility: String = "",
    val courseId: Int = 0,
    val score: Int = 0,
    val items: Int = 0,
    val evaluator: Double = 0.0,

    val assessmentType: String = "",
    val assessmentItems: String = ""
)