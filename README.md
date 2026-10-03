# 🎯 Reminder (Напоминания)

Современное Android-приложение для управления задачами, привычками и тренировками с гибкими настройками напоминаний и встроенным **режимом будильника**.

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room%20DB-4285F4?style=for-the-badge&logo=sqlite&logoColor=white)

---

## ✨ Основные возможности

- ⏰ **Режим будильника (Alarm Mode):** Переключайте режим напоминания в громкий будильник с рингтоном, непрерывной вибрацией и полноэкранным экраном (`AlarmActivity`), отображаемым даже поверх экрана блокировки.
- 🏋️‍♂️ **Категории и Эмодзи:** Удобное разделение задач по категориям (*"Тренировка"*, *"Задача"*, *"Здоровье"* и пользовательские категории) с интерактивной фильтрацией.
- 📅 **Гибкий повтор:** Настройка срабатывания по дням недели (каждый день, только по будням, выходным или определенным дням).
- ⚡ **Быстрые шаблоны:** Популярные готовые пресеты для утренней зарядки, силовой тренировки, питья воды, чтения и подъёма.
- 🔔 **Уведомления с действиями:** Кнопки прямо в уведомлении — *"Выполнено"* и *"Отложить на 10/15 минут"*.
- 🔄 **Автозапуск при перезагрузке:** Будильники и напоминания автоматически восстанавливаются после перезагрузки устройства (`BOOT_COMPLETED`).
- 💾 **Надежное хранение данных:** Интеграция с Room Database с поддержкой бесшовных миграций схемы (v1 ➔ v2 ➔ v3) без потери сохранённых данных.

---

## 🛠 Технологический стек

- **Язык:** Kotlin
- **UI:** Jetpack Compose, Material Design 3
- **Архитектура:** MVVM (Model-View-ViewModel), Clean UI State (StateFlow, Coroutines)
- **База данных:** Room Persistence Library
- **Фоновая работа:** `AlarmManager`, `BroadcastReceiver` (`ReminderReceiver`, `BootReceiver`), `NotificationCompat`
- **Сборка:** Gradle (Kotlin DSL), Version Catalogs (`libs.versions.toml`)

---

## 🚀 Сборка и запуск

### Требования
- Android Studio Ladybug (2024.2.1+) или новее
- JDK 17+
- Android SDK 26+ (Android 8.0 Oreo)

### Шаги
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/Zafidal/Reminder.git
   ```
2. Откройте проект в **Android Studio**.
3. Дождитесь завершения синхронизации Gradle.
4. Запустите приложение на эмуляторе или физическом устройстве (`Shift + F10`).

---

## 📄 Лицензия

Проект распространяется под лицензией MIT.
