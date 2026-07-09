package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class VerbatimQuiz(
    val scenario: String,
    val options: List<String>,
    val correctIndex: Int,
    val verbatimCorrection: String,
    val weekName: String = "",
    val subtopicName: String = ""
)

data class TopicBundle(
    val topicName: String,
    val quizzes: List<VerbatimQuiz>
)

class QuizRepository(
    private val context: Context,
    private val quizDao: QuizDao
) {
    val allAttempts: Flow<List<QuizAttempt>> = quizDao.getAllAttempts()
    val allBookmarks: Flow<List<BookmarkedQuestion>> = quizDao.getAllBookmarks()

    // Load static quiz questions from local assets
    fun loadVerbatimTopics(): List<TopicBundle> {
        val topics = mutableListOf<TopicBundle>()
        try {
            val inputStream = context.assets.open("jsondata.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonStr = reader.use { it.readText() }
            val root = JSONObject(jsonStr)
            val topicsArr = root.getJSONArray("topics")

            for (i in 0 until topicsArr.length()) {
                val topicObj = topicsArr.getJSONObject(i)
                val topicName = topicObj.getString("topicName")
                val subtopicsArr = topicObj.getJSONArray("subtopics")
                val quizList = mutableListOf<VerbatimQuiz>()

                for (j in 0 until subtopicsArr.length()) {
                    val subtopicObj = subtopicsArr.getJSONObject(j)
                    val subtopicName = subtopicObj.getString("subtopicName")
                    val quizzesArr = subtopicObj.getJSONArray("quizzes")

                    for (k in 0 until quizzesArr.length()) {
                        val quizObj = quizzesArr.getJSONObject(k)
                        val scenario = quizObj.getString("scenario")
                        val optionsArr = quizObj.getJSONArray("options")
                        val options = mutableListOf<String>()
                        for (o in 0 until optionsArr.length()) {
                            options.add(optionsArr.getString(o))
                        }
                        val correctIndex = quizObj.getInt("correctIndex")
                        val verbatimCorrection = quizObj.optString("verbatimCorrection", "")

                        quizList.add(
                            VerbatimQuiz(
                                scenario = scenario,
                                options = options,
                                correctIndex = correctIndex,
                                verbatimCorrection = verbatimCorrection,
                                weekName = topicName,
                                subtopicName = subtopicName
                            )
                        )
                    }
                }
                topics.add(TopicBundle(topicName, quizList))
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error parsing jsondata.json: ${e.message}", e)
        }
        return topics
    }

    suspend fun saveAttempt(weekName: String, scenario: String, selectedIndex: Int, correctIndex: Int) {
        val attempt = QuizAttempt(
            weekName = weekName,
            scenario = scenario,
            selectedIndex = selectedIndex,
            correctIndex = correctIndex
        )
        quizDao.insertAttempt(attempt)
        updateStreakOnQuizCompletion()
    }

    suspend fun clearAttempts() {
        quizDao.clearAllAttempts()
    }

    // Toggle bookmark of scenario
    suspend fun toggleBookmark(quiz: VerbatimQuiz): Boolean {
        val already = quizDao.isBookmarked(quiz.scenario)
        if (already) {
            quizDao.removeBookmarkByScenario(quiz.scenario)
            return false
        } else {
            val optionsStr = quiz.options.joinToString("|||")
            quizDao.insertBookmark(
                BookmarkedQuestion(
                    scenario = quiz.scenario,
                    weekName = quiz.weekName,
                    optionsJson = optionsStr,
                    correctIndex = quiz.correctIndex,
                    verbatimCorrection = quiz.verbatimCorrection
                )
            )
            return true
        }
    }

    suspend fun isBookmarked(scenario: String): Boolean {
        return quizDao.isBookmarked(scenario)
    }

    // Active Study Streak Maintenance
    suspend fun getStreak(): UserStreak {
        return quizDao.getUserStreak() ?: UserStreak()
    }

    private suspend fun updateStreakOnQuizCompletion() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val streakRecord = quizDao.getUserStreak() ?: UserStreak()

        if (streakRecord.lastActiveDate == todayStr) {
            // Already studied today, keep current streak
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var newCurrent = 1
        try {
            if (streakRecord.lastActiveDate.isNotEmpty()) {
                val lastDate = sdf.parse(streakRecord.lastActiveDate)
                val todayDate = sdf.parse(todayStr)
                if (lastDate != null && todayDate != null) {
                    val diff = todayDate.time - lastDate.time
                    val diffDays = diff / (24 * 60 * 60 * 1000)
                    if (diffDays == 1L) {
                        // Consecutive day!
                        newCurrent = streakRecord.currentStreak + 1
                    } else if (diffDays > 1L) {
                        // Broke streak, reset to 1
                        newCurrent = 1
                    } else {
                        // Somehow past date, keep same
                        newCurrent = streakRecord.currentStreak
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error calculating streak difference: ${e.message}")
        }

        val newLongest = maxOf(newCurrent, streakRecord.longestStreak)
        quizDao.insertOrUpdateStreak(
            UserStreak(
                id = 1,
                currentStreak = newCurrent,
                longestStreak = newLongest,
                lastActiveDate = todayStr
            )
        )
    }

    // Process background state verification to reset active streak if the user missed a day
    suspend fun checkAndRefreshStreak() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val streakRecord = quizDao.getUserStreak() ?: return

        if (streakRecord.lastActiveDate.isEmpty()) return

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val lastDate = sdf.parse(streakRecord.lastActiveDate)
            val todayDate = sdf.parse(todayStr)
            if (lastDate != null && todayDate != null) {
                val diff = todayDate.time - lastDate.time
                val diffDays = diff / (24 * 60 * 60 * 1000)
                if (diffDays > 1L) {
                    // Missed at least one day! Reset current streak to 0, longest is kept.
                    quizDao.insertOrUpdateStreak(
                        streakRecord.copy(currentStreak = 0)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error refreshing streak: ${e.message}")
        }
    }
}
