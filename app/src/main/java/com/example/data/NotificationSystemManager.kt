package com.example.data

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import java.util.Random

data class StudyNotification(
    val id: String,
    val title: String,
    val text: String,
    val topicName: String,
    val timestamp: Long,
    val delivered: Boolean
)

class NotificationSystemManager(private val context: Context) {
    private val sharedPrefs = context.getSharedPreferences("study_notifications_prefs", Context.MODE_PRIVATE)
    private val random = Random()

    companion object {
        const val CHANNEL_ID = "study_reminders"
        const val CHANNEL_NAME = "Study Reminders & Weak-Spots"
        const val PREF_SCHEDULED = "scheduled_notifications_json"
        const val PREF_HISTORY = "delivered_notifications_history_json"
        const val PREF_LAST_TEXT = "last_notification_text"
        const val PREF_DAILY_LIMIT = "notification_daily_limit"
        const val PREF_ENABLED = "notifications_enabled"
        const val ACTION_REFRESH_UI = "com.example.NOTIFICATION_REFRESH_UI"
    }

    init {
        createNotificationChannel()
    }

    var isNotificationsEnabled: Boolean
        get() = sharedPrefs.getBoolean(PREF_ENABLED, true)
        set(value) = sharedPrefs.edit().putBoolean(PREF_ENABLED, value).apply()

    var dailyLimit: Int
        get() = sharedPrefs.getInt(PREF_DAILY_LIMIT, 4)
        set(value) = sharedPrefs.edit().putInt(PREF_DAILY_LIMIT, value).apply()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Delivers randomized daily legal facts targeted to your weak spots."
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Load static quiz database using the repository
    private fun loadAllQuizzes(): List<VerbatimQuiz> {
        val database = AppDatabase.getDatabase(context)
        val repository = QuizRepository(context, database.quizDao())
        val topics = repository.loadVerbatimTopics()
        return topics.flatMap { topic ->
            topic.quizzes.map { q ->
                q.copy(weekName = topic.topicName)
            }
        }
    }

    // Generate a single randomized notification content based on weak-spots (if any), avoiding back-to-back repetition
    suspend fun generateRandomNotificationFact(): Pair<String, String> {
        val database = AppDatabase.getDatabase(context)
        val quizDao = database.quizDao()
        val allAttempts = quizDao.getAllAttempts().first()
        val incorrectAttempts = allAttempts.filter { it.selectedIndex != it.correctIndex }

        val allQuizzes = loadAllQuizzes()
        val lastText = sharedPrefs.getString(PREF_LAST_TEXT, "") ?: ""

        var selectedQuiz: VerbatimQuiz? = null
        
        if (incorrectAttempts.isNotEmpty() && allQuizzes.isNotEmpty()) {
            // Pick from incorrect questions
            val attemptsShuffled = incorrectAttempts.shuffled()
            for (attempt in attemptsShuffled) {
                val match = allQuizzes.find { 
                    it.scenario.equals(attempt.scenario, ignoreCase = true) 
                }
                if (match != null) {
                    val formatted = formatFactText(match)
                    if (formatted != lastText) {
                        selectedQuiz = match
                        break
                    }
                }
            }
        }

        // Fallback to completely random if no incorrect match found or none exist
        if (selectedQuiz == null && allQuizzes.isNotEmpty()) {
            val quizzesShuffled = allQuizzes.shuffled()
            for (quiz in quizzesShuffled) {
                val formatted = formatFactText(quiz)
                if (formatted != lastText) {
                    selectedQuiz = quiz
                    break
                }
            }
        }

        // Hardcoded default fallback in case there are absolutely no quizzes
        if (selectedQuiz == null) {
            val defaultTitle = "Law S Help Reminder"
            val defaultText = "In a Criminal Appeal, the standard of proof rests entirely on the prosecution."
            return Pair(defaultTitle, defaultText)
        }

        val isWeakSpot = incorrectAttempts.any { it.scenario.equals(selectedQuiz!!.scenario, ignoreCase = true) }
        val title = if (isWeakSpot) {
            "🎯 Law S Help Weak-Spot Focus"
        } else {
            "💡 Law S Help Daily Fact"
        }

        val text = formatFactText(selectedQuiz)
        
        // Save as last text to prevent back-to-back repetition
        sharedPrefs.edit().putString(PREF_LAST_TEXT, text).apply()

        return Pair(title, text)
    }

    private fun formatFactText(quiz: VerbatimQuiz): String {
        val topicCleaned = formatWeekTitle(quiz.weekName)
        val correction = quiz.verbatimCorrection.trim()
        
        // Ensure the direct answer fact starts elegantly and doesn't sound like a question
        val prefix = when {
            topicCleaned.lowercase().contains("criminal") -> "Criminal Litigation Focus"
            topicCleaned.lowercase().contains("civil") -> "Civil Litigation Rule"
            topicCleaned.lowercase().contains("corporate") -> "Corporate Practice Rule"
            topicCleaned.lowercase().contains("property") -> "Property Law Fact"
            topicCleaned.lowercase().contains("ethics") -> "Professional Ethics Rule"
            else -> "Study Focus"
        }

        return "For $topicCleaned, remember: $correction"
    }

