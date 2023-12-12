package com.serrano.academically.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Session(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Int = 0,
    val courseId: Int,
    val tutorId: Int,
    val studentId: Int,
    val moduleId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String,
    val expireDate: LocalDateTime,
    val isComplete: Boolean = false
)