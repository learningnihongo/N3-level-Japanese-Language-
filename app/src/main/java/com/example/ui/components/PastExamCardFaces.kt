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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
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
import com.example.data.model.VocabCard
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseFontFamily
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.ReviewBlue
import com.example.ui.theme.WeakOrange
import com.example.ui.theme.WashiPaperCardLight
import com.example.ui.theme.WashiBorderLight
import com.example.ui.theme.VermilionRed
import kotlin.math.abs

/**
 * Premium Modern Japanese Flashcard Front Face
 * Designed with authentic stationery aesthetics, dynamic border glows, and crisp typography.
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
    // Dynamic border glow responding to swipe drag gestures or card mastery state
    val dynamicBorderColor by animateColorAsState(
        targetValue = when {
            dragOffsetX > 60f -> MasteredGreen.copy(alpha = 0.9f)
            dragOffsetX < -60f -> WeakOrange.copy(alpha = 0.9f)
            abs(dragOffsetY) > 60f -> ReviewBlue.copy(alpha = 0.85f)
            card.masteryLevel >= 3 -> MasteredGreen.copy(alpha = 0.55f)
            card.masteryLevel == 2 -> ReviewBlue.copy(alpha = 0.5f)
            card.masteryLevel == 1 -> WeakOrange.copy(alpha = 0.5f)
            else -> JapaneseCrimson.copy(alpha = 0.4f)
        },
        animationSpec = tween(150),
        label = "front_dynamic_border"
    )

    val surfaceGradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            WashiPaperCardLight.copy(alpha = 0.85f),
            dynamicBorderColor.copy(alpha = 0.04f)
        )
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .testTag("past_exam_card_front"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        border = BorderStroke(2.dp, dynamicBorderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceGradientBrush)
                .padding(22.dp)
        ) {
            // Top Header: Exam Tag + Mastery Badge + Bookmark & Speaker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exam Session Capsule
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = JapaneseCrimson.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.25f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = JapaneseCrimson,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = card.personalNote.ifBlank { "JLPT N3 Past Exam" },
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = FontWeight.Bold,
                                color = JapaneseCrimson,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Mastery Status Badge
                    val (masteryLabel, masteryColor) = when (card.masteryLevel) {
                        3 -> "ကျွမ်းကျင်" to MasteredGreen
                        2 -> "ပြန်လေ့လာ" to ReviewBlue
                        1 -> "လေ့လာဆဲ" to WeakOrange
                        else -> "အသစ်" to MaterialTheme.colorScheme.primary
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = masteryColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = masteryLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = masteryColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }
                }

                // Quick Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("past_exam_card_bookmark_btn")
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = { onSpeak(card.reading.ifBlank { card.kanji }) },
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("past_exam_card_speak_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Audio Pronunciation",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Center Content: Large Japanese Word / Meaning
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isFrontJapanese) {
                    Text(
                        text = card.kanji,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = JapaneseFontFamily,
                            fontSize = if (card.kanji.length <= 4) 44.sp else 34.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    if (card.reading.isNotBlank() && card.reading != card.kanji) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "〔 ${card.reading} 〕",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = JapaneseFontFamily,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (card.partOfSpeech.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = card.partOfSpeech,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }
                } else {
                    // Front is Burmese
                    Text(
                        text = card.meaningBurmese,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "ဂျပန်စာ စကားလုံးကို စဉ်းစားပါ",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Bottom Footer Info: ID, Star stats, and Interactive Flip hint
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "ID: #${card.id}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Text(
                        text = "👆 တို့ထိ၍ အဖြေလှန်ပါ • Tap to Flip",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (card.timesCorrect > 0) MasteredGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "${card.timesCorrect}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold,
                        color = if (card.timesCorrect > 0) MasteredGreen else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

/**
 * Premium Modern Japanese Flashcard Back Face
 * Shows exact meaning, full reading, exam question context quote, and recall stats.
 */
@Composable
fun PastExamCardBack(
    card: VocabCard,
    isFrontJapanese: Boolean,
    onSpeak: (String) -> Unit,
    onToggleBookmark: () -> Unit,
    dragOffsetX: Float = 0f,
    dragOffsetY: Float = 0f,
    flipVertically: Boolean = false,
    modifier: Modifier = Modifier
) {
    val dynamicBorderColor by animateColorAsState(
        targetValue = when {
            dragOffsetX > 60f -> MasteredGreen.copy(alpha = 0.9f)
            dragOffsetX < -60f -> WeakOrange.copy(alpha = 0.9f)
            abs(dragOffsetY) > 60f -> ReviewBlue.copy(alpha = 0.85f)
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        },
        animationSpec = tween(150),
        label = "back_dynamic_border"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                if (flipVertically) {
                    rotationX = 180f
                } else {
                    rotationY = 180f
                }
            }
            .testTag("past_exam_card_back"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        border = BorderStroke(2.dp, dynamicBorderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            WashiPaperCardLight.copy(alpha = 0.88f),
                            dynamicBorderColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            // Top Header: Answer Badge + Bookmark + Audio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "အဖြေနှင့် စာမေးပွဲ မေးခွန်း • Answer",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(38.dp)
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
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = { onSpeak(card.reading.ifBlank { card.kanji }) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Audio Pronounce",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Center Content: Meaning + Reading + Authentic Exam Context
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isFrontJapanese) {
                    Text(
                        text = card.meaningBurmese,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            lineHeight = 34.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(
                        modifier = Modifier.width(64.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    )

                    Text(
                        text = "読み方: ${card.reading}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JapaneseFontFamily,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Real Exam Question Context Quote Banner
                    if (card.exampleSentence.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = JapaneseCrimson,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "📝 စာမေးပွဲ မေးခွန်း ပုံစံ Context:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = JapaneseCrimson
                                    )
                                }
                                Text(
                                    text = card.exampleSentence,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = JapaneseFontFamily,
                                        fontSize = 13.5.sp,
                                        lineHeight = 19.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (card.exampleMeaningBurmese.isNotBlank()) {
                                    Text(
                                        text = card.exampleMeaningBurmese,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
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
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "読み方: ${card.reading}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JapaneseFontFamily,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Recall Accuracy Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MasteredGreen.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "✓ မှန်: ${card.timesCorrect}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = MasteredGreen,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WeakOrange.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = "✗ မှား: ${card.timesIncorrect}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = FontWeight.Bold,
                            color = WeakOrange,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Bottom Footer Flip Back Hint
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "👆 တို့ထိ၍ ရှေ့မျက်နှာသို့ ပြန်လှန်ပါ • Tap to Flip Back",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }
        }
    }
}
