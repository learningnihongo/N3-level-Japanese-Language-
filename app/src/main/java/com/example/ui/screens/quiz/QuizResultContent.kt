package com.example.ui.screens.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.ReviewBlue
import com.example.ui.theme.StreakOrange
import com.example.ui.viewmodel.QuizState
import com.example.ui.viewmodel.QuizViewModel

@Composable
fun QuizResultContent(
    quizState: QuizState,
    quizViewModel: QuizViewModel,
    onBackToHub: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept back button to return to Quiz Hub cleanly
    BackHandler {
        onBackToHub()
    }

    val totalQuestions = quizState.questions.size
    val score = quizState.score
    val accuracyPercent = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
    val xpEarned = quizState.xpEarned

    val missedQuestions = remember(quizState.questions) {
        quizState.questions.filter { !it.isCorrect }
    }
    val missedCount = missedQuestions.size

    val (rankGrade, rankTitle, rankColor) = when {
        accuracyPercent == 100 -> Triple("S", "ပြီးပြည့်စုံသော စွမ်းဆောင်ရည်! 👑", MasteredGreen)
        accuracyPercent >= 80 -> Triple("A", "အထူးထူးချွန်ပါသည်! 🌟", MasteredGreen)
        accuracyPercent >= 60 -> Triple("B", "ကောင်းမွန်သော အောင်မြင်မှု! ✨", ReviewBlue)
        else -> Triple("C", "ကြိုးစားဆဲဖြစ်ပါသည်! 💪", StreakOrange)
    }

    var selectedReviewFilter by remember { mutableStateOf<String>("ALL") } // ALL, WRONG, CORRECT

    val filteredReviewQuestions = remember(quizState.questions, selectedReviewFilter) {
        when (selectedReviewFilter) {
            "WRONG" -> quizState.questions.filter { !it.isCorrect }
            "CORRECT" -> quizState.questions.filter { it.isCorrect }
            else -> quizState.questions
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("quiz_result_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --------------------------------------------------------------------
        // 1. Celebratory Rank Ring & Header
        // --------------------------------------------------------------------
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // Rank Circular Ring
                Surface(
                    shape = CircleShape,
                    color = rankColor.copy(alpha = 0.14f),
                    border = BorderStroke(3.dp, rankColor),
                    modifier = Modifier.size(108.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = rankGrade,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 38.sp
                                ),
                                color = rankColor
                            )
                            Text(
                                text = "$accuracyPercent%",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Text(
                    text = rankTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${quizState.quizType.displayName} • ${quizState.lessonTitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --------------------------------------------------------------------
        // 2. Clean 4-Metric Grid
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuizScoreMetricItem(title = "မေးခွန်း", value = "$totalQuestions", color = MaterialTheme.colorScheme.onSurface)
                    DividerBar()
                    QuizScoreMetricItem(title = "မှန်ကန်မှု", value = "$score", color = MasteredGreen)
                    DividerBar()
                    QuizScoreMetricItem(title = "တိကျမှု", value = "$accuracyPercent%", color = ReviewBlue)
                    DividerBar()
                    QuizScoreMetricItem(title = "ရရှိ XP", value = "+$xpEarned", color = JapaneseCrimson)
                }
            }
        }

        // Streak Achievement
        if (quizState.maxStreak >= 2) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StreakOrange.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = StreakOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "အကောင်းဆုံး ဆက်တိုက်မှန်ကန်မှု: ${quizState.maxStreak} ပုဒ် Streak! 🔥",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = StreakOrange
                        )
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 3. Smart Actions (Retry Missed, Bookmark Missed, New Quiz, History)
        // --------------------------------------------------------------------
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // If there are missed questions, offer targeted retry
                if (missedCount > 0) {
                    Button(
                        onClick = { quizViewModel.retryMissedQuestions() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("retry_missed_quiz_btn")
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "မှားခဲ့သော $missedCount ပုဒ်ကိုသာ ပြန်စစ်ဆေးမည်",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = { quizViewModel.bookmarkAllMissedCards() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("bookmark_all_missed_btn")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = StreakOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "မှားခဲ့သော $missedCount ပုဒ်အားလုံးကို Bookmark မှတ်သားမည် ⭐",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onBackToHub,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quiz_finish_return_hub_btn")
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Quiz အသစ်ရွေးမည်",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = onNavigateToHistory,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quiz_view_history_btn")
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "မှတ်တမ်းကြည့်မည်",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 4. Question Breakdown Review Section
        // --------------------------------------------------------------------
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "မေးခွန်းများ ပြန်လည်စစ်ဆေးချက်",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedReviewFilter == "ALL",
                        onClick = { selectedReviewFilter = "ALL" },
                        label = { Text("အားလုံး ($totalQuestions)") }
                    )
                    FilterChip(
                        selected = selectedReviewFilter == "WRONG",
                        onClick = { selectedReviewFilter = "WRONG" },
                        label = { Text("မှားယွင်းသည် ($missedCount)") }
                    )
                    FilterChip(
                        selected = selectedReviewFilter == "CORRECT",
                        onClick = { selectedReviewFilter = "CORRECT" },
                        label = { Text("မှန်ကန်သည် ($score)") }
                    )
                }
            }
        }

        // Questions Review Cards
        itemsIndexed(filteredReviewQuestions) { index, question ->
            val card = question.card
            val isCorrect = question.isCorrect

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("review_question_item_$index"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.dp,
                    if (isCorrect) MasteredGreen.copy(alpha = 0.4f) else JapaneseCrimson.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCorrect) MasteredGreen.copy(alpha = 0.14f) else JapaneseCrimson.copy(alpha = 0.14f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isCorrect) MasteredGreen else JapaneseCrimson,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isCorrect) "မှန်ကန်သည်" else "မှားယွင်းခဲ့သည်",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isCorrect) MasteredGreen else JapaneseCrimson
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // TTS Speaker
                            IconButton(
                                onClick = { quizViewModel.speak(card.kanji) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Pronounce",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Individual Bookmark Star
                            IconButton(
                                onClick = { quizViewModel.toggleBookmarkInReview(card.id, card.isBookmarked) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (card.isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (card.isBookmarked) StreakOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Word Details
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = card.kanji,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JapaneseFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = card.reading,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = JapaneseFontFamily,
                                fontSize = 13.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "မြန်မာအဓိပ္ပာယ်: ${card.meaningBurmese}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Answer Breakdown
                    if (!isCorrect) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "သင်ရွေးချယ်ခဲ့သောအဖြေ: ${question.selectedAnswer ?: "အချိန်ကုန်သွားသည်"}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = JapaneseCrimson)
                                )
                                Text(
                                    text = "အဖြေမှန်: ${question.correctAnswer}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MasteredGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun QuizScoreMetricItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DividerBar() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(26.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    )
}
