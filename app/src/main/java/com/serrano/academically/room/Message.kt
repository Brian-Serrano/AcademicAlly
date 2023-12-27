package com.serrano.academically.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)
    val messageId: Int = 0,
    val courseId: Int = 0,
    val moduleId: Int = 0,
    val studentId: Int = 0,
    val tutorId: Int = 0,
    val studentMessage: String = "NA",
    val expireDate: LocalDateTime = LocalDateTime.MIN,
    val status: String = "WAITING",
    val tutorViewed: Boolean = false
)