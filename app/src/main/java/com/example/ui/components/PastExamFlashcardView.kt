package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PastExamData
import com.example.data.model.VocabCard
import com.example.data.srs.ReviewRating
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.ReviewBlue
import com.example.ui.theme.WeakOrange

@Composable
fun PastExamFlashcardView(
    allPastExamCards: List<VocabCard>,
    onSpeak: (String) -> Unit,
    onRateCard: (VocabCard, ReviewRating) -> Unit,
    onToggleBookmark: (VocabCard) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSessionCode by remember { mutableStateOf<String?>(null) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isFrontJapanese by remember { mutableStateOf(true) }

    val activeDeck = remember(allPastExamCards, selectedSessionCode) {
        val list = if (selectedSessionCode == null) {
            allPastExamCards
        } else {
            allPastExamCards.filter { it.tags.contains(selectedSessionCode!!) }
        }
        if (list.isEmpty()) allPastExamCards else list
    }

    var workingDeck by remember(activeDeck) {
        mutableStateOf(activeDeck)
    }

    // Reset card index on filter change
    LaunchedEffect(selectedSessionCode, activeDeck) {
        workingDeck = activeDeck
        currentIndex = 0
        isFlipped = false
    }

    val currentCard = workingDeck.getOrNull(currentIndex) ?: allPastExamCards.firstOrNull()

    // Flip animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 380),
        label = "flashcard_rotation"
    )

    fun nextCard() {
        if (currentIndex < workingDeck.size - 1) {
            isFlipped = false
            currentIndex++
        }
    }

    fun prevCard() {
        if (currentIndex > 0) {
            isFlipped = false
            currentIndex--
        }
    }

    fun shuffleDeck() {
        workingDeck = workingDeck.shuffled()
        currentIndex = 0
        isFlipped = false
    }

    fun resetDeck() {
        workingDeck = activeDeck
        currentIndex = 0
        isFlipped = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Session Filter Chips Row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "စာမေးပွဲ အကြိမ်ရွေးချယ်ရန် • Select Exam Session",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${workingDeck.size} ကတ်",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedSessionCode == null,
                        onClick = { selectedSessionCode = null },
                        label = { Text("အားလုံး (${allPastExamCards.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
                items(PastExamData.SESSIONS) { session ->
                    FilterChip(
                        selected = selectedSessionCode == session.code,
                        onClick = { selectedSessionCode = session.code },
                        label = { Text("${session.code} (${session.wordCount})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Progress & Flip Orientation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ကတ်နံပါတ် ${currentIndex + 1} / ${workingDeck.size}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isFlipped) "အဖြေကြည့်နေသည် (Answer Shown)" else "ရှေ့မျက်နှာ (Tap card to flip)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Mode switch: Japanese first vs Myanmar first
            OutlinedButton(
                onClick = { isFrontJapanese = !isFrontJapanese },
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Flip,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFrontJapanese) "ဂျပန်စာ → မြန်မာ" else "မြန်မာ → ဂျပန်စာ",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LinearProgressIndicator(
            progress = {
                if (workingDeck.isEmpty()) 0f else (currentIndex + 1).toFloat() / workingDeck.size
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // 3D Flippable Card
        if (currentCard != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 14f * density
                    }
                    .clickable { isFlipped = !isFlipped }
                    .testTag("past_exam_3d_card"),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    // FRONT SIDE
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            JapaneseCrimson.copy(alpha = 0.06f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            // Top Tag & Speaker
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = currentCard.personalNote.ifBlank { "JLPT N3 မေးခွန်းဟောင်း" },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onSpeak(currentCard.reading.ifBlank { currentCard.kanji }) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak Pronunciation",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Center Content
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (isFrontJapanese) {
                                    Text(
                                        text = currentCard.kanji,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontFamily = JapaneseFontFamily,
                                            fontSize = 38.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )

                                    if (currentCard.reading.isNotBlank() && currentCard.reading != currentCard.kanji) {
                                        Text(
                                            text = currentCard.reading,
                                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = JapaneseFontFamily),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Text(
                                        text = currentCard.meaningBurmese,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Bottom Hint
                            Text(
                                text = "👆 တို့ထိပြီး အဖြေလှန်ကြည့်ပါ • Tap to Flip",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                } else {
                    // BACK SIDE (Reversed rotation)
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f },
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f),
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            // Top Tag & Bookmark
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = "အဖြေ • Answer",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onToggleBookmark(currentCard) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (currentCard.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (currentCard.isBookmarked) JapaneseCrimson else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            // Center Content
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (isFrontJapanese) {
                                    Text(
                                        text = currentCard.meaningBurmese,
                                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.width(60.dp),
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "読み方: ${currentCard.reading}",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = JapaneseFontFamily),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        IconButton(
                                            onClick = { onSpeak(currentCard.reading.ifBlank { currentCard.kanji }) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Pronounce",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = currentCard.kanji,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontFamily = JapaneseFontFamily,
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "読み方: ${currentCard.reading}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = JapaneseFontFamily),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Bottom Hint
                            Text(
                                text = "👆 တို့ထိပြီး ရှေ့မျက်နှာသို့ ပြန်လှန်ပါ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }

            // SRS Rating Buttons (Easy, Good, Hard)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "မှတ်မိနိုင်စွမ်း အဆင့်သတ်မှတ်ပါ • Rate Recall:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onRateCard(currentCard, ReviewRating.AGAIN)
                            nextCard()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WeakOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("ခက်သည် (Hard)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onRateCard(currentCard, ReviewRating.GOOD)
                            nextCard()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ReviewBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("သင့်သည် (Good)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onRateCard(currentCard, ReviewRating.EASY)
                            nextCard()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MasteredGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("လွယ်သည် (Easy)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Navigation Controls: Prev, Flip, Next, Shuffle, Restart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { shuffleDeck() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = "Shuffle Deck")
                    }
                    IconButton(
                        onClick = { resetDeck() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = "Restart Deck")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(
                        onClick = { prevCard() },
                        enabled = currentIndex > 0,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Card"
                        )
                    }

                    Button(
                        onClick = { isFlipped = !isFlipped },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFlipped) "ရှေ့သို့" else "အဖြေလှန်",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { nextCard() },
                        enabled = currentIndex < workingDeck.size - 1,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Card"
                        )
                    }
                }
            }
        }
    }
}
