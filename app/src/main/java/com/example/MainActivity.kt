package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import com.example.ui.theme.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val model: QuizViewModel = viewModel()
            val isLightTheme by model.isLightTheme.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = !isLightTheme) {
                MainAppContent(model)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(model: QuizViewModel = viewModel()) {
    val screenState by model.screenState.collectAsStateWithLifecycle()
    val activeQuizState by model.activeQuiz.collectAsStateWithLifecycle()
    val allBookmarks by model.allBookmarks.collectAsStateWithLifecycle()
    val currentStreak by model.currentStreak.collectAsStateWithLifecycle()
    val isLightTheme by model.isLightTheme.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "ELITE EDTECH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = BentoSubtext
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Law Architect",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .background(BentoDarkAccent, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Offline Mode",
                                tint = BentoAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "100% OFFLINE ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = BentoAccent
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Theme Toggle icon button elegantly integrated
                        IconButton(
                            onClick = { model.toggleTheme() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(BentoDarkAccent, RoundedCornerShape(100))
                                .border(1.dp, BentoBorder, RoundedCornerShape(100))
                                .testTag("theme_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (isLightTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Toggle Theme",
                                tint = BentoAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Bookmark icon button elegantly integrated
                        IconButton(
                            onClick = { model.navigateTo(ScreenState.BOOKMARKS) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(BentoDarkAccent, RoundedCornerShape(100))
                                .border(1.dp, BentoBorder, RoundedCornerShape(100))
                                .testTag("nav_bookmarks_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmarks",
                                tint = BentoAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Streak Badge (with orange flame)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(BentoDarkAccent, RoundedCornerShape(100))
                                .border(1.dp, BentoBorder, RoundedCornerShape(100))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = "🔥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$currentStreak Day Streak",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Gradient Avatar representing the elite user profile
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(100))
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(BentoAccent, BentoHighlight)
                                    )
                                )
                                .border(1.5.dp, BentoBorder, RoundedCornerShape(100))
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = BentoBorder,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
            ) {
                NavigationBarItem(
                    selected = screenState == ScreenState.HOME,
                    onClick = { model.navigateTo(ScreenState.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BentoHighlight,
                        selectedTextColor = BentoAccent,
                        indicatorColor = BentoAccent,
                        unselectedIconColor = BentoSubtext,
                        unselectedTextColor = BentoSubtext
                    ),
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = screenState == ScreenState.WEEK_SELECT,
                    onClick = { model.navigateTo(ScreenState.WEEK_SELECT) },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Curriculum") },
                    label = { Text("Quizzes", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BentoHighlight,
                        selectedTextColor = BentoAccent,
                        indicatorColor = BentoAccent,
                        unselectedIconColor = BentoSubtext,
                        unselectedTextColor = BentoSubtext
                    ),
                    modifier = Modifier.testTag("nav_weeks")
                )
                NavigationBarItem(
                    selected = screenState == ScreenState.EXAM,
                    onClick = { model.navigateTo(ScreenState.EXAM) },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Exam Mode") },
                    label = { Text("Exam", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BentoHighlight,
                        selectedTextColor = BentoAccent,
                        indicatorColor = BentoAccent,
                        unselectedIconColor = BentoSubtext,
                        unselectedTextColor = BentoSubtext
                    ),
                    modifier = Modifier.testTag("nav_exam")
                )
                NavigationBarItem(
                    selected = screenState == ScreenState.ANALYTICS,
                    onClick = { model.navigateTo(ScreenState.ANALYTICS) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Insights") },
                    label = { Text("Stats", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BentoHighlight,
                        selectedTextColor = BentoAccent,
                        indicatorColor = BentoAccent,
                        unselectedIconColor = BentoSubtext,
                        unselectedTextColor = BentoSubtext
                    ),
                    modifier = Modifier.testTag("nav_insights")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (screenState) {
                ScreenState.HOME -> HomeScreen(model = model)
                ScreenState.WEEK_SELECT -> WeekSelectScreen(model = model)
                ScreenState.QUIZ -> ActiveQuizScreen(model = model, state = activeQuizState)
                ScreenState.BOOKMARKS -> BookmarksScreen(model = model, bookmarks = allBookmarks)
                ScreenState.ANALYTICS -> AnalyticsInsightsScreen(model = model)
                ScreenState.EXAM -> ExamScreen(model = model)
            }
        }
    }
}

@Composable
fun CourseSelectorTabs(
    activeCourse: String,
    onCourseSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .testTag("course_selector_card"),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BentoBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // First Row of 3 items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoHighlight, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "civil" to "Civil",
                    "corporate" to "Corporate",
                    "property" to "Property"
                ).forEach { (key, label) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeCourse == key) BentoAccent else Color.Transparent)
                            .clickable { onCourseSelected(key) }
                            .padding(vertical = 8.dp)
                            .testTag("course_${key}_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (activeCourse == key) BentoSurface else BentoSubtext
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Second Row of 2 items
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoHighlight, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "ethics" to "Ethics",
                    "criminal" to "Criminal"
                ).forEach { (key, label) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeCourse == key) BentoAccent else Color.Transparent)
                            .clickable { onCourseSelected(key) }
                            .padding(vertical = 8.dp)
                            .testTag("course_${key}_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (activeCourse == key) BentoSurface else BentoSubtext
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (activeCourse) {
                    "civil" -> "Verbatim scenario training for High Court civil jurisdiction, actions, pleadings, and trials."
                    "corporate" -> "Active recall for company formation, governance, corporate restructuring, and winding up."
                    "property" -> "Verbatim practice for deeds, leases, sale of land, mortgages, wills, and personal representatives."
                    "ethics" -> "Active testing on Professional Ethics, regulatory bodies, and rules of professional conduct verbatim."
                    "criminal" -> "Verbatim active recall for Criminal Litigation, trial procedures, constitutional safeguards, and appeals."
                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = BentoSubtext,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun HomeScreen(model: QuizViewModel) {
    val currentStreak by model.currentStreak.collectAsStateWithLifecycle()
    val longestStreak by model.longestStreak.collectAsStateWithLifecycle()
    val weakestWeeks by model.weakestWeeks.collectAsStateWithLifecycle()
    val microQuizState by model.microQuizState.collectAsStateWithLifecycle()
    val overallAccuracy by model.overallAccuracy.collectAsStateWithLifecycle()
    val currentCourse by model.currentCourse.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // --- COURSE SELECTOR TABS ---
        item {
            CourseSelectorTabs(
                activeCourse = currentCourse,
                onCourseSelected = { model.switchCourse(it) }
            )
        }

        // --- BENTO CARD 1: OVERALL PERFORMANCE ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bento_perf_card"),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, BentoBorder),
                colors = CardDefaults.cardColors(containerColor = BentoSurface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Overall Performance",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                color = BentoAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${overallAccuracy.toInt()}%",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (overallAccuracy >= 60f) "+2.1%" else "-1.5%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (overallAccuracy >= 60f) CorrectGreen else IncorrectRed,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Mini visual bar chart / histogram graphic (matching bento grid design)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(48.dp)
                        ) {
                            listOf(16.dp, 28.dp, 20.dp, 44.dp, 36.dp).forEachIndexed { i, h ->
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(h)
                                        .clip(RoundedCornerShape(100))
                                        .background(if (i == 3) BentoAccent else BentoHighlight)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FOCUS TOPIC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = BentoSubtext
                            )
                            Text(
                                text = when (currentCourse) {
                                    "civil" -> "Civil Litigation"
                                    "corporate" -> "Corporate Practice"
                                    "property" -> "Property Law"
                                    "ethics" -> "Professional Ethics"
                                    "criminal" -> "Criminal Litigation"
                                    else -> "Criminal Litigation"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = { model.navigateTo(ScreenState.ANALYTICS) },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoHighlight),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(100)
                        ) {
                            Text("View Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoAccent)
                        }
                    }
                }
            }
        }

        // --- BENTO ROW 2: SIDE-BY-SIDE CARDS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weak Spot Bento Card (Left half)
                val hasWeakSpot = weakestWeeks.isNotEmpty()
                val weakWeekName = if (hasWeakSpot) weakestWeeks[0].weekName else "WEEK 3"
                val weakAccuracy = if (hasWeakSpot) weakestWeeks[0].accuracyPercent.toInt() else 54

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable {
                            model.startTopicQuiz(weakWeekName)
                        }
                        .testTag("bento_weak_spot_card"),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = CardDefaults.cardColors(containerColor = BentoDarkAccent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(IncorrectRed.copy(alpha = 0.2f), RoundedCornerShape(100)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(IncorrectRed, RoundedCornerShape(100))
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WEAK SPOT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = BentoSubtext
                            )
                        }
                        Column {
                            Text(
                                text = weakWeekName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (hasWeakSpot) "$weakAccuracy% accuracy" else "Review Rules",
                                style = MaterialTheme.typography.labelSmall,
                                color = IncorrectRed
                            )
                        }
                    }
                }

                // Longest Streak Bento Card (Right half)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .testTag("bento_streak_card"),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "MAX STREAK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = BentoSubtext
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔥", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$longestStreak Days",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = WarmAmber
                            )
                        }
                        Text(
                            text = "Continuous active recall",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoSubtext
                        )
                    }
                }
            }
        }

        // --- BENTO CARD 3: STUDY BREAK MICRO QUIZ ---
        item {
            if (microQuizState != null) {
                val state = microQuizState!!
                if (state.completed) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bento_micro_quiz_completed"),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, BentoBorder),
                        colors = CardDefaults.cardColors(
                            containerColor = BentoAccent,
                            contentColor = BentoHighlight
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = BentoHighlight,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Study Break Completed!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoHighlight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You scored ${state.correctAnswersCount} of ${state.questions.size} correct.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BentoHighlight.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { model.generateNewMicroQuiz() },
                                colors = ButtonDefaults.buttonColors(containerColor = BentoHighlight),
                                shape = RoundedCornerShape(100)
                            ) {
                                Text("Take Another Study Break", color = BentoAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    val currentQuiz = state.questions[state.currentIndex]
                    val isCoverPage = state.selectedOptionIndex == -1 && state.currentIndex == 0 && !state.isAnswered

                    if (isCoverPage) {
                        // Covered state (matches "3-Min Micro Quiz" in design HTML)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    model.selectOption(-2, isMicro = true)
                                }
                                .testTag("bento_micro_quiz_cover"),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = BentoAccent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "BREAK TIME?",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = BentoHighlight.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "3-Min Micro Quiz",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = BentoHighlight
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Verify Civil Litigation procedural guidelines",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BentoHighlight.copy(alpha = 0.8f)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(BentoHighlight, RoundedCornerShape(100)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Start",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Open interactive question state
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bento_micro_quiz_active"),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, BentoBorder),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Micro Quiz",
                                            tint = BentoAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Micro-Recall Study Break",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                    Button(
                                        onClick = { model.generateNewMicroQuiz() },
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoDarkAccent),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp),
                                        shape = RoundedCornerShape(100)
                                    ) {
                                        Text("New Break", fontSize = 10.sp, color = BentoAccent, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Progress Indicator
                                LinearProgressIndicator(
                                    progress = { (state.currentIndex + 1).toFloat() / state.questions.size.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(100)),
                                    color = BentoAccent,
                                    trackColor = BentoDarkAccent
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Question ${state.currentIndex + 1} of ${state.questions.size} (Verbatim Scenario)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoAccent
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = currentQuiz.scenario,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                                    color = Color.White,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Dynamic Options
                                currentQuiz.options.forEachIndexed { oIdx, option ->
                                    val isSelected = state.selectedOptionIndex == oIdx
                                    val cardBg = when {
                                        state.isAnswered && oIdx == currentQuiz.correctIndex -> CorrectGreen.copy(alpha = 0.2f)
                                        state.isAnswered && isSelected && oIdx != currentQuiz.correctIndex -> IncorrectRed.copy(alpha = 0.2f)
                                        isSelected -> BentoAccent.copy(alpha = 0.15f)
                                        else -> BentoDarkAccent.copy(alpha = 0.3f)
                                    }
                                    val borderStroke = when {
                                        state.isAnswered && oIdx == currentQuiz.correctIndex -> BorderStroke(2.dp, CorrectGreen)
                                        state.isAnswered && isSelected && oIdx != currentQuiz.correctIndex -> BorderStroke(2.dp, IncorrectRed)
                                        isSelected -> BorderStroke(2.dp, BentoAccent)
                                        else -> BorderStroke(1.dp, BentoBorder.copy(alpha = 0.5f))
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(cardBg)
                                            .border(borderStroke, RoundedCornerShape(12.dp))
                                            .clickable(enabled = !state.isAnswered) {
                                                model.selectOption(oIdx, isMicro = true)
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSelected) BentoAccent else BentoDarkAccent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = when(oIdx) { 0 -> "A" 1 -> "B" 2 -> "C" else -> "D" },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) BentoHighlight else Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White
                                        )
                                    }
                                }

                                // Interactive Submission & Grading
                                if (state.isAnswered) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        colors = CardDefaults.cardColors(containerColor = BentoDarkAccent),
                                        border = BorderStroke(1.dp, BentoBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "VERBATIM SOLUTION:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = BentoAccent
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = currentQuiz.verbatimCorrection.ifEmpty { "Procedural guidelines apply." },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { model.nextQuestion(isMicro = true) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("micro_next_btn"),
                                        shape = RoundedCornerShape(100),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoAccent)
                                    ) {
                                        Text(
                                            text = if (state.currentIndex + 1 < state.questions.size) "Next Scenario" else "Complete Study Break",
                                            color = BentoHighlight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { model.lockAndSubmitAnswer(isMicro = true) },
                                        enabled = state.selectedOptionIndex != null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("micro_submit_btn"),
                                        shape = RoundedCornerShape(100),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = BentoAccent,
                                            disabledContainerColor = BentoDarkAccent
                                        )
                                    ) {
                                        Text(
                                            text = "Grade Instant Recall",
                                            color = if (state.selectedOptionIndex != null) BentoHighlight else BentoSubtext,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- BENTO ROW 4: WEEKLY CURRICULUM & RANDOM BOARD SIDE-BY-SIDE ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Curriculum Topic Selector Card (Left side)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CIVIL LIT: WK 3-19",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = BentoSubtext
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "W5" to "Pleadings",
                                "W8" to "Discovery",
                                "W12" to "Summ. Judg."
                            ).forEachIndexed { index, (wk, name) ->
                                val isSelected = index == 2
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) BentoAccent else BentoDarkAccent)
                                        .clickable { model.startTopicQuiz(if(wk == "W5") "Week 5: Pleadings" else if(wk == "W8") "Week 8: Discovery" else "Week 12: Summary Judgment") }
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(if (isSelected) BentoHighlight else BentoBorder, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            wk,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) BentoAccent else Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = name,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BentoHighlight else Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { model.navigateTo(ScreenState.WEEK_SELECT) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoDarkAccent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("All Topics", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoAccent)
                        }
                    }
                }

                // Random Board Card (Right side)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp)
                        .clickable { model.startRandomQuiz() }
                        .testTag("bento_random_board_card"),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Casino",
                                tint = WarmAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "RANDOM BOARD",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BentoSubtext
                            )
                        }

                        Column {
                            Text(
                                text = "Shuffled Scenarios",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "15 randomized civil litigation scenarios from Weeks 3-19.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 11.sp),
                                color = BentoSubtext,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = { model.startRandomQuiz() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Roll Scenarios", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoBg)
                        }
                    }
                }
            }
        }

        // --- BENTO CARD 5: INSIGHT TOGGLE PANEL (BOTTOM WIDE) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, BentoBorder),
                colors = CardDefaults.cardColors(containerColor = BentoSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Toggle 1: Analytics
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { model.navigateTo(ScreenState.ANALYTICS) }
                            .padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(18.dp)
                                .background(BentoHighlight, RoundedCornerShape(100)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(10.dp)
                                    .background(BentoAccent, RoundedCornerShape(100))
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Analytics", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoSubtext)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(BentoBorder)
                    )

                    // Toggle 2: Corrections
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { model.navigateTo(ScreenState.BOOKMARKS) }
                            .padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(18.dp)
                                .background(BentoBorder, RoundedCornerShape(100)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(10.dp)
                                    .background(BentoSubtext, RoundedCornerShape(100))
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Corrections", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoSubtext)
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(BentoBorder)
                    )

                    // Toggle 3: Scenarios
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { model.navigateTo(ScreenState.WEEK_SELECT) }
                            .padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(18.dp)
                                .background(BentoHighlight, RoundedCornerShape(100)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(10.dp)
                                    .background(BentoAccent, RoundedCornerShape(100))
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Scenarios", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoSubtext)
                    }
                }
            }
        }
    }
}

