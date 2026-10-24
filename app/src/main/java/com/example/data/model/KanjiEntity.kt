package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity representing a Kanji character entry for offline study.
 */
@Entity(tableName = "kanji_entries")
data class KanjiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val kanji: String,                          // e.g. "日", "本", "語"
    val onyomi: String = "",                     // Katakana on-readings
    val kunyomi: String = "",                    // Hiragana kun-readings
    val meaningBurmese: String,                 // Meaning in Myanmar/Burmese
    val meaningEnglish: String = "",            // Meaning in English
    val exampleCompounds: String = "",          // Example compound words
    val strokeCount: Int = 0,                   // Stroke count
    val jlptLevel: String = "N3",               // JLPT Level (N5, N4, N3, N2, N1)
    val lessonNumber: Int = 1,                  // Lesson grouping
    val personalNotes: String = "",             // User notes
    val isBookmarked: Boolean = false,          // Favorite / Bookmarked
    val masteryLevel: Int = 0,                  // 0: New, 1: Learning, 2: Review, 3: Mastered
    val repetitions: Int = 0,                   // SM-2 repetitions
    val intervalDays: Int = 0,                  // Days until next review
    val easeFactor: Float = 2.5f,               // SM-2 Ease Factor
    val nextReviewTimestamp: Long = 0L,         // Next review epoch millis
    val lastReviewedTimestamp: Long = 0L,       // Last review epoch millis
    val timesCorrect: Int = 0,                  // Correct counter
    val timesIncorrect: Int = 0,                // Incorrect counter
    val createdAt: Long = System.currentTimeMillis()
) {
    val isDue: Boolean
        get() = repetitions == 0 || nextReviewTimestamp <= System.currentTimeMillis()
}
