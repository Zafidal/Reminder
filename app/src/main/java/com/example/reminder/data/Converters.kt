package com.example.reminder.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromRepeatDays(days: Set<Int>): String {
        return days.joinToString(",")
    }

    @TypeConverter
    fun toRepeatDays(value: String): Set<Int> {
        if (value.isBlank()) return emptySet()
        return value.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }
}
