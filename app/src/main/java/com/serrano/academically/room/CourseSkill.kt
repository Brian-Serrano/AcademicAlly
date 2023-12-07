package com.serrano.academically.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CourseSkill(
    @PrimaryKey(autoGenerate = true)
    val courseSkillId: Int = 0,
    val courseId: Int,
    val userId: Int,
    val role: String,
    val courseAssessmentTaken: Int,
    val courseAssessmentScore: Int,
    val courseAssessmentItemsTotal: Int,
    val courseAssessmentEvaluator: Float
)