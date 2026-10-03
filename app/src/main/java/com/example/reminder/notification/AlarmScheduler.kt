package com.example.reminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.reminder.MainActivity
import com.example.reminder.data.Reminder
import com.example.reminder.receiver.ReminderReceiver
import java.util.Calendar

object AlarmScheduler {

    fun schedule(context: Context, reminder: Reminder) {
        if (!reminder.isEnabled) {
            cancel(context, reminder)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val targetTime = calculateNextTriggerTime(reminder.hour, reminder.minute, reminder.repeatDays)

        val showIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(targetTime, showIntent),
                pendingIntent
            )
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetTime,
                pendingIntent
            )
        }
    }

    fun cancel(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun calculateNextTriggerTime(hour: Int, minute: Int, repeatDays: Set<Int>): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If repeatDays is empty -> daily repeat
        if (repeatDays.isEmpty()) {
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return target.timeInMillis
        }

        // Convert Calendar.DAY_OF_WEEK (Sun=1, Mon=2, ..., Sat=7) to our format (Mon=1 ... Sun=7)
        var daysChecked = 0
        while (daysChecked < 7) {
            val calDay = target.get(Calendar.DAY_OF_WEEK)
            val ourDay = if (calDay == Calendar.SUNDAY) 7 else calDay - 1

            if (repeatDays.contains(ourDay) && target.after(now)) {
                return target.timeInMillis
            }
            target.add(Calendar.DAY_OF_YEAR, 1)
            daysChecked++
        }

        return target.timeInMillis
    }
}
