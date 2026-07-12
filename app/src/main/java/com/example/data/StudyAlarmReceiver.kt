package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.json.JSONArray
import org.json.JSONObject

class StudyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val id = intent.getStringExtra("id") ?: ""
        val title = intent.getStringExtra("title") ?: "Law S Help Reminder"
        val text = intent.getStringExtra("text") ?: ""

        if (id.isEmpty()) return

        val manager = NotificationSystemManager(context)
        if (!manager.isNotificationsEnabled) return

        // 1. Post to system notification bar
        manager.postSystemNotification(title, text, id.hashCode())

        // 2. Mark as delivered in scheduled list and add to history list
        val sharedPrefs = context.getSharedPreferences("study_notifications_prefs", Context.MODE_PRIVATE)
        val scheduledJson = sharedPrefs.getString(NotificationSystemManager.PREF_SCHEDULED, "[]") ?: "[]"
        val historyJson = sharedPrefs.getString(NotificationSystemManager.PREF_HISTORY, "[]") ?: "[]"

        val scheduledList = mutableListOf<StudyNotification>()
        var foundNotif: StudyNotification? = null

        try {
            val schedArr = JSONArray(scheduledJson)
            for (i in 0 until schedArr.length()) {
                val obj = schedArr.getJSONObject(i)
                val notif = StudyNotification(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    text = obj.getString("text"),
                    topicName = obj.optString("topicName", ""),
                    timestamp = obj.getLong("timestamp"),
                    delivered = obj.getBoolean("delivered")
                )
                if (notif.id == id) {
                    foundNotif = notif.copy(delivered = true)
                } else {
                    scheduledList.add(notif)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val historyList = mutableListOf<StudyNotification>()
        try {
            val histArr = JSONArray(historyJson)
            for (i in 0 until histArr.length()) {
                val obj = histArr.getJSONObject(i)
                historyList.add(
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

        // If found, add to history list
        if (foundNotif != null) {
            historyList.add(0, foundNotif)
        } else {
            // Fallback if not found in scheduled, create it
            historyList.add(0, StudyNotification(
                id = id,
                title = title,
                text = text,
                topicName = "Scheduled Study",
                timestamp = System.currentTimeMillis(),
                delivered = true
            ))
        }

        // Save back
        val newSchedArr = JSONArray()
        for (item in scheduledList) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("text", item.text)
                put("topicName", item.topicName)
                put("timestamp", item.timestamp)
                put("delivered", item.delivered)
            }
            newSchedArr.put(obj)
        }

        val newHistArr = JSONArray()
        for (item in historyList) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("text", item.text)
                put("topicName", item.topicName)
                put("timestamp", item.timestamp)
                put("delivered", item.delivered)
            }
            newHistArr.put(obj)
        }

        sharedPrefs.edit()
            .putString(NotificationSystemManager.PREF_SCHEDULED, newSchedArr.toString())
            .putString(NotificationSystemManager.PREF_HISTORY, newHistArr.toString())
            .apply()

        // Send broadcase refresh so that the active UI is updated
        context.sendBroadcast(Intent(NotificationSystemManager.ACTION_REFRESH_UI))
    }
}
