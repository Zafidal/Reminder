package com.example.reminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminder.data.ReminderDatabase
import com.example.reminder.notification.AlarmScheduler
import com.example.reminder.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = ReminderDatabase.getDatabase(context).reminderDao()
                val reminder = dao.getReminderById(reminderId)

                if (reminder != null && reminder.isEnabled) {
                    if (reminder.isAlarm) {
                        NotificationHelper.showAlarmNotification(
                            context = context,
                            id = reminder.id,
                            title = reminder.title,
                            description = reminder.description,
                            categoryEmoji = reminder.categoryEmoji
                        )
                    } else {
                        NotificationHelper.showNotification(
                            context = context,
                            id = reminder.id,
                            title = reminder.title,
                            description = reminder.description,
                            categoryEmoji = reminder.categoryEmoji
                        )
                    }

                    // Schedule next repeat alarm
                    AlarmScheduler.schedule(context, reminder)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }
}
