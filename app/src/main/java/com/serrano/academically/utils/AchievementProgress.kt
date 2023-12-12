package com.serrano.academically.utils

import android.content.Context
import android.widget.Toast

object AchievementProgress {

    fun computeAchievementProgress(
        data: Double,
        achievementsGoal: List<Int>,
        achievementsIndex: List<Int>,
        achievementProgress: List<Double>
    ): List<Double> {
        return achievementProgress.mapIndexed { idx, progress ->
            if (achievementsIndex.indexOf(idx) != -1) (100 * (data / achievementsGoal[achievementsIndex.indexOf(idx)])).coerceAtMost(100.0) else progress
        }
    }

    fun checkCompletedAchievements(currentProgress: List<Double>, computedProgress: List<Double>, showToast: (Int) -> Unit) {
        for (idx in currentProgress.indices) {
            if (currentProgress[idx] != computedProgress[idx] && computedProgress[idx] >= 100.0) {
                showToast(idx)
            }
        }
    }
}