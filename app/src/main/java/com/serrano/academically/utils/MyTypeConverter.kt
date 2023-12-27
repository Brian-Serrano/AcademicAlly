package com.serrano.academically.utils

import androidx.room.TypeConverter
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
    fun fromStringToListOfDouble(string: String): List<Double> {
        return string.split(",").map { it.toDouble() }
    }

    @TypeConverter
    fun fromListOfDoubleToString(doubleList: List<Double>): String {
        return doubleList.joinToString(separator = ",")
    }

}