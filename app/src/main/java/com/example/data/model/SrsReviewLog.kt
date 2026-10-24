package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "srs_review_logs")
data class SrsReviewLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardId: Long = 0,
    val kanji: String = "",
    val reading: String = "",
    val meaningBurmese: String = "",
    val rating: String = "",
    val ratingQuality: Int = 0,
    val repetitionsBefore: Int = 0,
    val repetitionsAfter: Int = 0,
    val intervalDaysBefore: Int = 0,
    val intervalDaysAfter: Int = 0,
    val easeFactorBefore: Float = 2.5f,
    val easeFactorAfter: Float = 2.5f,
    val reviewTimestamp: Long = System.currentTimeMillis(),
    val scheduledNextReviewTimestamp: Long = 0L,
    val isRetentionSuccess: Boolean = true,
    val timeSpentSeconds: Int = 0
)
