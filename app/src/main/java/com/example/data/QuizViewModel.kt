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

fun extractWeekNumber(rawName: String): Int {
    val cleanLower = rawName.trim().lowercase()
    val matchResult = """\b(?:week|wk|w)\s*(\d+)""".toRegex().find(cleanLower)
    if (matchResult != null) {
        return matchResult.groupValues[1].toIntOrNull() ?: 9999
    }
    val fallbackResult = """\d+""".toRegex().find(cleanLower)
    return fallbackResult?.value?.toIntOrNull() ?: 9999
}

fun formatWeekTitle(rawName: String, category: String = ""): String {
    val clean = rawName.trim()
    val cleanLower = clean.lowercase()

    // 1. Check property
    if (category.equals("property", ignoreCase = true) || cleanLower.contains("deeds") || cleanLower.contains("leases") || cleanLower.contains("sale of land") || cleanLower.contains("attorney") || cleanLower.contains("mortgages") || cleanLower.contains("probate") || cleanLower.contains("wills") || cleanLower.contains("assent")) {
        return when {
            cleanLower.contains("week 1") -> "Week 1: Deeds & Documents"
            cleanLower.contains("week 2") -> "Week 2: Leases & Tenancies"
            cleanLower.contains("week 3") -> "Week 3: Sale of Land"
            cleanLower.contains("week 4") && cleanLower.contains("applicable") -> "Week 4: Property Laws"
            cleanLower.contains("week 4") && cleanLower.contains("taxation") -> "Week 4: Property Taxation"
            cleanLower.contains("week 4") -> "Week 4: Property Overview"
            cleanLower.contains("week 5") -> "Week 5: Power of Attorney"
            cleanLower.contains("week 6") -> "Week 6: Mortgages & Charges"
            cleanLower.contains("week 7") -> "Week 7: Land Registration"
            cleanLower.contains("week 8") -> "Week 8: Probate Practice"
            cleanLower.contains("week 9") -> "Week 9: Wills & Codicils"
            cleanLower.contains("week 10") -> "Week 10: Property Billing"
            cleanLower.contains("week 11") -> "Week 11: Property Taxation"
            cleanLower.contains("week 12") -> "Week 12: Personal Representatives"
            else -> clean
        }
    }

    // 2. Check corporate
    if (category.equals("corporate", ignoreCase = true) || cleanLower.contains("corporate") || cleanLower.contains("company") || (cleanLower.startsWith("week") && (cleanLower.endsWith("general") || cleanLower.contains("general")) && !category.equals("civil", ignoreCase = true) && !category.equals("ethics", ignoreCase = true))) {
        return when {
            cleanLower.contains("week 3") -> "Week 3: Company Law Intro"
            cleanLower.contains("week 4") -> "Week 4: Company Promotion"
            cleanLower.contains("week 5") -> "Week 5: Company Registration"
            cleanLower.contains("week 6") -> "Week 6: Company Operations"
            cleanLower.contains("week 7") -> "Week 7: Share Capital"
            cleanLower.contains("week 8") -> "Week 8: Directors & Secretaries"
            cleanLower.contains("week 9") -> "Week 9: Foreign Participation"
            cleanLower.contains("week 10") -> "Week 10: Company Winding Up"
            cleanLower.contains("week 11") -> "Week 11: Public Companies"
            cleanLower.contains("week 12") -> "Week 12: Corporate Practice"
            cleanLower.contains("week 13") -> "Week 13: Joint Ventures"
            cleanLower.contains("week 14") -> "Week 14: Non-Profit Orgs"
            cleanLower.contains("week 15") -> "Week 15: Specialized Corporate Forms"
            cleanLower.contains("week 16") -> "Week 16: Corporate Restructuring"
            cleanLower.contains("week 17") -> "Week 17: Insolvency & Receivership"
            else -> clean
        }
    }

    // 3. Check criminal
    if (category.equals("criminal", ignoreCase = true) || cleanLower.contains("criminal") || cleanLower.contains("proceedings") || cleanLower.contains("safeguards") || cleanLower.contains("arrest") || cleanLower.contains("misjoinder") || cleanLower.contains("duplicity") || cleanLower.contains("arraignment") || cleanLower.contains("appeals")) {
        return when {
            cleanLower.contains("week 1") -> "Week 1: Introduction to Criminal Litigation"
            cleanLower.contains("week 2") -> "Week 2: Criminal Proceedings"
            cleanLower.contains("week 3") -> "Week 3: Court Jurisdiction & Venues"
            cleanLower.contains("week 4") -> "Week 4: Investigation & Interviews"
            cleanLower.contains("week 5") -> "Week 5: Arrests & Searches"
            cleanLower.contains("week 6") -> "Week 6: Constitutional Safeguards"
            cleanLower.contains("week 7") -> "Week 7: Bail Pending Trial"
            cleanLower.contains("week 8") && cleanLower.contains("offenders") -> "Week 8: Misjoinder of Offenders"
            cleanLower.contains("week 8") && cleanLower.contains("offences") -> "Week 8: Misjoinder of Offences"
            cleanLower.contains("week 8") && cleanLower.contains("duplicity") -> "Week 8: Duplicity Rule"
            cleanLower.contains("week 8") && cleanLower.contains("authority") -> "Week 8: Drafting Authority"
            cleanLower.contains("week 8") && cleanLower.contains("ambiguity") -> "Week 8: Ambiguity Rule"
            cleanLower.contains("week 8") && cleanLower.contains("amendment") -> "Week 8: Amendment Procedure"
            cleanLower.contains("week 8") && cleanLower.contains("post-amendment requirements") -> "Week 8: Post-Amendment Procedure"
            cleanLower.contains("week 8") && cleanLower.contains("ethics") -> "Week 8: Professional Ethics"
            cleanLower.contains("week 8") && cleanLower.contains("objections") -> "Week 8: Objections to Charges"
            cleanLower.contains("week 8") && cleanLower.contains("forms") -> "Week 8: Statutory Forms"
            cleanLower.contains("week 8") && cleanLower.contains("formatting") -> "Week 8: Charge Formatting"
            cleanLower.contains("week 8") && cleanLower.contains("ambiguity in charges") -> "Week 8: Ambiguity in Charges"
            cleanLower.contains("week 8") && cleanLower.contains("information") -> "Week 8: Information vs Charge"
            cleanLower.contains("week 8") && cleanLower.contains("joinder") -> "Week 8: Joinder of Offenders"
            cleanLower.contains("week 8") && cleanLower.contains("post-amendment procedure") -> "Week 8: Post-Amendment Procedure"
            cleanLower.contains("week 8") -> "Week 8: Charges & Procedures"
            cleanLower.contains("week 9") -> "Week 9: Judgment & Sentencing"
            cleanLower.contains("week 10") -> "Week 10: Witness Examination"
            cleanLower.contains("week 11") -> "Week 11: Defence Case"
            cleanLower.contains("week 12") -> "Week 12: Arraignment & Attendance"
            cleanLower.contains("week 13") -> "Week 13: Trial Prep & Evidence"
            cleanLower.contains("week 14") -> "Week 14: Criminal Appeals"
            else -> clean
        }
    }

    // 4. Check ethics
    if (category.equals("ethics", ignoreCase = true) || cleanLower.contains("ethics") || cleanLower.contains("professional conduct") || cleanLower.contains("trust accounts") || cleanLower.contains("discipline")) {
        return when {
            cleanLower.contains("week 1") -> "Week 1: Ethics Introduction"
            cleanLower.contains("week 2") -> "Week 2: Regulatory Bodies"
            cleanLower.contains("week 3") -> "Week 3: Call to Bar & Practice"
            cleanLower.contains("week 4") -> "Week 4: Professional Conduct"
            cleanLower.contains("week 5") -> "Week 5: Advocate Duties"
            cleanLower.contains("week 6") -> "Week 6: Duties to Client"
            cleanLower.contains("week 7") -> "Week 7: Duties to Court"
            cleanLower.contains("week 8") -> "Week 8: Duties to Fellow Lawyers"
            cleanLower.contains("week 9") -> "Week 9: Professional Fees"
            cleanLower.contains("week 10") -> "Week 10: Trust Accounts"
            cleanLower.contains("week 11") -> "Week 11: Advertising & Solicitation"
            cleanLower.contains("week 12") -> "Week 12: Improper Attractions"
            cleanLower.contains("week 13") -> "Week 13: Professional Discipline"
            cleanLower.contains("week 14") -> "Week 14: Rules of Professional Conduct"
            cleanLower.contains("week 15") -> "Week 15: Contempt and Sanctions"
            cleanLower.contains("week 16") -> "Week 16: Global Ethics Codes"
            cleanLower.contains("week 17") -> "Week 17: Corporate & Public Ethics"
            else -> clean
        }
    }

    // 5. Check civil
    if (category.equals("civil", ignoreCase = true) || cleanLower.contains("civil") || cleanLower.contains("jurisdiction") || cleanLower.contains("pleadings") || cleanLower.contains("discovery") || cleanLower.contains("summary judgment") || cleanLower.contains("parties")) {
        return when {
            cleanLower.contains("week 3") -> "Week 3: Courts & Jurisdiction"
            cleanLower.contains("week 4") -> "Week 4: Pre-Action & ADR"
            cleanLower.contains("week 5") -> "Week 5: Pleadings"
            cleanLower.contains("week 6") -> "Week 6: Parties & Joinder"
            cleanLower.contains("week 7") -> "Week 7: Service of Processes"
            cleanLower.contains("week 8") -> "Week 8: Discovery"
            cleanLower.contains("week 9") -> "Week 9: Interlocutory Motions"
            cleanLower.contains("week 10") -> "Week 10: Summary Judgment"
            cleanLower.contains("week 11") -> "Week 11: Pre-Trial Conferences"
            cleanLower.contains("week 12") -> "Week 12: Trials and Evidence"
            cleanLower.contains("week 13") -> "Week 13: Summary Judgments"
            cleanLower.contains("week 14") -> "Week 14: Enforcement of Judgments"
            cleanLower.contains("week 15") -> "Week 15: Appeals & Revision"
            cleanLower.contains("week 16") -> "Week 16: Constitutional Practice"
            cleanLower.contains("week 17") -> "Week 17: Special Procedures"
            cleanLower.contains("week 18") -> "Week 18: Injunctions"
            cleanLower.contains("week 19") -> "Week 19: Costs & Orders"
            else -> clean
        }
    }

    // Fallback based purely on week parsing
    val weekRegex = """.*week\s*(\d+).*""".toRegex()
    val matchResult = weekRegex.find(cleanLower)
    if (matchResult != null) {
        val num = matchResult.groupValues[1]
        val rest = clean.substringAfter("-").trim().substringAfter(":").trim()
        if (rest.isNotEmpty() && rest != "General" && !rest.equals("week $num", ignoreCase = true)) {
            return "Week $num: $rest"
        } else {
            return "Week $num: General Study"
        }
    }

    return clean
}

