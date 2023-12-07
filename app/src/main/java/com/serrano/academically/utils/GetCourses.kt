package com.serrano.academically.utils

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Path
import com.serrano.academically.ui.theme.Strings
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

object GetCourses {

    fun getCourseNameById(id: Int, context: Context): String {
        return getAllCourses(context)[id - 1][1]
    }

    fun getCourseAndDescription(id: Int, context: Context): Pair<String, String> {
        val courses = getAllCourses(context)
        return Pair(courses[id - 1][1], courses[id - 1][2])
    }

    fun getAllCourses(context: Context): List<List<String>> {
        val workSheet = WorkbookFactory.create(context.assets.open("Courses.xlsx")).getSheetAt(0)
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