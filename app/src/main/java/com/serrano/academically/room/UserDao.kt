package com.serrano.academically.room

import com.serrano.academically.utils.AnalyticsData
import com.serrano.academically.utils.LeaderboardData
import com.serrano.academically.utils.RoomIsDumb
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfo
import com.serrano.academically.utils.UserInfoAndCredentials
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Usage Signup
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    // Usage Profile
    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    // Usage Login, Signup
    @Query("SELECT id FROM User WHERE email = :email AND password = :password AND role = :role")
    fun getUserId(email: String, password: String, role: String): Flow<Int>

    // Usage Signup, Account
    @Query("SELECT name FROM User")
    fun getUserNames(): Flow<List<String>>

    // Usage Signup
    @Query("SELECT email FROM User")
    fun getUserEmails(): Flow<List<String>>

    // Usage Drawer
    @Query("SELECT id, name, role, email, degree FROM User WHERE id = :id")
    fun getUserDataForDrawer(id: Int): Flow<UserDrawerData>

    // Usage AboutSession, MessageTutor
    @Query("SELECT name FROM User WHERE id = :id")
    fun getUserName(id: Int): Flow<String>

    // Usage AboutStudent, AboutTutor
    @Query("SELECT id, name, degree, age, address, contactNumber, summary, educationalBackground FROM User WHERE id = :id")
    fun getUserInfo(id: Int): Flow<UserInfo>

    // Usage Account
    @Query("SELECT id, name, role, email, password, imagePath, degree, age, address, contactNumber, summary, educationalBackground FROM User WHERE id = :id")
    fun getUserInfoAndCredentials(id: Int): Flow<UserInfoAndCredentials>

    // Usage Account
    @Query("UPDATE User SET name = :name, age = :age, degree = :degree, address = :address, contactNumber = :contactNumber, summary = :summary, educationalBackground = :educationalBackground WHERE id = :id")
    suspend fun updateUserInfo(name: String, age: Int, degree: String, address: String, contactNumber: String, summary: String, educationalBackground: String, id: Int)

    // Usage Account
    @Query("UPDATE User SET password = :password WHERE id = :id")
    suspend fun updateUserPassword(password: String, id: Int)

    // Usage Account
    @Query("UPDATE User SET role = :role WHERE id = :id")
    suspend fun updateUserRole(role: String, id: Int)

    // Usage Analytics
    @Query("SELECT id, name, role, email, degree, studentPoints, studentAssessmentPoints, studentBadgePoints, studentRequestPoints, studentSessionPoints, sessionsCompletedAsStudent, requestsSent, deniedRequests, acceptedRequests, assessmentsTakenAsStudent, badgeProgressAsStudent, tutorPoints, tutorAssessmentPoints, tutorBadgePoints, tutorRequestPoints, tutorSessionPoints, sessionsCompletedAsTutor, requestsAccepted, requestsDenied, requestsReceived, assessmentsTakenAsTutor, badgeProgressAsTutor FROM User WHERE id = :id")
    fun getAnalyticsData(id: Int): Flow<AnalyticsData>

    // Usage Leaderboards
    @Query("SELECT id, name, studentPoints AS points FROM User ORDER BY studentPoints DESC LIMIT 20")
    fun getLeaderboardStudentPoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, tutorPoints AS points FROM User ORDER BY tutorPoints DESC LIMIT 20")
    fun getLeaderboardTutorPoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, studentAssessmentPoints AS points FROM User ORDER BY studentAssessmentPoints DESC LIMIT 20")
    fun getLeaderboardStudentAssessmentPoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, tutorAssessmentPoints AS points FROM User ORDER BY tutorAssessmentPoints DESC LIMIT 20")
    fun getLeaderboardTutorAssessmentPoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, studentBadgePoints AS points FROM User ORDER BY studentBadgePoints DESC LIMIT 20")
    fun getLeaderboardStudentBadgePoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, tutorBadgePoints AS points FROM User ORDER BY tutorBadgePoints DESC LIMIT 20")
    fun getLeaderboardTutorBadgePoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, studentRequestPoints AS points FROM User ORDER BY studentRequestPoints DESC LIMIT 20")
    fun getLeaderboardStudentRequestPoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, tutorRequestPoints AS points FROM User ORDER BY tutorRequestPoints DESC LIMIT 20")
    fun getLeaderboardTutorRequestPoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, studentSessionPoints AS points FROM User ORDER BY studentSessionPoints DESC LIMIT 20")
    fun getLeaderboardStudentSessionPoints(): Flow<List<LeaderboardData>>

    // Usage Leaderboards
    @Query("SELECT id, name, tutorSessionPoints AS points FROM User ORDER BY tutorSessionPoints DESC LIMIT 20")
    fun getLeaderboardTutorSessionPoints(): Flow<List<LeaderboardData>>

    // Usage Achievements
    @Query("SELECT id, badgeProgressAsStudent AS achievement FROM User WHERE id = :id")
    fun getBadgeProgressAsStudent(id: Int): Flow<RoomIsDumb>

    // Usage Achievements
    @Query("SELECT id, badgeProgressAsTutor AS achievement FROM User WHERE id = :id")
    fun getBadgeProgressAsTutor(id: Int): Flow<RoomIsDumb>
}