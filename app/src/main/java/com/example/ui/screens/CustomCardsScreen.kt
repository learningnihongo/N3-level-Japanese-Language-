package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomFlashcard
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCardsScreen(
    currentUser: FirebaseUser?,
    customCards: List<CustomFlashcard>,
    onBackClick: () -> Unit,
    onAddCard: (CustomFlashcard) -> Unit,
    onDeleteCard: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ကိုယ်ပိုင် Flashcard များ (${customCards.size})", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("custom_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_custom_card_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (currentUser == null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "💡 Google ဖြင့် Sign-In ဝင်ထားပါက ကတ်များကို Firebase Cloud ပေါ်တွင် လုံခြုံစွာ သိမ်းဆည်းပေးမည်ဖြစ်ပါသည်။",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (customCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = "Empty",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ကိုယ်ပိုင် Flashcard မရှိသေးပါ။",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ညာဘက်အောက်ခြေရှိ '+' ခလုတ်ကို နှိပ်၍ အသစ်ထည့်သွင်းပါ",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(customCards, key = { it.id }) { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("custom_card_${card.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = card.front,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (card.reading.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "[${card.reading}]",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = card.back,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (card.exampleSentence.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = card.exampleSentence,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteCard(card.id) },
                                    modifier = Modifier.testTag("delete_card_${card.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var front by remember { mutableStateOf("") }
        var reading by remember { mutableStateOf("") }
        var back by remember { mutableStateOf("") }
        var exampleSentence by remember { mutableStateOf("") }
        var exampleMeaning by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Flashcard အသစ်ထည့်သွင်းရန်", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = front,
                        onValueChange = { front = it },
                        label = { Text("ဂျပန်စာလုံး / Kanji (Front) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_card_front")
                    )
                    OutlinedTextField(
                        value = reading,
                        onValueChange = { reading = it },
                        label = { Text("အသံထွက် / Furigana") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_card_reading")
                    )
                    OutlinedTextField(
                        value = back,
                        onValueChange = { back = it },
                        label = { Text("မြန်မာအဓိပ္ပာယ် (Back) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_card_back")
                    )
                    OutlinedTextField(
                        value = exampleSentence,
                        onValueChange = { exampleSentence = it },
                        label = { Text("နမူနာဝါကျ (Example Sentence)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_card_ex")
                    )
                    OutlinedTextField(
                        value = exampleMeaning,
                        onValueChange = { exampleMeaning = it },
                        label = { Text("ဝါကျအဓိပ္ပာယ် (Sentence Meaning)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_card_ex_meaning")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (front.isNotBlank() && back.isNotBlank()) {
                            val newCard = CustomFlashcard(
                                id = "card_${System.currentTimeMillis()}",
                                userId = currentUser?.uid ?: "local_user",
                                front = front.trim(),
                                reading = reading.trim(),
                                back = back.trim(),
                                exampleSentence = exampleSentence.trim(),
                                exampleMeaning = exampleMeaning.trim(),
                                masteryLevel = 0L
                            )
                            onAddCard(newCard)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_custom_card_btn"),
                    enabled = front.isNotBlank() && back.isNotBlank()
                ) {
                    Text("သိမ်းဆည်းမည် (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("မလုပ်တော့ပါ (Cancel)")
                }
            }
        )
    }
}
