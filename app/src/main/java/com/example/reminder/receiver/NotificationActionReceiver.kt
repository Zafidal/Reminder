package com.example.reminder.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        if (id == -1L) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id.toInt())

        when (intent.action) {
            ACTION_DONE -> {
                // Dismiss notification
            }
            ACTION_SNOOZE -> {
                // Snooze for 15 minutes
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra(ReminderReceiver.EXTRA_REMINDER_ID, id)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (id * 10 + 9).toInt(),
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val triggerAt = System.currentTimeMillis() + 15 * 60 * 1000 // 15 mins
                val showIntent = PendingIntent.getActivity(
                    context,
                    (id * 10 + 9).toInt(),
                    Intent(context, com.example.reminder.MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                try {
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(triggerAt, showIntent),
                        pendingIntent
                    )
                } catch (_: Exception) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            }
        }
    }

    companion object {
        const val ACTION_DONE = "com.example.reminder.ACTION_DONE"
        const val ACTION_SNOOZE = "com.example.reminder.ACTION_SNOOZE"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESC = "extra_desc"
    }
}
