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

    override fun getLeaderboardStudentBadgePoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardStudentBadgePoints()

    override fun getLeaderboardTutorBadgePoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardTutorBadgePoints()

    override fun getLeaderboardStudentRequestPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardStudentRequestPoints()

    override fun getLeaderboardTutorRequestPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardTutorRequestPoints()

    override fun getLeaderboardStudentSessionPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardStudentSessionPoints()

    override fun getLeaderboardTutorSessionPoints(): Flow<List<LeaderboardData>> = userDao.getLeaderboardTutorSessionPoints()

    override fun getBadgeProgressAsStudent(id: Int): Flow<RoomIsDumb> = userDao.getBadgeProgressAsStudent(id)

    override fun getBadgeProgressAsTutor(id: Int): Flow<RoomIsDumb> = userDao.getBadgeProgressAsTutor(id)
}