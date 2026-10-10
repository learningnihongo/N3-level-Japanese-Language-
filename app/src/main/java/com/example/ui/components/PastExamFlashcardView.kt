package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VocabCard
import com.example.data.srs.ReviewRating
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.MasteredGreen
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.TouchApp
import kotlin.math.abs
import kotlin.math.roundToInt

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
    var gestureStyle by remember { mutableStateOf(FlashcardGestureStyle.SWIPE_REVIEW) }

    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

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

    LaunchedEffect(selectedSessionCode, activeDeck) {
        workingDeck = activeDeck
        currentIndex = 0
        isFlipped = false
        dragOffsetX = 0f
        dragOffsetY = 0f
    }

    val currentCard = workingDeck.getOrNull(currentIndex) ?: allPastExamCards.firstOrNull()

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 360),
        label = "past_exam_flip_rotation"
    )

    fun nextCard() {
        if (currentIndex < workingDeck.size - 1) {
            isFlipped = false
            dragOffsetX = 0f
            dragOffsetY = 0f
            currentIndex++
        }
    }

    fun prevCard() {
        if (currentIndex > 0) {
            isFlipped = false
            dragOffsetX = 0f
            dragOffsetY = 0f
            currentIndex--
        }
    }

    fun shuffleDeck() {
        workingDeck = workingDeck.shuffled()
        currentIndex = 0
        isFlipped = false
        dragOffsetX = 0f
        dragOffsetY = 0f
    }

    fun resetDeck() {
        workingDeck = activeDeck
        currentIndex = 0
        isFlipped = false
        dragOffsetX = 0f
        dragOffsetY = 0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Gesture Style Selector (Swipe Review, Slide Carousel, Tap Flip)
        PastExamGestureSelector(
            currentStyle = gestureStyle,
            onStyleChange = { gestureStyle = it }
        )

        // 2. Exam Session Filter Chips Row
        PastExamSessionSelector(
            allCardsCount = allPastExamCards.size,
            workingDeckCount = workingDeck.size,
            selectedSessionCode = selectedSessionCode,
            onSelectSession = { selectedSessionCode = it }
        )

        // 3. Progress & Flip Orientation Bar
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
                    text = if (isFlipped) "အဖြေကြည့်နေသည် (Answer Shown)" else "ရှေ့မျက်နှာ (Question Shown)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Quick 1-tap gesture style switcher
                OutlinedButton(
                    onClick = {
                        val nextIdx = (gestureStyle.ordinal + 1) % FlashcardGestureStyle.entries.size
                        gestureStyle = FlashcardGestureStyle.entries[nextIdx]
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = when (gestureStyle) {
                            FlashcardGestureStyle.SWIPE_REVIEW -> Icons.Default.Swipe
                            FlashcardGestureStyle.SLIDE_CAROUSEL -> Icons.Default.PanTool
                            FlashcardGestureStyle.VERTICAL_FLIP -> Icons.Default.Flip
                            FlashcardGestureStyle.TAP_ONLY -> Icons.Default.TouchApp
                        },
                        contentDescription = "Cycle Gesture Style",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gestureStyle.burmeseLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

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

        // 4. Interactive Gesture Card Area
        if (currentCard != null) {
            val gestureModifier = when (gestureStyle) {
                FlashcardGestureStyle.SWIPE_REVIEW -> {
                    Modifier.pointerInput(currentIndex) {
                        detectDragGestures(
                            onDragEnd = {
                                if (dragOffsetX > 100f) {
                                    onRateCard(currentCard, ReviewRating.GOOD)
                                    nextCard()
                                } else if (dragOffsetX < -100f) {
                                    onRateCard(currentCard, ReviewRating.AGAIN)
                                    nextCard()
                                } else if (abs(dragOffsetY) > 80f) {
                                    isFlipped = !isFlipped
                                }
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDragCancel = {
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                                dragOffsetY += dragAmount.y
                            }
                        )
                    }
                }
                FlashcardGestureStyle.SLIDE_CAROUSEL -> {
                    Modifier.pointerInput(currentIndex) {
                        detectDragGestures(
                            onDragEnd = {
                                if (dragOffsetX > 75f) {
                                    prevCard()
                                } else if (dragOffsetX < -75f) {
                                    nextCard()
                                } else if (abs(dragOffsetY) > 75f) {
                                    isFlipped = !isFlipped
                                }
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDragCancel = {
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                                dragOffsetY += dragAmount.y
                            }
                        )
                    }
                }
                FlashcardGestureStyle.VERTICAL_FLIP -> {
                    Modifier.pointerInput(currentIndex) {
                        detectDragGestures(
                            onDragEnd = {
                                if (abs(dragOffsetY) > 60f) {
                                    isFlipped = !isFlipped
                                } else if (dragOffsetX > 85f) {
                                    prevCard()
                                } else if (dragOffsetX < -85f) {
                                    nextCard()
                                }
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDragCancel = {
                                dragOffsetX = 0f
                                dragOffsetY = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                                dragOffsetY += dragAmount.y
                            }
                        )
                    }
                }
                FlashcardGestureStyle.TAP_ONLY -> Modifier
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .offset {
                        when (gestureStyle) {
                            FlashcardGestureStyle.SWIPE_REVIEW -> IntOffset(dragOffsetX.roundToInt(), (dragOffsetY * 0.25f).roundToInt())
                            FlashcardGestureStyle.SLIDE_CAROUSEL -> IntOffset(dragOffsetX.roundToInt(), 0)
                            FlashcardGestureStyle.VERTICAL_FLIP -> IntOffset((dragOffsetX * 0.25f).roundToInt(), (dragOffsetY * 0.35f).roundToInt())
                            FlashcardGestureStyle.TAP_ONLY -> IntOffset.Zero
                        }
                    }
                    .rotate(
                        when (gestureStyle) {
                            FlashcardGestureStyle.SWIPE_REVIEW -> dragOffsetX * 0.04f
                            FlashcardGestureStyle.SLIDE_CAROUSEL -> dragOffsetX * 0.015f
                            else -> 0f
                        }
                    )
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 14f * density
                    }
                    .then(gestureModifier)
                    .clickable { isFlipped = !isFlipped }
                    .testTag("past_exam_gesture_card"),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    PastExamCardFront(
                        card = currentCard,
                        isFrontJapanese = isFrontJapanese,
                        onSpeak = onSpeak
                    )
                } else {
                    PastExamCardBack(
                        card = currentCard,
                        isFrontJapanese = isFrontJapanese,
                        onSpeak = onSpeak,
                        onToggleBookmark = { onToggleBookmark(currentCard) }
                    )
                }

                // Dynamic Swipe Badges (Visible during Swipe Review gesture)
                if (gestureStyle == FlashcardGestureStyle.SWIPE_REVIEW && abs(dragOffsetX) > 35f) {
                    if (dragOffsetX > 35f) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MasteredGreen.copy(alpha = 0.92f),
                            border = BorderStroke(2.dp, Color.White),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                Text(
                                    text = "GOOD • မှတ်မိသည်",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = JapaneseCrimson.copy(alpha = 0.92f),
                            border = BorderStroke(2.dp, Color.White),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                                Text(
                                    text = "AGAIN • ပြန်လေ့လာ",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Dynamic Visual Feedback for Slide Carousel
                if (gestureStyle == FlashcardGestureStyle.SLIDE_CAROUSEL && abs(dragOffsetX) > 35f) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(if (dragOffsetX > 0) Alignment.CenterStart else Alignment.CenterEnd)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (dragOffsetX > 0) "← ရှေ့ကတ် (Prev)" else "နောက်ကတ် (Next) →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                // Dynamic Visual Feedback for Vertical Flip
                if (gestureStyle == FlashcardGestureStyle.VERTICAL_FLIP && abs(dragOffsetY) > 35f) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "↕️ အဖြေလှန်ရန် လွှတ်ပါ (Flip)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 5. SRS Rating & Navigation Controls
            PastExamBottomControls(
                currentCard = currentCard,
                currentIndex = currentIndex,
                totalCount = workingDeck.size,
                isFlipped = isFlipped,
                onRateCard = onRateCard,
                onFlip = { isFlipped = !isFlipped },
                onPrev = { prevCard() },
                onNext = { nextCard() },
                onShuffle = { shuffleDeck() },
                onReset = { resetDeck() }
            )
        }
    }
}
