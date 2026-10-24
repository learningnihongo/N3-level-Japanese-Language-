package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.MasteredGreen
import kotlin.math.roundToInt

@Composable
fun DailyGoalProgressRing(
    progress: Float,
    reviewedCount: Int,
    targetGoal: Int,
    modifier: Modifier = Modifier,
    ringSize: Dp = 74.dp,
    strokeWidth: Dp = 8.dp
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 800),
        label = "DailyGoalRingAnim"
    )

    val ringColor = when {
        progress >= 1.0f -> MasteredGreen
        progress >= 0.5f -> JapaneseCrimson
        else -> MaterialTheme.colorScheme.primary
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .size(ringSize)
            .testTag("daily_goal_progress_ring"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(ringSize)) {
            val strokePx = strokeWidth.toPx()

            // Background circle track
            drawCircle(
                color = trackColor,
                radius = (size.minDimension - strokePx) / 2f,
                style = Stroke(width = strokePx)
            )

            // Progress arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val pct = (clampedProgress * 100).toInt()
            Text(
                text = "$pct%",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$reviewedCount/$targetGoal",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun SetDailyGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSaveGoal: (Int) -> Unit
) {
    var goalValue by remember(currentGoal) { mutableFloatStateOf(currentGoal.toFloat().coerceIn(5f, 50f)) }

    val presetGoals = listOf(10, 15, 20, 30, 40)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("set_daily_goal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Daily Study Goal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Set how many Kanji / Kotoba flashcards you want to study and review every day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${goalValue.roundToInt()} Cards / Day",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = JapaneseCrimson
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = goalValue,
                        onValueChange = { goalValue = it },
                        valueRange = 5f..50f,
                        steps = 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_goal_slider")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presetGoals.forEach { preset ->
                        FilterChip(
                            selected = goalValue.roundToInt() == preset,
                            onClick = { goalValue = preset.toFloat() },
                            label = { Text("$preset") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JapaneseCrimson.copy(alpha = 0.2f),
                                selectedLabelColor = JapaneseCrimson
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onSaveGoal(goalValue.roundToInt()) },
                        modifier = Modifier.testTag("save_daily_goal_btn")
                    ) {
                        Text("Save Goal")
                    }
                }
            }
        }
    }
}
