package com.serrano.academically.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)
    val messageId: Int = 0,
    val courseId: Int,
    val moduleId: Int,
    val studentId: Int,
    val tutorId: Int,
    val studentMessage: String,
    val expireDate: LocalDateTime,
    val status: String = "WAITING"
)