package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PastExamData
import com.example.data.model.VocabCard
import com.example.ui.components.PastExamFlashcardView
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.viewmodel.VocabViewModel

enum class PastExamTab(val title: String, val subtitle: String) {
    KOTOBA("Kotoba (ဝေါဟာရ)", "語彙・単語"),
    KANJI("Kanji (ခန်းဂျီး)", "漢字・文字"),
    FLASHCARDS("Flashcards (ကတ်ပြား)", "3D ဖလက်ရှ်ကတ်")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastExamSectionScreen(
    vocabViewModel: VocabViewModel,
    onNavigateBack: () -> Unit,
    onStartPastExamQuiz: (List<VocabCard>) -> Unit,
    modifier: Modifier = Modifier
) {
    val allCards by vocabViewModel.allCards.collectAsState()
    val pastExamCards = remember(allCards) {
        allCards.filter { PastExamData.isPastExamCard(it) }
    }

    var selectedTab by remember { mutableStateOf(PastExamTab.KOTOBA) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSessionFilter by remember { mutableStateOf<String?>(null) }

    // Cards containing Kanji characters
    val pastExamKanjiCards = remember(pastExamCards) {
        pastExamCards.filter { PastExamData.hasKanji(it.kanji) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "မေးခွန်းဟောင်း (Past Exams)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = JapaneseCrimson.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "2014-2025",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = JapaneseCrimson,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "JLPT N3 စာမေးပွဲ မေးခွန်းဟောင်း စုစည်းမှု • ${pastExamCards.size} စကားလုံး",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag("past_exam_back_btn")
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                actions = {
                    // Quick Start Quiz on Past Exams
                    IconButton(
                        onClick = { onStartPastExamQuiz(pastExamCards) },
                        modifier = Modifier
                            .testTag("past_exam_quiz_btn")
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = "Start Past Exam Quiz",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row: Kotoba, Kanji, Flashcards
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                PastExamTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = tab.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                    color = if (selectedTab == tab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        modifier = Modifier.testTag("past_exam_tab_${tab.name.lowercase()}")
                    )
                }
            }

            when (selectedTab) {
                PastExamTab.KOTOBA -> {
                    KotobaListView(
                        cards = pastExamCards,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedSession = selectedSessionFilter,
                        onSelectSession = { selectedSessionFilter = it },
                        onSpeak = { vocabViewModel.speakJapanese(it) },
                        onToggleBookmark = { vocabViewModel.toggleBookmark(it) }
                    )
                }
                PastExamTab.KANJI -> {
                    KanjiListView(
                        cards = pastExamKanjiCards,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedSession = selectedSessionFilter,
                        onSelectSession = { selectedSessionFilter = it },
                        onSpeak = { vocabViewModel.speakJapanese(it) },
                        onToggleBookmark = { vocabViewModel.toggleBookmark(it) }
                    )
                }
                PastExamTab.FLASHCARDS -> {
                    PastExamFlashcardView(
                        allPastExamCards = pastExamCards,
                        onSpeak = { vocabViewModel.speakJapanese(it) },
                        onRateCard = { card, rating -> vocabViewModel.rateFlashcardRecall(card, rating) },
                        onToggleBookmark = { vocabViewModel.toggleBookmark(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KotobaListView(
    cards: List<VocabCard>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSession: String?,
    onSelectSession: (String?) -> Unit,
    onSpeak: (String) -> Unit,
    onToggleBookmark: (VocabCard) -> Unit
) {
    val filteredCards = remember(cards, searchQuery, selectedSession) {
        cards.filter { card ->
            val matchSession = selectedSession == null || card.tags.contains(selectedSession)
            val matchQuery = searchQuery.isBlank() ||
                    card.kanji.contains(searchQuery, ignoreCase = true) ||
                    card.reading.contains(searchQuery, ignoreCase = true) ||
                    card.meaningBurmese.contains(searchQuery, ignoreCase = true)
            matchSession && matchQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search past exam word, reading, meaning...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("past_exam_kotoba_search")
        )

        // Session Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedSession == null,
                    onClick = { onSelectSession(null) },
                    label = { Text("အားလုံး (${cards.size})", fontSize = 11.5.sp) }
                )
            }
            items(PastExamData.SESSIONS) { s ->
                FilterChip(
                    selected = selectedSession == s.code,
                    onClick = { onSelectSession(if (selectedSession == s.code) null else s.code) },
                    label = { Text("${s.code} (${s.wordCount})", fontSize = 11.5.sp) }
                )
            }
        }

        Text(
            text = "တွေ့ရှိသော စကားလုံး ${filteredCards.size} ခု",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCards, key = { it.id }) { card ->
                PastExamWordItem(
                    card = card,
                    onSpeak = { onSpeak(card.reading.ifBlank { card.kanji }) },
                    onToggleBookmark = { onToggleBookmark(card) }
                )
            }
        }
    }
}

@Composable
private fun KanjiListView(
    cards: List<VocabCard>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSession: String?,
    onSelectSession: (String?) -> Unit,
    onSpeak: (String) -> Unit,
    onToggleBookmark: (VocabCard) -> Unit
) {
    val filteredCards = remember(cards, searchQuery, selectedSession) {
        cards.filter { card ->
            val matchSession = selectedSession == null || card.tags.contains(selectedSession)
            val matchQuery = searchQuery.isBlank() ||
                    card.kanji.contains(searchQuery, ignoreCase = true) ||
                    card.reading.contains(searchQuery, ignoreCase = true) ||
                    card.meaningBurmese.contains(searchQuery, ignoreCase = true)
            matchSession && matchQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search Kanji character, reading, or meaning...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("past_exam_kanji_search")
        )

        // Session Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedSession == null,
                    onClick = { onSelectSession(null) },
                    label = { Text("ခန်းဂျီး အားလုံး (${cards.size})", fontSize = 11.5.sp) }
                )
            }
            items(PastExamData.SESSIONS) { s ->
                FilterChip(
                    selected = selectedSession == s.code,
                    onClick = { onSelectSession(if (selectedSession == s.code) null else s.code) },
                    label = { Text(s.code, fontSize = 11.5.sp) }
                )
            }
        }

        Text(
            text = "တွေ့ရှိသော ခန်းဂျီးစကားလုံး ${filteredCards.size} ခု",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredCards, key = { it.id }) { card ->
                PastExamKanjiItem(
                    card = card,
                    onSpeak = { onSpeak(card.reading.ifBlank { card.kanji }) },
                    onToggleBookmark = { onToggleBookmark(card) }
                )
            }
        }
    }
}

@Composable
private fun PastExamWordItem(
    card: VocabCard,
    onSpeak: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = card.kanji,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JapaneseFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (card.reading.isNotBlank() && card.reading != card.kanji) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = card.reading,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = JapaneseFontFamily),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = card.personalNote.replace("JLPT N3 ", ""),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = card.meaningBurmese,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (card.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (card.isBookmarked) JapaneseCrimson else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PastExamKanjiItem(
    card: VocabCard,
    onSpeak: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Large Kanji Character Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = JapaneseCrimson.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.25f)),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = card.kanji,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JapaneseFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = JapaneseCrimson
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = card.reading,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = JapaneseFontFamily,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = card.personalNote.replace("JLPT N3 ", ""),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = card.meaningBurmese,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSpeak, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speak",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = if (card.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (card.isBookmarked) JapaneseCrimson else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
