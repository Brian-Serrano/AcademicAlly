package com.serrano.academically.room

import com.serrano.academically.utils.AnalyticsData
import com.serrano.academically.utils.LeaderboardData
import com.serrano.academically.utils.RoomIsDumb
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfo
import com.serrano.academically.utils.UserInfoAndCredentials
import kotlinx.coroutines.flow.Flow

interface IUserRepository {

    suspend fun addUser(user: User)

    fun getUser(id: Int): Flow<User>

    fun getUserId(email: String, password: String, role: String): Flow<Int>

    fun getUserNames(): Flow<List<String>>

    fun getUserEmails(): Flow<List<String>>

    fun getUserDataForDrawer(id: Int): Flow<UserDrawerData>

    fun getUserName(id: Int): Flow<String>

    fun getUserInfo(id: Int): Flow<UserInfo>

    fun getUserInfoAndCredentials(id: Int): Flow<UserInfoAndCredentials>

    suspend fun updateUserInfo(name: String, age: Int, degree: String, address: String, contactNumber: String, summary: String, educationalBackground: String, id: Int)

    suspend fun updateUserPassword(password: String, id: Int)

    suspend fun updateUserRole(role: String, id: Int)

    fun getAnalyticsData(id: Int): Flow<AnalyticsData>

    fun getLeaderboardStudentPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardTutorPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardStudentAssessmentPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardTutorAssessmentPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardStudentRequestPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardTutorRequestPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardStudentSessionPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardTutorSessionPoints(): Flow<List<LeaderboardData>>

    fun getBadgeProgressAsStudent(id: Int): Flow<RoomIsDumb>

    fun getBadgeProgressAsTutor(id: Int): Flow<RoomIsDumb>

    suspend fun updateStudentCompletedSessions(points: Double, id: Int)

    suspend fun updateTutorCompletedSessions(points: Double, id: Int)

    suspend fun updateStudentRequests(points: Double, id: Int)

    suspend fun updateTutorRequests(points: Double, id: Int)

    suspend fun updateStudentDeniedRequests(id: Int)

    suspend fun updateTutorDeniedRequests(id: Int)

    suspend fun updateStudentAcceptedRequests(points: Double, id: Int)

    suspend fun updateTutorAcceptedRequests(points: Double, id: Int)

    suspend fun updateStudentAssessments(points: Double, id: Int)

    suspend fun updateTutorAssessments(points: Double, id: Int)

    suspend fun updateStudentBadgeProgress(badgeProgress: List<Double>, id: Int)

    suspend fun updateTutorBadgeProgress(badgeProgress: List<Double>, id: Int)

    fun getStudentSentRequests(id: Int): Flow<Int>

    fun getStudentAcceptedRequests(id: Int): Flow<Int>

    fun getStudentCompletedSessions(id: Int): Flow<Int>

    fun getTutorAcceptedRequests(id: Int): Flow<Int>

    fun getTutorDeniedRequests(id: Int): Flow<Int>

    fun getTutorCompletedSessions(id: Int): Flow<Int>

    fun getStudentPoints(id: Int): Flow<Double>

    fun getTutorPoints(id: Int): Flow<Double>
}