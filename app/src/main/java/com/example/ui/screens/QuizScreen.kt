package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VocabCard
import com.example.data.seed.SeedData
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.ReviewBlue
import com.example.ui.theme.StreakOrange
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.VocabViewModel
import kotlinx.coroutines.launch

data class DynamicQuizQuestion(
    val categoryBadge: String,
    val prompt: String,
    val subPrompt: String,
    val speakText: String = "",
    val options: List<String>,
    val correctIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    quizViewModel: QuizViewModel? = null,
    vocabViewModel: VocabViewModel? = null,
    onBackClick: () -> Unit,
    onQuizFinished: (score: Long, total: Long, xpEarned: Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allCards by vocabViewModel?.allCards?.collectAsState() ?: remember { mutableStateOf(emptyList<VocabCard>()) }

    var quizSeed by remember { mutableIntStateOf(0) }

    val questions = remember(allCards, quizSeed) {
        generateDynamicQuestions(allCards)
    }

    var currentQuestionIndex by remember(quizSeed) { mutableIntStateOf(0) }
    var selectedOptionIndex by remember(quizSeed, currentQuestionIndex) { mutableStateOf<Int?>(null) }
    var score by remember(quizSeed) { mutableIntStateOf(0) }
    var currentStreak by remember(quizSeed) { mutableIntStateOf(0) }
    var maxStreak by remember(quizSeed) { mutableIntStateOf(0) }
    var isQuizCompleted by remember(quizSeed) { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "JLPT N3 Quiz Arena",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (!isQuizCompleted && questions.isNotEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = "${currentQuestionIndex + 1}/${questions.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("quiz_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isQuizCompleted) {
            val totalQuestions = questions.size
            val accuracyPercent = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
            val xpEarned = (score * 15 + maxStreak * 5).toLong()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Celebratory Circular Score Ring
                Surface(
                    shape = CircleShape,
                    color = if (accuracyPercent >= 80) MasteredGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    border = BorderStroke(2.dp, if (accuracyPercent >= 80) MasteredGreen else MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = if (accuracyPercent >= 80) StreakOrange else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "$accuracyPercent%",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Motivational Heading
                Text(
                    text = when {
                        accuracyPercent >= 80 -> "ဂုဏ်ယူပါသည်! အထူးထူးချွန်ပါသည် 🎉"
                        accuracyPercent >= 50 -> "အောင်မြင်ပါသည်! ကောင်းမွန်သော ရလဒ်ဖြစ်ပါသည် ✨"
                        else -> "ကြိုးစားဆဲဖြစ်ပါသည်! နောက်တစ်ကြိမ် လေ့ကျင့်ပါ 💪"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "合格おめでとうございます • JLPT N3 စစ်ဆေးမှု ပြီးဆုံးပါပြီ",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = JapaneseFontFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Clean 4-Metric Grid
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuizScoreMetric(
                            title = "မေးခွန်း",
                            value = "$totalQuestions",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        )
                        QuizScoreMetric(
                            title = "မှန်ကန်မှု",
                            value = "$score",
                            color = MasteredGreen
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        )
                        QuizScoreMetric(
                            title = "တိကျမှု",
                            value = "$accuracyPercent%",
                            color = ReviewBlue
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        )
                        QuizScoreMetric(
                            title = "ရရှိ XP",
                            value = "+$xpEarned",
                            color = JapaneseCrimson
                        )
                    }
                }

                // Max Streak Achievement
                if (maxStreak >= 2) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StreakOrange.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.3f)),
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
                                text = "အကောင်းဆုံး ရက်ဆက် Streak: $maxStreak ပုဒ် ဆက်တိုက်မှန်ကန်!",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = StreakOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions: Retake Quiz + Finish
                Button(
                    onClick = {
                        coroutineScope.launch {
                            quizViewModel?.repository?.recordQuizResult(
                                quizType = "JLPT N3 Mixed Quiz",
                                lessonFilter = "All Lessons",
                                score = score,
                                totalQuestions = totalQuestions,
                                durationSeconds = 45
                            )
                        }
                        onQuizFinished(score.toLong(), totalQuestions.toLong(), xpEarned)
                        onBackClick()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("quiz_finish_btn")
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ရလဒ် သိမ်းဆည်းပြီး ပြန်သွားမည်",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = {
                        quizSeed++
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("quiz_retake_btn")
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "နောက်တစ်ကြိမ် စစ်ဆေးမည် (Retake Quiz)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        } else if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Linear Progress Indicator
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Streak Pill & Category Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = JapaneseCrimson.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = currentQuestion.categoryBadge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = JapaneseCrimson,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (currentStreak >= 2) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = StreakOrange.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = StreakOrange,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "$currentStreak Streak 🔥",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = StreakOrange
                                    )
                                }
                            }
                        }
                    }

                    // Question Prompt Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = currentQuestion.prompt,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = JapaneseFontFamily,
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (currentQuestion.speakText.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                vocabViewModel?.speakJapanese(currentQuestion.speakText)
                                                    ?: quizViewModel?.speak(currentQuestion.speakText)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Speak",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = currentQuestion.subPrompt,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // 4 Interactive Options with Instant Feedback
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentQuestion.options.forEachIndexed { index, option ->
                            val isSelected = selectedOptionIndex == index
                            val isCorrect = index == currentQuestion.correctIndex
                            val isAnswered = selectedOptionIndex != null

                            val optionContainerColor = when {
                                !isAnswered -> MaterialTheme.colorScheme.surface
                                isCorrect -> MasteredGreen.copy(alpha = 0.14f)
                                isSelected -> JapaneseCrimson.copy(alpha = 0.14f)
                                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            }

                            val optionBorderColor = when {
                                !isAnswered -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                isCorrect -> MasteredGreen
                                isSelected -> JapaneseCrimson
                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable(enabled = !isAnswered) {
                                        selectedOptionIndex = index
                                        if (isCorrect) {
                                            score++
                                            currentStreak++
                                            if (currentStreak > maxStreak) maxStreak = currentStreak
                                        } else {
                                            currentStreak = 0
                                        }
                                    }
                                    .testTag("quiz_option_$index"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = optionContainerColor),
                                border = BorderStroke(if (isAnswered && (isCorrect || isSelected)) 1.8.dp else 1.dp, optionBorderColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
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
                                                    text = "${'A' + index}",
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
                                                fontWeight = if (isAnswered && (isCorrect || isSelected)) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = when {
                                                isAnswered && isCorrect -> MasteredGreen
                                                isAnswered && isSelected -> JapaneseCrimson
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }

                                    // Instant Icon Feedback
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
                }

                // Next Button (Appears as soon as an option is selected)
                AnimatedVisibility(
                    visible = selectedOptionIndex != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedOptionIndex = null
                            } else {
                                isQuizCompleted = true
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_btn")
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) "နောက်တစ်ပုဒ် (Next Question) →"
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
}

@Composable
private fun QuizScoreMetric(
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

private fun generateDynamicQuestions(allCards: List<VocabCard>): List<DynamicQuizQuestion> {
    val list = mutableListOf<DynamicQuizQuestion>()

    val activePool = if (allCards.size >= 8) allCards.shuffled() else SeedData.kanjiList.map { k ->
        VocabCard(
            lessonNumber = 1,
            lessonTitle = "Lesson 1",
            sectionTitle = "Kanji",
            kanji = k.kanji,
            reading = k.onyomi,
            meaningBurmese = k.meaningBurmese
        )
    } + SeedData.kotobaList.map { w ->
        VocabCard(
            lessonNumber = 1,
            lessonTitle = "Lesson 1",
            sectionTitle = "Vocabulary",
            kanji = w.word,
            reading = w.reading,
            meaningBurmese = w.meaningBurmese
        )
    }

    val sampleCards = activePool.take(10)

    for ((index, card) in sampleCards.withIndex()) {
        val distractors = activePool.filter { it.kanji != card.kanji }

        when (index % 3) {
            0 -> {
                // Kanji -> Burmese meaning
                val correct = card.meaningBurmese
                val wrong = distractors.map { it.meaningBurmese }.distinct().filter { it != correct }.shuffled().take(3)
                val options = (wrong + correct).shuffled()
                list.add(
                    DynamicQuizQuestion(
                        categoryBadge = "Kanji → Meaning",
                        prompt = card.kanji,
                        subPrompt = "「${card.reading}」၏ မှန်ကန်သော မြန်မာအဓိပ္ပာယ်ကို ရွေးပါ",
                        speakText = card.kanji,
                        options = options,
                        correctIndex = options.indexOf(correct)
                    )
                )
            }
            1 -> {
                // Burmese meaning -> Kanji
                val correct = "${card.kanji} (${card.reading})"
                val wrong = distractors.map { "${it.kanji} (${it.reading})" }.distinct().filter { it != correct }.shuffled().take(3)
                val options = (wrong + correct).shuffled()
                list.add(
                    DynamicQuizQuestion(
                        categoryBadge = "Meaning → Kanji",
                        prompt = card.meaningBurmese,
                        subPrompt = "ဤမြန်မာအဓိပ္ပာယ်နှင့် ကိုက်ညီသော ခန်းဂျီးစကားလုံးကို ရွေးပါ",
                        speakText = card.kanji,
                        options = options,
                        correctIndex = options.indexOf(correct)
                    )
                )
            }
            else -> {
                // Reading Challenge
                val correct = card.reading
                val wrong = distractors.map { it.reading }.distinct().filter { it != correct }.shuffled().take(3)
                val options = (wrong + correct).shuffled()
                list.add(
                    DynamicQuizQuestion(
                        categoryBadge = "Reading • 読み方",
                        prompt = card.kanji,
                        subPrompt = "「${card.meaningBurmese}」၏ မှန်ကန်သော အသံထွက် (Furigana) ကို ရွေးပါ",
                        speakText = card.kanji,
                        options = options,
                        correctIndex = options.indexOf(correct)
                    )
                )
            }
        }
    }

    return list
}
