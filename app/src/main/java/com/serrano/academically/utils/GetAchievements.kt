package com.serrano.academically.utils

import android.content.Context
import com.serrano.academically.ui.theme.Strings
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

object GetAchievements {

    fun getAchievements(role: Int, context: Context): List<List<String>> {
        val workSheet = WorkbookFactory.create(context.assets.open("Achievements.xlsx")).getSheetAt(role)
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

}