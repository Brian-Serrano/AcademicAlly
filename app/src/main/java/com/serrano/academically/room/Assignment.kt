package com.serrano.academically.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val assignmentId: Int = 0,
    val studentId: Int,
    val tutorId: Int,
    val courseId: Int,
    val moduleId: Int,
    val assessmentIds: List<Int>,
    val type: String,
    val deadLine: LocalDateTime,
    val studentScore: Int = 0,
    val status: String = "UNCOMPLETED"
)