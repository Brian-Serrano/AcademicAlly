package com.serrano.academically.utils

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import com.serrano.academically.R
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory

object GetAchievements {

    fun getAchievements(role: Int, context: Context): List<List<String>> {
        val workSheet =
            WorkbookFactory.create(context.assets.open("Achievements.xlsx")).getSheetAt(role)
        return workSheet.map { row ->
            row.map {
                when (it.cellType) {
                    CellType.STRING -> it.stringCellValue.trim()
                    CellType.NUMERIC -> "${it.numericCellValue.toInt()}"
                    CellType.BOOLEAN -> "${it.booleanCellValue}"
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    fun getAchievementBadgeIcons(role: String): List<Int> {
        return listOf(
            if (role == "STUDENT") R.drawable.interview else R.drawable.yes,
            if (role == "STUDENT") R.drawable.communication else R.drawable.agreement,
            if (role == "STUDENT") R.drawable.interview_1 else R.drawable.approved,
            if (role == "STUDENT") R.drawable.personal else R.drawable.interactions,
            if (role == "STUDENT") R.drawable.accept else R.drawable.rejected,
            if (role == "STUDENT") R.drawable.acceptance else R.drawable.rejected_1,
            if (role == "STUDENT") R.drawable.accept_1 else R.drawable.reject,
            R.drawable.reward,
            R.drawable.loyal_customer,
            R.drawable.hand_gesture,
            R.drawable.stars,
            R.drawable.falling_star,
            R.drawable.education,
            R.drawable.management,
            R.drawable.open,
            R.drawable.criteria,
            R.drawable.candidate,
            R.drawable.skills,
            R.drawable.abilities,
            R.drawable.assignment,
            R.drawable.assignment_1,
            R.drawable.distribution,
            R.drawable.rating,
            R.drawable.review,
            R.drawable.rating_1,
            R.drawable.rating_2,
            R.drawable.star_rating,
            R.drawable.customer_review
        )
    }

}