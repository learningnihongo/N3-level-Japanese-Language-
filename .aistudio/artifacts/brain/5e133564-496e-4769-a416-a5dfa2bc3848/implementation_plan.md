# Mobile UI Streamlining, Past Exam Gestures Fix, and Quiz Arena Enhancement Plan

A comprehensive overhaul focusing on mobile-first ergonomics, decluttering verbose text across Home, Stats, and Profile screens, fixing gesture recognition in Past Exam flashcards, and upgrading the Quiz Arena with instant feedback and clean scorecards.

---

## 1. User Feedback & Confirmed Requirements

1. **Mobile Layout Density (Home, Stats, Profile)**:
   - Streamlined, clean mobile cards with focused essential KPIs and compact, readable Burmese/Japanese typography.
   - Eliminate redundant multi-line explanations, duplicate chart panels, and dense text clutter that degrades phone usability.
2. **Past Exam Flashcard Gestures**:
   - Fix gestures to provide responsive **horizontal swipe** for navigating between next and previous cards, combined with **tap to flip** the flashcard front/back.
3. **Quiz Arena Enhancement**:
   - Streamlined mobile quiz interface with **instant visual feedback** upon selecting an option (highlighting correct/incorrect states immediately), smooth question transitions, and a clean, motivating post-quiz scorecard.
4. **Home Section Cleanliness**:
   - Minimalist, distraction-free Home dashboard focusing on immediate study actions (Due Review, Past Exams, Daily Word) without UI-cluttering paragraph text.

---

## 2. Proposed Architecture & Component Changes

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Mobile Polish Plan                            │
├──────────────────┬──────────────────┬──────────────────┬───────────────┤
│  Home Screen     │  Stats Screen    │  Profile Screen  │  Past Exams   │
│  - Clean Hero    │  - Essential KPI │  - Minimal Card  │  - Fixed Drag │
│  - 4 Mode Tiles  │  - Mastery Bar   │  - Clean Ribbon  │  - Tap to Flip│
│  - Focused Word  │  - Unified Chart │  - Concise Text  │  - Smooth Nav │
└──────────────────┴──────────────────┴──────────────────┴───────────────┘
                                   │
                                   ▼
                       ┌───────────────────────┐
                       │   Quiz Arena Screen   │
                       │   - Dynamic Deck      │
                       │   - Instant Feedback  │
                       │   - Clean Scorecard   │
                       └───────────────────────┘
```

### Component 1: Fix Past Exam Flashcards Gesture Handling (`PastExamFlashcardView.kt`)
- **Current Issue**: Card `Box` chains `.then(dragModifier).then(tapModifier)` which causes pointer event cancellation between `detectHorizontalDragGestures` and `detectTapGestures`, while parent `verticalScroll` competes for drag gestures.
- **Solution**:
  - Implement unified horizontal dragging with `Modifier.draggable` (or unified `pointerInput` with drag threshold) so horizontal swipes cleanly trigger `nextCard()` / `prevCard()` with spring offset reset.
  - Pair with `clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { isFlipped = !isFlipped }` ensuring reliable tap-to-flip functionality.
  - Set default gesture mode to **Slide Carousel (Next/Prev swipe)** with clear, non-intrusive gesture hints.

### Component 2: Declutter & Streamline Home Screen (`HomeScreen.kt`)
- **Current Issue**: Excessive stacked cards (Hero greeting, SRS ring, goal tuner, 4 study modes, Bento cards, past exams banner, D3 dashboard banner, word of the day, proverb, collocations, 100 lessons, categories) with dense subtitles.
- **Solution**:
  - **Hero Learning Card**: Keep the hanko seal and concise greeting, sleek streak pill, and single prominent "Start Daily Study" CTA.
  - **Quick Study Modes Grid**: 4 clean, compact tiles (Past Exams, 3D Flashcards, Collocations, Kanji Goal) with single-line labels and clear icons.
  - **Focused Daily Card**: Clean word presentation (Kanji, Furigana, concise Burmese meaning) without paragraph clutter.
  - Clean spacing conforming to 8.dp mobile grid.

### Component 3: Declutter & Focus Statistics Screen (`StatisticsScreen.kt`)
- **Current Issue**: Stacks 5 heavy cards with duplicate visualizations (Hero banner, tab switcher, Donut chart, Daily bar chart, Visual summary dashboard with radar and stacked bars, Quiz accuracy curve).
- **Solution**:
  - **Sleek Metric Grid**: 4 essential metrics (Mastered, Accuracy %, Streak, Due Review) in a compact, scannable ribbon.
  - **Mastery Progress Card**: Clean progress bar and 3 simple counts (Mastered, Reviewing, Learning).
  - **Unified Activity & Accuracy Chart**: Keep the primary interactive weekly performance chart and remove the redundant multi-layer radar/stacked category dump.
  - Direct quick action to jump into Due Review or Quiz History.

### Component 4: Streamline Profile Screen (`ProfileScreen.kt`)
- **Current Issue**: Bulky multi-paragraph cards, heavy offline storage description, and dense subtitles.
- **Solution**:
  - **Student Header**: Compact avatar, Level, XP bar, and Target JLPT badge.
  - **Sleek Stats Ribbon**: 4 clean pills for Streak, Mastered, Due, and Total.
  - **Concise Settings Card**: Clean toggles for Theme Mode, Aesthetic (Material 3 vs Washi Minimalist), and Audio settings without paragraphs of explanatory text.
  - Remove redundant 100% offline storage text card.

### Component 5: Elevate Quiz Arena (`QuizScreen.kt`)
- **Current Issue**: Hardcoded to only 10 fixed questions from SeedData, no interactive feedback on tap, and basic scorecard.
- **Solution**:
  - **Dynamic Question Pool**: Generate questions from full active vocabulary (Kanji, Reading, Burmese translation).
  - **Instant Feedback**: When the user taps an option:
    - Instantly lock options to prevent double-tap.
    - Highlight selected choice in green (if correct) or red (if wrong).
    - If wrong, simultaneously highlight the correct answer in green with a subtle checkmark.
    - Auto-advance to the next question with a smooth transition (or Continue button).
  - **Clean Scorecard**:
    - Circular score percentage badge with celebratory icon.
    - Stats breakdown: Total Questions, Correct, Accuracy %, and XP earned.
    - Quick actions: "Retake Quiz" and "Back to Study".

---

## 3. Verification & Testing Plan

1. **Compilation Check**:
   - Run `compile_applet` to ensure zero compilation or syntax errors.
2. **Gesture Verification**:
   - Verify that horizontal swipe moves between previous and next cards in `PastExamFlashcardView`.
   - Verify that tap reliably flips the flashcard between question and answer.
3. **Mobile Layout Verification**:
   - Verify that Home, Stats, and Profile screens fit comfortably on phone aspect ratios without text overflow or vertical clutter.
4. **Quiz Flow Verification**:
   - Verify quiz questions load dynamically, option selection provides instant feedback, and the finish screen displays the clean scorecard.
