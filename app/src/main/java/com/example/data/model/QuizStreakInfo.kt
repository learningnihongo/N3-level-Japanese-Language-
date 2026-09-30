package com.example.data.model

data class StreakCalendarDay(
    val dayName: String,
    val dayOfMonth: Int,
    val dateLabel: String,
    val isCompleted: Boolean,
    val isToday: Boolean,
    val quizCount: Int = 0
)

data class QuizStreakInfo(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isQuizCompletedToday: Boolean = false,
    val isGracePeriodActive: Boolean = false,
    val lastQuizTimestamp: Long = 0L,
    val nextMilestone: Int = 3,
    val daysUntilNextMilestone: Int = 3,
    val streakStatusMessage: String = "Complete a quiz today to start your streak!",
    val recentDays: List<StreakCalendarDay> = emptyList()
) {
    val tierTitle: String
        get() = when {
            currentStreak >= 100 -> "Centurion Legend"
            currentStreak >= 30 -> "Monthly Master"
            currentStreak >= 14 -> "Samurai Warrior"
            currentStreak >= 7 -> "Weekly Champion"
            currentStreak >= 3 -> "Consistent Scholar"
            currentStreak >= 1 -> "Active Learner"
            else -> "Unranked"
        }

    val iconEmoji: String
        get() = when {
            currentStreak >= 30 -> "👑"
            currentStreak >= 14 -> "⚔️"
            currentStreak >= 7 -> "⚡"
            currentStreak >= 3 -> "🔥"
            currentStreak >= 1 -> "✨"
            else -> "🎯"
        }

    val badgeColorHex: Long
        get() = when {
            currentStreak >= 30 -> 0xFFFFD700 // Gold
            currentStreak >= 14 -> 0xFFFF7043 // Deep Orange Flame
            currentStreak >= 7 -> 0xFFFF9800 // Orange
            currentStreak >= 3 -> 0xFFFFA726 // Light Amber Flame
            currentStreak >= 1 -> 0xFF4CAF50 // Green
            else -> 0xFF9E9E9E // Grey
        }
}
