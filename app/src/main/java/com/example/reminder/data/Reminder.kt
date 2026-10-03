package com.example.reminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val categoryId: Long = 1L,
    val categoryName: String = "Тренировка",
    val categoryEmoji: String = "🏋️‍♂️",
    val hour: Int,
    val minute: Int,
    val repeatDays: Set<Int> = emptySet(),
    val isEnabled: Boolean = true,
    val isAlarm: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
