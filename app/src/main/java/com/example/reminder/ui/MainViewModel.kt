package com.example.reminder.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminder.data.Category
import com.example.reminder.data.Reminder
import com.example.reminder.data.ReminderDatabase
import com.example.reminder.data.ReminderRepository
import com.example.reminder.notification.AlarmScheduler
import com.example.reminder.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository

    val selectedCategory = MutableStateFlow<Category?>(null)
    val isAddDialogOpen = MutableStateFlow(false)
    val isAddCategoryDialogOpen = MutableStateFlow(false)
    val isManageCategoriesDialogOpen = MutableStateFlow(false)
    val editingReminder = MutableStateFlow<Reminder?>(null)

    val categories: StateFlow<List<Category>>
    val reminders: StateFlow<List<Reminder>>

    init {
        val db = ReminderDatabase.getDatabase(application)
        repository = ReminderRepository(db.reminderDao(), db.categoryDao())

        categories = repository.allCategories.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        reminders = combine(repository.allReminders, selectedCategory) { list, category ->
            if (category == null) list else list.filter { it.categoryId == category.id }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun setCategoryFilter(category: Category?) {
        selectedCategory.value = category
    }

    fun openAddEditDialog(reminder: Reminder? = null) {
        editingReminder.value = reminder
        isAddDialogOpen.value = true
    }

    fun closeAddEditDialog() {
        isAddDialogOpen.value = false
        editingReminder.value = null
    }

    fun openAddCategoryDialog() {
        isAddCategoryDialogOpen.value = true
    }

    fun closeAddCategoryDialog() {
        isAddCategoryDialogOpen.value = false
    }

    fun openManageCategoriesDialog() {
        isManageCategoriesDialogOpen.value = true
    }

    fun closeManageCategoriesDialog() {
        isManageCategoriesDialogOpen.value = false
    }

    fun addCategory(name: String, emoji: String) {
        viewModelScope.launch {
            val newCategory = Category(name = name, emoji = emoji, isDefault = false)
            repository.insertCategory(newCategory)
            closeAddCategoryDialog()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            if (selectedCategory.value?.id == category.id) {
                selectedCategory.value = null
            }
            repository.deleteCategory(category)
        }
    }

    fun toggleReminder(context: Context, reminder: Reminder) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            repository.updateReminder(updated)
            if (updated.isEnabled) {
                AlarmScheduler.schedule(context, updated)
            } else {
                AlarmScheduler.cancel(context, updated)
            }
        }
    }

    fun saveReminder(
        context: Context,
        title: String,
        description: String,
        category: Category,
        hour: Int,
        minute: Int,
        repeatDays: Set<Int>,
        isAlarm: Boolean = false
    ) {
        viewModelScope.launch {
            val currentEditing = editingReminder.value
            if (currentEditing != null) {
                val updated = currentEditing.copy(
                    title = title,
                    description = description,
                    categoryId = category.id,
                    categoryName = category.name,
                    categoryEmoji = category.emoji,
                    hour = hour,
                    minute = minute,
                    repeatDays = repeatDays,
                    isAlarm = isAlarm,
                    isEnabled = true
                )
                repository.updateReminder(updated)
                AlarmScheduler.schedule(context, updated)
            } else {
                val newReminder = Reminder(
                    title = title,
                    description = description,
                    categoryId = category.id,
                    categoryName = category.name,
                    categoryEmoji = category.emoji,
                    hour = hour,
                    minute = minute,
                    repeatDays = repeatDays,
                    isAlarm = isAlarm,
                    isEnabled = true
                )
                val newId = repository.insertReminder(newReminder)
                val savedReminder = newReminder.copy(id = newId)
                AlarmScheduler.schedule(context, savedReminder)
            }
            closeAddEditDialog()
        }
    }

    fun deleteReminder(context: Context, reminder: Reminder) {
        viewModelScope.launch {
            AlarmScheduler.cancel(context, reminder)
            repository.deleteReminder(reminder)
        }
    }

    fun sendTestNotification(context: Context) {
        NotificationHelper.showTestNotification(context)
    }
}
