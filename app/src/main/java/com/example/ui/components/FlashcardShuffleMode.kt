package com.example.ui.components

import com.example.data.model.VocabCard
import kotlin.random.Random

enum class FlashcardShuffleMode(
    val title: String,
    val burmeseLabel: String,
    val description: String
) {
    SMART_SRS(
        title = "Smart SRS Shuffle",
        burmeseLabel = "⚡ Smart SRS (ခက်ခဲကတ် ဦးစားပေး)",
        description = "မကြာခဏ မှားတတ်သောကတ်များ၊ အသစ်များနှင့် မှတ်သားထားသော (Bookmarked) မေးခွန်းများကို ရှေ့ဆုံးတွင် ဦးစားပေး စီစဉ်ပေးပါသည်။"
    ),
    TRUE_RANDOM(
        title = "Random Shuffle",
        burmeseLabel = "🎲 Random (ကျပန်း မွှေနှောက်)",
        description = "လက်ရှိ ရွေးချယ်ထားသော မေးခွန်းအားလုံးကို ကျပန်းစနစ်ဖြင့် လုံးဝ ရောနှောစီစဉ်ပေးပါသည်။"
    ),
    DIFFICULT_ONLY(
        title = "Difficult Only Drill",
        burmeseLabel = "🔥 ခက်ခဲသော မေးခွန်းများသာ",
        description = "မှားဖူးသော မေးခွန်းများနှင့် လေ့လာဆဲကတ်များကိုသာ သီးသန့် ရွေးထုတ်၍ လေ့ကျင့်ပေးပါသည်။"
    ),
    EXAM_CHRONO(
        title = "Exam Order",
        burmeseLabel = "📋 မေးခွန်းစဉ် မူလအတိုင်း",
        description = "စာမေးပွဲ နှစ်အလိုက်၊ မေးခွန်းနံပါတ်အလိုက် မူလအတိုင်း စဉ်ဆက်မပြတ် လေ့လာပါမည်။"
    );

    companion object {
        fun applyShuffle(cards: List<VocabCard>, mode: FlashcardShuffleMode): List<VocabCard> {
            return when (mode) {
                SMART_SRS -> {
                    // Smart weighting algorithm:
                    // Priority is higher for bookmarked, unmastered (masteryLevel <= 1), and high error count
                    cards.sortedByDescending { card ->
                        val bookmarkScore = if (card.isBookmarked) 120 else 0
                        val masteryScore = when (card.masteryLevel) {
                            0 -> 100 // New
                            1 -> 80  // Learning
                            2 -> 30  // Review
                            else -> 10 // Mastered
                        }
                        val errorScore = card.timesIncorrect * 40
                        val successReduction = card.timesCorrect * 15
                        val randomJitter = Random.nextInt(-15, 15)
                        bookmarkScore + masteryScore + errorScore - successReduction + randomJitter
                    }
                }
                TRUE_RANDOM -> {
                    cards.shuffled()
                }
                DIFFICULT_ONLY -> {
                    val difficultCards = cards.filter { 
                        it.isBookmarked || it.timesIncorrect > 0 || it.masteryLevel <= 1 
                    }
                    if (difficultCards.isNotEmpty()) {
                        difficultCards.shuffled()
                    } else {
                        cards.shuffled()
                    }
                }
                EXAM_CHRONO -> {
                    cards
                }
            }
        }
    }
}
