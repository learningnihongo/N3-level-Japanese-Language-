package com.example.ui.viewmodel

import com.example.data.model.VocabCard

enum class FlashcardStudyMode(val label: String, val subtitle: String) {
    JP_TO_MY("JP → MM", "Japanese to Burmese"),
    MY_TO_JP("MM → JP", "Burmese to Japanese"),
    AUDIO_FIRST("Audio First", "Listen & Recall"),
    COLLOCATION("တွဲလုံးများ", "Collocations & Compounds")
}

enum class VocabFilterType {
    ALL,
    DUE_REVIEWS,
    BOOKMARKED,
    CUSTOM_CARDS,
    WEAK_CARDS,
    MASTERED,
    PAST_EXAMS
}

enum class SearchFilterTarget(val label: String, val burmeseLabel: String, val subtitle: String) {
    ALL("All Fields", "အားလုံး", "Kanji, Reading, Burmese & Notes"),
    READING("Reading", "အသံထွက်", "Hiragana / Katakana reading"),
    MEANING("Burmese Meaning", "မြန်မာအဓိပ္ပာယ်", "Definition & Explanation")
}

enum class StatsTimeRange(val days: Int, val label: String) {
    DAYS_7(7, "7 Days"),
    DAYS_14(14, "14 Days"),
    DAYS_30(30, "30 Days")
}

data class AccuracySummary(
    val totalCorrect: Int = 0,
    val totalIncorrect: Int = 0,
    val accuracyPercent: Int = 0,
    val totalReviews: Int = 0
)

data class StreakDayStatus(
    val dayName: String = "",
    val dateLabel: String = "",
    val dayOfMonth: Int = 1,
    val isCompleted: Boolean = false,
    val isToday: Boolean = false,
    val cardsCount: Int = 0,
    val xpEarned: Int = 0
)

data class DailyStreakSummary(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val streakDays: List<StreakDayStatus> = emptyList(),
    val activeDaysThisMonth: Int = 0,
    val nextMilestoneStreak: Int = 1,
    val daysUntilMilestone: Int = 0,
    val totalReviewsCount: Int = 0,
    val streakConsistencyPercent: Int = 0
)

data class DailyStudyPoint(
    val dateLabel: String = "",
    val dayName: String = "",
    val cardsCount: Int = 0,
    val xpCount: Int = 0,
    val isToday: Boolean = false,
    val goalCount: Int = 15
)

data class KanjiCategoryMastery(
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryJapanese: String = "",
    val iconEmoji: String = "📖",
    val lessonRange: String = "",
    val totalCount: Int = 0,
    val masteredCount: Int = 0,
    val reviewingCount: Int = 0,
    val learningCount: Int = 0,
    val newCount: Int = 0,
    val masteryPercent: Float = 0f,
    val accuracyPercent: Int = 0,
    val sampleKanji: List<String> = emptyList()
)

data class KanjiTimelinePoint(
    val dateLabel: String = "",
    val shortDateLabel: String = "",
    val dayName: String = "",
    val timestamp: Long = 0L,
    val cumulativeLearned: Int = 0,
    val cumulativeMastered: Int = 0,
    val dailyNewKanji: Int = 0,
    val dailyReviewedKanji: Int = 0,
    val projectedTarget: Int = 0,
    val isToday: Boolean = false,
    val retentionPercent: Int = 0
)

data class LessonMasteryDetail(
    val lessonNumber: Int = 1,
    val lessonTitle: String = "",
    val totalCount: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val dueCount: Int = 0,
    val masteryPercent: Float = 0f
)

data class MasteryBreakdown(
    val masteredCount: Int = 0,
    val reviewingCount: Int = 0,
    val learningCount: Int = 0,
    val newCount: Int = 0,
    val totalCards: Int = 0,
    val masteryPercent: Float = 0f,
    val jlptLevel: String = "N3"
)

data class QuizTrendPoint(
    val quizType: String = "",
    val scorePercent: Int = 0,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val timestamp: Long = 0L,
    val dateLabel: String = ""
)

data class SessionStudyStats(
    val totalStudied: Int = 0,
    val againCount: Int = 0,
    val hardCount: Int = 0,
    val goodCount: Int = 0,
    val easyCount: Int = 0,
    val xpEarned: Int = 0,
    val mistakeCards: List<VocabCard> = emptyList()
)

data class WeeklyKanjiProgress(
    val dayName: String = "",
    val fullDateLabel: String = "",
    val shortDateLabel: String = "",
    val dayOfMonth: Int = 1,
    val isToday: Boolean = false,
    val kanjiReviewed: Int = 0,
    val kanjiMastered: Int = 0,
    val goalCount: Int = 15,
    val masteryRatePercent: Int = 0
)

data class WeeklyMasteryVsReviewedSummary(
    val dailyProgressList: List<WeeklyKanjiProgress> = emptyList(),
    val totalReviewedWeek: Int = 0,
    val totalMasteredWeek: Int = 0,
    val avgDailyReviewed: Int = 0,
    val avgDailyMastered: Int = 0,
    val weeklyMasteryConversionRate: Int = 0,
    val bestDayName: String = "",
    val targetWeeklyGoal: Int = 100,
    val goalReachedDaysCount: Int = 0
)

data class KanjiProgressDashboardSummary(
    val selectedTimeRange: StatsTimeRange = StatsTimeRange.DAYS_7,
    val timelinePoints: List<KanjiTimelinePoint> = emptyList(),
    val totalTargetKanji: Int = 0,
    val currentLearnedKanji: Int = 0,
    val currentMasteredKanji: Int = 0,
    val currentLearningKanji: Int = 0,
    val currentNewKanji: Int = 0,
    val overallProgressPercent: Float = 0f,
    val weeklyVelocity: Float = 0f,
    val estimatedDaysToGoal: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isStreakActiveToday: Boolean = false,
    val todayLearnedCount: Int = 0,
    val dailyGoalCount: Int = 15,
    val consistencyPercent: Int = 0,
    val calendarHeatmapDays: List<StreakDayStatus> = emptyList(),
    val totalReviews: Int = 0
)
