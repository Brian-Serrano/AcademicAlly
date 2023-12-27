package com.serrano.academically.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val assignmentId: Int = 0,
    val studentId: Int = 0,
    val tutorId: Int = 0,
    val courseId: Int = 0,
    val moduleId: Int = 0,
    val data: String = "",
    val type: String = "Multiple Choice",
    val deadLine: LocalDateTime = LocalDateTime.MIN,
    val studentScore: Int = 0,
    val status: String = "UNCOMPLETED",
    val studentViewed: Boolean = false
)