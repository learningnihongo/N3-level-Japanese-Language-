package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.QuizStreakInfo
import com.example.data.model.StreakCalendarDay
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.StreakOrange

/**
 * Prominent Quiz Streak Badge displayed in the Profile Header card.
 * Clearly displays consecutive days of completed quizzes, live status (Done Today vs Grace Period),
 * and triggers a detailed streak breakdown dialog on tap.
 */
@Composable
fun QuizStreakHeaderBadge(
    streakInfo: QuizStreakInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (streakInfo.currentStreak > 0) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val streakColor = when {
        streakInfo.currentStreak >= 30 -> Color(0xFFFFD700) // Gold
        streakInfo.currentStreak >= 14 -> Color(0xFFFF5722) // Deep Orange
        streakInfo.currentStreak >= 7 -> StreakOrange       // Orange
        streakInfo.currentStreak >= 3 -> Color(0xFFFFA726) // Amber
        streakInfo.currentStreak >= 1 -> Color(0xFF4CAF50) // Green
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val containerBg = when {
        streakInfo.isGracePeriodActive -> Color(0xFFFFF8E1).copy(alpha = 0.7f)
        streakInfo.currentStreak > 0 -> streakColor.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    val borderColor = when {
        streakInfo.isGracePeriodActive -> Color(0xFFFFB300).copy(alpha = 0.5f)
        streakInfo.currentStreak > 0 -> streakColor.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("quiz_streak_header_badge"),
        shape = RoundedCornerShape(14.dp),
        color = containerBg,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Animated Streak Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                streakColor.copy(alpha = 0.25f),
                                streakColor.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(1.5.dp, streakColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = streakInfo.iconEmoji,
                    fontSize = 20.sp
                )
            }

            // Streak Text & Status Pill
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (streakInfo.currentStreak > 0) {
                            "${streakInfo.currentStreak} Day Quiz Streak"
                        } else {
                            "Daily Quiz Streak"
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Live Status Pill
                    when {
                        streakInfo.isQuizCompletedToday -> {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MasteredGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, MasteredGreen.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MasteredGreen,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "Done Today",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MasteredGreen
                                    )
                                }
                            }
                        }
                        streakInfo.isGracePeriodActive -> {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFFF9800).copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "Grace Period",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFFE65100)
                                    )
                                }
                            }
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "Ready",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Subtitle context
                Text(
                    text = when {
                        streakInfo.isGracePeriodActive -> "Quiz needed before midnight to maintain streak!"
                        streakInfo.isQuizCompletedToday -> "Locked in! Next milestone: ${streakInfo.nextMilestone} days (${streakInfo.daysUntilNextMilestone}d left)"
                        streakInfo.currentStreak > 0 -> "Take a quiz today to reach ${streakInfo.currentStreak + 1} consecutive days!"
                        else -> "Complete a quiz in Quiz Arena to start your daily streak!"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = if (streakInfo.isGracePeriodActive) Color(0xFFE65100) else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Arrow to details
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "View Streak Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Detailed Quiz Streak Dialog showing recent streak calendar, milestone progress,
 * grace period rules, and motivational wisdom.
 */
@Composable
fun QuizStreakDetailsDialog(
    streakInfo: QuizStreakInfo,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("quiz_streak_details_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Close Button & Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Quiz Streak",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Streak Big Badge Hero
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    StreakOrange.copy(alpha = 0.25f),
                                    Color(0xFFFFD700).copy(alpha = 0.15f)
                                )
                            )
                        )
                        .border(2.dp, StreakOrange.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = streakInfo.iconEmoji,
                        fontSize = 40.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${streakInfo.currentStreak} Days",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = streakInfo.tierTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = StreakOrange
                        )
                    )
                }

                // Grace Period Information Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (streakInfo.isGracePeriodActive) {
                        Color(0xFFFFF3E0)
                    } else if (streakInfo.isQuizCompletedToday) {
                        MasteredGreen.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    border = BorderStroke(
                        1.dp,
                        if (streakInfo.isGracePeriodActive) Color(0xFFFFB74D)
                        else if (streakInfo.isQuizCompletedToday) MasteredGreen.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (streakInfo.isGracePeriodActive) Icons.Default.Warning
                            else if (streakInfo.isQuizCompletedToday) Icons.Default.Check
                            else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (streakInfo.isGracePeriodActive) Color(0xFFE65100)
                            else if (streakInfo.isQuizCompletedToday) MasteredGreen
                            else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = if (streakInfo.isGracePeriodActive) "Grace Period Active (Until 11:59 PM)"
                                else if (streakInfo.isQuizCompletedToday) "Today's Streak Secured!"
                                else "Grace Period Rule",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (streakInfo.isGracePeriodActive) Color(0xFFE65100)
                                else if (streakInfo.isQuizCompletedToday) MasteredGreen
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (streakInfo.isGracePeriodActive) {
                                    "You completed a quiz yesterday! Take a quiz today before midnight so your ${streakInfo.currentStreak}-day streak doesn't reset."
                                } else if (streakInfo.isQuizCompletedToday) {
                                    "You've completed a quiz today! Your streak is preserved through tomorrow."
                                } else {
                                    "If you complete a quiz on any day, you have until midnight the next day before your streak resets."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 7-Day Visual Calendar Matrix
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Last 7 Days Activity",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        streakInfo.recentDays.forEach { day ->
                            DayStreakCalendarDot(day = day)
                        }
                    }
                }

                // Stats Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Best Streak",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${streakInfo.bestStreak}d",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Next Goal",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${streakInfo.nextMilestone}d (${streakInfo.daysUntilNextMilestone}d left)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Motivational Japanese proverb
                Text(
                    text = "継続は力なり\n(Continuity is strength)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                // Dismiss Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got It")
                }
            }
        }
    }
}

@Composable
private fun DayStreakCalendarDot(day: StreakCalendarDay) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = day.dayName.take(3),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        day.isCompleted -> StreakOrange.copy(alpha = 0.2f)
                        day.isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                )
                .border(
                    width = if (day.isToday) 1.5.dp else 1.dp,
                    color = when {
                        day.isCompleted -> StreakOrange
                        day.isToday -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (day.isCompleted) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Quiz Taken",
                    tint = StreakOrange,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = day.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
