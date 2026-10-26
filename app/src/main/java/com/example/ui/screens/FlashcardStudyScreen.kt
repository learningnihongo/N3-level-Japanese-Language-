package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.testTag
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VocabCard
import com.example.data.srs.ReviewRating
import com.example.ui.components.*
import com.example.ui.viewmodel.FlashcardStudyMode
import com.example.ui.viewmodel.VocabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardStudyScreen(
    vocabViewModel: VocabViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deck by vocabViewModel.studyDeck.collectAsState()
    val currentIndex by vocabViewModel.currentCardIndex.collectAsState()
    val isFlipped by vocabViewModel.isCardFlipped.collectAsState()
    val isFinished by vocabViewModel.isSessionFinished.collectAsState()
    val studyMode by vocabViewModel.studyMode.collectAsState()
    val showFurigana by vocabViewModel.showFurigana.collectAsState()
    val isAutoPlay by vocabViewModel.isAutoPlay.collectAsState()
    val isQuickReviewMode by vocabViewModel.isQuickReviewMode.collectAsState()
    val quickReviewRemainingMillis by vocabViewModel.quickReviewRemainingMillis.collectAsState()
    val sessionStats by vocabViewModel.sessionStats.collectAsState()
    val fontScale by vocabViewModel.flashcardFontScale.collectAsState()
    val themeSettings by vocabViewModel.themeSettings.collectAsState()
    val isSpeaking by vocabViewModel.isSpeaking.collectAsState()

    var showNoteDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var showSrsScheduleSheet by remember { mutableStateOf(false) }
    var showCompoundLookupSheet by remember { mutableStateOf(false) }
    var showDeckSettingsSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (deck.isEmpty() && !isFinished) {
            vocabViewModel.startDueCardsStudySession()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (deck.isNotEmpty()) "Card ${currentIndex + 1} of ${deck.size}" else "Study Session",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = studyMode.label + " • " + studyMode.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vocabViewModel.toggleFurigana() }) {
                        Icon(
                            if (showFurigana) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Furigana"
                        )
                    }
                    IconButton(onClick = { vocabViewModel.cycleFlashcardFontScale() }) {
                        Icon(Icons.Filled.FormatSize, contentDescription = "Font Scale")
                    }
                    IconButton(onClick = { showSrsScheduleSheet = true }) {
                        Icon(Icons.Filled.Schedule, contentDescription = "SRS Schedule")
                    }
                    IconButton(
                        onClick = { showDeckSettingsSheet = true },
                        modifier = Modifier.testTag("deck_settings_button")
                    ) {
                        Icon(Icons.Filled.Tune, contentDescription = "Deck Settings")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (isFinished) {
            // Session Completion Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("🎉 Study Session Complete!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Total Studied: ${sessionStats.totalStudied}", style = MaterialTheme.typography.bodyLarge)
                        Text("XP Earned: +${sessionStats.xpEarned} XP", style = MaterialTheme.typography.titleMedium, color = Color(0xFFFF9800))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text("Again: ${sessionStats.againCount}", color = Color(0xFFE53935))
                            Text("Hard: ${sessionStats.hardCount}", color = Color(0xFFFB8C00))
                            Text("Good: ${sessionStats.goodCount}", color = Color(0xFF43A047))
                            Text("Easy: ${sessionStats.easyCount}", color = Color(0xFF1E88E5))
                        }

                        if (sessionStats.mistakeCards.isNotEmpty()) {
                            Button(
                                onClick = { vocabViewModel.restartMistakesOnly() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Drill Mistakes (${sessionStats.mistakeCards.size} cards)")
                            }
                        }

                        Button(
                            onClick = { vocabViewModel.restartStudySession() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Restart Full Deck")
                        }

                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Return Home")
                        }
                    }
                }
            }
        } else if (deck.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "3D ဖလက်ရှ်ကတ်များကို ပြင်ဆင်နေပါသည်...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val card = deck.getOrNull(currentIndex) ?: deck.first()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Utilities Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = isQuickReviewMode,
                            onClick = { vocabViewModel.toggleQuickReviewMode() },
                            label = { Text(if (isQuickReviewMode) "⚡ 3s Flip" else "Quick Review", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Bolt,
                                    contentDescription = null,
                                    tint = if (isQuickReviewMode) Color(0xFFFF9800) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.testTag("quick_review_chip")
                        )

                        AssistChip(
                            onClick = { showCompoundLookupSheet = true },
                            label = { Text("Lookup") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showDeckSettingsSheet = true },
                            modifier = Modifier.testTag("deck_settings_icon_button")
                        ) {
                            Icon(Icons.Filled.Tune, contentDescription = "Deck Settings", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { vocabViewModel.toggleBookmark(card) }) {
                            Icon(
                                if (card.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (card.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showNoteDialog = true }) {
                            Icon(Icons.Filled.EditNote, contentDescription = "Note")
                        }
                    }
                }

                // ⚡ Quick Review Mode Countdown Banner
                if (isQuickReviewMode) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isFlipped) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, if (!isFlipped) Color(0xFFFFB74D) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Bolt,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (!isFlipped) "⚡ Quick Review: Card auto-flips in 3s" else "⚡ Quick Review: Rate recall to continue",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isFlipped) Color(0xFFE65100) else MaterialTheme.colorScheme.primary
                                    )
                                }

                                if (!isFlipped) {
                                    Text(
                                        text = "${((quickReviewRemainingMillis + 999) / 1000).coerceAtLeast(1)}s",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                            }

                            if (!isFlipped) {
                                LinearProgressIndicator(
                                    progress = { (quickReviewRemainingMillis / 3000f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp),
                                    color = Color(0xFFFF9800),
                                    trackColor = Color(0xFFFFE0B2)
                                )
                            }
                        }
                    }
                }

                // 3D Card Face with GraphicsLayer Flip & realistic spring physics
                StudyFlashcardCard(
                    card = card,
                    isFlipped = isFlipped,
                    onFlip = { vocabViewModel.flipCard() },
                    studyMode = studyMode,
                    fontScale = fontScale,
                    showFurigana = showFurigana,
                    showRomajiHint = false,
                    isSpeaking = isSpeaking,
                    onSpeakWord = { vocabViewModel.speakJapanese(card.kanji) },
                    onSpeakReading = { vocabViewModel.speakJapanese(card.reading) },
                    onSpeakSlow = { vocabViewModel.speakJapanese(card.kanji, rate = 0.7f) },
                    onSpeakSentence = { if (card.exampleSentence.isNotBlank()) vocabViewModel.speakJapanese(card.exampleSentence) },
                    onToggleBookmark = { vocabViewModel.toggleBookmark(card) },
                    onToggleFurigana = { vocabViewModel.toggleFurigana() },
                    onOpenNoteDialog = { showNoteDialog = true },
                    onOpenCompoundLookup = { showCompoundLookupSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp)
                )

                // Bottom SRS Rating Buttons Bar
                if (isFlipped) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { vocabViewModel.rateCurrentCard(ReviewRating.AGAIN) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Again", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("၁ ရက်", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                            }
                        }
                        Button(
                            onClick = { vocabViewModel.rateCurrentCard(ReviewRating.HARD) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Hard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("၃ ရက်", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                            }
                        }
                        Button(
                            onClick = { vocabViewModel.rateCurrentCard(ReviewRating.GOOD) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Good", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("၇ ရက်", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                            }
                        }
                        Button(
                            onClick = { vocabViewModel.rateCurrentCard(ReviewRating.EASY) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Easy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("၁၄ ရက်", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { vocabViewModel.previousCard() },
                            enabled = currentIndex > 0,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ရှေ့သို့")
                        }

                        Button(
                            onClick = { vocabViewModel.flipCard() },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("3D အဖြေလှန်မည် (Flip)", fontWeight = FontWeight.Bold)
                        }

                        IconButton(onClick = { vocabViewModel.toggleAutoPlay() }) {
                            Icon(
                                if (isAutoPlay) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                                contentDescription = "Auto Play",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // Dialogs
            if (showNoteDialog) {
                PersonalNoteDialog(
                    card = card,
                    onDismiss = { showNoteDialog = false },
                    onSaveNote = { note ->
                        vocabViewModel.updatePersonalNote(card.id, note)
                        showNoteDialog = false
                    }
                )
            }

            if (showCompoundLookupSheet) {
                KanjiCompoundLookupSheet(
                    kanjiChar = card.kanji.firstOrNull() ?: ' ',
                    allCards = deck,
                    onDismiss = { showCompoundLookupSheet = false }
                )
            }

            if (showSrsScheduleSheet) {
                SrsScheduleTrackingSheet(
                    vocabViewModel = vocabViewModel,
                    onDismiss = { showSrsScheduleSheet = false },
                    onStartReview = { showSrsScheduleSheet = false }
                )
            }

            if (showDeckSettingsSheet) {
                FlashcardDeckSettingsSheet(
                    vocabViewModel = vocabViewModel,
                    onDismiss = { showDeckSettingsSheet = false }
                )
            }
        }
    }
}
