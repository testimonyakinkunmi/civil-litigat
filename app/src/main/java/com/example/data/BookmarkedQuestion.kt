package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_questions")
data class BookmarkedQuestion(
    @PrimaryKey val scenario: String, // scenarios are unique strings in the prompt's material
    val weekName: String,
    val optionsJson: String, // comma separated or serialized
    val correctIndex: Int,
    val verbatimCorrection: String,
    val timestamp: Long = System.currentTimeMillis()
)
