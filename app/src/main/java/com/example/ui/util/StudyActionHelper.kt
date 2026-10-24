package com.example.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.VocabCard
import java.net.URLEncoder

object StudyActionHelper {
    private var sharedTts: TtsHelper? = null

    fun isKanji(c: Char): Boolean {
        val code = c.code
        return (code in 19968..40959) || (code in 13312..19903)
    }

    fun copyToClipboard(context: Context, text: String, label: String = "KanjiKotoba", toastMessage: String? = null) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)
        if (!toastMessage.isNullOrBlank()) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun copyCardDetails(context: Context, card: VocabCard) {
        val sb = StringBuilder()
        sb.append(card.kanji).append(" (").append(card.reading).append(")\n")
        sb.append("အဓိပ္ပာယ်: ").append(card.meaningBurmese).append("\n")
        if (card.exampleSentence.isNotBlank()) {
            sb.append("ဥပမာ: ").append(card.exampleSentence).append("\n")
            if (card.exampleSentenceBurmese.isNotBlank()) {
                sb.append("  ").append(card.exampleSentenceBurmese).append("\n")
            }
        }
        copyToClipboard(context, sb.toString().trim(), "KanjiKotobaCard", "${card.kanji} အချက်အလက်အားလုံးကို Copy ကူးပြီးပါပြီ ✓")
    }

    fun copyFullCard(context: Context, card: VocabCard) {
        copyCardDetails(context, card)
    }

    fun openGoogleTranslate(context: Context, text: String, sourceLang: String = "ja", targetLang: String = "my") {
        if (text.isBlank()) return
        val trimmed = text.trim()
        try {
            copyToClipboard(context, trimmed, "Japanese Text")
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, trimmed)
                setPackage("com.google.android.apps.translate")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                Toast.makeText(context, "'$trimmed' ကို Copy ကူးပြီး Google Translate ဖွင့်နေပါသည် ✓", Toast.LENGTH_SHORT).show()
                context.startActivity(intent)
            } else {
                val webUrl = "https://translate.google.com/?sl=$sourceLang&tl=$targetLang&text=${URLEncoder.encode(trimmed, "UTF-8")}&op=translate"
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                Toast.makeText(context, "'$trimmed' ကို Copy ကူးပြီး Google Translate ဖွင့်နေပါသည် ✓", Toast.LENGTH_SHORT).show()
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Google Translate ဖွင့်၍မရပါ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openJisho(context: Context, query: String, isKanjiLookup: Boolean = false) {
        if (query.isBlank()) return
        try {
            var searchStr = query.trim()
            if (isKanjiLookup && searchStr.length == 1 && isKanji(searchStr[0])) {
                searchStr = "$searchStr #kanji"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jisho.org/search/${URLEncoder.encode(searchStr, "UTF-8")}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "jisho.org ဖွင့်၍မရပါ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun findRelatedCompounds(kanjiChar: Char, allCards: List<VocabCard>): List<VocabCard> {
        val s = kanjiChar.toString()
        return allCards.filter { it.kanji.contains(s) }
    }

    fun speakText(context: Context, text: String, rate: Float? = null) {
        if (text.isBlank()) return
        if (sharedTts == null) {
            sharedTts = TtsHelper(context.applicationContext)
        }
        sharedTts?.speak(text, rate)
    }

    fun speakKanji(context: Context, kanji: String, reading: String? = null, rate: Float? = null) {
        if (kanji.isBlank()) return
        if (sharedTts == null) {
            sharedTts = TtsHelper(context.applicationContext)
        }
        sharedTts?.speakKanji(kanji, reading, rate)
    }

    fun speakSlow(context: Context, text: String) {
        if (text.isBlank()) return
        if (sharedTts == null) {
            sharedTts = TtsHelper(context.applicationContext)
        }
        sharedTts?.speakSlow(text)
    }

    fun speakSentence(context: Context, sentence: String) {
        if (sentence.isBlank()) return
        if (sharedTts == null) {
            sharedTts = TtsHelper(context.applicationContext)
        }
        sharedTts?.speakSentence(sentence)
    }

    fun extractKanjiCharacters(text: String): List<Char> {
        return text.filter { isKanji(it) }.toList().distinct()
    }
}
