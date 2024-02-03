package com.serrano.academically.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AcademicallyApi {

    @Multipart
    @POST("/post_routes/upload_image")
    suspend fun uploadImage(@Part file: MultipartBody.Part): NoCurrentUser<Success>

    @GET("/get_routes/get_session")
    suspend fun getSession(@Query("session_id") sessionId: Int): WithCurrentUser<Session>

    @GET("/get_routes/get_session_for_assignment")
    suspend fun getSessionForAssignment(@Query("session_id") sessionId: Int): WithCurrentUser<SessionForAssignment>

    @GET("/get_routes/get_student")
    suspend fun getStudent(@Query("message_id") messageId: Int): WithCurrentUser<Student>

    @POST("/post_routes/reject_student")
    suspend fun rejectStudent(@Body rejectStudentBody: RejectStudentBody): NoCurrentUser<List<String>>

    @GET("/get_routes/get_tutor")
    suspend fun getTutor(@Query("tutor_id") tutorId: Int): WithCurrentUser<Tutor>

    @GET("/get_routes/get_info")
    suspend fun getInfo(): NoCurrentUser<Info>

    @POST("/post_routes/update_info")
    suspend fun updateInfo(@Body infoBody: InfoBody): NoCurrentUser<Validation>

    @POST("/post_routes/update_password")
    suspend fun updatePassword(@Body passwordBody: PasswordBody): NoCurrentUser<Validation>

    @POST("/post_routes/switch_role")
    suspend fun switchRole(): NoCurrentUser<Success>

    @GET("/get_routes/get_achievements")
    suspend fun getAchievements(): WithCurrentUser<AchievementWrapper>

    @GET("/get_routes/get_analytics")
    suspend fun getAnalytics(): NoCurrentUser<Analytics>

    @GET("/get_routes/get_course_name_and_desc")
    suspend fun getCourseNameAndDesc(@Query("course_id") courseId: Int): OptionalCurrentUser<Course>

    @GET("/get_routes/get_assessment")
    suspend fun getAssessment(@Query("course_id") courseId: Int, @Query("items") items: Int, @Query("category") category: String): OptionalCurrentUser<Assessment>

    @POST("/post_routes/complete_assessment")
    suspend fun completeAssessment(@Body courseEligibilityBody: CourseEligibilityBody): NoCurrentUser<List<String>>

    @POST("/unauth_routes/login")
    @Unauthorized
    suspend fun login(@Body loginBody: LoginBody): AuthenticationResponse

    @GET("/get_routes/get_assignment")
    suspend fun getAssignment(@Query("assignment_id") assignmentId: Int): WithCurrentUser<Assignment>

    @POST("/post_routes/complete_assignment")
    suspend fun completeAssignment(@Body assignmentBody: AssignmentBody): NoCurrentUser<List<String>>

    @GET("/get_routes/get_courses")
    suspend fun getCourses(): OptionalCurrentUser<List<Course2>>

    @GET("/get_routes/get_course_eligibility")
    suspend fun getCourseEligibility(): WithCurrentUser<List<CourseRating>>

    @POST("/post_routes/complete_session_and_create_assignment")
    suspend fun completeSessionAndCreateAssignment(@Body createAssignmentBody: CreateAssignmentBody): NoCurrentUser<List<String>>

    @GET("/get_routes/get_message")
    suspend fun getMessage(@Query("message_id") messageId: Int): WithCurrentUser<Message>

    @POST("/post_routes/create_session")
    suspend fun createSession(@Body createSessionBody: CreateSessionBody): NoCurrentUser<List<String>>

    @GET("/get_routes/get_dashboard_data")
    suspend fun getDashboardData(): WithCurrentUser<Dashboard>

    @GET("/get_routes/get_session_settings")
    suspend fun getSessionSettings(@Query("session_id") sessionId: Int): WithCurrentUser<SessionData>

    @POST("/post_routes/update_session")
    suspend fun updateSession(@Body updateSessionBody: UpdateSessionBody): NoCurrentUser<Success>

    @GET("/get_routes/get_leaderboard")
    suspend fun getLeaderboard(): WithCurrentUser<List<Leaderboard>>

    @GET("/get_routes/get_tutor_eligible_courses")
    suspend fun getTutorEligibleCourses(@Query("tutor_id") tutorId: Int): WithCurrentUser<TutorCourses>

    @POST("/post_routes/send_tutor_request")
    suspend fun sendTutorRequest(@Body tutorRequestBody: TutorRequestBody): MessageResponse

    @GET("/get_routes/get_message_notifications")
    suspend fun getMessageNotifications(): NoCurrentUser<List<MessageNotifications>>

    @GET("/get_routes/get_session_notifications")
    suspend fun getSessionNotifications(): NoCurrentUser<List<SessionNotifications>>

    @GET("/get_routes/get_assignment_notifications")
    suspend fun getAssignmentNotifications(): NoCurrentUser<List<AssignmentNotifications>>

    @GET("/get_routes/get_profile")
    suspend fun getProfile(@Query("other_id") otherId: Int): WithCurrentUser<Profile>

    @POST("/unauth_routes/signup")
    @Unauthorized
    suspend fun signup(@Body signupBody: SignupBody): AuthenticationResponse

    @GET("/get_routes/get_tutors")
    suspend fun getTutors(): WithCurrentUser<FindTutor>

    @GET("/get_routes/search_tutor")
    suspend fun searchTutor(@Query("course_filter") courseFilter: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<FindTutorData>>

    @GET("/get_routes/search_message_archives")
    suspend fun searchMessageArchives(@Query("status") status: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<MessageNotifications>>

    @GET("/get_routes/search_session_archives")
    suspend fun searchSessionArchives(@Query("status") status: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<SessionArchive>>

    @GET("/get_routes/search_assignment_archives")
    suspend fun searchAssignmentArchives(@Query("status") status: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<AssignmentNotifications>>

    @POST("/post_routes/rate_user")
    suspend fun rateUser(@Body rateBody: RateBody): NoCurrentUser<List<String>>

    @GET("/get_routes/get_learning_pattern_assessment")
    suspend fun getLearningPatternAssessment(): WithCurrentUser<List<PatternAssessment>>

    @POST("/post_routes/complete_learning_pattern_assessment")
    suspend fun completeLearningPatternAssessment(@Body patternAssessmentBody: PatternAssessmentBody): NoCurrentUser<Success>

    @GET("/get_routes/get_current_user")
    suspend fun getCurrentUser(): NoCurrentUser<DrawerData>

    @POST("/unauth_routes/forgot_password")
    @Unauthorized
    suspend fun forgotPassword(@Body emailBody: EmailBody): NoCurrentUser<Success>

    @POST("/post_routes/send_support_message")
    suspend fun sendSupportMessage(@Body supportBody: SupportBody): NoCurrentUser<Success>

    @GET("/get_routes/get_support_messages")
    suspend fun getSupportMessages(): WithCurrentUser<List<SupportMessage>>

}