// Screen Navigation State for seamless transitions
enum class ScreenState {
    HOME,
    WEEK_SELECT,
    QUIZ,
    BOOKMARKS,
    ANALYTICS,
    EXAM,
    NOTIFICATIONS
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
    val totalTimeSeconds: Int = 0
)

// Active Exam State holder for the 100-Question Exam Simulation
data class ActiveExamState(
    val questions: List<VerbatimQuiz> = emptyList(),
    val userAnswers: List<Int> = emptyList(),
    val isSubmitted: Boolean = false,
    val timeLeftSeconds: Int = 3600, // 60 minutes
    val correctCount: Int = 0,
    val timeTakenSeconds: Int = 0
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuizRepository

    val notificationManager = NotificationSystemManager(application)
    val scheduledNotifications = MutableStateFlow<List<StudyNotification>>(emptyList())
    val deliveredNotifications = MutableStateFlow<List<StudyNotification>>(emptyList())

    fun loadNotifications() {
        scheduledNotifications.value = notificationManager.getScheduledNotifications()
        deliveredNotifications.value = notificationManager.getDeliveredHistory()
    }

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

        // Load courses verbatim from local json files and sort them strictly in chronological order
        topics = repository.loadVerbatimTopics().sortedWith(
            compareBy<TopicBundle> { extractWeekNumber(it.topicName) }
                .thenBy { it.topicName }
        )

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
            notificationManager.checkAndDeliverMissedScheduledNotifications()
            loadNotifications()
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
                title = formatWeekTitle(topicBundle.topicName, topicBundle.category),
                questions = questions,
                answers = List(questions.size) { -1 },
                timeLeftSeconds = totalSeconds,
                totalTimeSeconds = totalSeconds
            )
            resetTimer()
            navigateTo(ScreenState.QUIZ)
        }
    }

    // Returns the name of the next chronological topic in the current active course
    fun getNextTopicName(currentTopicTitle: String): String? {
        val course = currentCourse.value
        val sortedTopics = topics.filter { it.category.equals(course, ignoreCase = true) }
        val currentIndex = sortedTopics.indexOfFirst {
            it.topicName.equals(currentTopicTitle, ignoreCase = true) ||
            formatWeekTitle(it.topicName, it.category).equals(currentTopicTitle, ignoreCase = true)
        }
        if (currentIndex != -1 && currentIndex + 1 < sortedTopics.size) {
            return sortedTopics[currentIndex + 1].topicName
        }
        return null
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
        
        activeExam.value = exam.copy(
            userAnswers = newAnswers
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
            questionsToSave.forEach { (question, selected) ->
                if (selected == question.correctIndex) {
                    correct++
                }
                // Save the attempt to Room database to update analytics
                repository.saveAttempt(
                    weekName = question.weekName,
                    scenario = question.scenario,
                    selectedIndex = selected,
                    correctIndex = question.correctIndex
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
        
        activeQuiz.value = quiz.copy(
            answers = newAnswers
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
            questionsToSave.forEach { (question, selected) ->
                if (selected == question.correctIndex) {
                    correct++
                }
                // Save the attempt to Room database to update analytics
                repository.saveAttempt(
                    weekName = question.weekName,
                    scenario = question.scenario,
                    selectedIndex = selected,
                    correctIndex = question.correctIndex
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
}
