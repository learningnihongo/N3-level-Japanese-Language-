package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CustomFlashcard
import com.example.data.model.VocabCard
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseIndigo
import com.example.ui.theme.MasteredGreen
import com.example.ui.viewmodel.VocabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCardsScreen(
    vocabViewModel: VocabViewModel,
    onBackClick: () -> Unit,
    onNavigateToStudy: () -> Unit
) {
    val currentUser by vocabViewModel.currentUser.collectAsState()
    val customCards by vocabViewModel.cloudCustomCards.collectAsState()
    val allLibraryCards by vocabViewModel.allCards.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Decks, 1 = All Cards
    var showCreateDeckFromLibraryDialog by remember { mutableStateOf(false) }
    var showManualAddCardDialog by remember { mutableStateOf(false) }
    var preselectedDeckNameForAdd by remember { mutableStateOf("General") }

    // Group cards by deck
    val decksMap = remember(customCards) {
        customCards.groupBy { it.deckName.ifBlank { "General" } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Custom Flashcard Decks",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "ကိုယ်ပိုင်ကတ်တွဲများနှင့် Flashcard များ (${customCards.size} cards)",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("custom_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCreateDeckFromLibraryDialog = true },
                        modifier = Modifier.testTag("create_deck_from_library_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryAdd,
                            contentDescription = "Create Deck from Library",
                            tint = JapaneseCrimson
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDeckFromLibraryDialog = true },
                containerColor = JapaneseCrimson,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Deck / ကတ်တွဲသစ်", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("add_custom_deck_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Cloud Status Header Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (currentUser != null) MasteredGreen.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                border = BorderStroke(
                    1.dp,
                    if (currentUser != null) MasteredGreen.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (currentUser != null) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = if (currentUser != null) MasteredGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (currentUser != null) "✓ Firebase Cloud Profile ပေါ်သို့ အလိုအလျောက် သိမ်းဆည်းနေပါသည် (${currentUser?.email ?: "Synced"})"
                        else "💡 Profile ကဏ္ဍတွင် Google Sign-In ဝင်ပါက ကတ်တွဲများကို Cloud ပေါ်တွင် လုံခြုံစွာ သိမ်းဆည်းပေးမည်ဖြစ်ပါသည်။",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showCreateDeckFromLibraryDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = JapaneseCrimson
                    ),
                    border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick from Library", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        preselectedDeckNameForAdd = "General"
                        showManualAddCardDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = JapaneseIndigo)
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Manual Input", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Tab Switcher
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Decks (${decksMap.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("All Cards (${customCards.size})", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Content
            if (selectedTab == 0) {
                // Decks List
                if (decksMap.isEmpty()) {
                    EmptyDecksView(
                        onCreateFromLibrary = { showCreateDeckFromLibraryDialog = true },
                        onManualAdd = { showManualAddCardDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(decksMap.keys.toList(), key = { it }) { deckName ->
                            val cardsInDeck = decksMap[deckName] ?: emptyList()
                            CustomDeckCard(
                                deckName = deckName,
                                cards = cardsInDeck,
                                onStudyDeck = {
                                    vocabViewModel.startCustomDeckStudy(cardsInDeck)
                                    onNavigateToStudy()
                                },
                                onAddCardToDeck = {
                                    preselectedDeckNameForAdd = deckName
                                    showManualAddCardDialog = true
                                },
                                onDeleteDeck = {
                                    vocabViewModel.deleteDeck(deckName)
                                }
                            )
                        }
                    }
                }
            } else {
                // All Cards List
                if (customCards.isEmpty()) {
                    EmptyDecksView(
                        onCreateFromLibrary = { showCreateDeckFromLibraryDialog = true },
                        onManualAdd = { showManualAddCardDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(customCards, key = { it.id }) { card ->
                            CustomCardItem(
                                card = card,
                                onDelete = { vocabViewModel.deleteCustomCard(card.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog 1: Create Deck from Library (attached seed data)
    if (showCreateDeckFromLibraryDialog) {
        CreateDeckFromLibraryDialog(
            allCards = allLibraryCards,
            onDismiss = { showCreateDeckFromLibraryDialog = false },
            onConfirmCreate = { deckName, selectedCards ->
                vocabViewModel.createCustomDeckFromSelection(deckName, selectedCards)
                showCreateDeckFromLibraryDialog = false
            }
        )
    }

    // Dialog 2: Manual Card Creation Dialog
    if (showManualAddCardDialog) {
        ManualAddCardDialog(
            initialDeckName = preselectedDeckNameForAdd,
            existingDecks = decksMap.keys.toList(),
            onDismiss = { showManualAddCardDialog = false },
            onSave = { front, reading, back, cardType, deckName, exSentence, exMeaning ->
                vocabViewModel.addCustomCard(
                    front = front,
                    reading = reading,
                    back = back,
                    cardType = cardType,
                    category = "Custom Deck",
                    deckName = deckName,
                    exampleSentence = exSentence,
                    exampleMeaning = exMeaning
                )
                showManualAddCardDialog = false
            }
        )
    }
}

@Composable
private fun CustomDeckCard(
    deckName: String,
    cards: List<CustomFlashcard>,
    onStudyDeck: () -> Unit,
    onAddCardToDeck: () -> Unit,
    onDeleteDeck: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deck_card_$deckName"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Deck Name & Count Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(JapaneseCrimson.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = null,
                            tint = JapaneseCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = deckName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${cards.size} Cards • Flashcard Deck",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Deck",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Preview Chips of words in this deck
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cards.take(6)) { card ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = card.front,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                if (cards.size > 6) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "+${cards.size - 6} more",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Actions: Study Deck + Add More
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAddCardToDeck,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Word", fontSize = 12.sp)
                }

                Button(
                    onClick = onStudyDeck,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Study Deck", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Deck '$deckName'?") },
            text = { Text("ဤကတ်တွဲရှိ ကတ်အားလုံး (${cards.size} ခု) ကို ဖျက်ပစ်ပါမည်လား။") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDeck()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CustomCardItem(
    card: CustomFlashcard,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("custom_card_${card.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = card.front,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (card.reading.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "[${card.reading}]",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = JapaneseIndigo.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = card.deckName.ifBlank { "General" },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = JapaneseIndigo,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.back,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (card.exampleSentence.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.exampleSentence,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_card_${card.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyDecksView(
    onCreateFromLibrary: () -> Unit,
    onManualAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(JapaneseCrimson.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Style,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = JapaneseCrimson
                )
            }

            Text(
                text = "ကိုယ်ပိုင် Flashcard ကတ်တွဲ မရှိသေးပါ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "မေးခွန်းဟောင်းများ (1,357 cards) သို့မဟုတ် ဝေါဟာရများမှ\nစိတ်ကြိုက် ကတ်တွဲသစ် ပြုလုပ်နိုင်ပါသည်",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onCreateFromLibrary,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson)
                ) {
                    Icon(Icons.Default.LibraryAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick from Library")
                }

                OutlinedButton(
                    onClick = onManualAdd,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Manual Add")
                }
            }
        }
    }
}

// Dialog: Create Deck from Library (Past Exams, Kanji, Kotoba)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateDeckFromLibraryDialog(
    allCards: List<VocabCard>,
    onDismiss: () -> Unit,
    onConfirmCreate: (String, List<VocabCard>) -> Unit
) {
    var deckName by remember { mutableStateOf("JLPT N3 Study Deck") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") } // "All", "Past Exam", "Kanji", "Kotoba"
    val selectedCards = remember { mutableStateListOf<VocabCard>() }

    val filteredList = remember(allCards, searchQuery, selectedCategoryFilter) {
        allCards.filter { card ->
            val matchesCategory = when (selectedCategoryFilter) {
                "Past Exam" -> card.lessonTitle.contains("Exam", ignoreCase = true) || card.lessonTitle.contains("Past", ignoreCase = true)
                "Kanji" -> card.sectionTitle.contains("Kanji", ignoreCase = true)
                "Kotoba" -> !card.sectionTitle.contains("Kanji", ignoreCase = true)
                else -> true
            }
            val matchesQuery = searchQuery.isBlank() ||
                card.kanji.contains(searchQuery, ignoreCase = true) ||
                card.reading.contains(searchQuery, ignoreCase = true) ||
                card.meaningBurmese.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Create Deck from Library",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapaneseCrimson
                )

                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Deck Name (ကတ်တွဲအမည်) *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("deck_name_input")
                )

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Kanji, Reading, Burmese...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val cats = listOf("All", "Past Exam", "Kanji", "Kotoba")
                    items(cats) { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Quick Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected: ${selectedCards.size} cards",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = {
                                val top15 = filteredList.take(15)
                                for (c in top15) {
                                    if (!selectedCards.contains(c)) selectedCards.add(c)
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("+ Top 15", fontSize = 11.sp)
                        }

                        TextButton(
                            onClick = { selectedCards.clear() },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Clear", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Cards Multi-selection List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredList.take(60), key = { it.id }) { card ->
                        val isChecked = selectedCards.any { it.id == card.id }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(
                                1.dp,
                                if (isChecked) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isChecked) {
                                        selectedCards.removeAll { it.id == card.id }
                                    } else {
                                        selectedCards.add(card)
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedCards.add(card)
                                        else selectedCards.removeAll { it.id == card.id }
                                    },
                                    modifier = Modifier.size(20.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = card.kanji.ifEmpty { card.reading },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (card.reading.isNotEmpty() && card.kanji.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "(${card.reading})",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(
                                        text = card.meaningBurmese,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = if (card.lessonNumber >= 100) "Exam" else "Word",
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (deckName.isNotBlank() && selectedCards.isNotEmpty()) {
                                onConfirmCreate(deckName.trim(), selectedCards.toList())
                            }
                        },
                        enabled = deckName.isNotBlank() && selectedCards.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create Deck (${selectedCards.size})")
                    }
                }
            }
        }
    }
}

// Dialog: Manual Card Creation
@Composable
private fun ManualAddCardDialog(
    initialDeckName: String,
    existingDecks: List<String>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String) -> Unit
) {
    var front by remember { mutableStateOf("") }
    var reading by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    var deckName by remember { mutableStateOf(initialDeckName) }
    var cardType by remember { mutableStateOf("Kotoba") }
    var exampleSentence by remember { mutableStateOf("") }
    var exampleMeaning by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Add Custom Flashcard",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = JapaneseIndigo
                )

                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    label = { Text("Deck Name (ကတ်တွဲအမည်)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = front,
                    onValueChange = { front = it },
                    label = { Text("Japanese Word / Kanji (Front) *") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_card_front")
                )

                OutlinedTextField(
                    value = reading,
                    onValueChange = { reading = it },
                    label = { Text("Furigana Reading (အသံထွက်)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_card_reading")
                )

                OutlinedTextField(
                    value = back,
                    onValueChange = { back = it },
                    label = { Text("Burmese Meaning (အဓိပ္ပာယ်) *") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_card_back")
                )

                OutlinedTextField(
                    value = exampleSentence,
                    onValueChange = { exampleSentence = it },
                    label = { Text("Example Sentence (နမူနာဝါကျ)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (front.isNotBlank() && back.isNotBlank()) {
                                onSave(
                                    front.trim(),
                                    reading.trim(),
                                    back.trim(),
                                    cardType,
                                    deckName.ifBlank { "General" }.trim(),
                                    exampleSentence.trim(),
                                    exampleMeaning.trim()
                                )
                            }
                        },
                        enabled = front.isNotBlank() && back.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = JapaneseIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Card")
                    }
                }
            }
        }
    }
}
