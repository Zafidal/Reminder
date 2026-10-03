package com.example.reminder.data

enum class ReminderCategory(val title: String, val emoji: String) {
    WORKOUT("Тренировка", "🏋️‍♂️"),
    DAILY_TASK("Задача", "📋"),
    HEALTH("Здоровье", "💧"),
    OTHER("Другое", "🔔")
}
