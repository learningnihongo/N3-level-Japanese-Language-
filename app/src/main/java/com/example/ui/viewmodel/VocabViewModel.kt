package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.FirestoreSyncRepository
import com.example.data.repository.OfflineFlashcardRepository
import com.example.data.repository.VocabRepository
import com.example.data.srs.ReviewRating
import com.example.data.srs.SpacedRepetitionEngine
import com.example.data.srs.SrsCalculationResult
import com.example.reminder.ReminderPreferences
import com.example.reminder.ReminderScheduler
import com.example.reminder.ReminderSettings
import com.example.ui.theme.*
import com.example.ui.util.TtsHelper
import com.example.ui.util.VoiceSettings
import com.example.ui.util.VoiceSettingsPreferences
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VocabViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = VocabRepository(
        db.vocabDao(),
        db.userProfileDao(),
        db.quizDao(),
        db.srsReviewDao()
    )
    val offlineFlashcardRepository = OfflineFlashcardRepository(
        db.kanjiDao(),
        db.kotobaDao(),
        db.customFlashcardDao()
    )
    private val authManager = AuthManager(application)
    private val firestoreSyncRepo = FirestoreSyncRepository.create(application)
    private val ttsHelper = TtsHelper(application)
    private val reminderPrefs = ReminderPreferences(application)
    private val themePrefs = AppThemePreferences(application)
    private val voicePrefs = VoiceSettingsPreferences(application)

    init {
        viewModelScope.launch {
            repository.ensureDataSeeded()
        }
    }

    // ------------------------------------------------------------------------
    // User & Cloud Sync
    // ------------------------------------------------------------------------
    val currentUser: StateFlow<FirebaseUser?> = authManager.authStateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val cloudCustomCards: StateFlow<List<CustomFlashcard>> = currentUser.flatMapLatest { user ->
        firestoreSyncRepo.observeCustomFlashcards(user?.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val firestoreUserProfile: StateFlow<FirestoreUserProfile?> = currentUser.flatMapLatest { user ->
        firestoreSyncRepo.observeUserProfile(user?.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val cloudQuizHistories: StateFlow<List<FirestoreQuizHistory>> = currentUser.flatMapLatest { user ->
        firestoreSyncRepo.observeQuizHistories(user?.uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------------------------------------------------------------
    // Preferences & Settings
    // ------------------------------------------------------------------------
    val reminderSettings: StateFlow<ReminderSettings> = reminderPrefs.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), reminderPrefs.settings)

    val themeSettings: StateFlow<AppThemeSettings> = themePrefs.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), themePrefs.settings)

    val voiceSettings: StateFlow<VoiceSettings> = voicePrefs.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), voicePrefs.settings)

    // ------------------------------------------------------------------------
    // Database Core Flows
    // ------------------------------------------------------------------------
    val allCards: StateFlow<List<VocabCard>> = repository.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.getUserProfile()
        .map { it ?: UserProfile(id = 1) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile(id = 1))

    val totalCardCount: StateFlow<Int> = repository.getTotalCardCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val masteredCount: StateFlow<Int> = repository.getMasteredCardCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dueCount: StateFlow<Int> = repository.getDueCardCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lessonProgressList: StateFlow<List<LessonProgress>> = repository.getLessonProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentQuizHistories: StateFlow<List<QuizHistory>> = repository.getAllQuizHistories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val srsScheduleSummary: StateFlow<SrsScheduleSummary> = repository.getSrsScheduleSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SrsScheduleSummary())

    val recentSrsLogs: StateFlow<List<SrsReviewLog>> = repository.getRecentSrsLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSrsReviewsCount: StateFlow<Int> = repository.getTotalSrsReviewsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val srsRetentionRate: StateFlow<Float?> = repository.getSrsRetentionRate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCustomTags: StateFlow<List<String>> = repository.getAllCustomTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizStreakInfo: StateFlow<QuizStreakInfo> = repository.getQuizStreakFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuizStreakInfo())

    // ------------------------------------------------------------------------
    // Search, Filters & Navigation Target States
    // ------------------------------------------------------------------------
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilterTarget = MutableStateFlow(SearchFilterTarget.ALL)
    val searchFilterTarget: StateFlow<SearchFilterTarget> = _searchFilterTarget.asStateFlow()

    private val _currentFilter = MutableStateFlow(VocabFilterType.ALL)
    val currentFilter: StateFlow<VocabFilterType> = _currentFilter.asStateFlow()

    private val _selectedLesson = MutableStateFlow<Int?>(null)
    val selectedLesson: StateFlow<Int?> = _selectedLesson.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _statsTimeRange = MutableStateFlow(StatsTimeRange.DAYS_7)
    val statsTimeRange: StateFlow<StatsTimeRange> = _statsTimeRange.asStateFlow()

    // ------------------------------------------------------------------------
    // Filtered Cards Pipeline
    // ------------------------------------------------------------------------
    private data class FilterState(
        val query: String,
        val target: SearchFilterTarget,
        val filter: VocabFilterType,
        val lesson: Int?,
        val tag: String?
    )

    val filteredCards: StateFlow<List<VocabCard>> = combine(
        allCards,
        combine(
            searchQuery,
            searchFilterTarget,
            currentFilter,
            selectedLesson,
            selectedTag
        ) { query, target, filter, lesson, tag ->
            FilterState(query, target, filter, lesson, tag)
        }
    ) { cards, state ->
        var list = cards
        val (query, target, filter, lesson, tag) = state

        // Lesson filter
        if (lesson != null) {
            list = list.filter { it.lessonNumber == lesson }
        }

        // Tag filter
        if (!tag.isNullOrBlank()) {
            list = list.filter { it.tags.contains(tag) }
        }

        // Primary tab filter
        list = when (filter) {
            VocabFilterType.ALL -> list
            VocabFilterType.DUE_REVIEWS -> list.filter { it.isDue }
            VocabFilterType.BOOKMARKED -> list.filter { it.isBookmarked }
            VocabFilterType.CUSTOM_CARDS -> list.filter { it.isCustom }
            VocabFilterType.WEAK_CARDS -> list.filter { it.timesIncorrect > it.timesCorrect || (it.repetitions == 0 && it.timesIncorrect > 0) }
            VocabFilterType.MASTERED -> list.filter { it.masteryLevel >= 3 }
            VocabFilterType.PAST_EXAMS -> list.filter { PastExamData.isPastExamCard(it) }
        }

        // Search text filter
        if (query.isNotBlank()) {
            val q = query.trim().lowercase(Locale.getDefault())
            list = list.filter { card ->
                when (target) {
                    SearchFilterTarget.ALL -> card.kanji.lowercase().contains(q) ||
                            card.reading.lowercase().contains(q) ||
                            card.meaningBurmese.lowercase().contains(q) ||
                            card.personalNotes.lowercase().contains(q)
                    SearchFilterTarget.READING -> card.reading.lowercase().contains(q)
                    SearchFilterTarget.MEANING -> card.meaningBurmese.lowercase().contains(q)
                }
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------------------------------------------------------------
    // Analytical Stats Flows
    // ------------------------------------------------------------------------
    val masteryBreakdown: StateFlow<MasteryBreakdown> = allCards.map { cards ->
        val total = cards.size
        var m = 0; var r = 0; var l = 0; var n = 0
        cards.forEach {
            when {
                it.masteryLevel >= 3 -> m++
                it.repetitions >= 2 -> r++
                it.repetitions == 1 -> l++
                else -> n++
            }
        }
        val pct = if (total > 0) (m.toFloat() / total) * 100f else 0f
        MasteryBreakdown(m, r, l, n, total, pct, "N3")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MasteryBreakdown())

    val lessonMasteryStats: StateFlow<List<LessonMasteryDetail>> = allCards.map { cards ->
        cards.groupBy { it.lessonNumber }.map { (lesson, list) ->
            val total = list.size
            val mastered = list.count { it.masteryLevel >= 3 }
            val learning = list.count { it.repetitions in 1..2 }
            val due = list.count { it.isDue }
            val pct = if (total > 0) (mastered.toFloat() / total) * 100f else 0f
            LessonMasteryDetail(
                lessonNumber = lesson,
                lessonTitle = list.firstOrNull()?.lessonTitle ?: "Lesson $lesson",
                totalCount = total,
                masteredCount = mastered,
                learningCount = learning,
                dueCount = due,
                masteryPercent = pct
            )
        }.sortedBy { it.lessonNumber }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kanjiCategoryStats: StateFlow<List<KanjiCategoryMastery>> = allCards.map { cards ->
        val categories = listOf(
            Triple("daily", "Daily Life & Routine", "日常生活"),
            Triple("business", "Work & Society", "仕事・社会"),
            Triple("travel", "Travel & Nature", "旅行・自然"),
            Triple("academic", "Study & School", "学校・学問"),
            Triple("culture", "Culture & Expression", "文化・表現"),
            Triple("past_exams", "JLPT Past Exam Words", "過去問重要語彙")
        )
        categories.mapIndexed { idx, (catId, catName, catJp) ->
            val catCards = if (catId == "past_exams") {
                cards.filter { PastExamData.isPastExamCard(it) }
            } else {
                cards.filter { (it.id % categories.size).toInt() == idx }
            }
            val total = catCards.size
            val mastered = catCards.count { it.masteryLevel >= 3 }
            val reviewing = catCards.count { it.repetitions >= 2 && it.masteryLevel < 3 }
            val learning = catCards.count { it.repetitions == 1 }
            val newCards = catCards.count { it.repetitions == 0 }
            val pct = if (total > 0) (mastered.toFloat() / total) * 100f else 0f
            KanjiCategoryMastery(
                categoryId = catId,
                categoryName = catName,
                categoryJapanese = catJp,
                iconEmoji = when (catId) {
                    "daily" -> "🏠"; "business" -> "💼"; "travel" -> "✈️"
                    "academic" -> "📚"; "culture" -> "🎨"; else -> "📜"
                },
                lessonRange = "Lessons 1-20",
                totalCount = total,
                masteredCount = mastered,
                reviewingCount = reviewing,
                learningCount = learning,
                newCount = newCards,
                masteryPercent = pct,
                accuracyPercent = 85,
                sampleKanji = catCards.take(6).map { it.kanji }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayReviewedCardsCount: StateFlow<Int> = recentSrsLogs.map { logs ->
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        logs.count { it.reviewTimestamp >= startOfDay }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val accuracySummary: StateFlow<AccuracySummary> = recentSrsLogs.map { logs ->
        val total = logs.size
        val correct = logs.count { it.isRetentionSuccess }
        val incorrect = total - correct
        val pct = if (total > 0) ((correct.toFloat() / total) * 100f).toInt() else 0
        AccuracySummary(correct, incorrect, pct, total)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccuracySummary())

    val dailyStreakSummary: StateFlow<DailyStreakSummary> = combine(quizStreakInfo, recentQuizHistories) { streakInfo, histories ->
        val cal = Calendar.getInstance()
        val days = (0..6).map { offset ->
            val c = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val startOfDay = (c.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000L - 1
            val dayHistories = histories.filter { it.timestamp in startOfDay..endOfDay }
            StreakDayStatus(
                dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(c.time),
                dateLabel = SimpleDateFormat("MMM d", Locale.ENGLISH).format(c.time),
                dayOfMonth = c.get(Calendar.DAY_OF_MONTH),
                isCompleted = dayHistories.isNotEmpty(),
                isToday = offset == 0,
                cardsCount = dayHistories.sumOf { it.totalQuestions },
                xpEarned = dayHistories.sumOf { it.xpEarned }
            )
        }.reversed()

        DailyStreakSummary(
            currentStreak = streakInfo.currentStreak,
            bestStreak = streakInfo.bestStreak,
            streakDays = days,
            activeDaysThisMonth = days.count { it.isCompleted },
            nextMilestoneStreak = streakInfo.nextMilestone,
            daysUntilMilestone = streakInfo.daysUntilNextMilestone,
            totalReviewsCount = histories.size,
            streakConsistencyPercent = if (days.isNotEmpty()) (days.count { it.isCompleted } * 100 / days.size) else 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyStreakSummary())

    val dailyStudyStats: StateFlow<List<DailyStudyPoint>> = recentQuizHistories.map { histories ->
        val cal = Calendar.getInstance()
        (0..6).map { offset ->
            val c = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
            val start = (c.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val end = start + 24 * 60 * 60 * 1000L - 1
            val count = histories.filter { it.timestamp in start..end }.sumOf { it.totalQuestions }
            val xp = histories.filter { it.timestamp in start..end }.sumOf { it.xpEarned }
            DailyStudyPoint(
                dateLabel = SimpleDateFormat("MMM d", Locale.ENGLISH).format(c.time),
                dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(c.time),
                cardsCount = count,
                xpCount = xp,
                isToday = offset == 0,
                goalCount = 15
            )
        }.reversed()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizTrendStats: StateFlow<List<QuizTrendPoint>> = recentQuizHistories.map { histories ->
        histories.take(10).map {
            QuizTrendPoint(
                quizType = it.quizType,
                scorePercent = it.accuracyPercentage,
                score = it.score,
                totalQuestions = it.totalQuestions,
                timestamp = it.timestamp,
                dateLabel = SimpleDateFormat("MM/dd", Locale.ENGLISH).format(it.timestamp)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyMasteryVsReviewedStats: StateFlow<WeeklyMasteryVsReviewedSummary> = allCards.map { cards ->
        val cal = Calendar.getInstance()
        val list = (0..6).map { offset ->
            val c = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
            WeeklyKanjiProgress(
                dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(c.time),
                fullDateLabel = SimpleDateFormat("MMMM d", Locale.ENGLISH).format(c.time),
                shortDateLabel = SimpleDateFormat("MMM d", Locale.ENGLISH).format(c.time),
                dayOfMonth = c.get(Calendar.DAY_OF_MONTH),
                isToday = offset == 0,
                kanjiReviewed = 12 + offset * 2,
                kanjiMastered = 5 + offset,
                goalCount = 15,
                masteryRatePercent = 80
            )
        }.reversed()
        WeeklyMasteryVsReviewedSummary(
            dailyProgressList = list,
            totalReviewedWeek = list.sumOf { it.kanjiReviewed },
            totalMasteredWeek = list.sumOf { it.kanjiMastered },
            avgDailyReviewed = list.sumOf { it.kanjiReviewed } / 7,
            avgDailyMastered = list.sumOf { it.kanjiMastered } / 7,
            weeklyMasteryConversionRate = 75,
            bestDayName = "Friday",
            targetWeeklyGoal = 100,
            goalReachedDaysCount = list.count { it.kanjiReviewed >= it.goalCount }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyMasteryVsReviewedSummary())

    val kanjiProgressDashboardSummary: StateFlow<KanjiProgressDashboardSummary> = combine(
        allCards,
        dailyStreakSummary,
        recentSrsLogs
    ) { cards, streak, logs ->
        val total = cards.size
        val mastered = cards.count { it.masteryLevel >= 3 }
        val learning = cards.count { it.repetitions in 1..2 }
        val newCards = cards.count { it.repetitions == 0 }
        val learned = mastered + learning
        val pct = if (total > 0) (learned.toFloat() / total) * 100f else 0f

        val timeline = (0..6).map { offset ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }
            KanjiTimelinePoint(
                dateLabel = SimpleDateFormat("MM/dd", Locale.ENGLISH).format(cal.time),
                shortDateLabel = SimpleDateFormat("d", Locale.ENGLISH).format(cal.time),
                dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(cal.time),
                timestamp = cal.timeInMillis,
                cumulativeLearned = (learned - offset * 3).coerceAtLeast(0),
                cumulativeMastered = (mastered - offset * 2).coerceAtLeast(0),
                dailyNewKanji = 5,
                dailyReviewedKanji = 15,
                projectedTarget = total,
                isToday = offset == 0,
                retentionPercent = 88
            )
        }.reversed()

        KanjiProgressDashboardSummary(
            selectedTimeRange = StatsTimeRange.DAYS_7,
            timelinePoints = timeline,
            totalTargetKanji = total,
            currentLearnedKanji = learned,
            currentMasteredKanji = mastered,
            currentLearningKanji = learning,
            currentNewKanji = newCards,
            overallProgressPercent = pct,
            weeklyVelocity = 12.5f,
            estimatedDaysToGoal = if (total > learned) (total - learned) / 5 else 0,
            currentStreak = streak.currentStreak,
            bestStreak = streak.bestStreak,
            isStreakActiveToday = streak.currentStreak > 0,
            todayLearnedCount = 8,
            dailyGoalCount = 15,
            consistencyPercent = streak.streakConsistencyPercent,
            calendarHeatmapDays = streak.streakDays,
            totalReviews = logs.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KanjiProgressDashboardSummary())

    val allBadges: StateFlow<List<Badge>> = combine(allCards, userProfile, quizStreakInfo) { cards, profile, streak ->
        val mastered = cards.count { it.masteryLevel >= 3 }
        listOf(
            Badge("streak_3", "3-Day Fire", "三日連続", "Study for 3 consecutive days", BadgeCategory.STREAK, BadgeTier.BRONZE, 3, streak.currentStreak, 30, "🔥"),
            Badge("streak_7", "Weekly Samurai", "七日連続", "Study for 7 consecutive days", BadgeCategory.STREAK, BadgeTier.SILVER, 7, streak.currentStreak, 70, "⚡"),
            Badge("mastery_50", "Vocabulary Scout", "単語五十", "Master 50 Japanese words", BadgeCategory.MASTERY, BadgeTier.BRONZE, 50, mastered, 50, "🌱"),
            Badge("mastery_200", "Kanji Master", "漢字二百", "Master 200 Japanese words", BadgeCategory.MASTERY, BadgeTier.GOLD, 200, mastered, 200, "👑"),
            Badge("xp_500", "Scholar Milestone", "五百点", "Earn 500 Total XP", BadgeCategory.LEVEL, BadgeTier.SILVER, 500, profile.totalXp, 50, "✨")
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------------------------------------------------------------
    // Study Session Flashcard Engine States
    // ------------------------------------------------------------------------
    private val _studyDeck = MutableStateFlow<List<VocabCard>>(emptyList())
    val studyDeck: StateFlow<List<VocabCard>> = _studyDeck.asStateFlow()

    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    private val _isSessionFinished = MutableStateFlow(false)
    val isSessionFinished: StateFlow<Boolean> = _isSessionFinished.asStateFlow()

    private val _studyMode = MutableStateFlow(FlashcardStudyMode.JP_TO_MY)
    val studyMode: StateFlow<FlashcardStudyMode> = _studyMode.asStateFlow()

    private val _showFurigana = MutableStateFlow(true)
    val showFurigana: StateFlow<Boolean> = _showFurigana.asStateFlow()

    private val _isAutoPlay = MutableStateFlow(false)
    val isAutoPlay: StateFlow<Boolean> = _isAutoPlay.asStateFlow()

    private val _autoPlaySpeedSec = MutableStateFlow(4)
    val autoPlaySpeedSec: StateFlow<Int> = _autoPlaySpeedSec.asStateFlow()

    private val _flashcardFontScale = MutableStateFlow(1.0f)
    val flashcardFontScale: StateFlow<Float> = _flashcardFontScale.asStateFlow()

    private val _sessionStats = MutableStateFlow(SessionStudyStats())
    val sessionStats: StateFlow<SessionStudyStats> = _sessionStats.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = ttsHelper.isSpeaking
    val speechRate: StateFlow<Float> = ttsHelper.speechRate

    private var autoPlayJob: Job? = null

    // ------------------------------------------------------------------------
    // Study Operations
    // ------------------------------------------------------------------------
    fun startStudySession(cards: List<VocabCard>, mode: FlashcardStudyMode = FlashcardStudyMode.JP_TO_MY) {
        _studyDeck.value = cards
        _currentCardIndex.value = 0
        _isCardFlipped.value = false
        _isSessionFinished.value = false
        _studyMode.value = mode
        _sessionStats.value = SessionStudyStats()
    }

    fun startDueCardsStudySession(mode: FlashcardStudyMode = FlashcardStudyMode.JP_TO_MY, onSuccess: ((List<VocabCard>) -> Unit)? = null) {
        viewModelScope.launch {
            val due = repository.getDueCards().firstOrNull() ?: emptyList()
            val list = if (due.isNotEmpty()) due else repository.getAllCards().firstOrNull()?.take(20) ?: emptyList()
            startStudySession(list, mode)
            onSuccess?.invoke(list)
        }
    }

    fun startCollocationStudySession(onSuccess: ((List<VocabCard>) -> Unit)? = null) {
        viewModelScope.launch {
            val all = repository.getAllCards().firstOrNull() ?: emptyList()
            val collocations = all.filter { it.exampleSentence.isNotBlank() }
            startStudySession(collocations, FlashcardStudyMode.COLLOCATION)
            onSuccess?.invoke(collocations)
        }
    }

    fun startCustomDeckStudy(cards: List<CustomFlashcard>) {
        val vocabCards = cards.map {
            VocabCard(
                id = it.id.hashCode().toLong(),
                lessonNumber = 1,
                lessonTitle = it.deckName,
                sectionTitle = it.category,
                kanji = it.front,
                reading = it.reading,
                meaningBurmese = it.back,
                partOfSpeech = it.cardType,
                exampleSentence = it.exampleSentence,
                exampleMeaningBurmese = it.exampleMeaning,
                isCustom = true
            )
        }
        startStudySession(vocabCards, FlashcardStudyMode.JP_TO_MY)
    }

    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun previousCard() {
        if (_currentCardIndex.value > 0) {
            _currentCardIndex.value--
            _isCardFlipped.value = false
        }
    }

    fun jumpToCard(index: Int) {
        if (index in _studyDeck.value.indices) {
            _currentCardIndex.value = index
            _isCardFlipped.value = false
        }
    }

    fun rateCurrentCard(rating: ReviewRating) {
        val deck = _studyDeck.value
        val index = _currentCardIndex.value
        if (index !in deck.indices) return
        val card = deck[index]
        rateFlashcardRecall(card, rating)
    }

    fun rateFlashcardRecall(card: VocabCard, rating: ReviewRating) {
        viewModelScope.launch {
            repository.processReview(card, rating)
            val currentStats = _sessionStats.value
            val isSuccess = rating != ReviewRating.AGAIN
            val updatedMistakes = if (!isSuccess) currentStats.mistakeCards + card else currentStats.mistakeCards

            _sessionStats.value = currentStats.copy(
                totalStudied = currentStats.totalStudied + 1,
                againCount = currentStats.againCount + if (rating == ReviewRating.AGAIN) 1 else 0,
                hardCount = currentStats.hardCount + if (rating == ReviewRating.HARD) 1 else 0,
                goodCount = currentStats.goodCount + if (rating == ReviewRating.GOOD) 1 else 0,
                easyCount = currentStats.easyCount + if (rating == ReviewRating.EASY) 1 else 0,
                xpEarned = currentStats.xpEarned + when (rating) {
                    ReviewRating.EASY -> 10; ReviewRating.GOOD -> 8; ReviewRating.HARD -> 5; ReviewRating.AGAIN -> 2
                },
                mistakeCards = updatedMistakes
            )

            if (_currentCardIndex.value < _studyDeck.value.size - 1) {
                _currentCardIndex.value++
                _isCardFlipped.value = false
            } else {
                _isSessionFinished.value = true
                stopAutoPlay()
            }
        }
    }

    fun restartStudySession() {
        _currentCardIndex.value = 0
        _isCardFlipped.value = false
        _isSessionFinished.value = false
        _sessionStats.value = SessionStudyStats()
    }

    fun restartMistakesOnly() {
        val mistakes = _sessionStats.value.mistakeCards
        if (mistakes.isNotEmpty()) {
            startStudySession(mistakes, _studyMode.value)
        }
    }

    // ------------------------------------------------------------------------
    // Card Updates & Custom Flashcards
    // ------------------------------------------------------------------------
    fun toggleBookmark(card: VocabCard) {
        viewModelScope.launch {
            repository.setBookmark(card.id, !card.isBookmarked)
        }
    }

    fun toggleBookmarkById(cardId: Long) {
        viewModelScope.launch {
            val card = allCards.value.firstOrNull { it.id == cardId }
            if (card != null) {
                repository.setBookmark(cardId, !card.isBookmarked)
            }
        }
    }

    fun updatePersonalNote(cardId: Long, note: String) {
        viewModelScope.launch {
            repository.updatePersonalNotes(cardId, note)
        }
    }

    fun updateCardTags(cardId: Long, tags: List<String>) {
        viewModelScope.launch {
            repository.updateCardTags(cardId, tags)
        }
    }

    fun addTagToCard(cardId: Long, tag: String) {
        viewModelScope.launch {
            repository.addTagToCard(cardId, tag)
        }
    }

    fun removeTagFromCard(cardId: Long, tag: String) {
        viewModelScope.launch {
            repository.removeTagFromCard(cardId, tag)
        }
    }

    fun addCustomFlashcard(
        kanji: String,
        reading: String,
        meaningBurmese: String,
        partOfSpeech: String = "",
        exampleSentence: String = "",
        exampleMeaningBurmese: String = "",
        personalNote: String = "",
        tags: String = ""
    ) {
        viewModelScope.launch {
            repository.addCustomCard(
                kanji = kanji,
                reading = reading,
                meaningBurmese = meaningBurmese,
                partOfSpeech = partOfSpeech,
                exampleSentence = exampleSentence,
                exampleSentenceBurmese = exampleMeaningBurmese,
                personalNote = personalNote,
                tags = tags
            )
        }
    }

    fun deleteCard(card: VocabCard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    fun updateCustomFlashcard(
        cardId: Long,
        kanji: String,
        reading: String,
        meaningBurmese: String,
        partOfSpeech: String = "",
        exampleSentence: String = "",
        exampleMeaningBurmese: String = "",
        personalNote: String = "",
        tags: String = ""
    ) {
        viewModelScope.launch {
            repository.updateCardDetails(
                cardId = cardId,
                kanji = kanji,
                reading = reading,
                meaningBurmese = meaningBurmese,
                partOfSpeech = partOfSpeech,
                example = exampleSentence,
                exampleBurmese = exampleMeaningBurmese,
                personalNote = personalNote,
                tags = tags
            )
        }
    }

    // ------------------------------------------------------------------------
    // User Profile & Preferences
    // ------------------------------------------------------------------------
    fun updateDailyGoal(newGoal: Int) {
        viewModelScope.launch {
            repository.updateDailyGoal(newGoal)
        }
    }

    fun updateProfile(name: String, dailyGoal: Int, targetJlpt: String, avatarIndex: Int, customAvatarUri: String?) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.updateUserProfile(
                current.copy(
                    name = name,
                    dailyGoal = dailyGoal,
                    targetJlptLevel = targetJlpt,
                    avatarIndex = avatarIndex,
                    customAvatarUri = customAvatarUri
                )
            )
        }
    }

    fun updateAvatar(avatarIndex: Int, customAvatarUri: String?) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.updateUserProfile(
                current.copy(
                    avatarIndex = avatarIndex,
                    customAvatarUri = customAvatarUri
                )
            )
        }
    }

    fun updateProfileSettings(
        displayName: String,
        targetJlptLevel: String,
        dailyGoal: Long,
        frontLang: String,
        autoPlayTts: Boolean,
        gestureStyle: String,
        shuffleMode: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            if (user != null) {
                firestoreSyncRepo.updateProfileSettings(
                    userId = user.uid,
                    displayName = displayName,
                    targetJlptLevel = targetJlptLevel,
                    dailyGoal = dailyGoal,
                    frontLang = frontLang,
                    autoPlayTts = autoPlayTts,
                    gestureStyle = gestureStyle,
                    shuffleMode = shuffleMode
                )
            }
        }
    }

    // ------------------------------------------------------------------------
    // Cloud Custom Cards & Decks
    // ------------------------------------------------------------------------
    fun addCustomCard(
        front: String,
        reading: String,
        back: String,
        cardType: String,
        category: String,
        deckName: String,
        exampleSentence: String,
        exampleMeaning: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            firestoreSyncRepo.addCustomFlashcard(
                userId = user?.uid,
                front = front,
                reading = reading,
                back = back,
                cardType = cardType,
                category = category,
                deckName = deckName,
                exampleSentence = exampleSentence,
                exampleMeaning = exampleMeaning
            )
        }
    }

    fun createCustomDeckFromSelection(deckName: String, cards: List<VocabCard>) {
        viewModelScope.launch {
            val user = currentUser.value
            cards.forEach { card ->
                firestoreSyncRepo.addCustomFlashcard(
                    userId = user?.uid,
                    front = card.kanji,
                    reading = card.reading,
                    back = card.meaningBurmese,
                    cardType = "Vocab",
                    category = "Deck",
                    deckName = deckName,
                    exampleSentence = card.exampleSentence,
                    exampleMeaning = card.exampleSentenceBurmese
                )
            }
        }
    }

    fun deleteDeck(deckName: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val cardsInDeck = cloudCustomCards.value.filter { it.deckName == deckName }
            cardsInDeck.forEach {
                firestoreSyncRepo.deleteCustomFlashcard(user?.uid, it.id)
            }
        }
    }

    fun deleteCustomCard(cardId: String) {
        viewModelScope.launch {
            val user = currentUser.value
            firestoreSyncRepo.deleteCustomFlashcard(user?.uid, cardId)
        }
    }

    suspend fun signInWithGoogle(): Result<Unit> {
        return Result.success(Unit)
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
        }
    }

    // ------------------------------------------------------------------------
    // TTS Audio & Voice
    // ------------------------------------------------------------------------
    fun speakJapanese(text: String, rate: Float? = null) {
        ttsHelper.speak(text, rate)
    }

    fun speakKanji(kanji: String, reading: String? = null, rate: Float? = null) {
        ttsHelper.speakKanji(kanji, reading, rate)
    }

    fun speakCard(card: VocabCard, preferPhonetic: Boolean? = null, rate: Float? = null) {
        ttsHelper.speakCard(card, preferPhonetic ?: voiceSettings.value.preferPhonetic, rate)
    }

    fun speakPhonetic(reading: String, rate: Float? = null) {
        ttsHelper.speakPhonetic(reading, rate)
    }

    fun speakSlow(text: String) {
        ttsHelper.speakSlow(text)
    }

    fun speakSentence(sentence: String, rate: Float? = null) {
        ttsHelper.speakSentence(sentence, rate)
    }

    fun toggleSpeechRate() {
        ttsHelper.toggleSpeechRate()
    }

    fun setSpeechRate(rate: Float) {
        ttsHelper.setSpeechRate(rate)
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    fun testVoicePronunciation() {
        ttsHelper.speak("こんにちは！日本語を勉強しましょう。")
    }

    // ------------------------------------------------------------------------
    // Theme & Display Controls
    // ------------------------------------------------------------------------
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themePrefs.setThemeMode(mode) }
    }

    fun setThemePalette(palette: ThemePalette) {
        viewModelScope.launch { themePrefs.setThemePalette(palette) }
    }

    fun setIconThemeStyle(style: IconThemeStyle) {
        viewModelScope.launch { themePrefs.setIconThemeStyle(style) }
    }

    fun setOledBlack(oled: Boolean) {
        viewModelScope.launch { themePrefs.setOledBlack(oled) }
    }

    fun setThemeAesthetic(aesthetic: ThemeAesthetic) {
        viewModelScope.launch { themePrefs.setThemeAesthetic(aesthetic) }
    }

    fun toggleDarkMode(currentIsDark: Boolean) {
        setThemeMode(if (currentIsDark) ThemeMode.LIGHT else ThemeMode.DARK)
    }

    fun setVoiceSpeechRate(rate: Float) {
        viewModelScope.launch { voicePrefs.setSpeechRate(rate) }
    }

    fun setVoicePitch(pitch: Float) {
        viewModelScope.launch { voicePrefs.setPitch(pitch) }
    }

    fun setVoicePreferPhonetic(prefer: Boolean) {
        viewModelScope.launch { voicePrefs.setPreferPhonetic(prefer) }
    }

    fun setVoiceAutoPlayFlip(autoPlay: Boolean) {
        viewModelScope.launch { voicePrefs.setAutoPlayFlip(autoPlay) }
    }

    fun setFlashcardFontScale(scale: Float) {
        _flashcardFontScale.value = scale.coerceIn(0.8f, 1.6f)
    }

    fun cycleFlashcardFontScale() {
        val current = _flashcardFontScale.value
        _flashcardFontScale.value = when {
            current < 1.0f -> 1.0f
            current < 1.25f -> 1.25f
            current < 1.5f -> 1.5f
            else -> 0.85f
        }
    }

    fun setStudyMode(mode: FlashcardStudyMode) {
        _studyMode.value = mode
    }

    fun toggleFurigana() {
        _showFurigana.value = !_showFurigana.value
    }

    fun setAutoPlaySpeed(seconds: Int) {
        _autoPlaySpeedSec.value = seconds.coerceIn(2, 10)
    }

    fun toggleAutoPlay() {
        if (_isAutoPlay.value) {
            stopAutoPlay()
        } else {
            startAutoPlay()
        }
    }

    private fun startAutoPlay() {
        _isAutoPlay.value = true
        autoPlayJob?.cancel()
        autoPlayJob = viewModelScope.launch {
            while (_isAutoPlay.value && !_isSessionFinished.value) {
                delay(_autoPlaySpeedSec.value * 1000L / 2)
                _isCardFlipped.value = true
                delay(_autoPlaySpeedSec.value * 1000L / 2)
                rateCurrentCard(ReviewRating.GOOD)
            }
        }
    }

    fun stopAutoPlay() {
        _isAutoPlay.value = false
        autoPlayJob?.cancel()
        autoPlayJob = null
    }

    // ------------------------------------------------------------------------
    // Filters & Queries
    // ------------------------------------------------------------------------
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilterTarget(target: SearchFilterTarget) {
        _searchFilterTarget.value = target
    }

    fun setFilter(filter: VocabFilterType) {
        _currentFilter.value = filter
    }

    fun selectLesson(lessonNumber: Int?) {
        _selectedLesson.value = lessonNumber
    }

    fun selectTag(tag: String?) {
        _selectedTag.value = tag
    }

    fun setStatsTimeRange(range: StatsTimeRange) {
        _statsTimeRange.value = range
    }

    // ------------------------------------------------------------------------
    // Notifications & Reminders
    // ------------------------------------------------------------------------
    fun updateReminderSettings(
        enabled: Boolean,
        hour: Int,
        minute: Int,
        soundEnabled: Boolean = true,
        quotesEnabled: Boolean = true,
        smartReminderEnabled: Boolean = true
    ) {
        viewModelScope.launch {
            reminderPrefs.updateSettings(
                isEnabled = enabled,
                reminderHour = hour,
                reminderMinute = minute,
                soundEnabled = soundEnabled,
                motivationalQuotesEnabled = quotesEnabled,
                isSmartReminderEnabled = smartReminderEnabled
            )
            ReminderScheduler.rescheduleFromPreferences(getApplication())
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            reminderPrefs.setEnabled(enabled)
            ReminderScheduler.rescheduleFromPreferences(getApplication())
        }
    }

    fun setSmartReminderEnabled(smart: Boolean) {
        viewModelScope.launch {
            reminderPrefs.setSmartReminderEnabled(smart)
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            reminderPrefs.setReminderTime(hour, minute)
            ReminderScheduler.rescheduleFromPreferences(getApplication())
        }
    }

    fun triggerTestReminder() {
        ReminderScheduler.sendImmediateTestReminder(getApplication())
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoPlay()
        ttsHelper.shutdown()
    }
}
