package com.serrano.academically.utils

import com.serrano.academically.api.AchievementWrapper
import com.serrano.academically.api.Analytics
import com.serrano.academically.api.Assessment
import com.serrano.academically.api.Assignment
import com.serrano.academically.api.AssignmentNotifications
import com.serrano.academically.api.Course
import com.serrano.academically.api.Course2
import com.serrano.academically.api.CourseRating
import com.serrano.academically.api.Dashboard
import com.serrano.academically.api.DrawerData
import com.serrano.academically.api.FindTutor
import com.serrano.academically.api.Info
import com.serrano.academically.api.Leaderboard
import com.serrano.academically.api.Message
import com.serrano.academically.api.MessageNotifications
import com.serrano.academically.api.PatternAssessment
import com.serrano.academically.api.Profile
import com.serrano.academically.api.Session
import com.serrano.academically.api.SessionArchive
import com.serrano.academically.api.SessionData
import com.serrano.academically.api.SessionForAssignment
import com.serrano.academically.api.SessionNotifications
import com.serrano.academically.api.Student
import com.serrano.academically.api.SupportMessage
import com.serrano.academically.api.Tutor
import com.serrano.academically.api.TutorCourses

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