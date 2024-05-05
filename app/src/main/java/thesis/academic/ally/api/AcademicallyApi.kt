package thesis.academic.ally.api

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AcademicallyApi {

    @Multipart
    @POST("/user_routes/upload_image")
    suspend fun uploadImage(@Part file: MultipartBody.Part): NoCurrentUser<Success>

    @GET("/session_routes/get_session")
    suspend fun getSession(@Query("session_id") sessionId: Int): WithCurrentUser<Session>

    @GET("/assignment_routes/get_session_for_assignment")
    suspend fun getSessionForAssignment(@Query("session_id") sessionId: Int): WithCurrentUser<SessionForAssignment>

    @GET("/request_routes/get_student")
    suspend fun getStudent(@Query("message_id") messageId: Int): WithCurrentUser<Student>

    @POST("/request_routes/reject_student")
    suspend fun rejectStudent(@Body rejectStudentBody: RejectStudentBody): NoCurrentUser<List<String>>

    @GET("/request_routes/get_tutor")
    suspend fun getTutor(@Query("tutor_id") tutorId: Int): WithCurrentUser<Tutor>

    @GET("/user_routes/get_info")
    suspend fun getInfo(): NoCurrentUser<Info>

    @POST("/user_routes/update_info")
    suspend fun updateInfo(@Body infoBody: InfoBody): NoCurrentUser<Validation>

    @POST("/user_routes/update_password")
    suspend fun updatePassword(@Body passwordBody: PasswordBody): NoCurrentUser<Validation>

    @POST("/user_routes/switch_role")
    suspend fun switchRole(): NoCurrentUser<Success>

    @GET("/user_routes/get_achievements")
    suspend fun getAchievements(): WithCurrentUser<AchievementWrapper>

    @GET("/user_routes/get_analytics")
    suspend fun getAnalytics(): NoCurrentUser<Analytics>

    @GET("/assessment_routes/get_course_name_and_desc")
    suspend fun getCourseNameAndDesc(@Query("course_id") courseId: Int): OptionalCurrentUser<Course>

    @GET("/assessment_routes/get_assessment")
    suspend fun getAssessment(@Query("course_id") courseId: Int, @Query("items") items: Int, @Query("category") category: String): OptionalCurrentUser<Assessment>

    @POST("/assessment_routes/complete_assessment")
    suspend fun completeAssessment(@Body courseEligibilityBody: CourseEligibilityBody): NoCurrentUser<List<String>>

    @POST("/auth_routes/login")
    @Unauthorized
    suspend fun login(@Body loginBody: LoginBody): AuthenticationResponse

    @GET("/assignment_routes/get_assignment")
    suspend fun getAssignment(@Query("assignment_id") assignmentId: Int): WithCurrentUser<Assignment>

    @POST("/assignment_routes/complete_assignment")
    suspend fun completeAssignment(@Body assignmentBody: AssignmentBody): NoCurrentUser<List<String>>

    @GET("/assessment_routes/get_courses")
    suspend fun getCourses(): OptionalCurrentUser<List<Course2>>

    @GET("/user_routes/get_course_eligibility")
    suspend fun getCourseEligibility(): WithCurrentUser<List<CourseRating>>

    @POST("/session_routes/complete_session_and_create_assignment")
    suspend fun completeSessionAndCreateAssignment(@Body createAssignmentBody: CreateAssignmentBody): NoCurrentUser<List<String>>

    @GET("/request_routes/get_message")
    suspend fun getMessage(@Query("message_id") messageId: Int): WithCurrentUser<Message>

    @POST("/session_routes/create_session")
    suspend fun createSession(@Body createSessionBody: CreateSessionBody): NoCurrentUser<List<String>>

    @GET("/user_routes/get_dashboard_data")
    suspend fun getDashboardData(): WithCurrentUser<Dashboard>

    @GET("/session_routes/get_session_settings")
    suspend fun getSessionSettings(@Query("session_id") sessionId: Int): WithCurrentUser<SessionData>

    @POST("/session_routes/update_session")
    suspend fun updateSession(@Body updateSessionBody: UpdateSessionBody): NoCurrentUser<Success>

    @GET("/user_routes/get_leaderboard")
    suspend fun getLeaderboard(): WithCurrentUser<List<Leaderboard>>

    @GET("/request_routes/get_tutor_eligible_courses")
    suspend fun getTutorEligibleCourses(@Query("tutor_id") tutorId: Int): WithCurrentUser<TutorCourses>

    @POST("/request_routes/send_tutor_request")
    suspend fun sendTutorRequest(@Body tutorRequestBody: TutorRequestBody): MessageResponse

    @GET("/request_routes/get_message_notifications")
    suspend fun getMessageNotifications(): NoCurrentUser<List<MessageNotifications>>

    @GET("/session_routes/get_session_notifications")
    suspend fun getSessionNotifications(): NoCurrentUser<List<SessionNotifications>>

    @GET("/assignment_routes/get_assignment_notifications")
    suspend fun getAssignmentNotifications(): NoCurrentUser<List<AssignmentNotifications>>

    @GET("/user_routes/get_profile")
    suspend fun getProfile(@Query("other_id") otherId: Int): WithCurrentUser<Profile>

    @POST("/auth_routes/signup")
    @Unauthorized
    suspend fun signup(@Body signupBody: SignupBody): AuthenticationResponse

    @GET("/request_routes/get_tutors")
    suspend fun getTutors(): WithCurrentUser<FindTutor>

    @GET("/request_routes/search_tutor")
    suspend fun searchTutor(@Query("course_filter") courseFilter: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<FindTutorData>>

    @GET("/request_routes/search_message_archives")
    suspend fun searchMessageArchives(@Query("status") status: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<MessageNotifications>>

    @GET("/session_routes/search_session_archives")
    suspend fun searchSessionArchives(@Query("status") status: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<SessionArchive>>

    @GET("/assignment_routes/search_assignment_archives")
    suspend fun searchAssignmentArchives(@Query("status") status: String, @Query("search_query") searchQuery: String): NoCurrentUser<List<AssignmentNotifications>>

    @POST("/session_routes/rate_user")
    suspend fun rateUser(@Body rateBody: RateBody): NoCurrentUser<List<String>>

    @GET("/assessment_routes/get_learning_pattern_assessment")
    suspend fun getLearningPatternAssessment(): WithCurrentUser<List<PatternAssessment>>

    @POST("/assessment_routes/complete_learning_pattern_assessment")
    suspend fun completeLearningPatternAssessment(@Body patternAssessmentBody: PatternAssessmentBody): NoCurrentUser<Success>

    @GET("/user_routes/get_current_user")
    suspend fun getCurrentUser(): NoCurrentUser<DrawerData>

    @POST("/auth_routes/forgot_password")
    @Unauthorized
    suspend fun forgotPassword(@Body emailBody: EmailBody): NoCurrentUser<Success>

    @POST("/support_routes/send_support_message")
    suspend fun sendSupportMessage(@Body supportBody: SupportBody): NoCurrentUser<Success>

    @GET("/support_routes/get_support_messages")
    suspend fun getSupportMessages(): WithCurrentUser<List<SupportMessage>>

    @POST("/user_routes/update_notifications_token")
    suspend fun updateNotificationsToken(@Body notificationTokenBody: NotificationTokenBody): NoCurrentUser<Success>

}