package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_histories")
data class QuizHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizType: String,
    val lessonFilter: String,
    val score: Int,
    val totalQuestions: Int,
    val timeSpentSeconds: Int = 0,
    val xpEarned: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    val accuracyPercentage: Int
        get() = if (totalQuestions > 0) ((score.toDouble() / totalQuestions) * 100).toInt() else 0
}
