package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<QuizAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttempt)

    @Query("DELETE FROM quiz_attempts")
    suspend fun clearAllAttempts()

    // Streak Operations
    @Query("SELECT * FROM user_streak WHERE id = 1 LIMIT 1")
    suspend fun getUserStreak(): UserStreak?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: UserStreak)

    // Bookmarks Operations
    @Query("SELECT * FROM bookmarked_questions ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkedQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkedQuestion)

    @Query("DELETE FROM bookmarked_questions WHERE scenario = :scenario")
    suspend fun removeBookmarkByScenario(scenario: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_questions WHERE scenario = :scenario)")
    suspend fun isBookmarked(scenario: String): Boolean
}
