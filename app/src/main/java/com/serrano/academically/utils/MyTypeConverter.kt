package com.serrano.academically.utils

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime

class MyTypeConverter {

    @TypeConverter
    fun fromDateToString(date: LocalDateTime): String {
        return date.toString()
    }
    @TypeConverter
    fun fromStringToDate(date: String): LocalDateTime {
        return LocalDateTime.parse(date)
    }

    @TypeConverter
    fun fromStringToListOfFloat(string: String): List<Float> {
        return string.split(",").map { it.toFloat() }
    }

    @TypeConverter
    fun fromListOfFloatToString(floatList: List<Float>): String {
        return floatList.joinToString(separator = ",")
    }

}