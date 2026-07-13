package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passive_notifications")
data class PassiveNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scenario: String,
    val topicName: String,
    val ruleFact: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
