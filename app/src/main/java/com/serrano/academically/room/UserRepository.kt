package com.serrano.academically.room

import com.serrano.academically.utils.AnalyticsData
import com.serrano.academically.utils.LeaderboardData
import com.serrano.academically.utils.RoomIsDumb
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfo
import com.serrano.academically.utils.UserInfoAndCredentials
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao): IUserRepository {

    override suspend fun addUser(user: User) = userDao.addUser(user)

    override fun getUser(id: Int): Flow<User> = userDao.getUser(id)

    override fun getUserId(email: String, password: String, role: String): Flow<Int> = userDao.getUserId(email, password, role)

    override fun getUserNames(): Flow<List<String>> = userDao.getUserNames()

    override fun getUserEmails(): Flow<List<String>> = userDao.getUserEmails()

    override fun getUserDataForDrawer(id: Int): Flow<UserDrawerData> = userDao.getUserDataForDrawer(id)

    override fun getUserName(id: Int): Flow<String> = userDao.getUserName(id)

    override fun getUserInfo(id: Int): Flow<UserInfo> = userDao.getUserInfo(id)

    override fun getUserInfoAndCredentials(id: Int): Flow<UserInfoAndCredentials> = userDao.getUserInfoAndCredentials(id)

    override suspend fun updateUserInfo(
        name: String,
        age: Int,
        degree: String,
        address: String,
        contactNumber: String,
        summary: String,
        educationalBackground: String,
        id: Int
    ) = userDao.updateUserInfo(name, age, degree, address, contactNumber, summary, educationalBackground, id)

    override suspend fun updateUserPassword(password: String, id: Int) = userDao.updateUserPassword(password, id)

    override suspend fun updateUserRole(role: String, id: Int) = userDao.updateUserRole(role, id)

    override fun getAnalyticsData(id: Int): Flow<AnalyticsData> = userDao.getAnalyticsData(id)

    override fun getLeaderboardStudentPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardStudentPoints()

    override fun getLeaderboardTutorPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardTutorPoints()

    override fun getLeaderboardStudentAssessmentPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardStudentAssessmentPoints()

    override fun getLeaderboardTutorAssessmentPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardTutorAssessmentPoints()

    override fun getLeaderboardStudentRequestPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardStudentRequestPoints()

    override fun getLeaderboardTutorRequestPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardTutorRequestPoints()

    override fun getLeaderboardStudentSessionPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardStudentSessionPoints()

    override fun getLeaderboardTutorSessionPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardTutorSessionPoints()

    override fun getBadgeProgressAsStudent(id: Int): Flow<RoomIsDumb> = userDao.getBadgeProgressAsStudent(id)

    override fun getBadgeProgressAsTutor(id: Int): Flow<RoomIsDumb> = userDao.getBadgeProgressAsTutor(id)

    override suspend fun updateStudentCompletedSessions(points: Double, id: Int) = userDao.updateStudentCompletedSessions(points, id)

    override suspend fun updateTutorCompletedSessions(points: Double, id: Int) = userDao.updateTutorCompletedSessions(points, id)

    override suspend fun updateStudentRequests(points: Double, id: Int) = userDao.updateStudentRequests(points, id)

    override suspend fun updateTutorRequests(points: Double, id: Int) = userDao.updateTutorRequests(points, id)

    override suspend fun updateStudentDeniedRequests(id: Int) = userDao.updateStudentDeniedRequests(id)

    override suspend fun updateTutorDeniedRequests(id: Int) = userDao.updateTutorDeniedRequests(id)

    override suspend fun updateStudentAcceptedRequests(points: Double, id: Int) = userDao.updateStudentAcceptedRequests(points, id)

    override suspend fun updateTutorAcceptedRequests(points: Double, id: Int) = userDao.updateTutorAcceptedRequests(points, id)

    override suspend fun updateStudentAssessments(points: Double, id: Int) = userDao.updateStudentAssessments(points, id)

    override suspend fun updateTutorAssessments(points: Double, id: Int) = userDao.updateTutorAssessments(points, id)

    override suspend fun updateStudentBadgeProgress(badgeProgress: List<Double>, id: Int) = userDao.updateStudentBadgeProgress(badgeProgress, id)

    override suspend fun updateTutorBadgeProgress(badgeProgress: List<Double>, id: Int) = userDao.updateTutorBadgeProgress(badgeProgress, id)

    override fun getStudentSentRequests(id: Int): Flow<Int> = userDao.getStudentSentRequests(id)

    override fun getStudentAcceptedRequests(id: Int): Flow<Int> = userDao.getStudentAcceptedRequests(id)

    override fun getStudentCompletedSessions(id: Int): Flow<Int> = userDao.getStudentCompletedSessions(id)

    override fun getTutorAcceptedRequests(id: Int): Flow<Int> = userDao.getTutorAcceptedRequests(id)

    override fun getTutorDeniedRequests(id: Int): Flow<Int> = userDao.getTutorDeniedRequests(id)

    override fun getTutorCompletedSessions(id: Int): Flow<Int> = userDao.getTutorCompletedSessions(id)

    override fun getStudentPoints(id: Int): Flow<Double> = userDao.getStudentPoints(id)

    override fun getTutorPoints(id: Int): Flow<Double> = userDao.getTutorPoints(id)
}