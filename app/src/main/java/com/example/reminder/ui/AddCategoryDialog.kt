package com.example.reminder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val SUGGESTED_EMOJIS = listOf(
    "🏋️‍♂️", "📋", "💧", "📚", "💼", "🧘", "🐕", "🚗",
    "🎨", "🎮", "⚽", "🍎", "💊", "🧹", "🛒", "💡",
    "🎯", "🎵", "✈️", "💰", "☕", "🚴‍♂️", "🏃‍♂️", "🏠"
)

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, emoji: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🎯") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), selectedEmoji)
                    }
                },
                enabled = name.isNotBlank()
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
                text = "Новая категория",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название категории *") },
                    placeholder = { Text("например: Учеба, Работа, Медитация") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Выбранный значок:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = selectedEmoji,
                        fontSize = 24.sp
                    )
                }

                OutlinedTextField(
                    value = selectedEmoji,
                    onValueChange = { if (it.length <= 4) selectedEmoji = it },
                    label = { Text("Ваш эмодзи") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Выберите эмодзи:",
                    style = MaterialTheme.typography.labelLarge
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(SUGGESTED_EMOJIS) { emoji ->
                        FilterChip(
                            selected = selectedEmoji == emoji,
                            onClick = { selectedEmoji = emoji },
                            label = {
                                Text(
                                    text = emoji,
                                    fontSize = 18.sp
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}
