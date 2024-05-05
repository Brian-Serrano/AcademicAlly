package thesis.academic.ally.utils

import thesis.academic.ally.api.AchievementWrapper
import thesis.academic.ally.api.Analytics
import thesis.academic.ally.api.Assignment
import thesis.academic.ally.api.AssignmentNotifications
import thesis.academic.ally.api.Course
import thesis.academic.ally.api.Course2
import thesis.academic.ally.api.CourseRating
import thesis.academic.ally.api.Dashboard
import thesis.academic.ally.api.DrawerData
import thesis.academic.ally.api.FindTutor
import thesis.academic.ally.api.Info
import thesis.academic.ally.api.Leaderboard
import thesis.academic.ally.api.Message
import thesis.academic.ally.api.MessageNotifications
import thesis.academic.ally.api.PatternAssessment
import thesis.academic.ally.api.Profile
import thesis.academic.ally.api.Session
import thesis.academic.ally.api.SessionArchive
import thesis.academic.ally.api.SessionData
import thesis.academic.ally.api.SessionForAssignment
import thesis.academic.ally.api.SessionNotifications
import thesis.academic.ally.api.Student
import thesis.academic.ally.api.SupportMessage
import thesis.academic.ally.api.Tutor
import thesis.academic.ally.api.TutorCourses

object ActivityCacheManager {

    val aboutSession: MutableMap<Int, Session> = mutableMapOf()

    val aboutStudent: MutableMap<Int, Student> = mutableMapOf()

    val aboutTutor: MutableMap<Int, Tutor> = mutableMapOf()

    var account: Info? = null

    var achievements: AchievementWrapper? = null

    var analytics: Analytics? = null

    var archiveRejectedMessages: List<MessageNotifications>? = null

    var archiveAcceptedMessages: List<MessageNotifications>? = null

    var archiveCancelledSessions: List<SessionArchive>? = null

    var archiveCompletedSessions: List<SessionArchive>? = null

    var archiveDeadlinedTasks: List<AssignmentNotifications>? = null

    var archiveCompletedTasks: List<AssignmentNotifications>? = null

    val assessmentOption: MutableMap<Int, Course> = mutableMapOf()

    val assignment: MutableMap<Int, Assignment> = mutableMapOf()

    val assignmentOption: MutableMap<Int, SessionForAssignment> = mutableMapOf()

    var chooseAssessment: List<Course2>? = null

    var coursesMenu: List<CourseRating>? = null

    val createAssignment: MutableMap<Int, SessionForAssignment> = mutableMapOf()

    val createSession: MutableMap<Int, Message> = mutableMapOf()

    var dashboard: Dashboard? = null

    val editSession: MutableMap<Int, SessionData> = mutableMapOf()

    var findTutor: FindTutor? = null

    var leaderboard: List<Leaderboard>? = null

    val messageTutor: MutableMap<Int, TutorCourses> = mutableMapOf()

    var notificationsMessages: List<MessageNotifications>? = null

    var notificationsSessions: List<SessionNotifications>? = null

    var notificationsAssignments: List<AssignmentNotifications>? = null

    var patternAssessment: List<PatternAssessment>? = null

    var profile: Profile? = null

    var supportChat: List<SupportMessage>? = null

    var currentUser: DrawerData? = null

    fun clearCache() {
        aboutSession.clear()
        aboutStudent.clear()
        aboutTutor.clear()
        account = null
        achievements = null
        analytics = null
        archiveRejectedMessages = null
        archiveAcceptedMessages = null
        archiveCancelledSessions = null
        archiveCompletedSessions = null
        archiveDeadlinedTasks = null
        archiveCompletedTasks = null
        assessmentOption.clear()
        assignment.clear()
        assignmentOption.clear()
        chooseAssessment = null
        coursesMenu = null
        createAssignment.clear()
        createSession.clear()
        dashboard = null
        editSession.clear()
        findTutor = null
        leaderboard = null
        messageTutor.clear()
        notificationsMessages = null
        notificationsSessions = null
        notificationsAssignments = null
        patternAssessment = null
        profile = null
        supportChat = null
        currentUser = null
    }
}