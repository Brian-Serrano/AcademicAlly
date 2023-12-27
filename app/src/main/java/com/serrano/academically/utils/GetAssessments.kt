package com.serrano.academically.utils

import android.content.Context
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory

object GetAssessments {

    // Ethics 15
    private val assessmentFileNames = listOf(
        "Computer Programming 1.xlsx",
        "Ethics.xlsx",
        "Application Development And Emerging Technologies.xlsx",
        "Advance Web Programming.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Artificial Intelligence.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Computer Systems Architecture.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Game Programming.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Information Assurance and Security (Cybersecurity Fundamentals).xlsx",
        "Ethics.xlsx",
        "Intermediate Web Programming.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Modeling and Simulation.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Physical Education 1.xlsx",
        "Physical Education 2.xlsx",
        "Physical Education 3.xlsx",
        "Physical Education 4.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Software Engineering.xlsx",
        "Ethics.xlsx",
        "The Contemporary World.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Baking and Pastry Production.xlsx",
        "Ethics.xlsx",
        "Culinary of Asian Countries.xlsx",
        "Ethics.xlsx",
        "Sanitation and Hygiene.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "21st Century Literature from the Philippines and the World.xlsx",
        "Basic Calculus.xlsx",
        "Earth Science.xlsx",
        "General Biology 1.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Komunikasyon at Pananaliksik sa Wika at Kulturang Pilipino.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx",
        "Ethics.xlsx"
    )

    fun getAssessments(
        courseId: Int,
        items: Int,
        type: String,
        context: Context
    ): List<List<String>> {
        return WorkbookFactory
            .create(
                context.assets.open(assessmentFileNames[courseId - 1])
            )
            .getSheetAt(
                when (type) {
                    "Multiple Choice" -> 0
                    "Identification" -> 1
                    else -> 2
                }
            ).shuffled().take(items).map { row ->
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