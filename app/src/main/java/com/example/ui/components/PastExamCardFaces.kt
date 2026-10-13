package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VocabCard
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.ReviewBlue
import com.example.ui.theme.WeakOrange
import kotlin.math.abs

/**
 * Modern Clean Card Front Face with Dynamic Color Accents & Responsive Drag Border Glow
 */
@Composable
fun PastExamCardFront(
    card: VocabCard,
    isFrontJapanese: Boolean,
    onSpeak: (String) -> Unit,
    onToggleBookmark: () -> Unit = {},
    dragOffsetX: Float = 0f,
    dragOffsetY: Float = 0f,
    modifier: Modifier = Modifier
) {
    // Dynamic Accent Color calculation based on drag or card mastery
    val dynamicBorderColor by animateColorAsState(
        targetValue = when {
            dragOffsetX > 60f -> MasteredGreen.copy(alpha = 0.85f)
            dragOffsetX < -60f -> WeakOrange.copy(alpha = 0.85f)
            abs(dragOffsetY) > 60f -> ReviewBlue.copy(alpha = 0.85f)
            card.masteryLevel >= 3 -> MasteredGreen.copy(alpha = 0.5f)
            card.masteryLevel == 2 -> ReviewBlue.copy(alpha = 0.45f)
            card.masteryLevel == 1 -> WeakOrange.copy(alpha = 0.45f)
            else -> JapaneseCrimson.copy(alpha = 0.35f)
        },
        animationSpec = tween(150),
        label = "front_dynamic_border"
    )

    val surfaceGradientBrush = Brush.verticalGradient(
        colors = listOf(
            dynamicBorderColor.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.surface,
            dynamicBorderColor.copy(alpha = 0.04f)
        )
    )

    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(2.dp, dynamicBorderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceGradientBrush)
                .padding(20.dp)
        ) {
            // Top Bar: Session Tag + Mastery Pill + Bookmark + Audio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Session pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = card.personalNote.ifBlank { "JLPT N3 Past Exam" },
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Mastery status pill
                    val (masteryLabel, masteryColor) = when (card.masteryLevel) {
                        3 -> "ကျွမ်းကျင်" to MasteredGreen
                        2 -> "ပြန်လေ့လာ" to ReviewBlue
                        1 -> "လေ့လာဆဲ" to WeakOrange
                        else -> "အသစ်" to MaterialTheme.colorScheme.outline
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = masteryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = masteryLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = masteryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (card.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (card.isBookmarked) JapaneseCrimson else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(
                            onClick = { onSpeak(card.reading.ifBlank { card.kanji }) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Speak Pronunciation",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Center Primary Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isFrontJapanese) {
                    Text(
                        text = card.kanji,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = JapaneseFontFamily,
                            fontSize = if (card.kanji.length <= 4) 42.sp else 32.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    if (card.reading.isNotBlank() && card.reading != card.kanji) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = "〔 ${card.reading} 〕",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = JapaneseFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (card.partOfSpeech.isNotBlank()) {
                        Text(
                            text = card.partOfSpeech,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    Text(
                        text = card.meaningBurmese,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bottom Visual Indicator & Hint
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: #${card.id}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )

                Text(
                    text = "👆 တို့ထိ၍ အဖြေလှန်ပါ • Tap to Flip",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (card.timesCorrect > 0) MasteredGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "${card.timesCorrect}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

/**
 * Modern Clean Card Back Face with Dynamic Color Accents & Past Exam Example Context
 */
@Composable
fun PastExamCardBack(
    card: VocabCard,
    isFrontJapanese: Boolean,
    onSpeak: (String) -> Unit,
    onToggleBookmark: () -> Unit,
    dragOffsetX: Float = 0f,
    dragOffsetY: Float = 0f,
    modifier: Modifier = Modifier
) {
    val dynamicBorderColor by animateColorAsState(
        targetValue = when {
            dragOffsetX > 60f -> MasteredGreen.copy(alpha = 0.85f)
            dragOffsetX < -60f -> WeakOrange.copy(alpha = 0.85f)
            abs(dragOffsetY) > 60f -> ReviewBlue.copy(alpha = 0.85f)
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        },
        animationSpec = tween(150),
        label = "back_dynamic_border"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { rotationY = 180f },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(2.dp, dynamicBorderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.surface,
                            dynamicBorderColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Top Bar: Answer Tag + Bookmark + Audio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "အဖြေနှင့် အဓိပ္ပာယ် • Answer",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (card.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (card.isBookmarked) JapaneseCrimson else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(
                            onClick = { onSpeak(card.reading.ifBlank { card.kanji }) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Pronounce",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Center Content: Meaning + Reading + Example Context
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isFrontJapanese) {
                    Text(
                        text = card.meaningBurmese,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            lineHeight = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(
                        modifier = Modifier.width(60.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )

                    Text(
                        text = "読み方: ${card.reading}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JapaneseFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Real Exam Sample Context / Sentence if present
                    if (card.exampleSentence.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "📝 စာမေးပွဲ မေးခွန်း ပုံစံ:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = card.exampleSentence,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = JapaneseFontFamily,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (card.exampleMeaningBurmese.isNotBlank()) {
                                    Text(
                                        text = card.exampleMeaningBurmese,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = card.kanji,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = JapaneseFontFamily,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "読み方: ${card.reading}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JapaneseFontFamily,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Recall Stats Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MasteredGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "✓ မှန်: ${card.timesCorrect}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = MasteredGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WeakOrange.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "✗ မှား: ${card.timesIncorrect}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = WeakOrange,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Bottom Hint
            Text(
                text = "👆 တို့ထိ၍ ရှေ့မျက်နှာသို့ ပြန်လှန်ပါ • Tap to Flip Back",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
