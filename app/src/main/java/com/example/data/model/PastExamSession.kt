package com.example.data.model

data class PastExamSession(
    val code: String,             // e.g. "2014/12"
    val lessonNumber: Int,        // e.g. 201
    val titleJapanese: String,    // e.g. "2014/12 日本語能力試験 N3"
    val titleBurmese: String,     // e.g. "၂၀၁၄ ဒီဇင်ဘာ N3 စာမေးပွဲ မေးခွန်းဟောင်း"
    val wordCount: Int
)

object PastExamData {
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

    const val TOTAL_PAST_EXAM_WORDS = 1357
    val PAST_EXAM_LESSON_RANGE = 201..220

    fun isPastExamCard(card: VocabCard): Boolean {
        return card.lessonNumber in PAST_EXAM_LESSON_RANGE || card.tags.contains("မေးခွန်းဟောင်း")
    }

    fun hasKanji(text: String): Boolean {
        return text.any { ch -> (ch in '\u4E00'..'\u9FFF') || (ch in '\u3400'..'\u4DBF') }
    }
}
