package com.serrano.academically.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.serrano.academically.utils.AnalyticsData
import com.serrano.academically.utils.LeaderboardData
import com.serrano.academically.utils.Rating
import com.serrano.academically.utils.RoomIsDumb
import com.serrano.academically.utils.UserDrawerData
import com.serrano.academically.utils.UserInfo
import com.serrano.academically.utils.UserInfoAndCredentials
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
    suspend fun updateUserInfo(
        name: String,
        age: Int,
        degree: String,
        address: String,
        contactNumber: String,
        summary: String,
        educationalBackground: String,
        id: Int
    )

    // Usage Account
    @Query("UPDATE User SET password = :password WHERE id = :id")
    suspend fun updateUserPassword(password: String, id: Int)

    // Usage Account
    @Query("UPDATE User SET role = :role WHERE id = :id")
    suspend fun updateUserRole(role: String, id: Int)

    // Usage Analytics
    @Query("SELECT id, name, role, email, degree, studentPoints, studentAssessmentPoints, studentRequestPoints, studentSessionPoints, studentAssignmentPoints, sessionsCompletedAsStudent, requestsSent, deniedRequests, acceptedRequests, assignmentsTaken, assessmentsTakenAsStudent, badgeProgressAsStudent, numberOfRatesAsStudent, totalRatingAsStudent, tutorsRated, tutorPoints, tutorAssessmentPoints, tutorRequestPoints, tutorSessionPoints, tutorAssignmentPoints, sessionsCompletedAsTutor, requestsAccepted, requestsDenied, requestsReceived, assignmentsCreated, assessmentsTakenAsTutor, badgeProgressAsTutor, numberOfRatesAsTutor, totalRatingAsTutor, studentsRated FROM User WHERE id = :id")
    fun getAnalyticsData(id: Int): Flow<AnalyticsData>

    // Usage Achievements
    @Query("SELECT id, badgeProgressAsStudent AS achievement FROM User WHERE id = :id")
    fun getBadgeProgressAsStudent(id: Int): Flow<RoomIsDumb>

    // Usage Achievements
    @Query("SELECT id, badgeProgressAsTutor AS achievement FROM User WHERE id = :id")
    fun getBadgeProgressAsTutor(id: Int): Flow<RoomIsDumb>

    // Usage CreateAssignment For Statistics
    @Query("UPDATE User SET sessionsCompletedAsStudent = sessionsCompletedAsStudent + 1, studentSessionPoints = studentSessionPoints + :points, studentPoints = studentPoints + :points WHERE id = :id")
    suspend fun updateStudentCompletedSessions(points: Double, id: Int)

    // Usage CreateAssignment For Statistics
    @Query("UPDATE User SET sessionsCompletedAsTutor = sessionsCompletedAsTutor + 1, tutorSessionPoints = tutorSessionPoints + :points, tutorPoints = tutorPoints + :points WHERE id = :id")
    suspend fun updateTutorCompletedSessions(points: Double, id: Int)

    // Usage MessageTutor For Statistics
    @Query("UPDATE User SET requestsSent = requestsSent + 1, studentRequestPoints = studentRequestPoints + :points, studentPoints = studentPoints + :points WHERE id = :id")
    suspend fun updateStudentRequests(points: Double, id: Int)

    // Usage MessageTutor For Statistics
    @Query("UPDATE User SET requestsReceived = requestsReceived + 1, tutorRequestPoints = tutorRequestPoints + :points, tutorPoints = tutorPoints + :points WHERE id = :id")
    suspend fun updateTutorRequests(points: Double, id: Int)

    // Usage AboutStudent For Statistics
    @Query("UPDATE User SET deniedRequests = deniedRequests + 1 WHERE id = :id")
    suspend fun updateStudentDeniedRequests(id: Int)

    // Usage AboutStudent For Statistics
    @Query("UPDATE User SET requestsDenied = requestsDenied + 1 WHERE id = :id")
    suspend fun updateTutorDeniedRequests(id: Int)

    // Usage CreateSession For Statistics
    @Query("UPDATE User SET acceptedRequests = acceptedRequests + 1, studentRequestPoints = studentRequestPoints + :points, studentPoints = studentPoints + :points WHERE id = :id")
    suspend fun updateStudentAcceptedRequests(points: Double, id: Int)

    // Usage CreateSession For Statistics
    @Query("UPDATE User SET requestsAccepted = requestsAccepted + 1, tutorRequestPoints = tutorRequestPoints + :points, tutorPoints = tutorPoints + :points WHERE id = :id")
    suspend fun updateTutorAcceptedRequests(points: Double, id: Int)

    // Usage Signup, Assessment For Statistics
    @Query("UPDATE User SET assessmentsTakenAsStudent = assessmentsTakenAsStudent + 1, studentAssessmentPoints = studentAssessmentPoints + :points, studentPoints = studentPoints + :points WHERE id = :id")
    suspend fun updateStudentAssessments(points: Double, id: Int)

    // Usage Signup, Assessment For Statistics
    @Query("UPDATE User SET assessmentsTakenAsTutor = assessmentsTakenAsTutor + 1, tutorAssessmentPoints = tutorAssessmentPoints + :points, tutorPoints = tutorPoints + :points WHERE id = :id")
    suspend fun updateTutorAssessments(points: Double, id: Int)

    // Usage MessageTutor, CreateAssignment, CreateSession, Signup, Assessment, Assignment
    @Query("UPDATE User SET badgeProgressAsStudent = :badgeProgress WHERE id = :id")
    suspend fun updateStudentBadgeProgress(badgeProgress: List<Double>, id: Int)

    // Usage MessageTutor, CreateAssignment, CreateSession, Signup, Assessment, AboutStudent
    @Query("UPDATE User SET badgeProgressAsTutor = :badgeProgress WHERE id = :id")
    suspend fun updateTutorBadgeProgress(badgeProgress: List<Double>, id: Int)

    // Usage MessageTutor
    @Query("SELECT requestsSent FROM User WHERE id = :id")
    fun getStudentSentRequests(id: Int): Flow<Int>

    // Usage CreateSession
    @Query("SELECT acceptedRequests FROM User WHERE id = :id")
    fun getStudentAcceptedRequests(id: Int): Flow<Int>

    // Usage CreateAssignment
    @Query("SELECT sessionsCompletedAsStudent FROM User WHERE id = :id")
    fun getStudentCompletedSessions(id: Int): Flow<Int>

    // Usage CreateSession
    @Query("SELECT requestsAccepted FROM User WHERE id = :id")
    fun getTutorAcceptedRequests(id: Int): Flow<Int>

    // Usage AboutStudent
    @Query("SELECT requestsDenied FROM User WHERE id = :id")
    fun getTutorDeniedRequests(id: Int): Flow<Int>

    // Usage CreateAssignment
    @Query("SELECT sessionsCompletedAsTutor FROM User WHERE id = :id")
    fun getTutorCompletedSessions(id: Int): Flow<Int>

    // Usage MessageTutor, CreateAssignment, CreateSession, Signup, Assessment, Assignment
    @Query("SELECT studentPoints FROM User WHERE id = :id")
    fun getStudentPoints(id: Int): Flow<Double>

    // Usage MessageTutor, CreateAssignment, CreateSession, Signup, Assessment
    @Query("SELECT tutorPoints FROM User WHERE id = :id")
    fun getTutorPoints(id: Int): Flow<Double>

    // Usage Assignment
    @Query("UPDATE User SET assignmentsTaken = assignmentsTaken + 1, studentAssignmentPoints = studentAssignmentPoints + :points, studentPoints = studentPoints + :points WHERE id = :id")
    suspend fun updateStudentAssignments(points: Double, id: Int)

    // Usage CreateAssignment
    @Query("UPDATE User SET assignmentsCreated = assignmentsCreated + 1, tutorAssignmentPoints = tutorAssignmentPoints + :points, tutorPoints = tutorPoints + :points WHERE id = :id")
    suspend fun updateTutorAssignments(points: Double, id: Int)

    // Usage Assignment
    @Query("SELECT assignmentsTaken FROM User WHERE id = :id")
    fun getStudentAssignments(id: Int): Flow<Int>

    // Usage CreateAssignment
    @Query("SELECT assignmentsCreated FROM User WHERE id = :id")
    fun getTutorAssignments(id: Int): Flow<Int>

    // Usage Dashboard
    @Query("SELECT totalRatingAsStudent AS rating, numberOfRatesAsStudent AS number FROM User WHERE id = :id")
    fun getStudentRating(id: Int): Flow<Rating>

    // Usage FindTutor, AboutTutor, Dashboard
    @Query("SELECT totalRatingAsTutor AS rating, numberOfRatesAsTutor AS number FROM User WHERE id = :id")
    fun getTutorRating(id: Int): Flow<Rating>

    // Usage Leaderboard
    @Query("SELECT id, name, totalRatingAsStudent AS rating, numberOfRatesAsStudent AS number FROM User ORDER BY totalRatingAsStudent / numberOfRatesAsStudent DESC LIMIT 20")
    fun getStudentLeaderboard(): Flow<List<LeaderboardData>>

    // Usage Leaderboard
    @Query("SELECT id, name, totalRatingAsTutor AS rating, numberOfRatesAsTutor AS number FROM User ORDER BY totalRatingAsTutor / numberOfRatesAsTutor DESC LIMIT 20")
    fun getTutorLeaderboard(): Flow<List<LeaderboardData>>

    // Usage Archive
    @Query("UPDATE User SET totalRatingAsStudent = totalRatingAsStudent + :rating, numberOfRatesAsStudent = numberOfRatesAsStudent + 1 WHERE id = :id")
    suspend fun updateStudentRating(rating: Double, id: Int)

    // Usage Archive
    @Query("UPDATE User SET tutorsRated = tutorsRated + 1 WHERE id = :id")
    suspend fun updateStudentRates(id: Int)

    // Usage Archive
    @Query("UPDATE User SET totalRatingAsTutor = totalRatingAsTutor + :rating, numberOfRatesAsTutor = numberOfRatesAsTutor + 1 WHERE id = :id")
    suspend fun updateTutorRating(rating: Double, id: Int)

    // Usage Archive
    @Query("UPDATE User SET studentsRated = studentsRated + 1 WHERE id = :id")
    suspend fun updateTutorRates(id: Int)

    // Usage Archive
    @Query("SELECT numberOfRatesAsStudent FROM User WHERE id = :id")
    fun getStudentRatingNumber(id: Int): Flow<Int>

    // Usage Archive
    @Query("SELECT numberOfRatesAsTutor FROM User WHERE id = :id")
    fun getTutorRatingNumber(id: Int): Flow<Int>

    // Usage Archive
    @Query("SELECT tutorsRated FROM User WHERE id = :id")
    fun getStudentRates(id: Int): Flow<Int>

    // Usage Archive
    @Query("SELECT studentsRated FROM User WHERE id = :id")
    fun getTutorRates(id: Int): Flow<Int>
}