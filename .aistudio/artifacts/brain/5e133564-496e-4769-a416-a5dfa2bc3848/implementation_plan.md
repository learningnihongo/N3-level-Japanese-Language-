# Streamlined Zen Profile Screen Redesign Plan

## Overview
Based on user feedback ("ရူပ်တဲ့ UI ကို မကြိုက်ဘူး" / dislikes cluttered UI), this plan streamlines the **Profile Screen** into an uncluttered, modern Japanese Zen-inspired single-surface experience. Instead of fragmented cards, duplicate counter tiles, and heavy nested boxes, the screen is consolidated into:
1. **Zen Single-Card Profile Header**: Clean avatar, learner name, target JLPT badge, and an integrated subtle XP level progress pill.
2. **Consolidated Essential Stats Row**: Combines duplicate counters into 3 core metrics (Streak 🔥, Mastered ⭐, Total Words 📚) in a single horizontal strip.
3. **Organized, Minimalist Settings Groups**: Grouped lists with standard M3 list items, subtle dividers, and clean toggle rows for Reminders, Voice Audio, and Theme display.
4. **Clean Dialogs & Popups**: Simple bottom sheets / dialogs for avatar customization and level details that keep the primary screen calm and distraction-free.

---

## Proposed Changes & User Experience

### 1. Unified Single Profile Card
- **Before:** Separate Profile Header Card, separate bulky XP Level Card with complex multiple progress bars and text labels, plus a separate 4-tile learning overview.
- **After:** 
  - A clean, unified card with generous padding (Japanese Zen aesthetic).
  - Avatar with subtle ring border and quick edit indicator.
  - User name, JLPT level badge (`N3`), and rank title.
  - A slim, elegant inline progress bar with a clean counter: `Lv. X • XXX / 150 XP` without visual clutter.

### 2. Consolidated Essential Stats Strip
- Consolidates redundant cards into 3 key metrics in a neat, lightweight row:
  - **Streak**: Current day streak with flame icon.
  - **Mastered**: Learned vocabulary count.
  - **Library**: Total available JLPT N3 vocabulary.
- Eliminates visual noise and repetitive card borders.

### 3. Clean Grouped Settings List
- Rather than overwhelming multi-tab cards with heavy nested containers:
  - Clean grouped categories:
    - **Study Preferences:** Daily Goal & Notification Reminder.
    - **Audio & Pronunciation:** Speech rate, pitch, and voice sample.
    - **Appearance:** Theme toggle (Light / Dark / System).
    - **Achievements & Badges:** Compact badge showcase with a tap-to-expand sheet.
  - Uses standard Material 3 `ListItem` styling with subtle leading icons and crisp trailing controls (Switches, Chevron arrows).

### 4. Code & Architecture Maintenance
- Updates `ProfileScreen.kt` in `app/src/main/java/com/example/ui/screens/ProfileScreen.kt`.
- Keeps full compatibility with `VocabViewModel` (all state flows, notification updates, audio settings, and custom avatar URIs remain fully functional).
- Ensures accessibility standards (minimum 48dp touch targets, semantic descriptions).

---

## Verification Plan
1. **Compilation Check:** Run `compile_applet` to verify clean build without syntax or dependency errors.
2. **UI Test & Visual Polish:** Verify layout in Android preview:
   - Check light and dark theme contrast.
   - Verify that all settings toggles (Voice, Reminders, Theme, Avatar picker) continue to work seamlessly.
   - Confirm touch targets meet 48dp minimum.
