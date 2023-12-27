package com.serrano.academically.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int = 0,
    val courseId: Int = 0,
    val tutorId: Int = 0,
    val studentId: Int = 0,
    val moduleId: Int = 0,
    val startTime: LocalDateTime = LocalDateTime.MIN,
    val endTime: LocalDateTime = LocalDateTime.MIN,
    val location: String = "Laoag",
    val expireDate: LocalDateTime = LocalDateTime.MIN,
    val status: String = "UPCOMING",
    val studentRate: Boolean = false,
    val tutorRate: Boolean = false,
    val studentViewed: Boolean = false
)