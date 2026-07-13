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
    ANALYTICS,
    EXAM
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
    val completed: Boolean = false,
    val timeLeftSeconds: Int = 0,
    val totalTimeSeconds: Int = 0,
    val elapsedAtClick: List<Int> = List(questions.size) { 0 } // stores seconds elapsed when question was clicked
)

// Active Exam State holder for the 100-Question Exam Simulation
data class ActiveExamState(
    val questions: List<VerbatimQuiz> = emptyList(),
    val userAnswers: List<Int> = emptyList(),
    val isSubmitted: Boolean = false,
    val timeLeftSeconds: Int = 3600, // 60 minutes
    val correctCount: Int = 0,
    val timeTakenSeconds: Int = 0,
    val elapsedAtClick: List<Int> = List(questions.size) { 0 } // stores seconds elapsed when question was clicked
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuizRepository

    // Raw verbatim topics
    val topics: List<TopicBundle>

    // Navigation and state Flows
    val screenState = MutableStateFlow(ScreenState.HOME)
    val currentCourse = MutableStateFlow("corporate")
    val activeQuiz = MutableStateFlow<ActiveQuizState?>(null)
    val activeExam = MutableStateFlow<ActiveExamState?>(null)

    // Optional dynamic exam timer states
    val isTimerEnabled = MutableStateFlow(false)
    val elapsedSeconds = MutableStateFlow(0)
    val isTimeExceeded = MutableStateFlow(false)
    val showTimeExceededAlert = MutableStateFlow(false)

    // Optional dynamic question shuffle states
    val isShuffleEnabled = MutableStateFlow(false)

    fun toggleShuffleEnabled(enabled: Boolean) {
        isShuffleEnabled.value = enabled
    }

    fun toggleTimerEnabled(enabled: Boolean) {
        isTimerEnabled.value = enabled
    }

    fun incrementElapsedSeconds() {
        val quiz = activeQuiz.value ?: return
        if (quiz.completed) return
        elapsedSeconds.value += 1
        
        val limit = quiz.questions.size * 36
        if (elapsedSeconds.value > limit && !isTimeExceeded.value) {
            isTimeExceeded.value = true
            showTimeExceededAlert.value = true
        }
    }

    fun dismissTimeAlert() {
        showTimeExceededAlert.value = false
    }

    fun resetTimer() {
        isTimerEnabled.value = false
        elapsedSeconds.value = 0
        isTimeExceeded.value = false
        showTimeExceededAlert.value = false
    }

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
    val allNotifications: StateFlow<List<PassiveNotification>>
    
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

    // Advanced Analytics Engine States
    val topicMasteryIndex: StateFlow<Map<String, Float>>
    val ebbinghausRetention: StateFlow<Map<String, Float>>
    val cognitiveVelocity: StateFlow<Float>
    val cognitiveFatigueAlert: StateFlow<String?>
    val examPassProbability: StateFlow<Float>

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

        allNotifications = repository.allNotifications.stateIn(
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

        // Item Response Theory (IRT) Inspired Mastery Weighting
        topicMasteryIndex = combine(allAttempts, currentCourse) { attempts, course ->
            val courseTopics = topics.filter { it.category.equals(course, ignoreCase = true) }
            courseTopics.associate { topic ->
                var totalWeight = 0f
                var correctWeight = 0f
                
                topic.quizzes.forEach { quiz ->
                    var weight = 3.0f
                    if (quiz.scenario.length > 150) {
                        weight += 1.0f
                    }
                    val quizAttempts = attempts.filter { it.scenario == quiz.scenario }
                    val failedCount = quizAttempts.count { it.selectedIndex != it.correctIndex }
                    weight += minOf(1.5f, failedCount * 0.5f)
                    
                    totalWeight += weight
                    if (quizAttempts.isEmpty()) {
                        correctWeight += weight * 0.5f // Baseline untested
                    } else {
                        val latest = quizAttempts.sortedBy { it.timestamp }.last()
                        if (latest.selectedIndex == latest.correctIndex) {
                            correctWeight += weight
                        }
                    }
                }
                val mastery = if (totalWeight > 0f) (correctWeight / totalWeight) * 100f else 0f
                topic.topicName to mastery
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

        // Ebbinghaus Time-Decay Retention Modeling
        ebbinghausRetention = combine(allAttempts, currentCourse) { attempts, course ->
            val courseTopics = topics.filter { it.category.equals(course, ignoreCase = true) }
            val now = System.currentTimeMillis()
            courseTopics.associate { topic ->
                var retentionSum = 0f
                if (topic.quizzes.isEmpty()) {
                    topic.topicName to 0f
                } else {
                    topic.quizzes.forEach { quiz ->
                        val quizAttempts = attempts.filter { it.scenario == quiz.scenario }.sortedBy { it.timestamp }
                        if (quizAttempts.isEmpty()) {
                            retentionSum += 50f
                        } else {
                            val latest = quizAttempts.last()
                            if (latest.selectedIndex != latest.correctIndex) {
                                retentionSum += 15f
                            } else {
                                val elapsedMs = now - latest.timestamp
                                val elapsedMinutes = elapsedMs / 60000f
                                val elapsedDays = elapsedMinutes / 4f
                                
                                var streakCount = 0
                                for (i in quizAttempts.indices.reversed()) {
                                    if (quizAttempts[i].selectedIndex == quizAttempts[i].correctIndex) {
                                        streakCount++
                                    } else {
                                        break
                                    }
                                }
                                val lambda = 0.15f / maxOf(1, streakCount)
                                val ret = 100f * kotlin.math.exp(-lambda * elapsedDays)
                                retentionSum += maxOf(15f, ret)
                            }
                        }
                    }
                    topic.topicName to (retentionSum / topic.quizzes.size)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

        // Latency and Cognitive Velocity Tracking
        cognitiveVelocity = combine(allAttempts, currentCourse) { attempts, course ->
            val courseTopics = topics.filter { it.category.equals(course, ignoreCase = true) }
            val courseAttempts = attempts.filter { att ->
                courseTopics.any { it.topicName.equals(att.weekName, ignoreCase = true) }
            }
            if (courseAttempts.isEmpty()) {
                75f
            } else {
                var speedSum = 0f
                courseAttempts.forEach { att ->
                    val t = att.responseTimeMs / 1000f
                    val isCorrect = att.selectedIndex == att.correctIndex
                    val score = when {
                        isCorrect && t in 4f..12f -> 100f
                        isCorrect && t > 12f -> maxOf(60f, 100f - (t - 12f) * 2f)
                        isCorrect && t < 4f -> 95f
                        !isCorrect && t < 4f -> 30f
                        !isCorrect && t > 12f -> 45f
                        else -> 50f
                    }
                    speedSum += score
                }
                speedSum / courseAttempts.size
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 75f
        )

        // Cognitive Fatigue Threshold Detection (60 questions / 35 minutes thresholds)
        cognitiveFatigueAlert = allAttempts.map { attempts ->
            val sessionAttempts = attempts.filter { System.currentTimeMillis() - it.timestamp < 2 * 3600 * 1000 }.sortedBy { it.timestamp }
            if (sessionAttempts.size >= 10) {
                val halfSize = sessionAttempts.size / 2
                val earlyAttempts = sessionAttempts.take(halfSize)
                val lateAttempts = sessionAttempts.takeLast(halfSize)
                
                val earlyErrorRate = earlyAttempts.count { it.selectedIndex != it.correctIndex }.toFloat() / earlyAttempts.size
                val lateErrorRate = lateAttempts.count { it.selectedIndex != it.correctIndex }.toFloat() / lateAttempts.size
                
                val errorIncrease = (lateErrorRate - earlyErrorRate) * 100f
                val cumulativeTimeMs = sessionAttempts.sumOf { it.responseTimeMs }
                val cumulativeMinutes = cumulativeTimeMs / 60000f
                
                if (cumulativeMinutes >= 35f && errorIncrease >= 15f) {
                    "Cognitive Fatigue detected after 35 minutes of testing! Recall error rate rose sharply by ${errorIncrease.toInt()}% in the second half of your study session. Take a 10-minute break to reset your mind!"
                } else if (sessionAttempts.size >= 60 && errorIncrease >= 15f) {
                    "Cognitive Fatigue detected at Question 60 threshold! Continuous high cognitive load has caused your error rate to spike by ${errorIncrease.toInt()}%. Take a breather!"
                } else if (errorIncrease >= 20f) {
                    "High Cognitive Load detected! Your error rate spiked by ${errorIncrease.toInt()}% in your recent attempts. Consider starting a Study Break Micro-Quiz or taking a rest!"
                } else {
                    null
                }
            } else {
                null
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        // Composite Exam Pass Probability Dashboard Indicator
        examPassProbability = combine(topicMasteryIndex, ebbinghausRetention, cognitiveVelocity, currentStreak) { mastery, retention, velocity, streak ->
            val avgMastery = if (mastery.isNotEmpty()) mastery.values.average().toFloat() else 0f
            val avgRetention = if (retention.isNotEmpty()) retention.values.average().toFloat() else 50f
            val avgVelocity = velocity
            
            var prob = (avgMastery * 0.5f) + (avgRetention * 0.3f) + (avgVelocity * 0.2f)
            if (streak > 3) {
                prob += 5f
            }
            maxOf(5f, minOf(99f, prob))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 50f
        )

        // Launch initial coroutines
        viewModelScope.launch {
            repository.checkAndRefreshStreak()
            refreshStreakState()
            generateNewMicroQuiz()
            
            // Random Timing / Passive Notification scheduler (Max 4 times daily)
            // To simulate randomized triggers across the day passively, we run a routine check
            while (true) {
                // Every 30 minutes, there is a small random chance to trigger a notification passively,
                // capped at 4 notifications per day
                kotlinx.coroutines.delay(30 * 60 * 1000L)
                val recentCount = allNotifications.value.count { System.currentTimeMillis() - it.timestamp < 24 * 3600 * 1000 }
                if (recentCount < 4 && (0..10).random() < 3) {
                    simulatePassiveNotification()
                }
            }
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
            val questions = if (isShuffleEnabled.value) {
                topicBundle.quizzes.shuffled()
            } else {
                topicBundle.quizzes
            }
            val totalSeconds = questions.size * 36
            activeQuiz.value = ActiveQuizState(
                title = topicBundle.topicName,
                questions = questions,
                answers = List(questions.size) { -1 },
                timeLeftSeconds = totalSeconds,
                totalTimeSeconds = totalSeconds
            )
            resetTimer()
            navigateTo(ScreenState.QUIZ)
        }
    }

    // Real Spaced Repetition (SRS) due questions practice
    fun startSrsDueQuiz() {
        val attempts = allAttempts.value
        val allQuizzes = topics
            .filter { it.category.equals(currentCourse.value, ignoreCase = true) }
            .flatMap { it.quizzes }
        
        val dueQuizzes = allQuizzes.filter { quiz ->
            val quizAttempts = attempts.filter { it.scenario == quiz.scenario }.sortedBy { it.timestamp }
            if (quizAttempts.isEmpty()) {
                true // Untested questions are baseline due!
            } else {
                val latest = quizAttempts.last()
                val isLatestCorrect = (latest.selectedIndex == latest.correctIndex)
                if (!isLatestCorrect) {
                    true // Incorrect questions are immediately due!
                } else {
                    // Count consecutive correct attempts from the end
                    var consecutiveCorrect = 0
                    for (i in quizAttempts.indices.reversed()) {
                        val att = quizAttempts[i]
                        if (att.selectedIndex == att.correctIndex) {
                            consecutiveCorrect++
                        } else {
                            break
                        }
                    }
                    val intervalDays = when (consecutiveCorrect) {
                        1 -> 1
                        2 -> 3
                        3 -> 7
                        else -> 14
                    }
                    val intervalMs = intervalDays * 24L * 60L * 60L * 1000L
                    System.currentTimeMillis() - latest.timestamp > intervalMs
                }
            }
        }.shuffled()

        if (dueQuizzes.isNotEmpty()) {
            val count = minOf(dueQuizzes.size, 15) // Limit to 15 questions
            val chosen = dueQuizzes.take(count)
            val totalSeconds = chosen.size * 36
            activeQuiz.value = ActiveQuizState(
                title = "Due Spaced Repetition (SRS) Quiz",
                questions = chosen,
                answers = List(chosen.size) { -1 },
                timeLeftSeconds = totalSeconds,
                totalTimeSeconds = totalSeconds
            )
            resetTimer()
            navigateTo(ScreenState.QUIZ)
        } else {
            // All caught up, pull 15 random practice questions
            val chosen = allQuizzes.shuffled().take(minOf(allQuizzes.size, 15))
            if (chosen.isNotEmpty()) {
                val totalSeconds = chosen.size * 36
                activeQuiz.value = ActiveQuizState(
                    title = "All SRS Caught Up! (Random Practice)",
                    questions = chosen,
                    answers = List(chosen.size) { -1 },
                    timeLeftSeconds = totalSeconds,
                    totalTimeSeconds = totalSeconds
                )
                resetTimer()
                navigateTo(ScreenState.QUIZ)
            }
        }
    }

    // Practice the weakest 20 questions based on user's actual incorrect attempts
    fun startWeakestPracticeQuiz() {
        val attempts = allAttempts.value
        val allQuizzes = topics
            .filter { it.category.equals(currentCourse.value, ignoreCase = true) }
            .flatMap { it.quizzes }
            
        // Group attempts by scenario
        val scenarioGroups = attempts.groupBy { it.scenario }
        
        // Calculate accuracy for each scenario
        val accuracyMap = scenarioGroups.mapValues { (_, atts) ->
            val correct = atts.count { it.selectedIndex == it.correctIndex }
            correct.toFloat() / atts.size.toFloat()
        }
        
        // Filter quizzes to only those that have been attempted and have accuracy < 100% (i.e., has some error),
        // sorted by accuracy ascending
        val weakQuizzes = allQuizzes
            .filter { accuracyMap.containsKey(it.scenario) && (accuracyMap[it.scenario] ?: 1f) < 1f }
            .sortedBy { accuracyMap[it.scenario] ?: 0f }
            
        val chosen = if (weakQuizzes.isNotEmpty()) {
            weakQuizzes.take(20)
        } else {
            // Fallback: take 20 random or untested questions
            allQuizzes.shuffled().take(minOf(allQuizzes.size, 20))
        }
        
        if (chosen.isNotEmpty()) {
            val totalSeconds = chosen.size * 36
            activeQuiz.value = ActiveQuizState(
                title = "Weakest Focus Practice (Top 20)",
                questions = chosen,
                answers = List(chosen.size) { -1 },
                timeLeftSeconds = totalSeconds,
                totalTimeSeconds = totalSeconds
            )
            resetTimer()
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
            val totalSeconds = chosen.size * 36
            activeQuiz.value = ActiveQuizState(
                title = "Random Cross-Week Exam",
                questions = chosen,
                answers = List(chosen.size) { -1 },
                timeLeftSeconds = totalSeconds,
                totalTimeSeconds = totalSeconds
            )
            resetTimer()
            navigateTo(ScreenState.QUIZ)
        }
    }

    // 100-Question Exam methods
    fun start100QuestionExam() {
        val categories = listOf("civil", "corporate", "property", "ethics", "criminal")
        val examQuestions = mutableListOf<VerbatimQuiz>()
        
        for (category in categories) {
            val allCategoryQuizzes = topics
                .filter { it.category.equals(category, ignoreCase = true) }
                .flatMap { it.quizzes }
                .shuffled()
            
            // Pull exactly 20 questions from each distinct subject
            val selectedQuizzes = allCategoryQuizzes.take(minOf(allCategoryQuizzes.size, 20))
            examQuestions.addAll(selectedQuizzes)
        }
        
        // Randomization: robust shuffling (Fisher-Yates) to mix questions randomly for every attempt
        val finalQuestions = examQuestions.shuffled()
        
        activeExam.value = ActiveExamState(
            questions = finalQuestions,
            userAnswers = List(finalQuestions.size) { -1 },
            isSubmitted = false,
            timeLeftSeconds = 3600, // 60 minutes
            correctCount = 0,
            timeTakenSeconds = 0
        )
        navigateTo(ScreenState.EXAM)
    }

    fun selectExamOption(questionIndex: Int, optionIndex: Int) {
        val exam = activeExam.value ?: return
        if (exam.isSubmitted) return
        
        val newAnswers = exam.userAnswers.toMutableList()
        newAnswers[questionIndex] = optionIndex
        
        val currentElapsed = exam.timeTakenSeconds
        val newElapsed = exam.elapsedAtClick.toMutableList()
        while (newElapsed.size <= questionIndex) {
            newElapsed.add(0)
        }
        newElapsed[questionIndex] = currentElapsed
        
        activeExam.value = exam.copy(
            userAnswers = newAnswers,
            elapsedAtClick = newElapsed
        )
    }

    fun decrementExamTime() {
        val exam = activeExam.value ?: return
        if (exam.isSubmitted) return
        
        if (exam.timeLeftSeconds > 0) {
            val nextTimeLeft = exam.timeLeftSeconds - 1
            activeExam.value = exam.copy(
                timeLeftSeconds = nextTimeLeft,
                timeTakenSeconds = exam.timeTakenSeconds + 1
            )
            if (nextTimeLeft == 0) {
                submitExam()
            }
        }
    }

    fun submitExam() {
        val exam = activeExam.value ?: return
        if (exam.isSubmitted) return
        
        var correct = 0
        val questionsToSave = exam.questions.zip(exam.userAnswers)
        
        viewModelScope.launch {
            questionsToSave.forEachIndexed { qIdx, (question, selected) ->
                if (selected == question.correctIndex) {
                    correct++
                }
                
                var latencyMs = if (exam.elapsedAtClick.getOrElse(qIdx) { 0 } > 0) {
                    val currentClick = exam.elapsedAtClick[qIdx].toLong() * 1000L
                    val prevClick = if (qIdx > 0) exam.elapsedAtClick[qIdx - 1].toLong() * 1000L else 0L
                    val computedDiff = currentClick - prevClick
                    if (computedDiff > 0L) computedDiff else 8000L
                } else {
                    val totalTimeTaken = exam.timeTakenSeconds
                    if (exam.questions.isNotEmpty()) totalTimeTaken.toLong() * 1000L / exam.questions.size else 6000L
                }
                latencyMs = maxOf(1000L, minOf(120000L, latencyMs))

                repository.saveAttempt(
                    weekName = question.weekName,
                    scenario = question.scenario,
                    selectedIndex = selected,
                    correctIndex = question.correctIndex,
                    responseTimeMs = latencyMs
                )
            }
            refreshStreakState()
        }
        
        activeExam.value = exam.copy(
            isSubmitted = true,
            correctCount = correct
        )
    }

    fun resetExam() {
        activeExam.value = null
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
        val totalSeconds = state.questions.size * 36

        targetFlow.value = ActiveQuizState(
            title = state.title,
            questions = state.questions,
            answers = List(state.questions.size) { -1 },
            timeLeftSeconds = if (isMicro) 0 else totalSeconds,
            totalTimeSeconds = if (isMicro) 0 else totalSeconds
        )
        if (!isMicro) {
            resetTimer()
        }
    }

    fun selectQuizOption(questionIndex: Int, optionIndex: Int) {
        val quiz = activeQuiz.value ?: return
        if (quiz.completed) return
        
        val newAnswers = quiz.answers.toMutableList()
        newAnswers[questionIndex] = optionIndex
        
        val currentElapsed = quiz.totalTimeSeconds - quiz.timeLeftSeconds
        val newElapsed = quiz.elapsedAtClick.toMutableList()
        while (newElapsed.size <= questionIndex) {
            newElapsed.add(0)
        }
        newElapsed[questionIndex] = currentElapsed
        
        activeQuiz.value = quiz.copy(
            answers = newAnswers,
            elapsedAtClick = newElapsed
        )
    }

    fun decrementQuizTime() {
        val quiz = activeQuiz.value ?: return
        if (quiz.completed) return
        
        if (quiz.timeLeftSeconds > 0) {
            val nextTimeLeft = quiz.timeLeftSeconds - 1
            activeQuiz.value = quiz.copy(
                timeLeftSeconds = nextTimeLeft
            )
            if (nextTimeLeft == 0) {
                submitQuiz()
            }
        }
    }

    fun submitQuiz() {
        val quiz = activeQuiz.value ?: return
        if (quiz.completed) return
        
        var correct = 0
        val questionsToSave = quiz.questions.zip(quiz.answers)
        
        viewModelScope.launch {
            questionsToSave.forEachIndexed { qIdx, (question, selected) ->
                if (selected == question.correctIndex) {
                    correct++
                }
                
                var latencyMs = if (quiz.elapsedAtClick.getOrElse(qIdx) { 0 } > 0) {
                    val currentClick = quiz.elapsedAtClick[qIdx].toLong() * 1000L
                    val prevClick = if (qIdx > 0) quiz.elapsedAtClick[qIdx - 1].toLong() * 1000L else 0L
                    val computedDiff = currentClick - prevClick
                    if (computedDiff > 0L) computedDiff else 8000L
                } else {
                    val totalTimeSpent = quiz.totalTimeSeconds - quiz.timeLeftSeconds
                    if (quiz.questions.isNotEmpty()) totalTimeSpent.toLong() * 1000L / quiz.questions.size else 6000L
                }
                latencyMs = maxOf(1000L, minOf(120000L, latencyMs))

                repository.saveAttempt(
                    weekName = question.weekName,
                    scenario = question.scenario,
                    selectedIndex = selected,
                    correctIndex = question.correctIndex,
                    responseTimeMs = latencyMs
                )
            }
            refreshStreakState()
        }
        
        activeQuiz.value = quiz.copy(
            completed = true,
            correctAnswersCount = correct
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

    // Passive Notifications API Simulation
    fun simulatePassiveNotification() {
        val attempts = allAttempts.value
        val incorrectAttempts = attempts.filter { it.selectedIndex != it.correctIndex }
        
        val selectedQuiz: VerbatimQuiz
        if (incorrectAttempts.isNotEmpty()) {
            val randomAttempt = incorrectAttempts.random()
            val allQuizzes = topics.flatMap { it.quizzes }
            selectedQuiz = allQuizzes.find { it.scenario == randomAttempt.scenario } 
                ?: allQuizzes.random()
        } else {
            val allQuizzes = topics.flatMap { it.quizzes }
            if (allQuizzes.isNotEmpty()) {
                selectedQuiz = allQuizzes.random()
            } else {
                return
            }
        }
        
        val ruleText = selectedQuiz.verbatimCorrection.ifEmpty {
            "Verify court procedural timelines for this legal scenario. Option index ${selectedQuiz.correctIndex + 1} is legally binding."
        }
        
        val topicNameShort = selectedQuiz.weekName.replace("WEEK ", "W").uppercase()
        val title = "💡 Weak-Spot Review [$topicNameShort]"
        val notificationBody = "Rule: $ruleText"
        
        viewModelScope.launch {
            val lastNotifications = allNotifications.value
            if (lastNotifications.isNotEmpty() && lastNotifications.first().ruleFact == ruleText) {
                return@launch
            }
            
            repository.saveNotification(
                PassiveNotification(
                    scenario = selectedQuiz.scenario,
                    topicName = selectedQuiz.weekName,
                    ruleFact = ruleText,
                    timestamp = System.currentTimeMillis()
                )
            )
            
            showAndroidSystemNotification(title, notificationBody)
        }
    }

    private fun showAndroidSystemNotification(title: String, body: String) {
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channelId = "passive_reviews"
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Passive Reviews",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Smart pass-through recall rule cards"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val intent = android.content.Intent(context, com.example.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
            
        try {
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: Exception) {
            android.util.Log.e("QuizViewModel", "Failed to post notification", e)
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }
}
