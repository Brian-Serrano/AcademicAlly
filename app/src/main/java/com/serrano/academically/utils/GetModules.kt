package com.serrano.academically.utils

import android.content.Context
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory

object GetModules {

    fun getAllModules(context: Context): List<List<String>> {
        val workSheet = WorkbookFactory.create(context.assets.open("Modules.xlsx")).getSheetAt(0)
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

    fun getModuleByCourseAndModuleId(courseId: Int, moduleId: Int, context: Context): String {
        return getAllModules(context)[courseId - 1][moduleId]
    }

}