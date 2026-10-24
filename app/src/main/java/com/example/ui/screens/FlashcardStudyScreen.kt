package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val sessionStats by vocabViewModel.sessionStats.collectAsState()
    val fontScale by vocabViewModel.flashcardFontScale.collectAsState()
    val themeSettings by vocabViewModel.themeSettings.collectAsState()

    var showNoteDialog by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var showSrsScheduleSheet by remember { mutableStateOf(false) }
    var showCompoundLookupSheet by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "card_rotation"
    )

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
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (deck.isEmpty() || isFinished) {
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
                    AssistChip(
                        onClick = { showCompoundLookupSheet = true },
                        label = { Text("Compound Lookup") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )

                    Row {
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

                // 3D Card Face with GraphicsLayer Flip
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 14f * density
                        }
                        .clickable { vocabViewModel.flipCard() },
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation <= 90f) {
                            // Front Face
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (showFurigana && card.reading.isNotBlank()) {
                                    Text(
                                        text = card.reading,
                                        fontSize = (18 * fontScale).sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                Text(
                                    text = card.kanji,
                                    fontSize = (38 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                IconButton(onClick = { vocabViewModel.speakCard(card) }) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak", tint = MaterialTheme.colorScheme.primary)
                                }
                                Text(
                                    text = "Tap to reveal answer",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        } else {
                            // Back Face (Mirrored)
                            Column(
                                modifier = Modifier.graphicsLayer { rotationY = 180f },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = card.meaningBurmese,
                                    fontSize = (22 * fontScale).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                if (card.partOfSpeech.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = card.partOfSpeech,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                if (card.exampleSentence.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = card.exampleSentence,
                                        fontSize = (15 * fontScale).sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    if (card.exampleSentenceBurmese.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = card.exampleSentenceBurmese,
                                            fontSize = (14 * fontScale).sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

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
                            Text("Again", color = Color.White)
                        }
                        Button(
                            onClick = { vocabViewModel.rateCurrentCard(ReviewRating.HARD) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Hard", color = Color.White)
                        }
                        Button(
                            onClick = { vocabViewModel.rateCurrentCard(ReviewRating.GOOD) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Good", color = Color.White)
                        }
                        Button(
                            onClick = { vocabViewModel.rateCurrentCard(ReviewRating.EASY) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Easy", color = Color.White)
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
                            enabled = currentIndex > 0
                        ) {
                            Text("Previous")
                        }

                        Button(
                            onClick = { vocabViewModel.flipCard() },
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        ) {
                            Text("Flip Card")
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
        }
    }
}
