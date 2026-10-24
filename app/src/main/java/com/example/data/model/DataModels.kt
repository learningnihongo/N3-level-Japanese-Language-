package com.example.data.model


data class StreakCalendarDay(
    val dayName: String = "",
    val dayOfMonth: Int = 1,
    val dateLabel: String = "",
    val isCompleted: Boolean = false,
    val isToday: Boolean = false,
    val quizCount: Int = 0
)

data class QuizStreakInfo(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isQuizCompletedToday: Boolean = false,
    val isGracePeriodActive: Boolean = false,
    val lastQuizTimestamp: Long = 0L,
    val nextMilestone: Int = 1,
    val daysUntilNextMilestone: Int = 1,
    val streakStatusMessage: String = "Start your study streak today!",
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
            currentStreak >= 14 -> "⚡"
            currentStreak >= 7 -> "🔥"
            currentStreak >= 3 -> "✨"
            currentStreak >= 1 -> "🌱"
            else -> "💤"
        }

    val badgeColorHex: Long
        get() = when {
            currentStreak >= 30 -> 0xFFFFD700
            currentStreak >= 14 -> 0xFFFF6D00
            currentStreak >= 7 -> 0xFFFF3D00
            currentStreak >= 3 -> 0xFF2979FF
            currentStreak >= 1 -> 0xFF00E676
            else -> 0xFF9E9E9E
        }
}

data class SrsDayForecast(
    val dayOffset: Int = 0,
    val dayLabel: String = "",
    val dateFormatted: String = "",
    val scheduledCardsCount: Int = 0,
    val isToday: Boolean = false
)

data class SrsScheduleSummary(
    val totalCards: Int = 0,
    val dueNowCount: Int = 0,
    val dueTodayCount: Int = 0,
    val dueTomorrowCount: Int = 0,
    val dueNext3DaysCount: Int = 0,
    val dueNext7DaysCount: Int = 0,
    val dueLaterCount: Int = 0,
    val newCardsCount: Int = 0,
    val learningCardsCount: Int = 0,
    val reviewingCardsCount: Int = 0,
    val masteredCardsCount: Int = 0,
    val averageEaseFactor: Float = 2.5f,
    val overallRetentionRate: Float = 0f,
    val totalReviewsLogged: Int = 0,
    val successfulReviewsCount: Int = 0,
    val dailyScheduleForecast: List<SrsDayForecast> = emptyList()
)

data class PastExamSession(
    val code: String,
    val lessonNumber: Int,
    val titleJapanese: String,
    val titleBurmese: String,
    val wordCount: Int
)

object PastExamData {
    const val TOTAL_PAST_EXAM_WORDS = 1357
    val PAST_EXAM_LESSON_RANGE = 201..220

    val SESSIONS = listOf(
        PastExamSession("2014/12", 201, "2014/12 日本語能力試験 N3", "၂၀၁၄ ဒီဇင်ဘာ N3 စာမေးပွဲ", 137),
        PastExamSession("2015/7", 202, "2015/7 日本語能力試験 N3", "၂၀၁၅ ဇူလိုင် N3 စာမေးပွဲ", 106),
        PastExamSession("2015/12", 203, "2015/12 日本語能力試験 N3", "၂၀၁၅ ဒီဇင်ဘာ N3 စာမေးပွဲ", 109),
        PastExamSession("2016/7", 204, "2016/7 日本語能力試験 N3", "၂၀၁၆ ဇူလိုင် N3 စာမေးပွဲ", 36),
        PastExamSession("2016/12", 205, "2016/12 日本語能力試験 N3", "၂၀၁၆ ဒီဇင်ဘာ N3 စာမေးပွဲ", 38),
        PastExamSession("2017/7", 206, "2017/7 日本語能力試験 N3", "၂၀၁၇ ဇူလိုင် N3 စာမေးပွဲ", 109),
        PastExamSession("2018/7", 207, "2018/7 日本語能力試験 N3", "၂၀၁၈ ဇူလိုင် N3 စာမေးပွဲ", 78),
        PastExamSession("2018/12", 208, "2018/12 日本語能力試験 N3", "၂၀၁၈ ဒီဇင်ဘာ N3 စာမေးပွဲ", 57),
        PastExamSession("2019/7", 209, "2019/7 日本語能力試験 N3", "၂၀၁၉ ဇူလိုင် N3 စာမေးပွဲ", 43),
        PastExamSession("2019/12", 210, "2019/12 日本語能力試験 N3", "၂၀၁၉ ဒီဇင်ဘာ N3 စာမေးပွဲ", 84),
        PastExamSession("2020/12", 211, "2020/12 日本語能力試験 N3", "၂၀၂၀ ဒီဇင်ဘာ N3 စာမေးပွဲ", 50),
        PastExamSession("2021/7", 212, "2021/7 日本語能力試験 N3", "၂၀၂၁ ဇူလိုင် N3 စာမေးပွဲ", 26),
        PastExamSession("2021/12", 213, "2021/12 日本語能力試験 N3", "၂၀၂၁ ဒီဇင်ဘာ N3 စာမေးပွဲ", 52),
        PastExamSession("2022/7", 214, "2022/7 日本語能力試験 N3", "၂၀၂၂ ဇူလိုင် N3 စာမေးပွဲ", 42),
        PastExamSession("2022/12", 215, "2022/12 日本語能力試験 N3", "၂၀၂၂ ဒီဇင်ဘာ N3 စာမေးပွဲ", 45),
        PastExamSession("2023/7", 216, "2023/7 日本語能力試験 N3", "၂၀၂၃ ဇူလိုင် N3 စာမေးပွဲ", 39),
        PastExamSession("2023/12", 217, "2023/12 日本語能力試験 N3", "၂၀၂၃ ဒီဇင်ဘာ N3 စာမေးပွဲ", 43),
        PastExamSession("2024/7", 218, "2024/7 日本語能力試験 N3", "၂၀၂၄ ဇူလိုင် N3 စာမေးပွဲ", 48),
        PastExamSession("2025/7", 219, "2025/7 日本語能力試験 N3", "၂၀၂၅ ဇူလိုင် N3 စာမေးပွဲ", 105),
        PastExamSession("2025/12", 220, "2025/12 日本語能力試験 N3", "၂၀၂၅ ဒီဇင်ဘာ N3 စာမေးပွဲ", 110)
    )

    fun isPastExamCard(card: VocabCard): Boolean {
        return (card.lessonNumber in PAST_EXAM_LESSON_RANGE) || card.tags.contains("မေးခွန်းဟောင်း")
    }

    fun hasKanji(text: String): Boolean {
        for (char in text) {
            val code = char.code
            if ((code in 19968..40959) || (code in 13312..19903)) {
                return true
            }
        }
        return false
    }
}
