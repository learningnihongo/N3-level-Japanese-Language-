package com.example.ui.screens

import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.components.QuizStreakDetailsDialog
import com.example.ui.theme.JapaneseCrimson
import com.example.ui.theme.JapaneseIndigo
import com.example.ui.theme.MasteredGreen
import com.example.ui.theme.StreakOrange
import com.example.ui.theme.ThemeMode
import com.example.ui.viewmodel.VocabViewModel
import kotlinx.coroutines.launch

private val PRESET_AVATARS = listOf(
    Pair("🧑‍🏫", "Sensei"),
    Pair("🌸", "Sakura"),
    Pair("⚔️", "Samurai"),
    Pair("🦊", "Kitsune"),
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
    val reminderSettings by vocabViewModel.reminderSettings.collectAsState()
    val themeSettings by vocabViewModel.themeSettings.collectAsState()
    val voiceSettings by vocabViewModel.voiceSettings.collectAsState()
    val quizStreak by vocabViewModel.quizStreakInfo.collectAsState()
    val customCards by vocabViewModel.cloudCustomCards.collectAsState()
    val currentUser by vocabViewModel.currentUser.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showStreakDetailsDialog by remember { mutableStateOf(false) }
    var isSigningIn by remember { mutableStateOf(false) }
    var authNoticeMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --------------------------------------------------------------------
        // 1. Sleek, Clean Profile Header Card
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar + Name + Edit Action Button
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
                            // Circular Avatar
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .clickable { showEditProfileDialog = true }
                            ) {
                                if (currentUser?.photoUrl != null && profile.customAvatarUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = currentUser?.photoUrl,
                                        contentDescription = "Google Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    )
                                } else if (!profile.customAvatarUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = profile.customAvatarUri,
                                        contentDescription = "Profile Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(2.dp, JapaneseCrimson, CircleShape)
                                    )
                                } else {
                                    val avatarPreset = PRESET_AVATARS.getOrNull(profile.avatarIndex)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(
                                                        JapaneseCrimson.copy(alpha = 0.22f),
                                                        JapaneseIndigo.copy(alpha = 0.12f)
                                                    )
                                                )
                                            )
                                            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = avatarPreset?.first ?: "🧑‍🏫", fontSize = 28.sp)
                                    }
                                }

                                // Photo camera indicator badge
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }

                            // Student Name and Target Level
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = currentUser?.displayName ?: profile.name.replace("JLPT N3", "").trim().ifBlank { "လေ့လာသူ" },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "တစ်နေ့ ${profile.dailyGoal} လုံး လေ့လာမည်",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Clean Outlined Edit Button (min 48dp touch target)
                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "ပြင်ဆင်ရန်",
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Level & XP Progress
                    val xp = profile.totalXp
                    val currentLevel = profile.level
                    val xpInLevel = xp % 150
                    val currentLevelProgress = (xpInLevel).toFloat() / 150f

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⭐ အဆင့် $currentLevel · ${profile.levelTitle}",
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
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // 3-Metric Clean Summary Strip
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetricItem(
                                icon = Icons.Default.LocalFireDepartment,
                                iconTint = JapaneseCrimson,
                                value = "${quizStreak.currentStreak} ရက်",
                                label = "ရက်ဆက်လေ့လာမှု",
                                modifier = Modifier.clickable { showStreakDetailsDialog = true }
                            )
                            Box(
                                modifier = Modifier
                                    .height(26.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            )
                            MetricItem(
                                icon = Icons.Default.School,
                                iconTint = MasteredGreen,
                                value = "$masteredCards",
                                label = "ကျက်မှတ်ပြီး"
                            )
                            Box(
                                modifier = Modifier
                                    .height(26.dp)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            )
                            MetricItem(
                                icon = Icons.Default.EmojiEvents,
                                iconTint = StreakOrange,
                                value = "$xp",
                                label = "XP အမှတ်"
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 2. Google Cloud Sync & Backup Card
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_google_account_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentUser != null) {
                        MasteredGreen.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (currentUser != null) MasteredGreen.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentUser != null) {
                        // Connected State
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MasteredGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = MasteredGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUser?.displayName ?: "Google အကောင့် ချိတ်ဆက်ပြီး",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = currentUser?.email ?: "Cloud Backup အဆင်သင့်ဖြစ်ပါပြီ",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Sign out action button
                            OutlinedButton(
                                onClick = { vocabViewModel.signOut() },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f)),
                                modifier = Modifier.testTag("profile_sign_out_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ထွက်မည်",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MasteredGreen.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MasteredGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "လေ့လာမှု မှတ်တမ်းများကို Cloud ပေါ်တွင် အလိုအလျောက် သိမ်းဆည်းထားပါသည်",
                                    fontSize = 11.5.sp,
                                    color = MasteredGreen,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        // Unauthenticated State: Clean Google Sign-in Card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GoogleLogoIcon(modifier = Modifier.size(24.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Google အကောင့် ချိတ်ဆက်ရန်",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "ဖုန်းပြောင်းလဲသော်လည်း မှတ်တမ်းများ မပျောက်စေရန် Cloud Backup ပြုလုပ်ပါ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Google Sign In Action Button
                        Button(
                            onClick = {
                                isSigningIn = true
                                authNoticeMessage = null
                                coroutineScope.launch {
                                    val res = vocabViewModel.signInWithGoogle(context)
                                    isSigningIn = false
                                    if (res.isFailure) {
                                        val err = res.exceptionOrNull()
                                        val msg = err?.localizedMessage ?: "Google အကောင့် ချိတ်ဆက်မှု မအောင်မြင်ပါ"
                                        authNoticeMessage = msg
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    } else {
                                        val user = res.getOrNull()
                                        Toast.makeText(
                                            context,
                                            "အကောင့်ချိတ်ဆက်ပြီးပါပြီ: ${user?.displayName ?: user?.email}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            enabled = !isSigningIn,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("profile_google_signin_btn")
                        ) {
                            if (isSigningIn) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Google သို့ ချိတ်ဆက်နေပါသည်...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                GoogleLogoIcon(modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Google ဖြင့် ဝင်မည် (Sign in with Google)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        authNoticeMessage?.let { notice ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = notice,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { authNoticeMessage = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss",
                                            tint = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 3. Daily Study Goal & JLPT Level
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_goal_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "တစ်နေ့တာ လေ့လာမှု ပစ်မှတ်",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "တစ်နေ့ ${profile.dailyGoal} လုံး",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Quick Goal Selection Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 15, 20, 30).forEach { goal ->
                            val isSelected = profile.dailyGoal == goal
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    vocabViewModel.updateProfile(
                                        name = profile.name,
                                        dailyGoal = goal,
                                        targetJlpt = profile.targetJlptLevel,
                                        avatarIndex = profile.avatarIndex,
                                        customAvatarUri = profile.customAvatarUri
                                    )
                                },
                                label = { Text("$goal လုံး", fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 4. Japanese Audio & Speech Speed Settings
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_voice_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "အသံထွက် ချိန်ညှိမှု (TTS Audio)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // Test Voice Button
                        OutlinedButton(
                            onClick = { vocabViewModel.testVoicePronunciation() },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("စမ်းသပ်မည်", fontSize = 11.5.sp)
                        }
                    }

                    // Speed Selection Chips
                    Text(
                        text = "အသံထွက် အမြန်နှုန်း ရွေးချယ်ရန်:",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val speeds = listOf(
                            Pair(0.8f, "ဖြည်းဖြည်း (0.8x)"),
                            Pair(1.0f, "ပုံမှန် (1.0x)"),
                            Pair(1.2f, "မြန်မြန် (1.2x)")
                        )
                        speeds.forEach { (rate, label) ->
                            val isSelected = (voiceSettings.speechRate - rate).let { Math.abs(it) < 0.05f }
                            FilterChip(
                                selected = isSelected,
                                onClick = { vocabViewModel.setVoiceSpeechRate(rate) },
                                label = { Text(label, fontSize = 11.5.sp) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 5. Theme & Appearance
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_theme_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsBrightness,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "အသွင်အပြင် (Theme Mode)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(ThemeMode.SYSTEM, "စနစ်အတိုင်း", Icons.Default.SettingsBrightness),
                            Triple(ThemeMode.LIGHT, "နေ့ခင်း", Icons.Default.LightMode),
                            Triple(ThemeMode.DARK, "ညဘက်", Icons.Default.DarkMode)
                        ).forEach { (mode, label, icon) ->
                            val isSelected = themeSettings.themeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { vocabViewModel.setThemeMode(mode) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                label = { Text(label, fontSize = 11.5.sp) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 6. Daily Study Reminder Notification
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_reminder_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "နေ့စဉ် လေ့လာရန် သတိပေးချက်",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (reminderSettings.isEnabled) {
                                        String.format("သတိပေးချိန်: %02d:%02d နာရီ (%s)", reminderSettings.reminderHour, reminderSettings.reminderMinute, reminderSettings.formattedTime)
                                    } else {
                                        "သတိပေးချက် ပိတ်ထားပါသည်"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = reminderSettings.isEnabled,
                            onCheckedChange = { vocabViewModel.setReminderEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    if (reminderSettings.isEnabled) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            vocabViewModel.setReminderTime(hourOfDay, minute)
                                        },
                                        reminderSettings.reminderHour,
                                        reminderSettings.reminderMinute,
                                        true
                                    ).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = String.format("အချိန်ပြောင်းမည် (%02d:%02d)", reminderSettings.reminderHour, reminderSettings.reminderMinute),
                                    fontSize = 12.sp
                                )
                            }

                            TextButton(
                                onClick = { vocabViewModel.triggerTestReminder() },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("စမ်းသပ်မည်", fontSize = 12.sp)
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                        // SRS Due Review Alerts Toggle & Test Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SRS စကားလုံးကျက်ချိန် သတိပေးချက်",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                )
                                Text(
                                    text = if (reminderSettings.isSrsReviewReminderEnabled) {
                                        "SRS အချိန်ရောက်ပါက Background တွင် အလိုအလျောက် သတိပေးမည်"
                                    } else {
                                        "SRS သတိပေးချက် ပိတ်ထားပါသည်"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = reminderSettings.isSrsReviewReminderEnabled,
                                onCheckedChange = { vocabViewModel.setSrsReminderEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = JapaneseCrimson,
                                    checkedTrackColor = JapaneseCrimson.copy(alpha = 0.3f)
                                )
                            )
                        }

                        if (reminderSettings.isSrsReviewReminderEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        vocabViewModel.triggerTestSrsReminder()
                                        Toast.makeText(context, "SRS သတိပေးချက် စမ်းသပ်မှု ပေးပို့လိုက်ပါပြီ", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    border = BorderStroke(1.dp, JapaneseCrimson.copy(alpha = 0.4f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = JapaneseCrimson,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "SRS သတိပေးချက် စမ်းသပ်မည်",
                                        fontSize = 11.5.sp,
                                        color = JapaneseCrimson
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 7. Custom Decks & Offline Database Status
        // --------------------------------------------------------------------
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_data_status_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Custom Decks Shortcut
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Style,
                                contentDescription = null,
                                tint = JapaneseCrimson,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "စိတ်ကြိုက် ဖလက်ရှ်ကတ်များ",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${customCards.size} ကတ် ဖန်တီးထားပါသည်",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToCustomDecks,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JapaneseCrimson),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("ဖွင့်ရန်", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Database Integrity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = MasteredGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "ဂျပန်စာ စကားလုံး ဒေတာဘေ့စ်",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$totalCards / ၄,၅၈၄ စကားလုံး (Offline အသင့်သုံး)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = MasteredGreen
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                vocabViewModel.refreshDatabaseCards(forceRefresh = true) { count ->
                                    Toast.makeText(context, "စကားလုံး $count ကတ်ကို စစ်ဆေးပြီးပါပြီ", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("စစ်ဆေးမည်", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Dialogs
    // ------------------------------------------------------------------------
    if (showStreakDetailsDialog) {
        QuizStreakDetailsDialog(
            streakInfo = quizStreak,
            onDismiss = { showStreakDetailsDialog = false }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = currentUser?.displayName ?: profile.name,
            currentGoal = profile.dailyGoal,
            currentLevel = profile.targetJlptLevel,
            currentAvatarIndex = profile.avatarIndex,
            currentCustomAvatarUri = profile.customAvatarUri,
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

// ----------------------------------------------------------------------------
// Clean Edit Profile Dialog (Fixed Text Areas, Buttons, and Icons)
// ----------------------------------------------------------------------------
@Composable
private fun EditProfileDialog(
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
                .padding(8.dp)
                .testTag("edit_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ပရိုဖိုင် ပြင်ဆင်ရန်",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Avatar Preview & Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                Text(text = preset?.first ?: "🧑‍🏫", fontSize = 32.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Pick Photo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // Avatar Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PRESET_AVATARS.forEachIndexed { index, (emoji, _) ->
                            val isSelected = selectedAvatarIndex == index && customAvatarUri.isNullOrBlank()
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.5.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                ),
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedAvatarIndex = index
                                        customAvatarUri = null
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    // Photo Picker Buttons
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
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ဓာတ်ပုံတင်ရန်", fontSize = 12.sp)
                        }

                        if (!customAvatarUri.isNullOrBlank()) {
                            TextButton(
                                onClick = { customAvatarUri = null },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ပုံဖျက်ရန်", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Clean Name Input Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("လေ့လာသူ အမည်") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (name.isNotEmpty()) {
                            IconButton(onClick = { name = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Clean Daily Goal Input Field
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 3) {
                            goalText = input
                        }
                    },
                    label = { Text("တစ်နေ့ လေ့လာမည့် စကားလုံး အရေအတွက်") },
                    leadingIcon = {
                        Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    supportingText = { Text("၁၀ မှ ၅၀ လုံး အကြံပြုပါသည်", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // JLPT Level Selection Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ပစ်မှတ် JLPT အဆင့်:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("N5", "N4", "N3", "N2", "N1").forEach { jlpt ->
                            val isSelected = level == jlpt
                            FilterChip(
                                selected = isSelected,
                                onClick = { level = jlpt },
                                label = { Text(jlpt, fontSize = 12.sp) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                // Action Buttons (Proper Touch Targets and Padding)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .padding(end = 8.dp)
                    ) {
                        Text("မလုပ်တော့ပါ", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val goal = goalText.toIntOrNull()?.coerceIn(5, 100) ?: 15
                            onSave(name.trim(), goal, level.trim(), selectedAvatarIndex, customAvatarUri)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("သိမ်းဆည်းမည်", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Reusable UI Helpers
// ----------------------------------------------------------------------------
@Composable
private fun MetricItem(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = width * 0.18f
        val center = Offset(width / 2f, height / 2f)

        // Red arc (top-left)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 195f,
            sweepAngle = 105f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )
        // Yellow arc (bottom-left)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 75f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )
        // Green arc (bottom-right)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 75f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )
        // Blue arc (right & center bar)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 310f,
            sweepAngle = 95f,
            useCenter = false,
            style = Stroke(strokeWidth)
        )
        // Horizontal bar
        drawLine(
            color = Color(0xFF4285F4),
            start = center,
            end = Offset(width - strokeWidth / 2f, height / 2f),
            strokeWidth = strokeWidth
        )
    }
}
