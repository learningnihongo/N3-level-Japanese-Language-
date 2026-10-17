package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.Badge
import com.example.ui.components.AppThemeSettingsCard
import com.example.ui.components.BadgeDetailDialog
import com.example.ui.components.DailyReminderSettingsCard
import com.example.ui.components.QuizStreakDetailsDialog
import com.example.ui.components.QuizStreakHeaderBadge
import com.example.ui.components.TrophyShowcaseCard
import com.example.ui.components.VoiceSettingsCard
import com.example.ui.components.WeeklyMasteryProgressChartCard
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseIndigo
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.StreakOrange
import com.example.ui.viewmodel.VocabViewModel

private val PRESET_AVATARS = listOf(
    Pair("🧑‍🏫", "Sensei"),
    Pair("🌸", "Sakura"),
    Pair("⚔️", "Samurai"),
    Pair("🥷", "Shinobi"),
    Pair("🦊", "Kitsune"),
    Pair("👺", "Tengu"),
    Pair("⛩️", "Torii"),
    Pair("🗻", "Fuji")
)

@Composable
fun ProfileScreen(
    vocabViewModel: VocabViewModel,
    onNavigateToCustomDecks: () -> Unit = {},
    onNavigateToQuiz: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by vocabViewModel.userProfile.collectAsState()
    val totalCards by vocabViewModel.totalCardCount.collectAsState()
    val masteredCards by vocabViewModel.masteredCount.collectAsState()
    val dueCards by vocabViewModel.dueCount.collectAsState()
    val badges by vocabViewModel.allBadges.collectAsState()
    val reminderSettings by vocabViewModel.reminderSettings.collectAsState()
    val themeSettings by vocabViewModel.themeSettings.collectAsState()
    val voiceSettings by vocabViewModel.voiceSettings.collectAsState()
    val isSpeaking by vocabViewModel.isSpeaking.collectAsState()
    val quizStreak by vocabViewModel.quizStreakInfo.collectAsState()
    val recentQuizzes by vocabViewModel.recentQuizHistories.collectAsState()
    val customCards by vocabViewModel.cloudCustomCards.collectAsState()
    val weeklySummary by vocabViewModel.weeklyMasteryVsReviewedStats.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showStreakDetailsDialog by remember { mutableStateOf(false) }
    var selectedBadgeForDetail by remember { mutableStateOf<Badge?>(null) }
    var selectedSegmentIndex by remember { mutableIntStateOf(0) }

    val segments = listOf(
        Pair("Settings & Flashcards", Icons.Default.Tune),
        Pair("Quiz Results (${recentQuizzes.size})", Icons.Default.Quiz),
        Pair("Badges & Trophies", Icons.Default.EmojiEvents)
    )

    val currentUser by vocabViewModel.currentUser.collectAsState()
    val firestoreProfile by vocabViewModel.firestoreUserProfile.collectAsState()
    var isSigningIn by remember { mutableStateOf(false) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp, top = 16.dp, start = 18.dp, end = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 0. Cloud Account & Authentication Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_auth_cloud_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentUser != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (currentUser != null) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (currentUser != null) MasteredGreen.copy(alpha = 0.15f) else JapaneseCrimson.copy(alpha = 0.12f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (currentUser != null) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                                        contentDescription = "Cloud Status",
                                        tint = if (currentUser != null) MasteredGreen else JapaneseCrimson,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = if (currentUser != null) "Cloud Account Connected" else "Cloud Account (Google Sign-In)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (currentUser != null) (currentUser?.email ?: "Synced with Firestore")
                                    else "Sign in to save study progress, quizzes & custom decks",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (currentUser != null) {
                            IconButton(
                                onClick = { vocabViewModel.signOut() },
                                modifier = Modifier.testTag("profile_sign_out_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Sign Out",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    if (currentUser == null) {
                        Button(
                            onClick = {
                                isSigningIn = true
                                authErrorMessage = null
                                coroutineScope.launch {
                                    val res = vocabViewModel.signInWithGoogle()
                                    isSigningIn = false
                                    if (res.isFailure) {
                                        authErrorMessage = res.exceptionOrNull()?.localizedMessage ?: "Sign-in cancelled"
                                    }
                                }
                            },
                            enabled = !isSigningIn,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JapaneseCrimson,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("profile_google_signin_btn")
                        ) {
                            if (isSigningIn) {
                                Text("Connecting to Google...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign in with Google • အကောင့်ဝင်ရန်", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        authErrorMessage?.let { err ->
                            Text(
                                text = "Notice: $err",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓ Real-time Sync Active",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MasteredGreen
                                )
                                Text(
                                    text = "Firestore Cloud",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. Zen Unified Profile & XP Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar + Profile Info + Quick Edit Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // User Avatar (Clickable to change)
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .clickable { showEditProfileDialog = true }
                            ) {
                                if (!profile?.customAvatarUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = profile?.customAvatarUri,
                                        contentDescription = "Profile Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(2.dp, JapaneseCrimson, CircleShape)
                                    )
                                } else {
                                    val avatarPreset = PRESET_AVATARS.getOrNull(profile?.avatarIndex ?: 0)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(
                                                        JapaneseCrimson.copy(alpha = 0.2f),
                                                        JapaneseIndigo.copy(alpha = 0.12f)
                                                    )
                                                )
                                            )
                                            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (avatarPreset != null) {
                                            Text(
                                                text = avatarPreset.first,
                                                fontSize = 28.sp
                                            )
                                        } else {
                                            Text(
                                                text = profile?.name?.take(2)?.uppercase() ?: "JL",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                        }
                                    }
                                }

                                // Subtle Camera Badge
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Change Photo",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }

                            // Profile Name and Goal Subtitle
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = profile?.name ?: "Sensei Student",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                    ) {
                                        Text(
                                            text = "JLPT ${profile?.targetJlptLevel ?: "N3"}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = "•",
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                        fontSize = 11.sp
                                    )

                                    Text(
                                        text = "${profile?.dailyGoal ?: 15} words/day",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Compact Edit Profile Button
                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Minimalist Level & XP Progress (No chunky nested box)
                    val xp = profile?.totalXp ?: 0
                    val currentLevel = profile?.level ?: 1
                    val xpInLevel = xp % 150
                    val currentLevelProgress = (xpInLevel).toFloat() / 150f

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⭐ Level $currentLevel · ${profile?.levelTitle ?: "Novice Learner"}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "$xpInLevel / 150 XP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        LinearProgressIndicator(
                            progress = { currentLevelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(50)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Prominent Quiz Streak Badge in Header
                    QuizStreakHeaderBadge(
                        streakInfo = quizStreak,
                        onClick = { showStreakDetailsDialog = true }
                    )
                }
            }
        }

        // 2. Sleek 4-Metric Ribbon Strip (Replaces the bulky 2x2 colored cards)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_stat_ribbon"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricRibbonItem(
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = if (quizStreak.currentStreak > 0) StreakOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                        value = "${quizStreak.currentStreak}d",
                        label = "Quiz Streak",
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    )

                    MetricRibbonItem(
                        icon = Icons.Default.CheckCircle,
                        iconTint = MasteredGreen,
                        value = "$masteredCards",
                        label = "Mastered",
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    )

                    MetricRibbonItem(
                        icon = Icons.Default.Psychology,
                        iconTint = MaterialTheme.colorScheme.primary,
                        value = "$dueCards",
                        label = "Due Review",
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    )

                    MetricRibbonItem(
                        icon = Icons.Default.School,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        value = "$totalCards",
                        label = "Total Words",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Clean 2-Segment Switcher (Settings & Audio vs Badges & Progress)
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    segments.forEachIndexed { index, (label, icon) ->
                        val isSelected = selectedSegmentIndex == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)) else null,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedSegmentIndex = index }
                                .testTag(if (index == 0) "tab_settings" else "tab_achievements")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 9.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Clean Segment Content
        item {
            AnimatedContent(
                targetState = selectedSegmentIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "profile_segment_content"
            ) { segmentIndex ->
                when (segmentIndex) {
                    0 -> {
                        // SEGMENT 0: Settings & Flashcards
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // 1. Custom Decks & Flashcards Shortcut Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_custom_decks_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.35f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(JapaneseCrimson.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Style,
                                            contentDescription = null,
                                            tint = JapaneseCrimson,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Custom Flashcard Decks",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${customCards.size} cards • Library & Custom Words",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = onNavigateToCustomDecks,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Open Decks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // 2. Personalized Flashcard Settings Card
                            PersonalizedFlashcardSettingsCard(
                                firestoreProfile = firestoreProfile,
                                onUpdateSettings = { frontLang, tts, gesture, shuffle ->
                                    vocabViewModel.updateProfileSettings(
                                        displayName = profile?.name ?: "Learner",
                                        targetJlptLevel = profile?.targetJlptLevel ?: "N3",
                                        dailyGoal = (profile?.dailyGoal ?: 15).toLong(),
                                        frontLang = frontLang,
                                        autoPlayTts = tts,
                                        gestureStyle = gesture,
                                        shuffleMode = shuffle
                                    )
                                }
                            )

                            // Reminder Settings
                            DailyReminderSettingsCard(
                                settings = reminderSettings,
                                onToggleEnabled = { enabled ->
                                    vocabViewModel.setReminderEnabled(enabled)
                                },
                                onUpdateTime = { hour, minute ->
                                    vocabViewModel.setReminderTime(hour, minute)
                                },
                                onToggleSmartReminder = { smart ->
                                    vocabViewModel.setSmartReminderEnabled(smart)
                                },
                                onTestReminder = {
                                    vocabViewModel.triggerTestReminder()
                                }
                            )

                            // Japanese Voice & Audio
                            VoiceSettingsCard(
                                settings = voiceSettings,
                                isSpeaking = isSpeaking,
                                onUpdateSpeed = { speed ->
                                    vocabViewModel.setVoiceSpeechRate(speed)
                                },
                                onUpdatePitch = { pitch ->
                                    vocabViewModel.setVoicePitch(pitch)
                                },
                                onTogglePhonetic = { phonetic ->
                                    vocabViewModel.setVoicePreferPhonetic(phonetic)
                                },
                                onToggleAutoPlayFlip = { autoFlip ->
                                    vocabViewModel.setVoiceAutoPlayFlip(autoFlip)
                                },
                                onTestVoice = {
                                    vocabViewModel.testVoicePronunciation()
                                }
                            )

                            // Theme & Visual Appearance
                            AppThemeSettingsCard(
                                themeSettings = themeSettings,
                                onSelectMode = { mode ->
                                    vocabViewModel.setThemeMode(mode)
                                },
                                onSelectPalette = { palette ->
                                    vocabViewModel.setThemePalette(palette)
                                },
                                onSelectIconStyle = { style ->
                                    vocabViewModel.setIconThemeStyle(style)
                                },
                                onToggleOledBlack = { oled ->
                                    vocabViewModel.setOledBlack(oled)
                                },
                                onSelectAesthetic = { aesthetic ->
                                    vocabViewModel.setThemeAesthetic(aesthetic)
                                }
                            )

                        }
                    }

                    1 -> {
                        // SEGMENT 1: Quiz Results & History
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quiz Results & History (${recentQuizzes.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Button(
                                    onClick = onNavigateToQuiz,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("New Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Interactive Weekly Quiz Performance & Progress Trends Line Graph (Recharts / D3)
                            WeeklyMasteryProgressChartCard(
                                weeklySummary = weeklySummary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("profile_weekly_performance_chart")
                            )

                            if (recentQuizzes.isEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Quiz,
                                            contentDescription = null,
                                            modifier = Modifier.size(44.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        )
                                        Text(
                                            text = "Quiz ဖြေဆိုထားသော မှတ်တမ်း မရှိသေးပါ",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Quiz စတင်ဖြေဆိုပြီး အမှတ်နှင့် XP များကို Profile တွင် သိမ်းဆည်းပါ",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                for (history in recentQuizzes.take(15)) {
                                    val accuracyPct = if (history.totalQuestions > 0) {
                                        ((history.score.toFloat() / history.totalQuestions) * 100).toInt()
                                    } else 0

                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (accuracyPct >= 80) MasteredGreen.copy(alpha = 0.15f)
                                                        else JapaneseIndigo.copy(alpha = 0.15f)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$accuracyPct%",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (accuracyPct >= 80) MasteredGreen else JapaneseIndigo
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = history.quizType,
                                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                                    )
                                                    if (history.lessonFilter.isNotEmpty()) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "• ${history.lessonFilter}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "Score: ${history.score} / ${history.totalQuestions} questions",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = StreakOrange.copy(alpha = 0.12f)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.EmojiEvents,
                                                        contentDescription = null,
                                                        tint = StreakOrange,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = "+${history.xpEarned} XP",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = StreakOrange
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // SEGMENT 2: Achievements & Trophies Showcase
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            TrophyShowcaseCard(
                                badges = badges,
                                onBadgeClick = { badge ->
                                    selectedBadgeForDetail = badge
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Badge details dialog
    selectedBadgeForDetail?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadgeForDetail = null }
        )
    }

    // Quiz streak details dialog
    if (showStreakDetailsDialog) {
        QuizStreakDetailsDialog(
            streakInfo = quizStreak,
            onDismiss = { showStreakDetailsDialog = false }
        )
    }

    // Clean Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileWithAvatarDialog(
            currentName = profile?.name ?: "Sensei Student",
            currentGoal = profile?.dailyGoal ?: 15,
            currentLevel = profile?.targetJlptLevel ?: "N3",
            currentAvatarIndex = profile?.avatarIndex ?: 0,
            currentCustomAvatarUri = profile?.customAvatarUri,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, goal, level, avatarIndex, customUri ->
                vocabViewModel.updateProfile(
                    name = name,
                    dailyGoal = goal,
                    targetJlpt = level,
                    avatarIndex = avatarIndex,
                    customAvatarUri = customUri
                )
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun MetricRibbonItem(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EditProfileWithAvatarDialog(
    currentName: String,
    currentGoal: Int,
    currentLevel: String,
    currentAvatarIndex: Int,
    currentCustomAvatarUri: String?,
    onDismiss: () -> Unit,
    onSave: (String, Int, String, Int, String?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    var level by remember { mutableStateOf(currentLevel) }
    var selectedAvatarIndex by remember { mutableIntStateOf(currentAvatarIndex) }
    var customAvatarUri by remember { mutableStateOf(currentCustomAvatarUri) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customAvatarUri = uri.toString()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("edit_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Edit Student Profile",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Avatar Preview & Gallery Photo Picker Controls
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        if (!customAvatarUri.isNullOrBlank()) {
                            AsyncImage(
                                model = customAvatarUri,
                                contentDescription = "Chosen Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        } else {
                            val preset = PRESET_AVATARS.getOrNull(selectedAvatarIndex)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = preset?.first ?: "🧑‍🏫",
                                    fontSize = 32.sp
                                )
                            }
                        }

                        // Overlay camera icon
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Pick Image",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // Actions for photo: Choose photo or Remove photo
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Choose Photo", fontSize = 12.sp)
                        }

                        if (!customAvatarUri.isNullOrBlank()) {
                            IconButton(
                                onClick = { customAvatarUri = null },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Photo",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }

                // Preset Avatars Selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Or Select Preset Avatar:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        itemsIndexed(PRESET_AVATARS) { index, preset ->
                            val isSelected = selectedAvatarIndex == index && customAvatarUri.isNullOrBlank()
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedAvatarIndex = index
                                        customAvatarUri = null
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = preset.first, fontSize = 16.sp)
                                    Text(
                                        text = preset.second,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    label = { Text("Daily Review Goal (Cards)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Target JLPT Level Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Target JLPT Level:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("N5", "N4", "N3", "N2", "N1").forEach { jlpt ->
                            FilterChip(
                                selected = level == jlpt,
                                onClick = { level = jlpt },
                                label = { Text(jlpt, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val goal = goalText.toIntOrNull() ?: 15
                            onSave(name.trim(), goal, level.trim(), selectedAvatarIndex, customAvatarUri)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalizedFlashcardSettingsCard(
    firestoreProfile: com.example.data.model.FirestoreUserProfile?,
    onUpdateSettings: (frontLang: String, tts: Boolean, gesture: String, shuffle: String) -> Unit
) {
    var frontLang by remember(firestoreProfile) {
        mutableStateOf(firestoreProfile?.flashcardFrontLang ?: "Japanese")
    }
    var autoPlayTts by remember(firestoreProfile) {
        mutableStateOf(firestoreProfile?.autoPlayTts ?: false)
    }
    var gestureStyle by remember(firestoreProfile) {
        mutableStateOf(firestoreProfile?.gestureStyle ?: "SWIPE_REVIEW")
    }
    var shuffleMode by remember(firestoreProfile) {
        mutableStateOf(firestoreProfile?.shuffleMode ?: "DEFAULT")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("personalized_flashcard_settings_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = JapaneseCrimson,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Personalized Flashcard Settings",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "စိတ်ကြိုက် Flashcard ပြသမှုနှင့် အလေ့အထများ",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // 1. Front Card Language
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Front Card Language (ကတ်မျက်နှာပြင်):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = frontLang == "Japanese",
                        onClick = {
                            frontLang = "Japanese"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("Japanese First (日本語)", fontSize = 12.sp) },
                        leadingIcon = if (frontLang == "Japanese") {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                    FilterChip(
                        selected = frontLang == "Burmese",
                        onClick = {
                            frontLang = "Burmese"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("Burmese First (မြန်မာ)", fontSize = 12.sp) },
                        leadingIcon = if (frontLang == "Burmese") {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }

            // 2. Audio TTS Auto-Play on Flip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-play Japanese Audio",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ကတ်လှန်ချိန်တွင် ဂျပန်အသံထွက်ကို အလိုအလျောက် ဖွင့်ပေးမည်",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoPlayTts,
                    onCheckedChange = { checked ->
                        autoPlayTts = checked
                        onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                    }
                )
            }

            // 3. Gesture Interaction Style
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Flashcard Gesture Style (ကတ်ထိန်းချုပ်မှု ပုံစံ):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = gestureStyle == "SWIPE_REVIEW",
                        onClick = {
                            gestureStyle = "SWIPE_REVIEW"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("Swipe Review", fontSize = 11.5.sp) }
                    )
                    FilterChip(
                        selected = gestureStyle == "TAP_ONLY",
                        onClick = {
                            gestureStyle = "TAP_ONLY"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("Tap Flip", fontSize = 11.5.sp) }
                    )
                    FilterChip(
                        selected = gestureStyle == "VERTICAL_FLIP",
                        onClick = {
                            gestureStyle = "VERTICAL_FLIP"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("Vertical 3D", fontSize = 11.5.sp) }
                    )
                }
            }

            // 4. Default Shuffle Mode
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Default Card Order (ကတ်အစီအစဉ်):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = shuffleMode == "DEFAULT",
                        onClick = {
                            shuffleMode = "DEFAULT"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("Sequential", fontSize = 11.5.sp) }
                    )
                    FilterChip(
                        selected = shuffleMode == "RANDOM",
                        onClick = {
                            shuffleMode = "RANDOM"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("True Random", fontSize = 11.5.sp) }
                    )
                    FilterChip(
                        selected = shuffleMode == "WEAK_FIRST",
                        onClick = {
                            shuffleMode = "WEAK_FIRST"
                            onUpdateSettings(frontLang, autoPlayTts, gestureStyle, shuffleMode)
                        },
                        label = { Text("Weak Words First", fontSize = 11.5.sp) }
                    )
                }
            }
        }
    }
}

