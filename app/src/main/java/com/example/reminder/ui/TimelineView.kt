package com.example.reminder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reminder.data.Reminder
import java.util.Calendar
import java.util.Locale

val FULL_DAYS_MAP = listOf(
    1 to "Понедельник",
    2 to "Вторник",
    3 to "Среда",
    4 to "Четверг",
    5 to "Пятница",
    6 to "Суббота",
    7 to "Воскресенье"
)

val SHORT_DAYS_MAP = mapOf(
    1 to "Пн",
    2 to "Вт",
    3 to "Ср",
    4 to "Чт",
    5 to "Пт",
    6 to "Сб",
    7 to "Вс"
)

@Composable
fun TimelineView(
    reminders: List<Reminder>,
    onToggle: (Reminder) -> Unit,
    onEdit: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = remember { Calendar.getInstance() }
    val calDay = now.get(Calendar.DAY_OF_WEEK)
    val todayDayIndex = if (calDay == Calendar.SUNDAY) 7 else calDay - 1
    val currentHour = now.get(Calendar.HOUR_OF_DAY)
    val currentMinute = now.get(Calendar.MINUTE)

    var selectedDayIndex by remember { mutableIntStateOf(todayDayIndex) }

    // Filter reminders active on the selected day
    val dayReminders = remember(reminders, selectedDayIndex) {
        reminders.filter { reminder ->
            reminder.repeatDays.isEmpty() || reminder.repeatDays.contains(selectedDayIndex)
        }
    }

    // Group reminders by hour
    val remindersByHour = remember(dayReminders) {
        dayReminders.groupBy { it.hour }
    }

    val listState = rememberLazyListState()

    // Scroll to current hour on initial load
    LaunchedEffect(selectedDayIndex) {
        val targetScrollHour = if (remindersByHour.containsKey(currentHour)) {
            currentHour
        } else {
            remindersByHour.keys.minOrNull() ?: currentHour
        }
        listState.animateScrollToItem(targetScrollHour.coerceIn(0, 23))
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Week Days Header (Pill / Tabs)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FULL_DAYS_MAP.firstOrNull { it.first == selectedDayIndex }?.second ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (selectedDayIndex == todayDayIndex) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Сегодня",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    items(FULL_DAYS_MAP) { (dayIdx, _) ->
                        val isSelected = selectedDayIndex == dayIdx
                        val isToday = todayDayIndex == dayIdx
                        val count = reminders.count { r ->
                            r.isEnabled && (r.repeatDays.isEmpty() || r.repeatDays.contains(dayIdx))
                        }

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { selectedDayIndex = dayIdx }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = SHORT_DAYS_MAP[dayIdx] ?: "",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
                                        else if (count > 0)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else
                                            Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else if (count > 0)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }

        // Timeline 24-hour vertical grid
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items((0..23).toList(), key = { it }) { hour ->
                val hourReminders = remindersByHour[hour] ?: emptyList()
                val isCurrentHour = (selectedDayIndex == todayDayIndex) && (currentHour == hour)

                TimelineHourRow(
                    hour = hour,
                    isCurrentHour = isCurrentHour,
                    currentMinute = currentMinute,
                    reminders = hourReminders,
                    onToggle = onToggle,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
fun TimelineHourRow(
    hour: Int,
    isCurrentHour: Boolean,
    currentMinute: Int,
    reminders: List<Reminder>,
    onToggle: (Reminder) -> Unit,
    onEdit: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    val timeLabel = String.format(Locale.getDefault(), "%02d:00", hour)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Time Label Column
        Column(
            modifier = Modifier
                .width(52.dp)
                .padding(top = 2.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = timeLabel,
                fontSize = 12.sp,
                fontWeight = if (isCurrentHour) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrentHour)
                    Color(0xFFFF3D00)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Vertical Line & Content Column
        Column(modifier = Modifier.weight(1f)) {
            // Top divider or Current Time marker
            if (isCurrentHour) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF3D00))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "Сейчас (%02d:%02d)", hour, currentMinute),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF3D00)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Divider(
                        color = Color(0xFFFF3D00),
                        thickness = 2.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )
            }

            // Reminders in this hour slot
            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .padding(vertical = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "—",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    reminders.forEach { reminder ->
                        TimelineReminderCard(
                            reminder = reminder,
                            onToggle = { onToggle(reminder) },
                            onEdit = { onEdit(reminder) },
                            onDelete = { onDelete(reminder) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineReminderCard(
    reminder: Reminder,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", reminder.hour, reminder.minute)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (reminder.isAlarm)
                    Modifier.border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                else
                    Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reminder.isEnabled)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Emoji
            Text(
                text = reminder.categoryEmoji,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedTime,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reminder.isEnabled)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )

                    if (reminder.isAlarm) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFF5252).copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = Color(0xFFFF5252)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Будильник",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5252)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = reminder.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (reminder.isEnabled)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.scale(0.8f)
                )
            }
        }
    }
}
