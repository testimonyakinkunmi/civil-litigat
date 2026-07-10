package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Screen Navigation State for seamless transitions
enum class ScreenState {
    HOME,
    WEEK_SELECT,
    QUIZ,
    BOOKMARKS,
    ANALYTICS
}

// Struct to represent a week's analytics
data class WeekAnalytic(
    val weekName: String,
    val totalAnswered: Int,
    val correctCount: Int,
    val accuracyPercent: Float
)

// Active Quiz State holder
data class ActiveQuizState(
    val title: String,
    val questions: List<VerbatimQuiz>,
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswered: Boolean = false,
    val answers: List<Int> = emptyList(), // stores user selected index for each question
    val correctAnswersCount: Int = 0,
    val completed: Boolean = false
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuizRepository

    // Raw verbatim topics
    val topics: List<TopicBundle>

    // Navigation and state Flows
    val screenState = MutableStateFlow(ScreenState.HOME)
    val currentCourse = MutableStateFlow("corporate")
    val activeQuiz = MutableStateFlow<ActiveQuizState?>(null)

    // Theme state (with persistence in SharedPreferences)
    private val sharedPrefs = application.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
    val isLightTheme = MutableStateFlow(sharedPrefs.getBoolean("is_light_theme", false))

    fun toggleTheme() {
        val newValue = !isLightTheme.value
        isLightTheme.value = newValue
        sharedPrefs.edit().putBoolean("is_light_theme", newValue).apply()
    }

    // Room Database Observables
    val allAttempts: StateFlow<List<QuizAttempt>>
    val allBookmarks: StateFlow<List<BookmarkedQuestion>>
    
    // Streaks
    val currentStreak = MutableStateFlow(0)
    val longestStreak = MutableStateFlow(0)

    // Interactive Insight Chart Toggles (Toggles which insights are visible)
    val showWeekAccuracyChart = MutableStateFlow(true)
    val showTrendChart = MutableStateFlow(true)
    val showCategoryDistribution = MutableStateFlow(true)

    // Current Micro Quiz (3-5 question micro-quizzes generated on the Home screen)
    val microQuizQuestions = MutableStateFlow<List<VerbatimQuiz>>(emptyList())
    val microQuizState = MutableStateFlow<ActiveQuizState?>(null)

    // Statistics derived reactively from raw Room attempts + static content
    val analyticsState: StateFlow<List<WeekAnalytic>>
    val weakestWeeks: StateFlow<List<WeekAnalytic>>
    val overallAccuracy: StateFlow<Float>
    val totalCompletedQuizzes: StateFlow<Int>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = QuizRepository(application, database.quizDao())

        // Load courses verbatim from local json files
        topics = repository.loadVerbatimTopics()

        // Room lists state maps
        allAttempts = repository.allAttempts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allBookmarks = repository.allBookmarks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Reactive mathematical calculations on user history
        analyticsState = combine(allAttempts, currentCourse) { attempts, course ->
            topics
                .filter { it.category.equals(course, ignoreCase = true) }
                .map { topic ->
                    val weekAttempts = attempts.filter { it.weekName.equals(topic.topicName, ignoreCase = true) }
                    if (weekAttempts.isEmpty()) {
                        WeekAnalytic(topic.topicName, 0, 0, 0f)
                    } else {
                        val correct = weekAttempts.count { it.selectedIndex == it.correctIndex }
                        val percent = (correct.toFloat() / weekAttempts.size.toFloat()) * 100f
                        WeekAnalytic(topic.topicName, weekAttempts.size, correct, percent)
                    }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        weakestWeeks = analyticsState.map { analytics ->
            analytics
                .filter { it.totalAnswered > 0 && it.accuracyPercent < 65f }
                .sortedBy { it.accuracyPercent }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        overallAccuracy = analyticsState.map { analytics ->
            val total = analytics.sumOf { it.totalAnswered }
            if (total == 0) {
                0f
            } else {
                val correct = analytics.sumOf { it.correctCount }
                (correct.toFloat() / total.toFloat()) * 100f
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )

        totalCompletedQuizzes = combine(allAttempts, currentCourse) { attempts, course ->
            attempts.count { attempt ->
                val topic = topics.find { it.topicName.equals(attempt.weekName, ignoreCase = true) }
                topic?.category?.equals(course, ignoreCase = true) ?: false
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        // Launch initial coroutines
        viewModelScope.launch {
            repository.checkAndRefreshStreak()
            refreshStreakState()
            generateNewMicroQuiz()
        }
    }

    fun navigateTo(state: ScreenState) {
        screenState.value = state
    }

    fun switchCourse(course: String) {
        currentCourse.value = course
        generateNewMicroQuiz()
    }

    private suspend fun refreshStreakState() {
        val s = repository.getStreak()
        currentStreak.value = s.currentStreak
        longestStreak.value = s.longestStreak
    }

    // Interactive Insight Chart Visibilities
    fun toggleWeekAccuracyChart() {
        showWeekAccuracyChart.value = !showWeekAccuracyChart.value
    }

    fun toggleTrendChart() {
        showTrendChart.value = !showTrendChart.value
    }

    fun toggleCategoryDistribution() {
        showCategoryDistribution.value = !showCategoryDistribution.value
    }

    // Toggle bookmark states
    fun toggleBookmark(quiz: VerbatimQuiz, onCompleted: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val isAdded = repository.toggleBookmark(quiz)
            onCompleted(isAdded)
        }
    }

    // Check bookmark
    suspend fun isBookmarked(scenario: String): Boolean {
        return repository.isBookmarked(scenario)
    }

    // Selects and launches a specific Week's study quiz session
    fun startTopicQuiz(weekName: String) {
        val topicBundle = topics.find { 
            it.topicName.equals(weekName, ignoreCase = true) && 
            it.category.equals(currentCourse.value, ignoreCase = true) 
        }
        if (topicBundle != null && topicBundle.quizzes.isNotEmpty()) {
            activeQuiz.value = ActiveQuizState(
                title = topicBundle.topicName,
                questions = topicBundle.quizzes,
                answers = List(topicBundle.quizzes.size) { -1 }
            )
            navigateTo(ScreenState.QUIZ)
        }
    }

    // Generate option to take a fully random cross-week practice exam
    fun startRandomQuiz() {
        val allQuizzes = topics
            .filter { it.category.equals(currentCourse.value, ignoreCase = true) }
            .flatMap { it.quizzes }
            .shuffled()
        if (allQuizzes.isNotEmpty()) {
            val count = minOf(allQuizzes.size, 15) // Standard full custom random session has 15 items
            val chosen = allQuizzes.take(count)
            activeQuiz.value = ActiveQuizState(
                title = "Random Cross-Week Exam",
                questions = chosen,
                answers = List(chosen.size) { -1 }
            )
            navigateTo(ScreenState.QUIZ)
        }
    }

    // Generates a 3-5 question micro-quiz to solve during a quick break on the Home screen
    fun generateNewMicroQuiz() {
        val allQuizzes = topics
            .filter { it.category.equals(currentCourse.value, ignoreCase = true) }
            .flatMap { it.quizzes }
            .shuffled()
        if (allQuizzes.isNotEmpty()) {
            val size = (3..5).random()
            val chosen = allQuizzes.take(minOf(allQuizzes.size, size))
            microQuizQuestions.value = chosen
            microQuizState.value = ActiveQuizState(
                title = "Home Micro-Recall",
                questions = chosen,
                answers = List(chosen.size) { -1 }
            )
        }
    }

    // Answers a question in either the Active Quiz or Micro Quiz
    fun selectOption(index: Int, isMicro: Boolean = false) {
        val targetFlow = if (isMicro) microQuizState else activeQuiz
        val state = targetFlow.value ?: return
        if (state.isAnswered) return // already locked

        targetFlow.value = state.copy(
            selectedOptionIndex = index
        )
    }

    // Lock option, reveal answer keys, show verbatim corrections instantly, and save attempt
    fun lockAndSubmitAnswer(isMicro: Boolean = false) {
        val targetFlow = if (isMicro) microQuizState else activeQuiz
        val state = targetFlow.value ?: return
        val chosenIndex = state.selectedOptionIndex ?: return
        if (state.isAnswered) return

        val question = state.questions[state.currentIndex]
        val isCorrect = (chosenIndex == question.correctIndex)
        val newCorrectCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount

        // Update list of answers taken
        val newAnswers = state.answers.toMutableList()
        newAnswers[state.currentIndex] = chosenIndex

        viewModelScope.launch {
            repository.saveAttempt(
                weekName = question.weekName,
                scenario = question.scenario,
                selectedIndex = chosenIndex,
                correctIndex = question.correctIndex
            )
            refreshStreakState()
        }

        targetFlow.value = state.copy(
            isAnswered = true,
            answers = newAnswers,
            correctAnswersCount = newCorrectCount
        )
    }

    // Proceeds to the next question in the active quiz sequence
    fun nextQuestion(isMicro: Boolean = false) {
        val targetFlow = if (isMicro) microQuizState else activeQuiz
        val state = targetFlow.value ?: return

        if (state.currentIndex + 1 < state.questions.size) {
            targetFlow.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedOptionIndex = null,
                isAnswered = false
            )
        } else {
            // End of Quiz, show final score card and verbatim summaries
            targetFlow.value = state.copy(completed = true)
        }
    }

    // Restart current active quiz
    fun restartQuiz(isMicro: Boolean = false) {
        val targetFlow = if (isMicro) microQuizState else activeQuiz
        val state = targetFlow.value ?: return

        targetFlow.value = ActiveQuizState(
            title = state.title,
            questions = state.questions,
            answers = List(state.questions.size) { -1 }
        )
    }

    // Clear all statistical records to start study clean
    fun resetDatabase() {
        viewModelScope.launch {
            repository.clearAttempts()
            generateNewMicroQuiz()
            refreshStreakState()
        }
    }
}
