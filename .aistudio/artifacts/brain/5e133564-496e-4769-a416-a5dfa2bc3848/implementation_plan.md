# Implementation Plan: Daily Quiz Streak Tracking System & Profile Streak Badges

Implement a robust daily quiz streak tracking system that calculates consecutive days of completed quizzes directly from the Room database history, incorporates a grace period until midnight the next day before resetting, and displays a prominent quiz streak badge in the Profile header and the trophy showcase.

## User Preferences & Requirements
1. **Placement**: Prominent streak badge in the Profile header and dedicated streak badges in the badge showcase.
2. **Streak Calculation**: Real-time consecutive days calculated from `quiz_histories` in the Room SQLite database.
3. **Grace Period**: Streak remains active with a grace period until midnight of the current day if a quiz was completed yesterday (giving the user until 11:59 PM today to complete today's quiz before dropping to 0).

---

## Architecture & Proposed Changes

### 1. Data Layer: Real-Time Consecutive Quiz Streak Calculator
- **File**: `app/src/main/java/com/example/data/repository/VocabRepository.kt` & `QuizDao.kt`
- Add a reactive flow in `VocabRepository` (and `QuizDao`) observing `getAllQuizHistories()`.
- Implement `calculateQuizStreak(histories: List<QuizHistory>): QuizStreakInfo`:
  - Group quiz timestamps by normalized calendar date (`YYYY-MM-DD` using the local calendar/timezone).
  - Determine if a quiz was taken **today** (`Day 0`) and **yesterday** (`Day -1`).
  - **Streak Rules**:
    - If today has a quiz: count consecutive days backwards starting from today (today, yesterday, 2 days ago...).
    - If today has NO quiz but yesterday has a quiz: **Grace Period active**. The consecutive streak through yesterday is retained! Display status indicating quiz needed today before midnight to maintain the streak.
    - If neither today nor yesterday has a quiz: streak resets to 0.
  - Calculate `currentStreak`, `bestStreak`, `isQuizDoneToday`, `daysUntilNextMilestone`, and next badge goal (1, 3, 7, 14, 30, 60 days).
  - Also sync `bestStreak` and `currentStreak` into `UserProfile` when updated.

### 2. ViewModel Layer: Expose Quiz Streak & Streak Badges
- **File**: `app/src/main/java/com/example/ui/viewmodel/VocabViewModel.kt`
- Expose `quizStreakInfo: StateFlow<QuizStreakInfo>` derived reactively from quiz history.
- Update `allBadges: StateFlow<List<Badge>>` so the `STREAK` category badges (e.g., *Spark of Kanji (1d)*, *7-Day Streak Warrior (7d)*, *Fortnight Samurai (14d)*, *Monthly Master (30d)*, *Centurion Streak (100d)*) use the real-time quiz streak value calculated from `QuizHistory`.
- Provide helper methods or status details (e.g., streak fire multiplier, motivational Japanese proverbs, grace period countdown).

### 3. UI Layer: Prominent Header Badge & Showcase in Profile Screen
- **File**: `app/src/main/java/com/example/ui/screens/ProfileScreen.kt`
  - **Profile Header Card**:
    - Add a prominent, glowing **Quiz Streak Badge** right in the profile header (next to the user's name/level info).
    - Features:
      - Animated flame icon 🔥 with pulsating glow when streak is active.
      - Dynamic streak counter with tier colors (Bronze, Silver, Gold, Flame Orange).
      - Status pill: "⚡ Streak Active · Done Today" or "⏳ Grace Period · Complete a quiz today".
      - Tap interaction to view streak details dialog with streak history calendar, days to next badge, and motivational quote.
  - **Stat Ribbon**:
    - Keep the 4-metric ribbon updated with the verified real-time quiz streak.
  - **Trophy Showcase (Badges & Progress segment)**:
    - Showcase all unlocked and in-progress quiz streak badges with real-time percentage progress bars, XP rewards, and achievement dates.
    - Highlight the user's next milestone badge.

---

## Verification Plan
1. **Compilation Check**: Run `compile_applet` to ensure full build success without errors.
2. **Streak Logic Verification**:
   - Verify calculation when no quizzes exist (streak = 0).
   - Verify calculation when quiz was completed today (streak = 1+).
   - Verify calculation when quiz was completed yesterday and not yet today (streak preserved in grace period).
   - Verify streak reset when last quiz was 2+ days ago.
3. **UI Verification**:
   - Check Profile screen header displays the prominent streak badge cleanly.
   - Check Trophy Showcase segment correctly shows streak badges updating with the real-time streak.
