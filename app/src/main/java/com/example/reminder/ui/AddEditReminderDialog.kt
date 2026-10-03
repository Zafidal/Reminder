package com.example.reminder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reminder.data.Category
import com.example.reminder.data.Reminder
import java.util.Locale

data class PresetTemplate(
    val emoji: String,
    val title: String,
    val categoryName: String,
    val hour: Int,
    val minute: Int,
    val days: Set<Int>,
    val isAlarm: Boolean = false
)

val PRESET_TEMPLATES = listOf(
    PresetTemplate("⏰", "Подъём", "Другое", 7, 0, setOf(1, 2, 3, 4, 5, 6, 7), isAlarm = true),
    PresetTemplate("🏋️‍♂️", "Утренняя зарядка", "Тренировка", 7, 30, setOf(1, 2, 3, 4, 5, 6, 7)),
    PresetTemplate("💪", "Силовая тренировка", "Тренировка", 18, 0, setOf(1, 3, 5)),
    PresetTemplate("💧", "Выпить стакан воды", "Здоровье", 10, 0, setOf(1, 2, 3, 4, 5, 6, 7)),
    PresetTemplate("🚶‍♂️", "Вечерняя прогулка", "Здоровье", 20, 0, setOf(1, 2, 3, 4, 5, 6, 7)),
    PresetTemplate("📋", "Ежедневный план", "Задача", 9, 0, setOf(1, 2, 3, 4, 5)),
    PresetTemplate("📖", "Чтение 20 минут", "Другое", 21, 30, setOf(1, 2, 3, 4, 5, 6, 7))
)

val DAYS_MAP = mapOf(
    1 to "Пн",
    2 to "Вт",
    3 to "Ср",
    4 to "Чт",
    5 to "Пт",
    6 to "Сб",
    7 to "Вс"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReminderDialog(
    reminderToEdit: Reminder? = null,
    categories: List<Category>,
    onAddCategoryClick: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, category: Category, hour: Int, minute: Int, repeatDays: Set<Int>, isAlarm: Boolean) -> Unit
) {
    val defaultCategory = categories.firstOrNull { it.id == reminderToEdit?.categoryId }
        ?: categories.firstOrNull()
        ?: Category(id = 1L, name = "Тренировка", emoji = "🏋️‍♂️", isDefault = true)

    var title by remember { mutableStateOf(reminderToEdit?.title ?: "") }
    var description by remember { mutableStateOf(reminderToEdit?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(defaultCategory) }
    var hour by remember { mutableIntStateOf(reminderToEdit?.hour ?: 8) }
    var minute by remember { mutableIntStateOf(reminderToEdit?.minute ?: 0) }
    var selectedDays by remember { mutableStateOf(reminderToEdit?.repeatDays ?: setOf(1, 2, 3, 4, 5, 6, 7)) }
    var isAlarm by remember { mutableStateOf(reminderToEdit?.isAlarm ?: false) }

    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), description.trim(), selectedCategory, hour, minute, selectedDays, isAlarm)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        title = {
            Text(
                text = if (reminderToEdit == null) "Новое напоминание" else "Редактировать",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Presets (only when adding new)
                if (reminderToEdit == null) {
                    Text("Быстрые шаблоны:", style = MaterialTheme.typography.labelLarge)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(PRESET_TEMPLATES) { preset ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    title = preset.title
                                    hour = preset.hour
                                    minute = preset.minute
                                    selectedDays = preset.days
                                    isAlarm = preset.isAlarm
                                    categories.firstOrNull { it.name.equals(preset.categoryName, ignoreCase = true) }?.let {
                                        selectedCategory = it
                                    }
                                },
                                label = { Text("${preset.emoji} ${preset.title}") }
                            )
                        }
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название *") },
                    placeholder = { Text("например: Отжимания и планка") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Alarm Mode Toggle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAlarm)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Режим будильника ⏰",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isAlarm)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Громкий сигнал и окно будильника",
                                fontSize = 12.sp,
                                color = if (isAlarm)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = isAlarm,
                            onCheckedChange = { isAlarm = it }
                        )
                    }
                }

                // Category selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Категория:", style = MaterialTheme.typography.labelLarge)
                    TextButton(onClick = onAddCategoryClick) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Своя категория", fontSize = 12.sp)
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory.id == category.id,
                            onClick = { selectedCategory = category },
                            label = { Text("${category.emoji} ${category.name}") }
                        )
                    }
                }

                // Time picker button
                Text("Время напоминания:", style = MaterialTheme.typography.labelLarge)
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Repeat days
                Text("Дни недели:", style = MaterialTheme.typography.labelLarge)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(DAYS_MAP.entries.toList()) { (dayIndex, dayLabel) ->
                        val isSelected = selectedDays.contains(dayIndex)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDays = if (isSelected) {
                                    selectedDays - dayIndex
                                } else {
                                    selectedDays + dayIndex
                                }
                            },
                            label = {
                                Text(
                                    text = dayLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }
                }

                // Quick Day Selection Buttons
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SuggestionChip(
                            onClick = { selectedDays = setOf(1, 2, 3, 4, 5, 6, 7) },
                            label = { Text("Каждый день") }
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { selectedDays = setOf(1, 2, 3, 4, 5) },
                            label = { Text("Будни") }
                        )
                    }
                    item {
                        SuggestionChip(
                            onClick = { selectedDays = setOf(6, 7) },
                            label = { Text("Выходные") }
                        )
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Заметки (необязательно)") },
                    placeholder = { Text("3 подхода по 15 раз") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                Button(onClick = {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                    showTimePicker = false
                }) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Отмена")
                }
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}
