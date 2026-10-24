package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity representing a custom user-created flashcard.
 */
@Entity(tableName = "custom_flashcards")
data class CustomFlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val front: String,                          // Front face (Kanji / Word / Phrase)
    val reading: String = "",                   // Furigana / Reading
    val back: String,                           // Back face (Meaning / Definition in Burmese)
    val exampleSentence: String = "",           // Optional example sentence
    val exampleMeaning: String = "",            // Example sentence meaning
    val tags: String = "",                      // Comma-separated tags
    val personalNotes: String = "",             // User notes
    val category: String = "General",           // Category / Deck
    val cardType: String = "VOCAB",             // "VOCAB", "KANJI", "GRAMMAR", "PHRASE"
    val isBookmarked: Boolean = false,          // Favorite / Bookmarked
    val masteryLevel: Int = 0,                  // 0: New, 1: Learning, 2: Review, 3: Mastered
    val repetitions: Int = 0,                   // SM-2 repetitions
    val intervalDays: Int = 0,                  // Days until next review
    val easeFactor: Float = 2.5f,               // SM-2 Ease Factor
    val nextReviewTimestamp: Long = 0L,         // Next review epoch millis
    val lastReviewedTimestamp: Long = 0L,       // Last review epoch millis
    val timesCorrect: Int = 0,                  // Correct counter
    val timesIncorrect: Int = 0,                // Incorrect counter
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isDue: Boolean
        get() = repetitions == 0 || nextReviewTimestamp <= System.currentTimeMillis()
}