@Composable
fun WeekSelectScreen(model: QuizViewModel) {
    val analytics by model.analyticsState.collectAsStateWithLifecycle()
    val currentCourse by model.currentCourse.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Curriculum Topics",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Select a week to review its verbatim scenario questions. Scoring and explanations are shown instantly.",
            style = MaterialTheme.typography.bodySmall,
            color = BentoSubtext,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        CourseSelectorTabs(
            activeCourse = currentCourse,
            onCourseSelected = { model.switchCourse(it) }
        )

        val isShuffleEnabled by model.isShuffleEnabled.collectAsStateWithLifecycle()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("shuffle_selector_card"),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BentoBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle Icon",
                        tint = if (isShuffleEnabled) BentoAccent else BentoSubtext,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Shuffle Questions",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Randomize question sequence when launching a quiz.",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoSubtext
                        )
                    }
                }
                Switch(
                    checked = isShuffleEnabled,
                    onCheckedChange = { model.toggleShuffleEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BentoHighlight,
                        checkedTrackColor = BentoAccent,
                        uncheckedThumbColor = BentoSubtext,
                        uncheckedTrackColor = BentoHighlight
                    ),
                    modifier = Modifier.testTag("shuffle_switch")
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(model.topics.filter { it.category.equals(currentCourse, ignoreCase = true) }) { topic ->
                val analytic = analytics.find { it.weekName.equals(topic.topicName, ignoreCase = true) }
                val hasAttempted = analytic != null && analytic.totalAnswered > 0
                val accuracyVal = if (hasAttempted) analytic!!.accuracyPercent.toInt() else 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { model.startTopicQuiz(topic.topicName) }
                        .testTag("topic_${topic.topicName.lowercase().replace(" ", "_")}"),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = topic.topicName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                if (hasAttempted) {
                                    val badgeColor = if (accuracyVal >= 60) CorrectGreen else IncorrectRed
                                    Box(
                                        modifier = Modifier
                                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(100))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$accuracyVal%",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${topic.quizzes.size} Verbatim Scenarios",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoSubtext
                            )
                            if (hasAttempted) {
                                Text(
                                    text = "${analytic!!.totalAnswered} scenarios logged",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoSubtext
                                )
                            } else {
                                Text(
                                    text = "Not attempted yet",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoSubtext.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BentoHighlight, RoundedCornerShape(100)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start",
                                tint = BentoAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveQuizScreen(model: QuizViewModel, state: ActiveQuizState?) {
    if (state == null) return

    val context = LocalContext.current
    val allBookmarks by model.allBookmarks.collectAsStateWithLifecycle()
    var showSubmitConfirmation by remember { mutableStateOf(false) }

    // Start automated, mandatory timer decrement
    LaunchedEffect(state.completed) {
        if (!state.completed) {
            while (true) {
                kotlinx.coroutines.delay(1000L)
                model.decrementQuizTime()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(16.dp)
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { model.navigateTo(ScreenState.WEEK_SELECT) },
                modifier = Modifier
                    .size(40.dp)
                    .background(BentoDarkAccent, RoundedCornerShape(12.dp))
                    .testTag("quiz_back_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = state.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.size(40.dp))
        }

        // Automatic Timer & Progress Indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, BentoBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = if (state.timeLeftSeconds < 60 && !state.completed) IncorrectRed else BentoAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.completed) "Session Ended" else "Mandatory Countdown",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    val mins = state.timeLeftSeconds / 60
                    val secs = state.timeLeftSeconds % 60
                    val timerStr = String.format("%02d:%02d", mins, secs)

                    Text(
                        text = if (state.completed) "Completed" else timerStr,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        ),
                        color = if (state.timeLeftSeconds < 60 && !state.completed) IncorrectRed else BentoAccent
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Time progress bar
                val timeProgress = if (state.totalTimeSeconds > 0 && !state.completed) {
                    state.timeLeftSeconds.toFloat() / state.totalTimeSeconds.toFloat()
                } else 1f

                LinearProgressIndicator(
                    progress = { timeProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(100)),
                    color = if (state.timeLeftSeconds < 60 && !state.completed) IncorrectRed else BentoAccent,
                    trackColor = BentoDarkAccent
                )
            }
        }

        // Main Continuous Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Results summary card if completed
            if (state.completed) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Events Trophy",
                                tint = WarmAmber,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Grade Summary",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val answeredCount = state.answers.count { it != -1 }
                            Text(
                                text = "You answered $answeredCount of ${state.questions.size} questions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoSubtext,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Score circular visual
                            Box(
                                modifier = Modifier.size(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val scorePercent = if (state.questions.isNotEmpty()) {
                                    state.correctAnswersCount.toFloat() / state.questions.size.toFloat()
                                } else 0f
                                val darkAccentColor = BentoDarkAccent
                                val strokeColor = if (scorePercent >= 0.6f) CorrectGreen else IncorrectRed
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawArc(
                                        color = darkAccentColor,
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = strokeColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f * scorePercent,
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${state.correctAnswersCount}/${state.questions.size}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${(scorePercent * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BentoSubtext
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Retry / Close Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { model.restartQuiz() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoDarkAccent),
                                    shape = RoundedCornerShape(100),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Retake Quiz", color = BentoAccent, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { model.navigateTo(ScreenState.WEEK_SELECT) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                                    shape = RoundedCornerShape(100),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Exit", color = BentoHighlight, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Questions continuous list
            items(state.questions.size) { qIdx ->
                val question = state.questions[qIdx]
                val selectedAnswer = state.answers.getOrElse(qIdx) { -1 }
                val isBookmarked = allBookmarks.any { it.scenario == question.scenario }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Card Header (Question index & Bookmark)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "QUESTION ${qIdx + 1} OF ${state.questions.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BentoAccent
                            )

                            IconButton(
                                onClick = {
                                    model.toggleBookmark(question) { _ -> }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BentoDarkAccent, RoundedCornerShape(10.dp))
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark Question",
                                    tint = BentoAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Verbatim Case Scenario
                        Text(
                            text = "CASE SCENARIO",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BentoSubtext
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.scenario,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render options
                        question.options.forEachIndexed { oIdx, optionText ->
                            val isSelected = selectedAnswer == oIdx
                            val isCorrectOption = question.correctIndex == oIdx

                            val cardBg = when {
                                state.completed && isCorrectOption -> CorrectGreen.copy(alpha = 0.2f)
                                state.completed && isSelected && !isCorrectOption -> IncorrectRed.copy(alpha = 0.2f)
                                isSelected -> BentoAccent.copy(alpha = 0.15f)
                                else -> BentoDarkAccent.copy(alpha = 0.3f)
                            }

                            val borderStroke = when {
                                state.completed && isCorrectOption -> BorderStroke(2.dp, CorrectGreen)
                                state.completed && isSelected && !isCorrectOption -> BorderStroke(2.dp, IncorrectRed)
                                isSelected -> BorderStroke(2.dp, BentoAccent)
                                else -> BorderStroke(1.dp, BentoBorder.copy(alpha = 0.5f))
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(cardBg)
                                    .border(borderStroke, RoundedCornerShape(16.dp))
                                    .clickable(enabled = !state.completed) {
                                        model.selectQuizOption(qIdx, oIdx)
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) BentoAccent else BentoDarkAccent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (oIdx) { 0 -> "A" 1 -> "B" 2 -> "C" else -> "D" },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BentoHighlight else Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                if (state.completed) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (isCorrectOption) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Correct",
                                            tint = CorrectGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Incorrect",
                                            tint = IncorrectRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Verbatim Correction panel if completed
                        if (state.completed) {
                            Spacer(modifier = Modifier.height(14.dp))
                            val isCorrect = selectedAnswer == question.correctIndex
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BentoDarkAccent),
                                border = BorderStroke(1.dp, if (isCorrect) CorrectGreen else IncorrectRed)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (isCorrect) "✓ CORRECT ANSWER KEY" else "✗ RECALL MISS / CORRECTION REQUIRED",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = if (isCorrect) CorrectGreen else IncorrectRed
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = question.verbatimCorrection.ifEmpty { "Refer to Criminal Litigation rules. Correct option is index: ${question.correctIndex + 1}" },
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Footer Submit Button / Score navigation buttons
            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (!state.completed) {
                    val answeredCount = state.answers.count { it != -1 }
                    Button(
                        onClick = { showSubmitConfirmation = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("quiz_submit_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoAccent)
                    ) {
                        Text(
                            text = "SUBMIT QUIZ ($answeredCount/${state.questions.size})",
                            color = BentoHighlight,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    Button(
                        onClick = { model.navigateTo(ScreenState.WEEK_SELECT) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("quiz_finish_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoAccent)
                    ) {
                        Text(
                            text = "FINISH SESSION",
                            color = BentoHighlight,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Submit confirmation Dialog
    if (showSubmitConfirmation) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirmation = false },
            title = {
                Text(
                    text = "Submit Quiz?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                val answeredCount = state.answers.count { it != -1 }
                Text(
                    text = "You have answered $answeredCount of ${state.questions.size} questions.\n\nAre you sure you want to submit your quiz and view the detailed correction key?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmation = false
                        model.submitQuiz()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                    shape = RoundedCornerShape(100)
                ) {
                    Text("Submit", color = BentoHighlight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmation = false }) {
                    Text("Cancel", color = BentoAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}


@Composable
fun BookmarksScreen(model: QuizViewModel, bookmarks: List<BookmarkedQuestion>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Flagged Scenarios",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color.White
            )
            IconButton(
                onClick = { model.navigateTo(ScreenState.HOME) },
                modifier = Modifier
                    .size(36.dp)
                    .background(BentoDarkAccent, RoundedCornerShape(100))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        Text(
            text = "Hard legal principles saved for specialized active recall sessions.",
            style = MaterialTheme.typography.bodySmall,
            color = BentoSubtext,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (bookmarks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "No Flagged",
                        modifier = Modifier.size(64.dp),
                        tint = BentoDarkAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No bookmarks saved.", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Flag questions during your active quizzes to save here.", color = BentoSubtext, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(bookmarks) { bookmark ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = bookmark.weekName,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoAccent,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Saved",
                                    tint = BentoAccent,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            model.toggleBookmark(
                                                VerbatimQuiz(
                                                    scenario = bookmark.scenario,
                                                    options = bookmark.optionsJson.split("|||"),
                                                    correctIndex = bookmark.correctIndex,
                                                    verbatimCorrection = bookmark.verbatimCorrection,
                                                    weekName = bookmark.weekName
                                                )
                                            )
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = bookmark.scenario,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BentoBorder)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "VERBATIM SOLUTION KEY:",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bookmark.verbatimCorrection.ifEmpty { "Refer to legal procedural rules." },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsInsightsScreen(model: QuizViewModel) {
    val showWeekAccuracyChart by model.showWeekAccuracyChart.collectAsStateWithLifecycle()
    val showTrendChart by model.showTrendChart.collectAsStateWithLifecycle()
    val showCategoryDistribution by model.showCategoryDistribution.collectAsStateWithLifecycle()
    val analytics by model.analyticsState.collectAsStateWithLifecycle()
    val attempts by model.allAttempts.collectAsStateWithLifecycle()
    val overallAccuracy by model.overallAccuracy.collectAsStateWithLifecycle()
    val totalQuizzes by model.totalCompletedQuizzes.collectAsStateWithLifecycle()
    val currentCourse by model.currentCourse.collectAsStateWithLifecycle()
    val streak by model.currentStreak.collectAsStateWithLifecycle()

    // New stateflows for advanced analytics & notifications
    val topicMastery = model.topicMasteryIndex.collectAsStateWithLifecycle(initialValue = emptyMap()).value
    val retention = model.ebbinghausRetention.collectAsStateWithLifecycle(initialValue = emptyMap()).value
    val velocity = model.cognitiveVelocity.collectAsStateWithLifecycle(initialValue = 75f).value
    val fatigueAlert = model.cognitiveFatigueAlert.collectAsStateWithLifecycle(initialValue = "").value ?: ""
    val passProbability = model.examPassProbability.collectAsStateWithLifecycle(initialValue = 50f).value
    val notifications = model.allNotifications.collectAsStateWithLifecycle(initialValue = emptyList()).value

    // Calculate Active Recall Retention Score (ARRS)
    val arrsScore = minOf(1000, ((overallAccuracy * 8) + (attempts.size * 4) + (streak * 20)).toInt())
    val arrsLevel = when {
        arrsScore < 400 -> "Novice Advocate"
        arrsScore < 650 -> "Proficient Practitioner"
        arrsScore < 850 -> "Advanced Recall Specialist"
        else -> "Master Legal Counsel"
    }

    // Cognitive Domain Breakdown
    val currentCourseTopics = model.topics.filter { it.category.equals(currentCourse, ignoreCase = true) }
    val currentCourseAttempts = attempts.filter { attempt ->
        currentCourseTopics.any { it.topicName.equals(attempt.weekName, ignoreCase = true) }
    }

    var proceduralCount = 0
    var proceduralCorrect = 0
    var statutoryCount = 0
    var statutoryCorrect = 0

    currentCourseAttempts.forEach { attempt ->
        val topic = currentCourseTopics.find { it.topicName.equals(attempt.weekName, ignoreCase = true) }
        val quiz = topic?.quizzes?.find { it.scenario.equals(attempt.scenario, ignoreCase = true) }
        val isCorrect = attempt.selectedIndex == attempt.correctIndex

        if (quiz != null) {
            val text = (quiz.scenario + " " + quiz.verbatimCorrection).lowercase()
            val isProc = text.contains("court") || text.contains("jurisdiction") || text.contains("parties") || 
                         text.contains("procedure") || text.contains("suit") || text.contains("motion") || 
                         text.contains("pleading") || text.contains("appeal") || text.contains("judge")
            if (isProc) {
                proceduralCount++
                if (isCorrect) proceduralCorrect++
            } else {
                statutoryCount++
                if (isCorrect) statutoryCorrect++
            }
        }
    }

    val proceduralAccuracy = if (proceduralCount > 0) (proceduralCorrect.toFloat() / proceduralCount * 100f).toInt() else 0
    val statutoryAccuracy = if (statutoryCount > 0) (statutoryCorrect.toFloat() / statutoryCount * 100f).toInt() else 0

    // Spaced Repetition suggestions
    val spacingRecommendations = currentCourseTopics.take(5).map { topic ->
        val analytic = analytics.find { it.weekName.equals(topic.topicName, ignoreCase = true) }
        val status = when {
            analytic == null || analytic.totalAnswered == 0 -> "UNTESTED BASELINE"
            analytic.accuracyPercent < 50f -> "CRITICAL REFRESH"
            analytic.accuracyPercent < 75f -> "MODERATE RECALL"
            else -> "STRONG RETENTION"
        }
        val interval = when (status) {
            "UNTESTED BASELINE" -> "Study next"
            "CRITICAL REFRESH" -> "Review in 24h"
            "MODERATE RECALL" -> "Review in 3d"
            else -> "Review in 7d"
        }
        val badgeColor = when (status) {
            "UNTESTED BASELINE" -> WarmAmber
            "CRITICAL REFRESH" -> IncorrectRed
            "MODERATE RECALL" -> BentoAccent
            else -> CorrectGreen
        }
        Triple(topic.topicName, interval, badgeColor)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Performance Stats",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color.White
            )
            IconButton(
                onClick = { model.navigateTo(ScreenState.HOME) },
                modifier = Modifier
                    .size(36.dp)
                    .background(BentoDarkAccent, RoundedCornerShape(100))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        Text(
            text = "US Patent US8340568B2 guided indicators to track your subject-wise retention over time.",
            style = MaterialTheme.typography.bodySmall,
            color = BentoSubtext,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Bento Switchboard / Filter panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BentoBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "VISUALIZATION ENGINE",
                    fontWeight = FontWeight.Bold,
                    color = BentoAccent,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { model.toggleWeekAccuracyChart() }
                    ) {
                        Checkbox(
                            checked = showWeekAccuracyChart,
                            onCheckedChange = { model.toggleWeekAccuracyChart() },
                            colors = CheckboxDefaults.colors(checkedColor = BentoAccent, uncheckedColor = BentoBorder)
                        )
                        Text("Accuracy", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { model.toggleTrendChart() }
                    ) {
                        Checkbox(
                            checked = showTrendChart,
                            onCheckedChange = { model.toggleTrendChart() },
                            colors = CheckboxDefaults.colors(checkedColor = BentoAccent, uncheckedColor = BentoBorder)
                        )
                        Text("Trend Graph", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { model.toggleCategoryDistribution() }
                    ) {
                        Checkbox(
                            checked = showCategoryDistribution,
                            onCheckedChange = { model.toggleCategoryDistribution() },
                            colors = CheckboxDefaults.colors(checkedColor = BentoAccent, uncheckedColor = BentoBorder)
                        )
                        Text("Progress", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Overall Score Metrics
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Hero Card: ARRS
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACTIVE RECALL RETENTION SCORE (ARRS)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoAccent,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$arrsScore / 1000",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Level: $arrsLevel",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BentoSubtext
                                )
                            }
                            // Premium Circular Ring representing ARRS / 1000
                            val bentoDarkAccentColor = BentoDarkAccent
                            val bentoAccentColor = BentoAccent
                            Box(
                                modifier = Modifier.size(64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawArc(
                                        color = bentoDarkAccentColor,
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx())
                                    )
                                    drawArc(
                                        color = bentoAccentColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f * (arrsScore / 1000f),
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Text(
                                    text = "${(arrsScore / 10).toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Side-by-side simple cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, BentoBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Accuracy Rating", fontSize = 10.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${overallAccuracy.toInt()}%",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (overallAccuracy >= 60f) CorrectGreen else IncorrectRed
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, BentoBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Scenarios Finished", fontSize = 10.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$totalQuizzes",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmAmber
                                )
                            }
                        }
                    }

                    // Subject Mastery Profile - Display performance metrics per subject individually
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "SUBJECT MASTERY PROFILE",
                                fontWeight = FontWeight.Bold,
                                color = BentoAccent,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "Individual performance metrics to identify specific knowledge gaps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoSubtext,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            val subjectsList = listOf(
                                Triple("civil", "Civil Litigation", "⚖️"),
                                Triple("corporate", "Corporate Practice", "🏢"),
                                Triple("property", "Property Law", "📜"),
                                Triple("ethics", "Professional Ethics", "🛡️"),
                                Triple("criminal", "Criminal Litigation", "🚨")
                            )

                            subjectsList.forEachIndexed { sIdx, (catId, catLabel, catEmoji) ->
                                val catTopics = model.topics.filter { it.category.equals(catId, ignoreCase = true) }
                                val catAttempts = attempts.filter { attempt ->
                                    catTopics.any { it.topicName.equals(attempt.weekName, ignoreCase = true) }
                                }
                                val catTotal = catAttempts.size
                                val catCorrect = catAttempts.count { it.selectedIndex == it.correctIndex }
                                val catAccuracy = if (catTotal > 0) (catCorrect.toFloat() / catTotal * 100f).toInt() else 0

                                val statusText = when {
                                    catTotal == 0 -> "UNTESTED"
                                    catAccuracy < 50 -> "CRITICAL REFRESH"
                                    catAccuracy < 75 -> "MODERATE RECALL"
                                    else -> "STRONG RETENTION"
                                }
                                val statusColor = when (statusText) {
                                    "UNTESTED" -> WarmAmber
                                    "CRITICAL REFRESH" -> IncorrectRed
                                    "MODERATE RECALL" -> BentoAccent
                                    else -> CorrectGreen
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(catEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = catLabel,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (catTotal > 0) "$catCorrect / $catTotal answered" else "0 questions answered",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BentoSubtext
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(100))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (catTotal > 0) "$catAccuracy% Accuracy" else "UNTESTED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
                                            )
                                        }
                                    }
                                }

                                if (sIdx < subjectsList.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        color = BentoBorder.copy(alpha = 0.5f),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }
                    }

                    // Cognitive Domain Breakdown
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Cognitive Domain Accuracy",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Applying verbatim precedents vs direct statutory recall",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoSubtext
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Case Application / Procedural row
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Case Application (Procedural)", fontSize = 12.sp, color = BentoText, fontWeight = FontWeight.Medium)
                                    Text("$proceduralAccuracy%", fontSize = 12.sp, color = BentoAccent, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(100))
                                        .background(BentoDarkAccent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(proceduralAccuracy / 100f)
                                            .clip(RoundedCornerShape(100))
                                            .background(BentoAccent)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Direct Factual / Statutory recall row
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Direct Factual / Statutory Recall", fontSize = 12.sp, color = BentoText, fontWeight = FontWeight.Medium)
                                    Text("$statutoryAccuracy%", fontSize = 12.sp, color = WarmAmber, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(100))
                                        .background(BentoDarkAccent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(statutoryAccuracy / 100f)
                                            .clip(RoundedCornerShape(100))
                                            .background(WarmAmber)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Week-by-Week Accuracy Bar Chart
            if (showWeekAccuracyChart) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Topic Retention Analytics",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Weekly Civil Litigation accuracy indicators",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoSubtext
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            val bentoBorderColor = BentoBorder
                            val bentoDarkAccentColor = BentoDarkAccent
                            val bentoAccentColor = BentoAccent
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val barWidth = 12.dp.toPx()
                                    val space = 18.dp.toPx()
                                    val maxHeight = size.height - 16.dp.toPx()

                                    // Draw background line
                                    drawLine(
                                        color = bentoBorderColor,
                                        start = Offset(0f, maxHeight),
                                        end = Offset(size.width, maxHeight),
                                        strokeWidth = 2f
                                    )

                                    analytics.take(10).forEachIndexed { index, weekAnalytic ->
                                        val x = index * (barWidth + space) + 12.dp.toPx()
                                        // Background bento container bar
                                        drawRect(
                                            color = bentoDarkAccentColor,
                                            topLeft = Offset(x, 0f),
                                            size = androidx.compose.ui.geometry.Size(barWidth, maxHeight)
                                        )
                                        // Highlighted accuracy bar
                                        val barHeight = maxHeight * (weekAnalytic.accuracyPercent / 100f)
                                        drawRect(
                                            color = bentoAccentColor,
                                            topLeft = Offset(x, maxHeight - barHeight),
                                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("W3", fontSize = 9.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Text("W6", fontSize = 9.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Text("W9", fontSize = 9.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Text("W12", fontSize = 9.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Text("W15", fontSize = 9.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Score Trend Timeline Graph (Line Chart)
            if (showTrendChart) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Retention Timeline",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Gradient trend of consecutive answers",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoSubtext
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                            ) {
                                if (attempts.size < 2) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "A minimum of 2 test attempts is required to draw trends.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BentoSubtext,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    val bentoAccentColor = BentoAccent
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val maxH = size.height - 12.dp.toPx()
                                        val points = attempts.take(10).reversed()
                                        val stepX = size.width / (points.size - 1)

                                        var lastOffset = Offset.Unspecified

                                        points.forEachIndexed { idx, attempt ->
                                            val isCorrect = attempt.selectedIndex == attempt.correctIndex
                                            val valY = if (isCorrect) maxH * 0.15f else maxH * 0.85f
                                            val currentOffset = Offset(idx * stepX, valY)

                                            if (lastOffset != Offset.Unspecified) {
                                                drawLine(
                                                    color = bentoAccentColor,
                                                    start = lastOffset,
                                                    end = currentOffset,
                                                    strokeWidth = 3.dp.toPx()
                                                )
                                            }
                                            drawCircle(
                                                color = if (isCorrect) CorrectGreen else IncorrectRed,
                                                radius = 6.dp.toPx(),
                                                center = currentOffset
                                            )
                                            lastOffset = currentOffset
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Radial sweep gauge for general completeness
            if (showCategoryDistribution) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Curriculum Completeness",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Text(
                                text = "Percentage of weeks attempted",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoSubtext,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val attemptedTopics = analytics.count { it.totalAnswered > 0 }
                            val totalTopics = model.topics.size
                            val completenessPercent = if (totalTopics > 0) attemptedTopics.toFloat() / totalTopics.toFloat() else 0f

                            Box(
                                modifier = Modifier.size(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val bentoDarkAccentColor = BentoDarkAccent
                                val warmAmberColor = WarmAmber
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawArc(
                                        color = bentoDarkAccentColor,
                                        startAngle = 135f,
                                        sweepAngle = 270f,
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = warmAmberColor,
                                        startAngle = 135f,
                                        sweepAngle = 270f * completenessPercent,
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$attemptedTopics/$totalTopics",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text("Weeks", fontSize = 8.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Advanced Analytics Panel
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "COGNITIVE & RETENTION INSIGHTS",
                        fontWeight = FontWeight.Bold,
                        color = BentoAccent,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )

                    // Average Mastery and Retention calculations
                    val avgMastery = if (topicMastery.isNotEmpty()) topicMastery.values.average().toFloat() else 0f
                    val avgRetention = if (retention.isNotEmpty()) retention.values.average().toFloat() else 100f

                    // Side-by-side: Mastery Index & Retention Curve
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, BentoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Mastery Index (IRT)", fontSize = 10.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${avgMastery.toInt()}%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Weighted by question difficulty constraints",
                                    fontSize = 9.sp,
                                    color = BentoSubtext,
                                    lineHeight = 12.sp
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, BentoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Forgetting Curve", fontSize = 10.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${avgRetention.toInt()}%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (avgRetention >= 70f) CorrectGreen else WarmAmber
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Estimated active recall chance today",
                                    fontSize = 9.sp,
                                    color = BentoSubtext,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }

                    // Side-by-side: Cognitive Velocity & Pass Probability
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, BentoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Cognitive Velocity", fontSize = 10.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${velocity.toInt()} pts",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Average response speed and accuracy rating",
                                    fontSize = 9.sp,
                                    color = BentoSubtext,
                                    lineHeight = 12.sp
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = BentoSurface),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, BentoBorder)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Exam Pass Prob.", fontSize = 10.sp, color = BentoSubtext, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${passProbability.toInt()}%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (passProbability >= 70f) CorrectGreen else WarmAmber
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Confidence interval based on historical data",
                                    fontSize = 9.sp,
                                    color = BentoSubtext,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }

                    // Cognitive Fatigue Alerts
                    if (fatigueAlert.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = IncorrectRed.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, IncorrectRed.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(IncorrectRed.copy(alpha = 0.2f), RoundedCornerShape(100)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Fatigue Alert",
                                        tint = IncorrectRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "COGNITIVE FATIGUE THRESHOLD",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IncorrectRed,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = fatigueAlert,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // Smart Weak-Spot Passive Review Log
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "PASSIVE RECALL NOTIFICATION HISTORY",
                                        fontWeight = FontWeight.Bold,
                                        color = BentoAccent,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Memorize legal rules pushed to your home screen passively",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BentoSubtext
                                    )
                                }
                                Button(
                                    onClick = { model.simulatePassiveNotification() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoDarkAccent),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Test Push", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (notifications.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No notifications generated yet. Pushes trigger randomly based on incorrect answers to weak topics.",
                                        fontSize = 11.sp,
                                        color = BentoSubtext,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    notifications.reversed().take(5).forEach { notif ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = BentoBg),
                                            shape = RoundedCornerShape(16.dp),
                                            border = BorderStroke(1.dp, BentoBorder.copy(alpha = 0.5f))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(WarmAmber.copy(alpha = 0.15f), RoundedCornerShape(100))
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = notif.topicName.uppercase(),
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = WarmAmber
                                                        )
                                                    }
                                                    Text(
                                                        text = "Delivered",
                                                        fontSize = 8.sp,
                                                        color = CorrectGreen,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = notif.ruleFact,
                                                    fontSize = 12.sp,
                                                    color = Color.White,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Spaced Repetition Recommendations Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Spaced Repetition Schedule",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Algorithmic recall suggestions based on topic mastery",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoSubtext
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        spacingRecommendations.forEach { (topic, interval, color) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = topic,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(100))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = interval,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = color
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { model.startSrsDueQuiz() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("start_srs_due_quiz"),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Practice Due SRS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoBg)
                            }

                            Button(
                                onClick = { model.startWeakestPracticeQuiz() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("start_weakest_practice_quiz"),
                                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Weakest 20 Practice", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BentoBg)
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { model.resetDatabase() },
                    colors = ButtonDefaults.buttonColors(containerColor = IncorrectRed),
                    shape = RoundedCornerShape(100),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear All Study Statistics", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(model: QuizViewModel) {
    val examState by model.activeExam.collectAsStateWithLifecycle()
    var showSubmitConfirmation by remember { mutableStateOf(false) }

    if (examState == null) {
        // --- 100-Question Exam Welcome/Start Screen ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exam_welcome_card"),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚖️ EXAM SIMULATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = BentoAccent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "100-Question Board Exam",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The ultimate test of competence for Nigerian Law School bar exams.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoSubtext,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BentoBorder),
                    colors = CardDefaults.cardColors(containerColor = BentoDarkAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "SIMULATION RULES & PARAMETERS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = BentoAccent
                        )

                        Row(verticalAlignment = Alignment.Top) {
                            Text("📚", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "100-Question Comprehensive Mix",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Pulls exactly 20 questions from each of our 5 main subjects: Civil Litigation, Corporate Practice, Property Law, Professional Ethics, and Criminal Litigation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BentoSubtext
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Text("🔀", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Full Shuffling Algorithm",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Questions from all subjects are randomly shuffled (using Fisher-Yates) for every new attempt to ensure a unique mix every single time.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BentoSubtext
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Text("⏱️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Strict 60-Minute Countdown",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "A hard-coded timer of exactly 60 minutes with a persistent display. The exam is automatically submitted and graded once time expires.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BentoSubtext
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Top) {
                            Text("📝", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Delayed Answer Evaluation",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "No instant feedback! The app hides correct/incorrect markers and detailed explanations until you explicitly click 'Submit Exam' at the bottom.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BentoSubtext
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { model.start100QuestionExam() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_exam_simulation_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "START EXAM SIMULATION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = BentoBg
                    )
                }
            }
        }
    } else {
        val state = examState!!
        val answeredCount = state.userAnswers.count { it != -1 }
        val totalQuestions = state.questions.size
        
        LaunchedEffect(state.isSubmitted) {
            if (!state.isSubmitted) {
                while (true) {
                    kotlinx.coroutines.delay(1000L)
                    model.decrementExamTime()
                }
            }
        }

        if (showSubmitConfirmation) {
            AlertDialog(
                onDismissRequest = { showSubmitConfirmation = false },
                title = { Text("Submit Exam?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("You have answered $answeredCount of $totalQuestions questions. Are you sure you want to submit your exam for immediate grading?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSubmitConfirmation = false
                            model.submitExam()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoAccent)
                    ) {
                        Text("Submit & Grade", color = BentoBg, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubmitConfirmation = false }) {
                        Text("Cancel", color = BentoAccent, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BentoBorder),
                colors = CardDefaults.cardColors(containerColor = BentoSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val mins = state.timeLeftSeconds / 60
                            val secs = state.timeLeftSeconds % 60
                            val timerStr = String.format("%02d:%02d", mins, secs)
                            val isLowTime = state.timeLeftSeconds < 300
                            
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (state.isSubmitted) BentoSubtext else if (isLowTime) IncorrectRed else BentoAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.isSubmitted) "EXAM COMPLETED" else timerStr,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = if (state.isSubmitted) Color.White else if (isLowTime) IncorrectRed else Color.White
                            )
                        }

                        Text(
                            text = if (state.isSubmitted) {
                                "Score: ${state.correctCount} / $totalQuestions"
                            } else {
                                "Answered: $answeredCount / $totalQuestions"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val progressValue = if (state.isSubmitted) {
                        state.correctCount.toFloat() / totalQuestions.toFloat()
                    } else {
                        answeredCount.toFloat() / totalQuestions.toFloat()
                    }
                    
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(100)),
                        color = if (state.isSubmitted) {
                            if (progressValue >= 0.7f) CorrectGreen else if (progressValue >= 0.4f) WarmAmber else IncorrectRed
                        } else BentoAccent,
                        trackColor = BentoDarkAccent
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
            ) {
                if (state.isSubmitted) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exam_results_summary_card"),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, BentoBorder),
                            colors = CardDefaults.cardColors(containerColor = BentoDarkAccent)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val scorePercent = (state.correctCount.toFloat() / totalQuestions.toFloat()) * 100f
                                
                                Text(
                                    text = "SIMULATION SCORE REPORT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = BentoSubtext
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = "${scorePercent.toInt()}%",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 48.sp
                                    ),
                                    color = if (scorePercent >= 70f) CorrectGreen else if (scorePercent >= 40f) WarmAmber else IncorrectRed
                                )
                                Text(
                                    text = "${state.correctCount} Correct of $totalQuestions",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val badgeText = when {
                                    scorePercent >= 70f -> "PASSED (Excellent - Honors Class)"
                                    scorePercent >= 40f -> "PASSED (Satisfactory)"
                                    else -> "FAILED (Review and Retake Needed)"
                                }
                                val badgeBg = when {
                                    scorePercent >= 70f -> CorrectGreen.copy(alpha = 0.2f)
                                    scorePercent >= 40f -> WarmAmber.copy(alpha = 0.2f)
                                    else -> IncorrectRed.copy(alpha = 0.2f)
                                }
                                val badgeColor = when {
                                    scorePercent >= 70f -> CorrectGreen
                                    scorePercent >= 40f -> WarmAmber
                                    else -> IncorrectRed
                                }

                                Box(
                                    modifier = Modifier
                                        .background(badgeBg, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = badgeColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "SUBJECT-WISE BREAKDOWN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BentoAccent,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val subjectCategories = listOf(
                                    "civil" to "Civil Litigation",
                                    "corporate" to "Corporate Practice",
                                    "property" to "Property Law",
                                    "ethics" to "Professional Ethics",
                                    "criminal" to "Criminal Litigation"
                                )

                                subjectCategories.forEach { (catKey, catName) ->
                                    val catQuestions = state.questions.filter { q ->
                                        val topicBundle = model.topics.find { it.topicName.equals(q.weekName, ignoreCase = true) }
                                        topicBundle?.category?.equals(catKey, ignoreCase = true) ?: false
                                    }
                                    
                                    val totalCatCount = catQuestions.size
                                    if (totalCatCount > 0) {
                                        val catCorrectCount = catQuestions.count { q ->
                                            val qIdx = state.questions.indexOf(q)
                                            state.userAnswers[qIdx] == q.correctIndex
                                        }
                                        val catPercent = (catCorrectCount.toFloat() / totalCatCount.toFloat()) * 100f

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = catName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "$catCorrectCount / $totalCatCount (${catPercent.toInt()}%)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (catPercent >= 70f) CorrectGreen else if (catPercent >= 40f) WarmAmber else IncorrectRed
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = { model.start100QuestionExam() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("RETAKE EXAM", fontWeight = FontWeight.Bold, color = BentoBg)
                                    }

                                    Button(
                                        onClick = { model.resetExam() },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoHighlight),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("EXIT", fontWeight = FontWeight.Bold, color = BentoAccent)
                                    }
                                }
                            }
                        }
                    }
                }

                itemsIndexed(state.questions) { index, question ->
                    val userSelected = state.userAnswers[index]
                    val isCorrect = userSelected == question.correctIndex
                    
                    val topicBundle = model.topics.find { it.topicName.equals(question.weekName, ignoreCase = true) }
                    val category = topicBundle?.category ?: "unknown"
                    
                    val categoryLabel = when (category) {
                        "civil" -> "CIVIL LITIGATION"
                        "corporate" -> "CORPORATE PRACTICE"
                        "property" -> "PROPERTY LAW"
                        "ethics" -> "PROFESSIONAL ETHICS"
                        "criminal" -> "CRIMINAL LITIGATION"
                        else -> "COMPREHENSIVE"
                    }

                    val categoryColor = when (category) {
                        "civil" -> androidx.compose.ui.graphics.Color(0xFF3498DB)
                        "corporate" -> androidx.compose.ui.graphics.Color(0xFF9B59B6)
                        "property" -> androidx.compose.ui.graphics.Color(0xFF1ABC9C)
                        "ethics" -> androidx.compose.ui.graphics.Color(0xFFF1C40F)
                        "criminal" -> androidx.compose.ui.graphics.Color(0xFFE67E22)
                        else -> BentoAccent
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exam_question_card_${index}"),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (state.isSubmitted) {
                                if (isCorrect) CorrectGreen.copy(alpha = 0.5f) else IncorrectRed.copy(alpha = 0.5f)
                            } else BentoBorder
                        ),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Question ${index + 1} of $totalQuestions",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BentoSubtext
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .border(1.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = categoryLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = categoryColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = question.scenario,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            question.options.forEachIndexed { oIdx, option ->
                                val isOptionSelected = userSelected == oIdx
                                
                                val optionBg = when {
                                    state.isSubmitted && oIdx == question.correctIndex -> CorrectGreen.copy(alpha = 0.15f)
                                    state.isSubmitted && isOptionSelected && oIdx != question.correctIndex -> IncorrectRed.copy(alpha = 0.15f)
                                    isOptionSelected -> BentoAccent.copy(alpha = 0.15f)
                                    else -> BentoDarkAccent.copy(alpha = 0.3f)
                                }

                                val optionBorder = when {
                                    state.isSubmitted && oIdx == question.correctIndex -> BorderStroke(2.dp, CorrectGreen)
                                    state.isSubmitted && isOptionSelected && oIdx != question.correctIndex -> BorderStroke(2.dp, IncorrectRed)
                                    isOptionSelected -> BorderStroke(2.dp, BentoAccent)
                                    else -> BorderStroke(1.dp, BentoBorder.copy(alpha = 0.5f))
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(optionBg)
                                        .border(optionBorder, RoundedCornerShape(12.dp))
                                        .clickable(enabled = !state.isSubmitted) {
                                            model.selectExamOption(index, oIdx)
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isOptionSelected) BentoAccent else BentoDarkAccent
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when(oIdx) { 0 -> "A" 1 -> "B" 2 -> "C" else -> "D" },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOptionSelected) BentoHighlight else Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (state.isSubmitted) {
                                        if (oIdx == question.correctIndex) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Correct Option",
                                                tint = CorrectGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else if (isOptionSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = "Your Incorrect Option",
                                                tint = IncorrectRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (state.isSubmitted) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = BentoDarkAccent),
                                    border = BorderStroke(1.dp, BentoBorder)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "Explanation Info",
                                                tint = BentoAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "VERBATIM SOLUTION WHY:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = BentoAccent
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = question.verbatimCorrection.ifEmpty { "Procedural guidelines apply." },
                                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (!state.isSubmitted) {
                    item {
                        Button(
                            onClick = { showSubmitConfirmation = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("submit_exam_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "SUBMIT EXAM FOR GRADING",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = BentoBg
                            )
                        }
                    }
                }
            }
        }
    }
}
