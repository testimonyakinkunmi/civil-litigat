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
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawArc(
                                        color = BentoDarkAccent,
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = if (scorePercent >= 0.6f) CorrectGreen else IncorrectRed,
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