    // Schedule 4 random notifications for the day
    fun scheduleDailyNotifications() {
        if (!isNotificationsEnabled) return

        val limit = dailyLimit
        val notifications = mutableListOf<StudyNotification>()

        // Divide the active hours (8:00 AM to 10:00 PM) into equal intervals
        val startHour = 8
        val endHour = 22
        val totalHours = endHour - startHour
        val intervalHours = totalHours.toFloat() / limit

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        CoroutineScope(Dispatchers.IO).launch {
            for (i in 0 until limit) {
                val minHour = startHour + (i * intervalHours).toInt()
                val maxHour = startHour + ((i + 1) * intervalHours).toInt()

                val randomHour = if (maxHour > minHour) random.nextInt(maxHour - minHour) + minHour else minHour
                val randomMinute = random.nextInt(60)

                val scheduleCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, randomHour)
                    set(Calendar.MINUTE, randomMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // If scheduled time has already passed for today, schedule for tomorrow
                if (scheduleCalendar.timeInMillis < System.currentTimeMillis()) {
                    scheduleCalendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                val (title, text) = generateRandomNotificationFact()

                val notification = StudyNotification(
                    id = "study_notif_${scheduleCalendar.timeInMillis}_$i",
                    title = title,
                    text = text,
                    topicName = "Scheduled Study",
                    timestamp = scheduleCalendar.timeInMillis,
                    delivered = false
                )
                notifications.add(notification)

                // Schedule alarm using AlarmManager
                setAlarm(notification)
            }

            saveScheduledNotifications(notifications)
        }
    }

    private fun setAlarm(notification: StudyNotification) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StudyAlarmReceiver::class.java).apply {
            putExtra("id", notification.id)
            putExtra("title", notification.title)
            putExtra("text", notification.text)
        }

        // Use flag IMMUTABLE and UPDATE_CURRENT
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notification.timestamp,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notification.timestamp,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for newer Android alarm rules
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                notification.timestamp,
                pendingIntent
            )
        }
    }

    // Trigger an immediate custom notification for user interaction/simulation
    fun simulateNotificationImmediate(onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            val (title, text) = generateRandomNotificationFact()
            val id = "simulated_${System.currentTimeMillis()}"
            val notification = StudyNotification(
                id = id,
                title = title,
                text = text,
                topicName = "Quick Sim",
                timestamp = System.currentTimeMillis(),
                delivered = true
            )

            // Post System Notification
            postSystemNotification(notification.title, notification.text, id.hashCode())

            // Save to Delivered History
            val history = getDeliveredHistory().toMutableList()
            history.add(0, notification) // Insert at top
            saveDeliveredHistory(history)

            // Send local broadcast to update any active UI
            context.sendBroadcast(Intent(ACTION_REFRESH_UI))

            onComplete()
        }
    }

    fun postSystemNotification(title: String, text: String, notificationId: Int) {
        // Build intent to open app when clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Standard info icon
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Handle lacking permissions
        }
    }

    // Helper to check and deliver any passed scheduled notifications when app opens
    fun checkAndDeliverMissedScheduledNotifications() {
        val scheduled = getScheduledNotifications().toMutableList()
        val now = System.currentTimeMillis()
        var changed = false

        val iterator = scheduled.iterator()
        val history = getDeliveredHistory().toMutableList()

        while (iterator.hasNext()) {
            val notif = iterator.next()
            if (notif.timestamp <= now) {
                // Should have been delivered, post it now and move to history
                postSystemNotification(notif.title, notif.text, notif.id.hashCode())
                history.add(0, notif.copy(delivered = true))
                iterator.remove()
                changed = true
            }
        }

        if (changed) {
            saveScheduledNotifications(scheduled)
            saveDeliveredHistory(history)
            context.sendBroadcast(Intent(ACTION_REFRESH_UI))
        }

        // If no scheduled notifications remain for today, let's schedule them!
        if (getScheduledNotifications().isEmpty()) {
            scheduleDailyNotifications()
        }
    }

    // SharedPreferences lists serialization helpers
    fun getScheduledNotifications(): List<StudyNotification> {
        val jsonStr = sharedPrefs.getString(PREF_SCHEDULED, "[]") ?: "[]"
        return deserializeList(jsonStr)
    }

    private fun saveScheduledNotifications(list: List<StudyNotification>) {
        val jsonStr = serializeList(list)
        sharedPrefs.edit().putString(PREF_SCHEDULED, jsonStr).apply()
    }

    fun getDeliveredHistory(): List<StudyNotification> {
        val jsonStr = sharedPrefs.getString(PREF_HISTORY, "[]") ?: "[]"
        return deserializeList(jsonStr)
    }

    fun clearHistory() {
        sharedPrefs.edit().putString(PREF_HISTORY, "[]").apply()
        context.sendBroadcast(Intent(ACTION_REFRESH_UI))
    }

    private fun saveDeliveredHistory(list: List<StudyNotification>) {
        val jsonStr = serializeList(list)
        sharedPrefs.edit().putString(PREF_HISTORY, jsonStr).apply()
    }

    private fun serializeList(list: List<StudyNotification>): String {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("text", item.text)
                put("topicName", item.topicName)
                put("timestamp", item.timestamp)
                put("delivered", item.delivered)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun deserializeList(jsonStr: String): List<StudyNotification> {
        val list = mutableListOf<StudyNotification>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    StudyNotification(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        text = obj.getString("text"),
                        topicName = obj.optString("topicName", ""),
                        timestamp = obj.getLong("timestamp"),
                        delivered = obj.getBoolean("delivered")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
