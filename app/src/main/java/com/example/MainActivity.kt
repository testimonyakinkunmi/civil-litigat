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
import androidx.compose.ui.graphics.Color
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
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    val model: QuizViewModel = viewModel()
    val screenState by model.screenState.collectAsStateWithLifecycle()
    val activeQuizState by model.activeQuiz.collectAsStateWithLifecycle()
    val allBookmarks by model.allBookmarks.collectAsStateWithLifecycle()
    val currentStreak by model.currentStreak.collectAsStateWithLifecycle()

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
            }
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
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
                                text = "Civil Litigation",
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
                val weakWeekName = if (hasWeakSpot) weakestWeeks[0].weekName else "Hearsay Rules"
                val weakAccuracy = if (hasWeakSpot) weakestWeeks[0].accuracyPercent.toInt() else 54

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable {
                            if (hasWeakSpot) {
                                model.startTopicQuiz(weakWeekName)
                            } else {
                                model.startTopicQuiz("Week 5: Pleadings")
                            }
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
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(model.topics) { topic ->
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

    val currentQuiz = state.questions[state.currentIndex]
    val context = LocalContext.current
    var isBookmarkedState by remember(currentQuiz.scenario) { mutableStateOf(false) }

    LaunchedEffect(currentQuiz.scenario) {
        isBookmarkedState = model.isBookmarked(currentQuiz.scenario)
    }

    if (state.completed) {
        // Quiz complete score card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
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
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Events Trophy",
                        tint = WarmAmber,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Session Complete",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoSubtext,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Score Wheel
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val scorePercent = (state.correctAnswersCount.toFloat() / state.questions.size.toFloat())
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = BentoDarkAccent,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = if (scorePercent >= 0.6f) CorrectGreen else IncorrectRed,
                                startAngle = -90f,
                                sweepAngle = 360f * scorePercent,
                                useCenter = false,
                                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.correctAnswersCount}/${state.questions.size}",
                                fontSize = 28.sp,
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

                    Spacer(modifier = Modifier.height(24.dp))

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
                            Text("Retry", color = BentoAccent, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { model.navigateTo(ScreenState.HOME) },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                            shape = RoundedCornerShape(100),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close", color = BentoHighlight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    } else {
        // Active Quiz HUD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { model.navigateTo(ScreenState.WEEK_SELECT) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(BentoDarkAccent, RoundedCornerShape(100))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = "${state.currentIndex + 1} / ${state.questions.size}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                IconButton(
                    onClick = {
                        model.toggleBookmark(currentQuiz) { added ->
                            isBookmarkedState = added
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(BentoDarkAccent, RoundedCornerShape(100))
                ) {
                    Icon(
                        imageVector = if (isBookmarkedState) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = BentoAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { (state.currentIndex + 1).toFloat() / state.questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(100)),
                color = BentoAccent,
                trackColor = BentoDarkAccent
            )

            // Scrollable Content for long scenarios
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "VERBATIM CASE SCENARIO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BentoAccent
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQuiz.scenario,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                color = Color.White
                            )
                        }
                    }
                }

                // Verbatim choice options
                items(currentQuiz.options.size) { oIdx ->
                    val optionText = currentQuiz.options[oIdx]
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
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(borderStroke, RoundedCornerShape(16.dp))
                            .clickable(enabled = !state.isAnswered) {
                                model.selectOption(oIdx)
                            }
                            .padding(16.dp),
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
                            color = Color.White
                        )
                    }
                }

                // If answered, display Correction panel instantly
                if (state.isAnswered) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BentoDarkAccent
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (state.selectedOptionIndex == currentQuiz.correctIndex) CorrectGreen else IncorrectRed
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (state.selectedOptionIndex == currentQuiz.correctIndex) "✓ CORRECT ANSWER KEY" else "✗ RECALL MISS / CORRECTION REQUIRED",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (state.selectedOptionIndex == currentQuiz.correctIndex) CorrectGreen else IncorrectRed
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentQuiz.verbatimCorrection.ifEmpty { "Refer to Civil Procedure rules. Correct option is index: ${currentQuiz.correctIndex + 1}" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            if (state.isAnswered) {
                Button(
                    onClick = { model.nextQuestion() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("quiz_next_btn"),
                    shape = RoundedCornerShape(100),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoAccent)
                ) {
                    Text(
                        text = if (state.currentIndex + 1 < state.questions.size) "Next Scenario" else "Finish Session",
                        color = BentoHighlight,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = { model.lockAndSubmitAnswer() },
                    enabled = state.selectedOptionIndex != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("quiz_submit_btn"),
                    shape = RoundedCornerShape(100),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoAccent,
                        disabledContainerColor = BentoDarkAccent
                    )
                ) {
                    Text(
                        "Grade and view answer keys",
                        color = if (state.selectedOptionIndex != null) BentoHighlight else BentoSubtext,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
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
                                        color = BentoBorder,
                                        start = Offset(0f, maxHeight),
                                        end = Offset(size.width, maxHeight),
                                        strokeWidth = 2f
                                    )

                                    analytics.take(10).forEachIndexed { index, weekAnalytic ->
                                        val x = index * (barWidth + space) + 12.dp.toPx()
                                        // Background bento container bar
                                        drawRect(
                                            color = BentoDarkAccent,
                                            topLeft = Offset(x, 0f),
                                            size = androidx.compose.ui.geometry.Size(barWidth, maxHeight)
                                        )
                                        // Highlighted accuracy bar
                                        val barHeight = maxHeight * (weekAnalytic.accuracyPercent / 100f)
                                        drawRect(
                                            color = BentoAccent,
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
                                                    color = BentoAccent,
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
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawArc(
                                        color = BentoDarkAccent,
                                        startAngle = 135f,
                                        sweepAngle = 270f,
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = WarmAmber,
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
