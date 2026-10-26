package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.theme.JapaneseIndigo
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.ReviewBlue
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.WeakOrange
import com.example.ui.viewmodel.QuizType
import com.example.ui.viewmodel.QuizViewModel
import com.example.ui.viewmodel.VocabViewModel

data class QuizModeUiItem(
    val type: QuizType,
    val seal: String,
    val titleMy: String,
    val titleJp: String,
    val descMy: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isHot: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizHubContent(
    quizViewModel: QuizViewModel,
    vocabViewModel: VocabViewModel? = null,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizHistory by quizViewModel.quizHistory.collectAsState()
    val quizStreak by vocabViewModel?.quizStreakInfo?.collectAsState() ?: remember { mutableStateOf(null) }

    var selectedQuestionCount by remember { mutableIntStateOf(10) }
    var selectedLessonFilter by remember { mutableStateOf<Int?>(null) } // null = All, -1 = Weak, -2 = Starred, -3 = Past exams, 1..29 = Specific
    var showLessonDropdown by remember { mutableStateOf(false) }

    val recentSessions = remember(quizHistory) { quizHistory.take(5) }
    val bestAccuracy = remember(quizHistory) { quizHistory.maxOfOrNull { it.accuracyPercentage } ?: 0 }
    val totalQuizzesTaken = quizHistory.size

    val quizModes = remember {
        listOf(
            QuizModeUiItem(
                type = QuizType.KANJI_MASTERY,
                seal = "漢字",
                titleMy = "Kanji Mastery Drill",
                titleJp = "漢字表記テスト",
                descMy = "Reading နှင့် မြန်မာအဓိပ္ပာယ်မှ တိကျသော ခန်းဂျီးစကားလုံးကို ရှာဖွေပါ",
                icon = Icons.Default.School,
                accentColor = JapaneseCrimson,
                isHot = true
            ),
            QuizModeUiItem(
                type = QuizType.KANJI_TO_MEANING,
                seal = "標準",
                titleMy = "Kanji → Burmese",
                titleJp = "意味の確認",
                descMy = "ခန်းဂျီးစကားလုံးကို ကြည့်ပြီး မှန်ကန်သော မြန်မာအဓိပ္ပာယ်ကို ရွေးချယ်ပါ",
                icon = Icons.Default.Translate,
                accentColor = ReviewBlue
            ),
            QuizModeUiItem(
                type = QuizType.MEANING_TO_KANJI,
                seal = "想起",
                titleMy = "Burmese → Kanji",
                titleJp = "単語の想起",
                descMy = "မြန်မာအဓိပ္ပာယ်မှ သက်ဆိုင်ရာ ဂျပန်စကားလုံးနှင့် ခန်းဂျီးကို ရှာပါ",
                icon = Icons.Default.Psychology,
                accentColor = MasteredGreen
            ),
            QuizModeUiItem(
                type = QuizType.READING_CHALLENGE,
                seal = "読み",
                titleMy = "Furigana Reading",
                titleJp = "読み方テスト",
                descMy = "ခန်းဂျီးစကားလုံး၏ တိကျသော Hiragana / Furigana အသံထွက်ကို စစ်ဆေးပါ",
                icon = Icons.Default.MenuBook,
                accentColor = JapaneseIndigo
            ),
            QuizModeUiItem(
                type = QuizType.HOMOPHONE_SIMILAR_DRILL,
                seal = "同音",
                titleMy = "Tricky Pairs & Homophones",
                titleJp = "同音異義語・ဆင်တူရိုးမှား",
                descMy = "အသံထွက်တူသော်လည်း ခန်းဂျီးကွဲပြားသည့် ဆင်တူရိုးမှား စကားလုံးများကို ယှဉ်တွဲလေ့ကျင့်ပါ",
                icon = Icons.Default.Bolt,
                accentColor = StreakOrange,
                isHot = true
            ),
            QuizModeUiItem(
                type = QuizType.LISTENING_CHALLENGE,
                seal = "聴解",
                titleMy = "Audio Listening Challenge",
                titleJp = "リスニング聴解",
                descMy = "သဘာဝကျသော ဂျပန်အသံထွက်ကို နားထောင်ပြီး အဓိပ္ပာယ်နှင့် စကားလုံးကို ရွေးပါ",
                icon = Icons.Default.Headphones,
                accentColor = JapaneseCrimson
            ),
            QuizModeUiItem(
                type = QuizType.SENTENCE_CLOZE,
                seal = "穴埋",
                titleMy = "Sentence Cloze (Fill-in)",
                titleJp = "短文穴埋め問題",
                descMy = "စစ်မှန်သော JLPT ဝါကျများထဲရှိ ကွက်လပ် 【 ? 】 တွင် စကားလုံးအမှန်ကို ဖြည့်သွင်းပါ",
                icon = Icons.Default.MenuBook,
                accentColor = ReviewBlue
            ),
            QuizModeUiItem(
                type = QuizType.TRUE_FALSE_DRILL,
                seal = "○/×",
                titleMy = "True / False Blitz",
                titleJp = "正誤判定スピード",
                descMy = "၁၀ စက္ကန့်အတွင်း ခန်းဂျီးနှင့် မြန်မာအဓိပ္ပာယ် ကိုက်ညီမှု ရှိ/မရှိ လျှပ်တစ်ပြက် ဆုံးဖြတ်ပါ",
                icon = Icons.Default.Bolt,
                accentColor = WeakOrange
            ),
            QuizModeUiItem(
                type = QuizType.SPEED_TEST,
                seal = "秒速",
                titleMy = "Speed Rush (10s)",
                titleJp = "10秒ラッシュ",
                descMy = "၁၀ စက္ကန့်အချိန်ကန့်သတ်ချက်ဖြင့် လျင်မြန်စွာ အမှတ်ရနိုင်စွမ်းကို စိန်ခေါ်ပါ",
                icon = Icons.Default.Speed,
                accentColor = JapaneseCrimson
            ),
            QuizModeUiItem(
                type = QuizType.BOOKMARKED_DRILL,
                seal = "⭐",
                titleMy = "Starred Review",
                titleJp = "ブックマーク単語",
                descMy = "သင် ကြယ်ပွင့်ပေးမှတ်သားထားသော ကိုယ်ပိုင်ခက်ခဲစကားလုံးများကိုသာ သီးသန့်စစ်ဆေးပါ",
                icon = Icons.Default.Star,
                accentColor = StreakOrange
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_hub_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --------------------------------------------------------------------
        // 1. Hero Arena Header Card
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_arena_hero_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    JapaneseCrimson.copy(alpha = 0.14f),
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = JapaneseCrimson,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "試",
                                            fontFamily = JapaneseFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Quiz Arena 試練場",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "စကားလုံး၊ ခန်းဂျီးနှင့် အသံထွက် စစ်ဆေးရေး",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Streak flame pill
                            val streakVal = quizStreak?.currentStreak ?: 0
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = StreakOrange.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, StreakOrange.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = StreakOrange,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "$streakVal ရက်",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = StreakOrange
                                    )
                                }
                            }
                        }

                        // Stats Metric Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$totalQuizzesTaken",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "စစ်ဆေးမှုကြိမ်ရေ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(22.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$bestAccuracy%",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MasteredGreen
                                )
                                Text(
                                    text = "အကောင်းဆုံးတိကျမှု",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(22.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "10 မေးခွန်းမုဒ်",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ReviewBlue
                                )
                                Text(
                                    text = "အသင့်သုံးစနစ်",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Fast Launch Button
                        Button(
                            onClick = {
                                quizViewModel.startQuiz(
                                    quizType = QuizType.KANJI_TO_MEANING,
                                    lessonFilter = selectedLessonFilter,
                                    questionCount = selectedQuestionCount
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("quick_quiz_launch_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "အမြန်စတင် စစ်ဆေးမည် (Quick $selectedQuestionCount-Question Drill)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 2. Quiz Configuration Bar (Question Count + Lesson Filter)
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_config_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "မေးခွန်းအရေအတွက်နှင့် သင်ခန်းစာ သတ်မှတ်ချက်",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Question Count Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(5, 10, 20, 30).forEach { count ->
                            val isSelected = selectedQuestionCount == count
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedQuestionCount = count },
                                label = {
                                    Text(
                                        text = "$count ပုဒ်${if (count == 10) " (ပုံမှန်)" else ""}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    // Scope Filters FlowRow
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedLessonFilter == null,
                            onClick = { selectedLessonFilter = null },
                            label = { Text("စကားလုံးအားလုံး (All)") }
                        )
                        FilterChip(
                            selected = selectedLessonFilter == -1,
                            onClick = { selectedLessonFilter = -1 },
                            label = { Text("အားနည်းသောကတ်များ (Weak)") }
                        )
                        FilterChip(
                            selected = selectedLessonFilter == -2,
                            onClick = { selectedLessonFilter = -2 },
                            label = { Text("မှတ်သားထားသောကတ် ⭐") }
                        )
                        FilterChip(
                            selected = selectedLessonFilter == -3,
                            onClick = { selectedLessonFilter = -3 },
                            label = { Text("မေးခွန်းဟောင်း (Past Exams)") }
                        )

                        // Specific lesson button
                        Box {
                            FilterChip(
                                selected = selectedLessonFilter != null && selectedLessonFilter!! > 0,
                                onClick = { showLessonDropdown = true },
                                label = {
                                    val text = if (selectedLessonFilter != null && selectedLessonFilter!! > 0)
                                        "Lesson ${selectedLessonFilter}"
                                    else
                                        "သင်ခန်းစာ ရွေးချယ်ရန်..."
                                    Text(text)
                                }
                            )

                            DropdownMenu(
                                expanded = showLessonDropdown,
                                onDismissRequest = { showLessonDropdown = false }
                            ) {
                                (1..29).forEach { lesson ->
                                    DropdownMenuItem(
                                        text = { Text("Lesson $lesson") },
                                        onClick = {
                                            selectedLessonFilter = lesson
                                            showLessonDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 3. Quiz Modes Header & Grid
        // --------------------------------------------------------------------
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "စစ်ဆေးမှု မုဒ်များ (Quiz Modes)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "မိမိလေ့ကျင့်လိုသည့် စစ်ဆေးမှုအမျိုးအစားကို ရွေးချယ်ပါ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToHistory,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("quiz_hub_history_btn")
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "မှတ်တမ်း",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Mode Cards List
        items(quizModes) { mode ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        quizViewModel.startQuiz(
                            quizType = mode.type,
                            lessonFilter = selectedLessonFilter,
                            questionCount = selectedQuestionCount
                        )
                    }
                    .testTag("quiz_mode_${mode.type.name.lowercase()}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Seal Badge Icon
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = mode.accentColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, mode.accentColor.copy(alpha = 0.3f)),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = mode.seal,
                                    fontFamily = JapaneseFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = mode.accentColor
                                )
                                Icon(
                                    imageVector = mode.icon,
                                    contentDescription = null,
                                    tint = mode.accentColor,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = mode.titleMy,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (mode.isHot) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = JapaneseCrimson
                                ) {
                                    Text(
                                        text = "HOT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = mode.titleJp,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = JapaneseFontFamily,
                                fontSize = 11.sp,
                                color = mode.accentColor
                            )
                        )

                        Text(
                            text = mode.descMy,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Arrow
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // --------------------------------------------------------------------
        // 4. Recent Test History Teaser
        // --------------------------------------------------------------------
        if (recentSessions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToHistory() }
                        .testTag("quiz_recent_history_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = ReviewBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "လတ်တလော စစ်ဆေးမှု ရလဒ်များ",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "အပြည့်အစုံ ကြည့်မည် →",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Last 3 sessions quick pill list
                        recentSessions.take(3).forEach { session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = session.quizType,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${session.score}/${session.totalQuestions} (${session.accuracyPercentage}%)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (session.accuracyPercentage >= 80) MasteredGreen else MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
