package com.example.data.util

import com.example.data.model.VocabCard

data class HomophoneEntry(
    val kanji: String,
    val reading: String,
    val burmeseMeaning: String,
    val nuance: String = "",
    val example: String = "",
    val exampleBurmese: String = ""
)

data class HomophoneGroup(
    val reading: String,
    val title: String,
    val entries: List<HomophoneEntry>
)

object HomophoneData {
    val JLPT_N3_HOMOPHONES: List<HomophoneGroup> = listOf(
        HomophoneGroup(
            reading = "はかる",
            title = "はかる (測る / 量る / 計る / 図る)",
            entries = listOf(
                HomophoneEntry("測る", "はかる", "တိုင်းတာသည် (အလျား၊ အနက်၊ အပူချိန်)", "အရှည်၊ အမြင့်၊ အပူချိန် တိုင်းခြင်း", "長さを測る", "အလျားကို တိုင်းသည်"),
                HomophoneEntry("量る", "はかる", "ချိန်တွယ်သည် (အလေးချိန်၊ ထုထည်)", "အလေးချိန်၊ ပမာဏ ချိန်တွယ်ခြင်း", "体重を量る", "ကိုယ်အလေးချိန် ချိန်သည်"),
                HomophoneEntry("計る", "はかる", "တွက်ချက်သည် / အချိန်တိုင်းသည်", "အချိန်၊ အရေအတွက် မှတ်သားတိုင်းတာခြင်း", "時間を計る", "အချိန်တိုင်းသည်"),
                HomophoneEntry("図る", "はかる", "ကြံစည်ကြိုးပမ်းသည်", "ရည်မှန်းချက် အောင်မြင်အောင် ဆောင်ရွက်ခြင်း", "解決を図る", "ပြဿနာဖြေရှင်းနိုင်ရန် ကြိုးပမ်းသည်")
            )
        ),
        HomophoneGroup(
            reading = "あう",
            title = "あう (会う / 合う / 遭う)",
            entries = listOf(
                HomophoneEntry("会う", "あう", "တွေ့ဆုံသည် (လူနှင့်)", "လူအချင်းချင်း တွေ့ဆုံခြင်း", "友達に会う", "သူငယ်ချင်းနှင့် တွေ့ဆုံသည်"),
                HomophoneEntry("合う", "あう", "ကိုက်ညီသည် / သင့်တော်သည်", "အရွယ်အစား၊ အဖြေ ကိုက်ညီခြင်း", "サイズが合う", "ဆိုဒ်ကွက်တိကျသည်"),
                HomophoneEntry("遭う", "あう", "ကြုံတွေ့ရသည် (မတော်တဆ/အခက်အခဲ)", "မကောင်းသော အဖြစ်အပျက် ကြုံရခြင်း", "事故に遭う", "မတော်တဆမှုနှင့် ကြုံရသည်")
            )
        ),
        HomophoneGroup(
            reading = "あける",
            title = "あける (開ける / 空ける / 明ける)",
            entries = listOf(
                HomophoneEntry("開ける", "あける", "ဖွင့်သည် (တံခါး၊ ပြတင်းပေါက်)", "ပိတ်ထားသောအရာကို ဖွင့်လှစ်ခြင်း", "ドアを開ける", "တံခါးဖွင့်သည်"),
                HomophoneEntry("空ける", "あける", "နေရာလစ်လပ်စေသည် / အချိန်ဖယ်ထားသည်", "နေရာ သို့မဟုတ် အချိန် နေရာလွတ်လုပ်ခြင်း", "席を空ける", "ထိုင်ခုံနေရာဖယ်ပေးသည်"),
                HomophoneEntry("明ける", "あける", "လင်းလာသည် / ကုန်ဆုံးသည်", "ညလင်းခြင်း၊ နှစ်သစ်ကူးခြင်း", "夜が明ける", "မိုးလင်းလာသည်")
            )
        ),
        HomophoneGroup(
            reading = "あやまる",
            title = "あやまる (謝る / 誤る)",
            entries = listOf(
                HomophoneEntry("謝る", "あやまる", "တောင်းပန်သည်", "အမှားဝန်ခံ တောင်းပန်ခြင်း", "素直に謝る", "ရိုးသားစွာ တောင်းပန်သည်"),
                HomophoneEntry("誤る", "あやまる", "မှားယွင်းသည် / ချွတ်ချော်သည်", "ဆုံးဖြတ်ချက်၊ လမ်းစဉ် မှားခြင်း", "判断を誤る", "ဆုံးဖြတ်ချက် မှားယွင်းသည်")
            )
        ),
        HomophoneGroup(
            reading = "おさめる",
            title = "おさめる (治める / 納める / 修める)",
            entries = listOf(
                HomophoneEntry("治める", "おさめる", "အုပ်ချုပ်သည် / ငြိမ်းချမ်းစေသည်", "နိုင်ငံအုပ်ချုပ်ခြင်း၊ ရောဂါငြိမ်းခြင်း", "国を治める", "တိုင်းပြည်ကို အုပ်ချုပ်သည်"),
                HomophoneEntry("納める", "おさめる", "ပေးသွင်းသည် / သိမ်းဆည်းသည်", "အခွန်၊ အခကြေးငွေ ပေးသွင်းခြင်း", "税金を納める", "အခွန်ပေးဆောင်သည်"),
                HomophoneEntry("修める", "おさめる", "ဆည်းပူးလေ့လာသည်", "ပညာ၊ အတတ်ပညာ ဆည်းပူးခြင်း", "学問を修める", "ပညာဆည်းပူးသည်")
            )
        ),
        HomophoneGroup(
            reading = "かえる",
            title = "かえる (変える / 換える / 代える / 替える)",
            entries = listOf(
                HomophoneEntry("変える", "かえる", "ပြောင်းလဲသည်", "ပုံစံ၊ သဘောထား ပြောင်းလဲခြင်း", "予定を変える", "အစီအစဉ်ကို ပြောင်းလဲသည်"),
                HomophoneEntry("換える", "かえる", "လဲလှယ်သည်", "ပစ္စည်းတစ်ခုနှင့်တစ်ခု လဲလှယ်ခြင်း", "空気を換える", "လေကောင်းလေသန့်လဲလှယ်သည်"),
                HomophoneEntry("代える", "かえる", "အစားထိုးသည်", "ကိုယ်စားပြု အစားထိုးခြင်း", "親に代わって挨拶する", "မိဘကိုယ်စား နှုတ်ဆက်သည်"),
                HomophoneEntry("替える", "かえる", "လဲသည် (အသစ်နှင့်)", "အသစ်လဲလှယ်တပ်ဆင်ခြင်း", "電池を替える", "ဓာတ်ခဲလဲသည်")
            )
        ),
        HomophoneGroup(
            reading = "きく",
            title = "きく (聞く / 聴く / 効く / 利く)",
            entries = listOf(
                HomophoneEntry("聞く", "きく", "ကြားသည် / မေးသည်", "အသံကြားခြင်း၊ စကားမေးခြင်း", "話を聞く", "စကားနားထောင်သည်"),
                HomophoneEntry("聴く", "きく", "သေချာနားထောင်သည်", "တေးဂီတ၊ ဟောပြောချက် အာရုံစိုက်နားထောင်ခြင်း", "音楽を聴く", "ဂီတကို နားဆင်သည်"),
                HomophoneEntry("効く", "きく", "အာနိသင်ရှိသည် / ထိရောက်သည်", "ဆေးဝါး၊ နည်းလမ်း ထိရောက်ခြင်း", "薬が効く", "ဆေးအာနိသင်ပြသည်"),
                HomophoneEntry("利く", "きく", "လုပ်ဆောင်နိုင်စွမ်းရှိသည် / အလုပ်လုပ်သည်", "အင်္ဂါအစိတ်အပိုင်း၊ စက်ပစ္စည်း လုပ်ဆောင်နိုင်ခြင်း", "ブレーキが利く", "ဘရိတ်မိသည်")
            )
        ),
        HomophoneGroup(
            reading = "つく",
            title = "つく (着く / 付く / 就く / 点く)",
            entries = listOf(
                HomophoneEntry("着く", "つく", "ရောက်ရှိသည်", "နေရာဒေသသို့ ဆိုက်ရောက်ခြင်း", "駅に着く", "ဘူတာသို့ ရောက်သည်"),
                HomophoneEntry("付く", "つく", "ကပ်ငြိသည် / ပါဝင်သည်", "အရာဝတ္ထု ပူးတွဲကပ်ခြင်း", "火が付く", "မီးစွဲကပ်သည်"),
                HomophoneEntry("就く", "つく", "ရာထူး/တာဝန် စတင်ထမ်းဆောင်သည်", "အလုပ်အကိုင်၊ ရာထူးရယူခြင်း", "仕事に就く", "အလုပ်စတင်ဝင်ရောက်သည်"),
                HomophoneEntry("点く", "つく", "မီးလင်းသည်", "မီးခလုတ်ဖွင့်၍ မီးလင်းခြင်း", "明かりが点く", "မီးရောင်လင်းသည်")
            )
        ),
        HomophoneGroup(
            reading = "とる",
            title = "とる (取る / 撮る / 採る / 捕る)",
            entries = listOf(
                HomophoneEntry("取る", "とる", "ယူသည် / ကိုင်တွယ်သည်", "လက်ဖြင့် ယူခြင်း", "手に取る", "လက်ဖြင့် ကိုင်ယူသည်"),
                HomophoneEntry("撮る", "とる", "ဓာတ်ပုံ/ရုပ်ရှင် ရိုက်သည်", "ကင်မရာဖြင့် ရိုက်ကူးခြင်း", "写真を撮る", "ဓာတ်ပုံရိုက်သည်"),
                HomophoneEntry("採る", "とる", "ရွေးချယ်ခန့်ထားသည် / ခူးယူသည်", "လူရွေးချယ်ခြင်း၊ အသီးခူးခြင်း", "新入社員を採る", "ဝန်ထမ်းသစ် ခန့်ထားသည်"),
                HomophoneEntry("捕る", "とる", "ဖမ်းဆီးသည်", "ငါး၊ သားကောင် ဖမ်းခြင်း", "魚を捕る", "ငါးဖမ်းသည်")
            )
        )
    )

    fun findCuratedGroup(card: VocabCard): HomophoneGroup? {
        return JLPT_N3_HOMOPHONES.firstOrNull { group ->
            group.entries.any { it.kanji == card.kanji || it.reading == card.reading }
        }
    }

    fun formatComparisonText(group: HomophoneGroup): String {
        val sb = StringBuilder()
        sb.append("💡 同音語・ဆင်တူရိုးမှား နှိုင်းယှဉ်ချက် 【${group.reading}】:\n")
        group.entries.forEach { entry ->
            sb.append("• ${entry.kanji} (${entry.reading}): ${entry.burmeseMeaning}")
            if (entry.nuance.isNotBlank()) {
                sb.append(" 「${entry.nuance}」")
            }
            if (entry.example.isNotBlank()) {
                sb.append("\n   ဥပမာ: ${entry.example} → ${entry.exampleBurmese}")
            }
            sb.append("\n")
        }
        return sb.toString().trim()
    }
}
