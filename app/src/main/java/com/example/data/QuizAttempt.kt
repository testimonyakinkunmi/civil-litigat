package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekName: String, // e.g., "WEEK 3", "WEEK 4"
    val scenario: String,
    val selectedIndex: Int,
    val correctIndex: Int,
    val timestamp: Long = System.currentTimeMillis()
)
