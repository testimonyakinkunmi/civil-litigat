package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuizViewModel
import com.example.data.ScreenState
import com.example.data.StudyNotification
import java.text.SimpleDateFormat
import java.util.*

// Aesthetic Palette matching the app's Bento Theme
val DarkSlate = Color(0xFF0F172A)
val BentoSurface = Color(0xFF1E293B)
val BentoBorder = Color(0xFF334155)
val BentoHighlight = Color(0xFF38BDF8)
val BentoAccent = Color(0xFFF43F5E)
val BentoDarkAccent = Color(0xFF881337)
val BentoSubtext = Color(0xFF94A3B8)
val CorrectGreen = Color(0xFF10B981)
val GoldAlert = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(model: QuizViewModel) {
    val context = LocalContext.current
    val scheduledList by model.scheduledNotifications.collectAsState()
    val deliveredList by model.deliveredNotifications.collectAsState()

    var isEnabled by remember { mutableStateOf(model.notificationManager.isNotificationsEnabled) }
    var dailyLimit by remember { mutableFloatStateOf(model.notificationManager.dailyLimit.toFloat()) }
    var showSimulationSuccess by remember { mutableStateOf(false) }

    // Synchronize UI settings back to the manager
    LaunchedEffect(isEnabled) {
        model.notificationManager.isNotificationsEnabled = isEnabled
        if (isEnabled) {
            model.notificationManager.checkAndDeliverMissedScheduledNotifications()
        }
    }

    LaunchedEffect(dailyLimit) {
        model.notificationManager.dailyLimit = dailyLimit.toInt()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Smart Study Center",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { model.navigateTo(ScreenState.HOME) },
                        modifier = Modifier.testTag("notif_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoSurface
                ),
                actions = {
                    IconButton(
                        onClick = {
                            model.notificationManager.clearHistory()
                            model.loadNotifications()
                        },
                        modifier = Modifier.testTag("notif_clear_history")
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear History", tint = BentoAccent)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkSlate)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            // --- HEADER HERO BANNER ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(BentoAccent, BentoHighlight)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Notifications active",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Smart Study Engine",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Weak-Spot Focus & Direct Fact Memory",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoSubtext
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Based on your incorrect answers, the engine targets your specific legal weak spots, formulating and sending direct, bite-sized direct rules throughout the day to maximize passive retention.",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                            color = BentoSubtext
                        )
                    }
                }
            }

            // --- SETTINGS CONTROLS ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Study Engine Settings",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )

                        // Toggle switch row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Daily Passive Study Alarms",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnabled) "Active and scheduling facts" else "Paused",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoSubtext
                                )
                            }
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { isEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BentoHighlight,
                                    uncheckedThumbColor = BentoSubtext,
                                    uncheckedTrackColor = BentoBorder
                                ),
                                modifier = Modifier.testTag("notif_enable_switch")
                            )
                        }

                        Divider(color = BentoBorder, thickness = 0.5.dp)

                        // Slider row for limit
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daily Notification Frequency",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${dailyLimit.toInt()} times / day",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoHighlight
                                    )
                                )
                            }
                            Text(
                                text = "Delivered completely at randomized intervals so your brain never expects them.",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoSubtext
                            )
                            Slider(
                                value = dailyLimit,
                                onValueChange = { dailyLimit = it },
                                valueRange = 1f..4f,
                                steps = 2,
                                colors = SliderDefaults.colors(
                                    thumbColor = BentoHighlight,
                                    activeTrackColor = BentoHighlight,
                                    inactiveTrackColor = BentoBorder
                                ),
                                modifier = Modifier.testTag("notif_limit_slider")
                            )
                        }
                    }
                }
            }

            // --- SIMULATE BUTTON AREA ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = BorderStroke(1.dp, BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Test & Simulation Center",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Instantly trigger the generator to build, deliver, and display a customized weak-spot study notification right now. Check your Android notification bar after clicking!",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoSubtext,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                model.notificationManager.simulateNotificationImmediate {
                                    model.loadNotifications()
                                    showSimulationSuccess = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("simulate_notif_button")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "Simulate", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simulate Next Study Alert", fontWeight = FontWeight.Bold)
                        }

                        AnimatedVisibility(
                            visible = showSimulationSuccess,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CorrectGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .border(1.dp, CorrectGreen, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = CorrectGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Alert simulated! See status-bar.",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CorrectGreen
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Dismiss",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CorrectGreen
                                    ),
                                    modifier = Modifier.clickable { showSimulationSuccess = false }
                                )
                            }
                        }
                    }
                }
            }

            // --- TODAY'S TIMING PLAN ---
            item {
                Text(
                    text = "Today's Randomized Plan",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (scheduledList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Text(
                            text = "No study alarms scheduled today. Check toggle settings above to automatically generate a schedule.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoSubtext,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        scheduledList.forEachIndexed { idx, item ->
                            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
                            val timeStr = formatter.format(Date(item.timestamp))

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("scheduled_card_$idx"),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BentoBorder),
                                colors = CardDefaults.cardColors(containerColor = BentoSurface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Scheduled",
                                        tint = BentoHighlight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Alert #${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = BentoSubtext
                                    )
                                    Text(
                                        text = timeStr,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- HISTORY INBOX TITLE ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Weak-Spot Fact Feed",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${deliveredList.size} delivered",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoSubtext
                    )
                }
            }

            if (deliveredList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface),
                        border = BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.School, contentDescription = "Learn", tint = BentoSubtext, modifier = Modifier.size(32.dp))
                            Text(
                                text = "Your Weak-Spot Fact Feed is Empty",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Delivered facts will be collected here like flashcards so you can review them easily.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoSubtext,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(deliveredList, key = { it.id }) { notif ->
                    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                    val timeDelivered = formatter.format(Date(notif.timestamp))
                    
                    val isWeakSpot = notif.title.contains("Weak-Spot", ignoreCase = true)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delivered_fact_card_${notif.id}"),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isWeakSpot) BentoAccent.copy(alpha = 0.4f) else BentoBorder),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                if (isWeakSpot) BentoAccent else CorrectGreen,
                                                RoundedCornerShape(100)
                                            )
                                    )
                                    Text(
                                        text = if (isWeakSpot) "WEAK-SPOT ALIGNED" else "DAILY MEMORY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = if (isWeakSpot) BentoAccent else CorrectGreen
                                    )
                                }
                                Text(
                                    text = timeDelivered,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = BentoSubtext
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = notif.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isWeakSpot) Icons.Default.TrendingDown else Icons.Default.Lightbulb,
                                    contentDescription = "Type",
                                    tint = if (isWeakSpot) BentoAccent else GoldAlert,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isWeakSpot) "Reviewing a previous mistake" else "General curriculum check",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoSubtext
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
