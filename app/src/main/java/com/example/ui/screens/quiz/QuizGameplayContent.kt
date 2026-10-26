package com.example.ui.screens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.ReviewBlue
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.WeakOrange
import com.example.ui.viewmodel.QuizState
import com.example.ui.viewmodel.QuizType
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun QuizGameplayContent(
    quizState: QuizState,
    quizViewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept back button to prevent accidental quiz abandonment
    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "Quiz မှ ထွက်မည်လား?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("လက်ရှိ ဖြေဆိုနေသော မေးခွန်းများနှင့် ရမှတ်များ သိမ်းဆည်းမည် မဟုတ်ပါ။ ပြန်လည်ထွက်ခွာလိုပါသလား?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        quizViewModel.exitQuiz()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson)
                ) {
                    Text("ထွက်မည် (Exit)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("ဆက်လက်ဖြေဆိုမည်")
                }
            }
        )
    }

    val currentQuestion = quizState.questions.getOrNull(quizState.currentIndex) ?: return
    val isAnswered = quizState.isAnswerSubmitted
    val totalQuestions = quizState.questions.size

    val isUrgentTimer = quizState.remainingSeconds <= 3 && !isAnswered
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val timerScale by pulseTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isUrgentTimer) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "timer_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("quiz_gameplay_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ----------------------------------------------------------------
            // 1. Sleek Top Bar (Close, Progress, Timer, Streak, Star)
            // ----------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Exit Button
                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("quiz_exit_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Quiz",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Question Progress Indicator
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "${quizState.currentIndex + 1} / $totalQuestions",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Combo Streak Pill
                if (quizState.currentStreak >= 2) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = StreakOrange.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = StreakOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "x${quizState.comboMultiplier} (${quizState.currentStreak})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StreakOrange
                            )
                        }
                    }
                }

                // Countdown Timer Pill
                val timerBgColor by animateColorAsState(
                    targetValue = if (isUrgentTimer) JapaneseCrimson.copy(alpha = 0.18f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    label = "timer_bg"
                )
                val timerTextColor by animateColorAsState(
                    targetValue = if (isUrgentTimer) JapaneseCrimson
                    else MaterialTheme.colorScheme.onSurface,
                    label = "timer_text"
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = timerBgColor,
                    border = BorderStroke(1.dp, if (isUrgentTimer) JapaneseCrimson else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.scale(timerScale)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = timerTextColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${quizState.remainingSeconds}s",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = timerTextColor
                        )
                    }
                }

                // Bookmark Star Button
                IconButton(
                    onClick = { quizViewModel.toggleCurrentCardBookmark() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("quiz_star_current_btn")
                ) {
                    Icon(
                        imageVector = if (quizState.isCurrentCardBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star card",
                        tint = if (quizState.isCurrentCardBookmarked) StreakOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Smooth Progress Bar
            LinearProgressIndicator(
                progress = { (quizState.currentIndex + 1).toFloat() / totalQuestions },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // ----------------------------------------------------------------
            // 2. Lifelines & Power-ups Row
            // ----------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 50:50 Lifeline
                OutlinedButton(
                    onClick = { quizViewModel.useFiftyFifty() },
                    enabled = !quizState.hasUsedFiftyFifty && !isAnswered && !currentQuestion.isTrueFalse,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("lifeline_50_50")
                ) {
                    Text(
                        text = if (quizState.hasUsedFiftyFifty) "50:50 (သုံးပြီး)" else "50:50",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // Clue / Hint Button
                OutlinedButton(
                    onClick = { quizViewModel.toggleClue() },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("lifeline_hint")
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (quizState.showClue) "Hint ဝှက်မည်" else "Hint 💡",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Swap Question Lifeline
                OutlinedButton(
                    onClick = { quizViewModel.swapCurrentQuestion() },
                    enabled = !quizState.hasUsedSwap && !isAnswered,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("lifeline_swap")
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (quizState.hasUsedSwap) "Swap (သုံးပြီး)" else "Swap 🔄",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // TTS Pronunciation Audio
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { quizViewModel.speak(currentQuestion.card.kanji) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (quizState.speechRate == 1.0f) "1.0x" else "0.75x",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Animated Hint Box
            AnimatedVisibility(
                visible = quizState.showClue && currentQuestion.clueHint.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WeakOrange.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, WeakOrange.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = WeakOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = currentQuestion.clueHint,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // ----------------------------------------------------------------
            // 3. Question Prompt Card
            // ----------------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_question_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Category Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = JapaneseCrimson.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = quizState.quizType.badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = JapaneseCrimson,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                            )
                        }

                        // Specific Content by Quiz Type
                        when {
                            quizState.quizType == QuizType.LISTENING_CHALLENGE -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = "Listening",
                                        tint = JapaneseCrimson,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Button(
                                        onClick = { quizViewModel.speak(currentQuestion.card.kanji) },
                                        shape = RoundedCornerShape(50),
                                        colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson.copy(alpha = 0.85f)),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("အသံထွက် ပြန်နားထောင်မည်", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                            quizState.quizType == QuizType.SENTENCE_CLOZE -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentQuestion.prompt,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = JapaneseFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 26.sp
                                        ),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (currentQuestion.sentenceMeaning.isNotBlank()) {
                                        Text(
                                            text = currentQuestion.sentenceMeaning,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            else -> {
                                Text(
                                    text = currentQuestion.prompt,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = JapaneseFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 28.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Subtitle Prompt
                        if (currentQuestion.promptSub.isNotBlank()) {
                            Text(
                                text = currentQuestion.promptSub,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // ----------------------------------------------------------------
            // 4. Interactive Options (A, B, C, D)
            // ----------------------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = quizState.selectedOptionIndex == index
                    val isCorrect = option == currentQuestion.correctAnswer
                    val isEliminated = quizState.eliminatedOptionIndices.contains(index)

                    val optionBgColor by animateColorAsState(
                        targetValue = when {
                            !isAnswered -> MaterialTheme.colorScheme.surface
                            isCorrect -> MasteredGreen.copy(alpha = 0.16f)
                            isSelected -> JapaneseCrimson.copy(alpha = 0.16f)
                            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        },
                        label = "opt_bg"
                    )

                    val optionBorderColor by animateColorAsState(
                        targetValue = when {
                            !isAnswered -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                            isCorrect -> MasteredGreen
                            isSelected -> JapaneseCrimson
                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                        },
                        label = "opt_border"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !isAnswered && !isEliminated) {
                                quizViewModel.selectOption(index)
                            }
                            .testTag("quiz_option_$index"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = optionBgColor),
                        border = BorderStroke(if (isAnswered && (isCorrect || isSelected)) 1.8.dp else 1.dp, optionBorderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = when {
                                        isAnswered && isCorrect -> MasteredGreen
                                        isAnswered && isSelected -> JapaneseCrimson
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (currentQuestion.isTrueFalse) {
                                                if (index == 0) "○" else "×"
                                            } else {
                                                "${'A' + index}"
                                            },
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            ),
                                            color = if (isAnswered && (isCorrect || isSelected)) Color.White
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        fontWeight = if (isAnswered && (isCorrect || isSelected)) FontWeight.Bold else FontWeight.Medium,
                                        textDecoration = if (isEliminated) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = when {
                                        isEliminated -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                                        isAnswered && isCorrect -> MasteredGreen
                                        isAnswered && isSelected -> JapaneseCrimson
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }

                            if (isAnswered) {
                                if (isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = MasteredGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Wrong",
                                        tint = JapaneseCrimson,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Homophone Educational Note on Submission
            if (isAnswered && currentQuestion.homophoneComparison.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ReviewBlue.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, ReviewBlue.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = currentQuestion.homophoneComparison,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 5. Next Question / View Results Floating Action Bar
        // --------------------------------------------------------------------
        AnimatedVisibility(
            visible = isAnswered,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // XP Earned Feedback
                val isCorrect = currentQuestion.isCorrect
                if (isCorrect) {
                    Text(
                        text = "+${10 * quizState.comboMultiplier} XP ရရှိပါသည်! ✨",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MasteredGreen
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Button(
                    onClick = { quizViewModel.nextQuestion() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("quiz_next_btn")
                ) {
                    Text(
                        text = if (quizState.currentIndex < totalQuestions - 1) "နောက်တစ်ပုဒ် (Next Question) →"
                        else "ရလဒ် အပြည့်အစုံ ကြည့်မည် (View Results) 🏆",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                    )
                }
            }
        }
    }
}
