package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.FlashcardStudyMode
import com.example.ui.viewmodel.VocabViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardDeckSettingsSheet(
    vocabViewModel: VocabViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isQuickReviewMode by vocabViewModel.isQuickReviewMode.collectAsState()
    val showFurigana by vocabViewModel.showFurigana.collectAsState()
    val studyMode by vocabViewModel.studyMode.collectAsState()
    val fontScale by vocabViewModel.flashcardFontScale.collectAsState()
    val voiceSettings by vocabViewModel.voiceSettings.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Deck Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ဖလက်ရှ်ကတ် ဆက်တင်များနှင့် အမြန်ပြန်နွှေးမုဒ်",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // ⚡ Quick Review Mode Card (Hero Setting)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isQuickReviewMode) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    }
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_review_setting_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (isQuickReviewMode) Color(0xFFFF9800) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bolt,
                                    contentDescription = null,
                                    tint = if (isQuickReviewMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Quick Review Mode",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isQuickReviewMode) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "⚡ 3s Flip",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isQuickReviewMode) Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "၃ စက္ကန့်အတွင်း အလိုအလျောက် အဖြေလှန်ပေးသော မုဒ်",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isQuickReviewMode,
                            onCheckedChange = { vocabViewModel.toggleQuickReviewMode(it) },
                            modifier = Modifier.testTag("quick_review_toggle")
                        )
                    }

                    Text(
                        text = "Fast-paced rapid recall: automatically flips the card after 3 seconds so you can test instant recognition before seeing the meaning and reading.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AnimatedVisibility(visible = isQuickReviewMode) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Ready: Cards will auto-flip 3 seconds after appearing.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Study Mode / Direction Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Study Orientation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        FlashcardStudyMode.JP_TO_MY to "JP ➔ MM",
                        FlashcardStudyMode.MY_TO_JP to "MM ➔ JP",
                        FlashcardStudyMode.AUDIO_FIRST to "Listening"
                    ).forEach { (mode, label) ->
                        val isSelected = studyMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { vocabViewModel.setStudyMode(mode) },
                            label = { Text(label, fontSize = 12.sp) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Furigana Ruby Text Setting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { vocabViewModel.toggleFurigana() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (showFurigana) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Show Furigana (ふりがな)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "ခန်ဂျီအပေါ်တွင် ဖတ်နည်း ဖူရိဂါန အက္ခရာများ ပြသပါ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = showFurigana,
                    onCheckedChange = { vocabViewModel.toggleFurigana() }
                )
            }

            // Audio on Flip Setting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { vocabViewModel.setVoiceAutoPlayFlip(!voiceSettings.autoPlayAudioOnFlip) }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Pronounce Audio on Flip",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "ကတ်လှန်သည့်အခါ အသံထွက် အလိုအလျောက် ဖွင့်မည်",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = voiceSettings.autoPlayAudioOnFlip,
                    onCheckedChange = { vocabViewModel.setVoiceAutoPlayFlip(it) }
                )
            }

            // Card Shuffle Options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Shuffle Deck Order",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { vocabViewModel.shuffleStudyDeck(FlashcardShuffleMode.SMART_SRS) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("⚡ Smart SRS", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { vocabViewModel.shuffleStudyDeck(FlashcardShuffleMode.TRUE_RANDOM) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🎲 Random", fontSize = 12.sp)
                    }
                }
            }

            // Font Scale Setting
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Card Font Scale",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(fontScale * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.9f to "90%", 1.0f to "100%", 1.15f to "115%", 1.3f to "130%").forEach { (scale, label) ->
                        val isSelected = fontScale == scale
                        FilterChip(
                            selected = isSelected,
                            onClick = { vocabViewModel.setFlashcardFontScale(scale) },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Close button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Apply & Resume Study")
            }
        }
    }
}
