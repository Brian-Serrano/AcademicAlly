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

    fun getLeaderboardStudentBadgePoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardTutorBadgePoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardStudentRequestPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardTutorRequestPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardStudentSessionPoints(): Flow<List<LeaderboardData>>

    fun getLeaderboardTutorSessionPoints(): Flow<List<LeaderboardData>>

    fun getBadgeProgressAsStudent(id: Int): Flow<RoomIsDumb>

    fun getBadgeProgressAsTutor(id: Int): Flow<RoomIsDumb>
}