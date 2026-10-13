package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomFlashcard
import com.example.data.seed.SeedData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    customCards: List<CustomFlashcard>,
    onBackClick: () -> Unit,
    onReviewCompleted: (xp: Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val kotobaList = remember { SeedData.kotobaList }

    val totalCount = if (selectedTab == 0) kotobaList.size else customCards.size
    var currentIndex by remember(selectedTab) { mutableIntStateOf(0) }
    var isFlipped by remember(selectedTab, currentIndex) { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcard Study", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("flashcard_back_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("JLPT N3 (${kotobaList.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Custom Cards (${customCards.size})") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (totalCount == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ကိုယ်ပိုင် Flashcard မရှိသေးပါ။ Custom Cards menu တွင် ကတ်အသစ်ထည့်သွင်းနိုင်ပါသည်။",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "${currentIndex + 1} / $totalCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        }
                        .clickable { isFlipped = !isFlipped }
                        .testTag("interactive_flashcard"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation <= 90f) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val frontText = if (selectedTab == 0) {
                                    kotobaList[currentIndex].word
                                } else {
                                    customCards[currentIndex].front
                                }
                                Text(
                                    text = frontText,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                        RoundedCornerShape(12.dp)
                                    ).padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.RotateRight,
                                        contentDescription = "Flip",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "အဓိပ္ပာယ်ကြည့်ရန် နှိပ်ပါ (Tap to flip)",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.graphicsLayer { rotationY = 180f },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (selectedTab == 0) {
                                    val item = kotobaList[currentIndex]
                                    Text(
                                        text = item.reading,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = item.romaji,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = item.meaningBurmese,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = item.exampleJapanese,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = item.exampleBurmese,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                } else {
                                    val item = customCards[currentIndex]
                                    if (item.reading.isNotEmpty()) {
                                        Text(
                                            text = item.reading,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                    Text(
                                        text = item.back,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        textAlign = TextAlign.Center
                                    )
                                    if (item.exampleSentence.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(text = item.exampleSentence, fontSize = 14.sp)
                                                if (item.exampleMeaning.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(text = item.exampleMeaning, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "မှတ်မိနိုင်စွမ်းကို ရွေးပါ (Spaced Repetition Rating)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onReviewCompleted(1L)
                            if (currentIndex < totalCount - 1) currentIndex++ else currentIndex = 0
                            isFlipped = false
                        },
                        modifier = Modifier.weight(1f).testTag("srs_hard_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("ခက်ခဲ (+1 XP)", fontSize = 11.sp, maxLines = 1)
                    }

                    FilledTonalButton(
                        onClick = {
                            onReviewCompleted(3L)
                            if (currentIndex < totalCount - 1) currentIndex++ else currentIndex = 0
                            isFlipped = false
                        },
                        modifier = Modifier.weight(1f).testTag("srs_good_btn")
                    ) {
                        Text("သင့်တော် (+3 XP)", fontSize = 11.sp, maxLines = 1)
                    }

                    Button(
                        onClick = {
                            onReviewCompleted(5L)
                            if (currentIndex < totalCount - 1) currentIndex++ else currentIndex = 0
                            isFlipped = false
                        },
                        modifier = Modifier.weight(1f).testTag("srs_easy_btn")
                    ) {
                        Text("လွယ်ကူ (+5 XP)", fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}
