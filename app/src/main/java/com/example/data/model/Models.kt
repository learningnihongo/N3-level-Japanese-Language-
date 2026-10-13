package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String? = null,
    val displayName: String = "Learner",
    val avatarUrl: String? = null,
    val targetJlptLevel: String = "N3",
    val dailyGoal: Long = 20L,
    val totalXp: Long = 0L,
    val currentStreak: Long = 1L,
    val bestStreak: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomFlashcard(
    @DocumentId val id: String = "",
    val userId: String = "",
    val front: String = "",
    val reading: String = "",
    val back: String = "",
    val cardType: String = "Kotoba",
    val category: String = "Custom",
    val exampleSentence: String = "",
    val exampleMeaning: String = "",
    val masteryLevel: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class QuizHistory(
    @DocumentId val id: String = "",
    val userId: String = "",
    val quizType: String = "Kanji",
    val score: Long = 0L,
    val totalQuestions: Long = 10L,
    val accuracyPercentage: Long = 0L,
    val xpEarned: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class KanjiItem(
    val id: Int,
    val kanji: String,
    val onyomi: String,
    val kunyomi: String,
    val meaningBurmese: String,
    val meaningEnglish: String,
    val strokeCount: Int,
    val jlptLevel: String = "N3",
    val examples: List<KanjiExample> = emptyList()
)

data class KanjiExample(
    val word: String,
    val reading: String,
    val meaningBurmese: String
)

data class KotobaItem(
    val id: Int,
    val word: String,
    val reading: String,
    val romaji: String,
    val meaningBurmese: String,
    val meaningEnglish: String,
    val partOfSpeech: String,
    val exampleJapanese: String,
    val exampleBurmese: String,
    val category: String = "Daily",
    val jlptLevel: String = "N3"
)
