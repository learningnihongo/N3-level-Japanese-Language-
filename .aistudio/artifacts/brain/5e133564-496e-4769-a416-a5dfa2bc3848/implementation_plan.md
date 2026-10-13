# Profile Section Declutter & APK Release

Streamline the Profile screen by eliminating visual clutter and redundant charts while retaining essential user personalization, audio preferences, and achievements, followed by compiling an installable release APK.

### User Review & Critical Decisions

> [!IMPORTANT]
> Based on your confirmed choices in Phase 1:
> - **Profile Features**: Keep essential settings, audio controls, and achievements in a clean, uncluttered layout without overwhelming chart duplicates.
> - **APK Release**: Build a direct-installation APK signed with the debug keystore so it can be sideloaded immediately on Android devices without keystore configuration errors.

- **Confirmed Decision 1**: Retain the user identity header (name, avatar, level progress), quick metric strip, and the two clean tabs ("Settings & Audio" and "Badges & Progress").
- **Confirmed Decision 2**: Remove the bulky and redundant weekly mastery chart from the Profile achievements tab, allowing the trophy showcase and badges to shine without scrolling clutter.
- **Confirmed Decision 3**: Assemble and export an installable `.apk` file ready for device testing and installation.

---

### 1. Overview & Core Concept

- **What It Does**: Transforms the Profile screen into a calm, focused management hub. Users can personalize their study goals and avatar, adjust Japanese text-to-speech audio speed/pitch, configure daily study reminders, customize app appearance (Dark/Light/AMOLED mode & color palette), and inspect their unlocked JLPT N3 trophy badges.
- **Target Audience / Persona**: Burmese learners studying JLPT N3 vocabulary who want quick, effortless access to learning preferences and achievement milestones without dense visual noise.
- **Key Value**: Clean, uncluttered Zen aesthetic that respects user attention, making audio tuning, study reminder toggles, and badge inspection immediate and intuitive.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. *Profile Overview*: User navigates to the Profile tab. They see a clean header with their chosen avatar, JLPT target level (N3), daily goal (words/day), level badge, and a slim 4-metric ribbon (Quiz Streak, Mastered, Due Review, Total Words).
  2. *Settings & Audio Tab*: Access to high-utility controls:
     - Daily study reminder scheduler with exact time picker.
     - Japanese TTS voice settings with speech rate slider, pitch slider, and "Test Voice" button.
     - Visual theme selector (Dark mode, AMOLED black, theme palette).
     - Lightweight offline status indicator confirming 100% offline database readiness.
  3. *Badges & Achievements Tab*: Clean showcase of unlocked and locked trophies (Kanji Master, Speed Demon, Streak Titan, etc.) with touch dialogs explaining how to earn each badge.
  4. *Profile Editing*: Simple dialog to update nickname, target daily goal, and avatar (preset or photo picker).

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Japanese Zen & Modern Material 3 minimalism.
  - *Palette*: Off-white / dark background, Japanese Crimson (`#BC002D`) and Indigo accents, Mastered Green for achievements, Streak Orange for fire streaks.
  - *Typography*: Bold M3 typography with Japanese character touches and clean Burmese bilingual captions.
  - *Layout Density*: Spacious 8.dp/16.dp grid rhythm, eliminating multi-nested cards and extra chart layers.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Elimination of `WeeklyMasteryProgressChartCard` in Profile**
  - *Chosen Approach*: Remove the duplicate weekly review chart from the Profile section.
  - *Why*: The dedicated Stats screen already provides deep, comprehensive analytics (Donut mastery, daily bar charts, category radar, and quiz accuracy). Repeating a heavy interactive chart inside the Profile tab creates visual clutter and slows rendering.
  - *Alternative Considered*: Keeping a mini-chart. Rejected because the user specifically requested removing unnecessary clutter and keeping only useful features.

- **Decision 2: Direct Installable APK Build Target**
  - *Chosen Approach*: Execute `gradle :app:assembleDebug` and place the generated APK in an easily accessible location.
  - *Why*: Standard Android release builds without an uploaded cloud keystore fail with signing errors. Using the debug-signed configuration produces a fully functional, production-ready binary that installs instantly on physical Android devices.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        ProfileScreen.kt                                │
├────────────────────────────────────────────────────────────────────────┤
│  1. ProfileHeaderCard                                                  │
│     - Avatar (AsyncImage / Preset) + Edit Profile Dialog               │
│     - Name, JLPT Level (N3), Words/Day Goal                            │
│     - XP & Level Progress Bar (Level 1..N)                             │
│     - QuizStreakHeaderBadge                                            │
├────────────────────────────────────────────────────────────────────────┤
│  2. MetricRibbonItem Strip                                             │
│     - Streak (d) │ Mastered (count) │ Due (count) │ Total Words        │
├────────────────────────────────────────────────────────────────────────┤
│  3. Segmented Tab Selector                                             │
│     [ Settings & Audio ]                 [ Badges & Achievements ]     │
├────────────────────────────────────────────────────────────────────────┤
│  Tab 0: Settings & Audio            │  Tab 1: Badges & Achievements    │
│  - DailyReminderSettingsCard        │  - TrophyShowcaseCard (Badges)   │
│  - VoiceSettingsCard (TTS engine)   │  - BadgeDetailDialog             │
│  - AppThemeSettingsCard (M3 themes) │                                  │
│  - Offline Ready Status             │                                  │
└─────────────────────────────────────┴──────────────────────────────────┘
                                   │
                                   ▼
                      Gradle Build & Release Stage
          :app:assembleDebug -> app-debug.apk -> /apk/kanjikotoba-v1.0.apk
```

- **Interactive State Handlers**:
  - `vocabViewModel.updateProfile(...)`: Persists user name, daily word goal, target JLPT level, and avatar URI.
  - `vocabViewModel.setVoiceSpeechRate(...)` / `setVoicePitch(...)`: Dynamically updates Android TTS playback parameters.
  - `vocabViewModel.setReminderEnabled(...)` / `setReminderTime(...)`: Schedules WorkManager daily alarms.
  - `vocabViewModel.setThemeMode(...)`: Toggles dark/light/system theme dynamically.

- **Verification & Release Execution**:
  1. Update `ProfileScreen.kt` to remove the redundant chart and streamline the layout.
  2. Verify build compilation via `compile_applet`.
  3. Run Gradle task `:app:assembleDebug` to generate the APK package.
  4. Copy and verify APK existence and file size in the project output directory.
