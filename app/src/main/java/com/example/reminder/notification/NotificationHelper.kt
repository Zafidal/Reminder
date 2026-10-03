package com.example.reminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.reminder.MainActivity
import com.example.reminder.R
import com.example.reminder.receiver.NotificationActionReceiver
import com.example.reminder.ui.AlarmActivity

object NotificationHelper {
    const val CHANNEL_ID = "reminders_channel"
    private const val CHANNEL_NAME = "Напоминания"
    private const val CHANNEL_DESC = "Канал для обычных напоминаний"

    const val ALARM_CHANNEL_ID = "alarm_channel"
    private const val ALARM_CHANNEL_NAME = "Будильники"
    private const val ALARM_CHANNEL_DESC = "Канал для громких будильников"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Regular reminder channel
            val reminderChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(reminderChannel)

            // Alarm channel
            val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val alarmAudioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                ALARM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = ALARM_CHANNEL_DESC
                enableVibration(true)
                setSound(alarmSoundUri, alarmAudioAttributes)
            }
            notificationManager.createNotificationChannel(alarmChannel)
        }
    }

    fun showNotification(
        context: Context,
        id: Long,
        title: String,
        description: String,
        categoryEmoji: String = "🔔"
    ) {
        createNotificationChannel(context)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            id.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val doneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_DONE
            putExtra(NotificationActionReceiver.EXTRA_REMINDER_ID, id)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            (id * 10 + 1).toInt(),
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
            putExtra(NotificationActionReceiver.EXTRA_REMINDER_ID, id)
            putExtra(NotificationActionReceiver.EXTRA_TITLE, title)
            putExtra(NotificationActionReceiver.EXTRA_DESC, description)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (id * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullTitle = "$categoryEmoji $title"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(fullTitle)
            .setContentText(description.ifBlank { "Время выполнить задачу!" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(0, "Выполнено", donePendingIntent)
            .addAction(0, "Отложить 15 мин", snoozePendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id.toInt(), builder.build())
    }

    fun showAlarmNotification(
        context: Context,
        id: Long,
        title: String,
        description: String,
        categoryEmoji: String = "⏰"
    ) {
        createNotificationChannel(context)

        // Intent for full-screen AlarmActivity
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmActivity.EXTRA_REMINDER_ID, id)
            putExtra(AlarmActivity.EXTRA_TITLE, title)
            putExtra(AlarmActivity.EXTRA_DESC, description)
            putExtra(AlarmActivity.EXTRA_EMOJI, categoryEmoji)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val fullTitle = "⏰ $categoryEmoji $title"

        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(fullTitle)
            .setContentText(description.ifBlank { "Сработал будильник!" })
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(alarmUri)
            .setVibrate(longArrayOf(0, 500, 500))
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id.toInt(), builder.build())

        // Also launch AlarmActivity directly if app is in foreground / background
        try {
            context.startActivity(alarmIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showTestNotification(context: Context) {
        showNotification(
            context = context,
            id = 99999,
            title = "Тестовое напоминание",
            description = "Уведомления работают отлично! Готовы к тренировкам.",
            categoryEmoji = "🏋️‍♂️"
        )
    }
}
