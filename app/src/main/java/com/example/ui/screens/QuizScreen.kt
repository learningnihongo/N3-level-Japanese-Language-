package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.SeedData
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SuccessGreen

data class QuizQuestion(
    val prompt: String,
    val subPrompt: String,
    val options: List<String>,
    val correctIndex: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onBackClick: () -> Unit,
    onQuizFinished: (score: Long, total: Long, xpEarned: Long) -> Unit
) {
    val questions = remember {
        val list = mutableListOf<QuizQuestion>()
        SeedData.kanjiList.take(5).forEach { k ->
            val allMeanings = SeedData.kanjiList.map { it.meaningBurmese }.shuffled()
            val wrongOptions = allMeanings.filter { it != k.meaningBurmese }.take(3)
            val options = (wrongOptions + k.meaningBurmese).shuffled()
            list.add(
                QuizQuestion(
                    prompt = k.kanji,
                    subPrompt = "ဤခန်းဂျီး၏ မြန်မာအဓိပ္ပာယ်ကို ရွေးပါ",
                    options = options,
                    correctIndex = options.indexOf(k.meaningBurmese)
                )
            )
        }
        SeedData.kotobaList.take(5).forEach { w ->
            val allMeanings = SeedData.kotobaList.map { it.meaningBurmese }.shuffled()
            val wrongOptions = allMeanings.filter { it != w.meaningBurmese }.take(3)
            val options = (wrongOptions + w.meaningBurmese).shuffled()
            list.add(
                QuizQuestion(
                    prompt = "${w.word} (${w.reading})",
                    subPrompt = "ဤစကားလုံး၏ မှန်ကန်သော အဓိပ္ပာယ်ကို ရွေးပါ",
                    options = options,
                    correctIndex = options.indexOf(w.meaningBurmese)
                )
            )
        }
        list.shuffled()
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("JLPT N3 Quiz", fontWeight = FontWeight.Bold) },
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
            val xpEarned = (score * 10).toLong()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = GoldAccent,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ဂုဏ်ယူပါသည်! Quiz ပြီးဆုံးပါပြီ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ရမှတ်: $score / ${questions.size} မှတ်",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ရရှိသော အတွေ့အကြုံရမှတ်: +$xpEarned XP",
                    fontSize = 15.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        onQuizFinished(score.toLong(), questions.size.toLong(), xpEarned)
                        onBackClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("quiz_finish_btn")
                ) {
                    Text("ရလဒ် သိမ်းဆည်းပြီး ပြန်သွားမည်", fontSize = 16.sp)
                }
            }
        } else if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Score: $score",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentQuestion.prompt,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentQuestion.subPrompt,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = index == currentQuestion.correctIndex
                        val isAnswered = selectedOptionIndex != null

                        val containerColor = when {
                            !isAnswered -> MaterialTheme.colorScheme.surface
                            isCorrect -> SuccessGreen.copy(alpha = 0.18f)
                            isSelected -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable(enabled = !isAnswered) {
                                    selectedOptionIndex = index
                                    if (isCorrect) score++
                                }
                                .testTag("quiz_option_$index"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + index)}.  $option",
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected || (isAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isAnswered) {
                                    if (isCorrect) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = SuccessGreen)
                                    } else if (isSelected) {
                                        Icon(Icons.Default.Close, contentDescription = "Wrong", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                if (selectedOptionIndex != null) {
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedOptionIndex = null
                            } else {
                                isQuizCompleted = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_btn")
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) "နောက်တစ်ပုဒ် (Next Question)" else "ရလဒ်ကြည့်မည် (View Results)",
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
