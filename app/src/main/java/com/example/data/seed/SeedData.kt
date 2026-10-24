package com.example.data.seed

import com.example.data.model.KanjiExample
import com.example.data.model.KanjiItem
import com.example.data.model.KotobaItem

object SeedData {
    val kanjiList = listOf(
        KanjiItem(
            id = 1,
            kanji = "政",
            onyomi = "セイ, ショウ",
            kunyomi = "まつりごと",
            meaningBurmese = "နိုင်ငံရေး၊ အုပ်ချုပ်ရေး",
            meaningEnglish = "Politics, Government",
            strokeCount = 9,
            examples = listOf(
                KanjiExample("政治", "せいじ", "နိုင်ငံရေး"),
                KanjiExample("政府", "せいふ", "အစိုးရ"),
                KanjiExample("政策", "せいさく", "မူဝါဒ")
            )
        ),
        KanjiItem(
            id = 2,
            kanji = "治",
            onyomi = "ジ, チ",
            kunyomi = "おさ・める, なお・る",
            meaningBurmese = "ကုသသည်၊ အုပ်ချုပ်သည်၊ ပျောက်ကင်းသည်",
            meaningEnglish = "Reign, Cure, Heal",
            strokeCount = 8,
            examples = listOf(
                KanjiExample("治療", "ちりょう", "ဆေးကုသမှု"),
                KanjiExample("治る", "なおる", "ရောဂါပျောက်ကင်းသည်"),
                KanjiExample("治安", "ちあん", "တရားဥပဒေစိုးမိုးရေး/အေးချမ်းသာယာမှု")
            )
        ),
        KanjiItem(
            id = 3,
            kanji = "経",
            onyomi = "ケイ, キョウ",
            kunyomi = "へ・る, たていと",
            meaningBurmese = "စီးပွားရေး၊ ဖြတ်သန်းသည်",
            meaningEnglish = "Pass through, Economy",
            strokeCount = 11,
            examples = listOf(
                KanjiExample("経済", "けいざい", "စီးပွားရေး"),
                KanjiExample("経験", "けいけん", "အတွေ့အကြုံ"),
                KanjiExample("経営", "けいえい", "စီးပွားရေးစီမံခန့်ခွဲမှု")
            )
        ),
        KanjiItem(
            id = 4,
            kanji = "済",
            onyomi = "サイ, ザイ",
            kunyomi = "す・む, す・ます",
            meaningBurmese = "ပြီးဆုံးသည်၊ အဆင်ပြေသည်",
            meaningEnglish = "Settle, Relieve, Finish",
            strokeCount = 11,
            examples = listOf(
                KanjiExample("経済学", "けいざいがく", "ဘောဂဗေဒ"),
                KanjiExample("返済", "へんさい", "ကြွေးပြန်ဆပ်ခြင်း"),
                KanjiExample("済む", "すむ", "ပြီးဆုံးသည်")
            )
        ),
        KanjiItem(
            id = 5,
            kanji = "歴",
            onyomi = "レキ",
            kunyomi = "-",
            meaningBurmese = "သမိုင်း၊ ဖြတ်သန်းမှုမှတ်တမ်း",
            meaningEnglish = "Curriculum, Continuation, Passage of time",
            strokeCount = 14,
            examples = listOf(
                KanjiExample("歴史", "れきし", "သမိုင်း"),
                KanjiExample("履歴書", "りれきしょ", "ကိုယ်ရေးရာဇဝင်"),
                KanjiExample("経歴", "けいれき", "လုပ်ငန်းအတွေ့အကြုံမှတ်တမ်း")
            )
        ),
        KanjiItem(
            id = 6,
            kanji = "史",
            onyomi = "シ",
            kunyomi = "-",
            meaningBurmese = "သမိုင်း၊ မှတ်တမ်း",
            meaningEnglish = "History, Chronicle",
            strokeCount = 5,
            examples = listOf(
                KanjiExample("日本史", "にほんし", "ဂျပန်သမိုင်း"),
                KanjiExample("世界史", "せかいし", "ကမ္ဘာ့သမိုင်း")
            )
        ),
        KanjiItem(
            id = 7,
            kanji = "育",
            onyomi = "イク",
            kunyomi = "そだ・つ, そだ・てる",
            meaningBurmese = "မွေးမြူပျိုးထောင်သည်၊ ပညာသင်ကြားသည်",
            meaningEnglish = "Bring up, Grow up, Educate",
            strokeCount = 8,
            examples = listOf(
                KanjiExample("教育", "きょういく", "ပညာရေး"),
                KanjiExample("育てる", "そだてる", "ပြုစုပျိုးထောင်သည်"),
                KanjiExample("体育", "たいいく", "ကာယပညာ")
            )
        ),
        KanjiItem(
            id = 8,
            kanji = "化",
            onyomi = "カ, ケ",
            kunyomi = "ば・ける",
            meaningBurmese = "ပြောင်းလဲခြင်း၊ ဓာတု",
            meaningEnglish = "Change, Take the form of, Influence",
            strokeCount = 4,
            examples = listOf(
                KanjiExample("文化", "ぶんか", "ယဉ်ကျေးမှု"),
                KanjiExample("化学", "かがく", "ဓာတုဗေဒ"),
                KanjiExample("変化", "へんか", "ပြောင်းလဲခြင်း")
            )
        ),
        KanjiItem(
            id = 9,
            kanji = "科",
            onyomi = "カ",
            kunyomi = "-",
            meaningBurmese = "ဌာန၊ သိပ္ပံဘာသာရပ်",
            meaningEnglish = "Department, Course, Science",
            strokeCount = 9,
            examples = listOf(
                KanjiExample("科学", "かがく", "သိပ္ပံပညာ"),
                KanjiExample("教科書", "きょうかしょ", "ကျောင်းသုံးပြဋ္ဌာန်းစာအုပ်"),
                KanjiExample("内科", "ないか", "အတွင်းလူနာဌာန")
            )
        ),
        KanjiItem(
            id = 10,
            kanji = "数",
            onyomi = "スウ, ス",
            kunyomi = "かず, かぞ・える",
            meaningBurmese = "ဂဏန်း၊ အရေအတွက်၊ ရေတွက်သည်",
            meaningEnglish = "Number, Count",
            strokeCount = 13,
            examples = listOf(
                KanjiExample("数字", "すうじ", "ဂဏန်းသင်္ကေတ"),
                KanjiExample("数学", "すうがく", "သင်္ချာ"),
                KanjiExample("数える", "かぞえる", "ရေတွက်သည်")
            )
        ),
        KanjiItem(
            id = 11,
            kanji = "備",
            onyomi = "ビ",
            kunyomi = "そな・える, そな・わる",
            meaningBurmese = "ပြင်ဆင်ထားသည်၊ တပ်ဆင်ထားသည်",
            meaningEnglish = "Equip, Prepare, Provide",
            strokeCount = 12,
            examples = listOf(
                KanjiExample("準備", "じゅんび", "ကြိုတင်ပြင်ဆင်ခြင်း"),
                KanjiExample("設備", "せつび", "စက်ကိရိယာတပ်ဆင်မှု"),
                KanjiExample("備える", "そなえる", "ကြိုတင်ပြင်ဆင်ထားသည်")
            )
        ),
        KanjiItem(
            id = 12,
            kanji = "準",
            onyomi = "ジュン",
            kunyomi = "-",
            meaningBurmese = "စံချိန်စံညွှန်း၊ တန်းတူညီမျှ",
            meaningEnglish = "Semi-, Associate, Standard",
            strokeCount = 13,
            examples = listOf(
                KanjiExample("基準", "きじゅん", "စံနှုန်း"),
                KanjiExample("準備", "じゅんび", "ကြိုတင်ပြင်ဆင်မှု"),
                KanjiExample("準決勝", "じゅんけっしょう", "ဆီမီးဖိုင်နယ်")
            )
        ),
        KanjiItem(
            id = 13,
            kanji = "関",
            onyomi = "カン",
            kunyomi = "せき, かか・わる",
            meaningBurmese = "ပတ်သက်ဆက်နွယ်မှု၊ ဂိတ်ပေါက်",
            meaningEnglish = "Connection, Barrier, Relate",
            strokeCount = 14,
            examples = listOf(
                KanjiExample("関係", "かんけい", "ပတ်သက်ဆက်နွယ်မှု"),
                KanjiExample("関心", "かんしん", "စိတ်ဝင်စားမှု"),
                KanjiExample("玄関", "げんかん", "အိမ်အဝင်ပေါက်")
            )
        ),
        KanjiItem(
            id = 14,
            kanji = "係",
            onyomi = "ケイ",
            kunyomi = "かか・る, かかり",
            meaningBurmese = "တာဝန်ခံ၊ ဆက်စပ်မှု",
            meaningEnglish = "Person in charge, Connection",
            strokeCount = 9,
            examples = listOf(
                KanjiExample("係員", "かかりいん", "တာဝန်ကျဝန်ထမ်း"),
                KanjiExample("関係", "かんけい", "ဆက်ဆံရေး/ပတ်သက်မှု")
            )
        ),
        KanjiItem(
            id = 15,
            kanji = "情",
            onyomi = "ジョウ, セイ",
            kunyomi = "なさ・け",
            meaningBurmese = "သတင်းအချက်အလက်၊ သံယောဇဉ်၊ ခံစားချက်",
            meaningEnglish = "Feelings, Emotion, Condition",
            strokeCount = 11,
            examples = listOf(
                KanjiExample("情報", "じょうほう", "သတင်းအချက်အလက်"),
                KanjiExample("感情", "かんじょう", "ခံစားချက်"),
                KanjiExample("表情", "ひょうじょう", "မျက်နှာအမူအရာ")
            )
        ),
        KanjiItem(
            id = 16,
            kanji = "報",
            onyomi = "ホウ",
            kunyomi = "むく・いる",
            meaningBurmese = "သတင်းပို့သည်၊ အစီရင်ခံသည်",
            meaningEnglish = "Report, News, Reward",
            strokeCount = 12,
            examples = listOf(
                KanjiExample("報告", "ほうこく", "အစီရင်ခံခြင်း"),
                KanjiExample("予報", "よほう", "ကြိုတင်ခန့်မှန်းချက်"),
                KanjiExample("情報", "じょうほう", "သတင်းအချက်အလက်")
            )
        ),
        KanjiItem(
            id = 17,
            kanji = "告",
            onyomi = "コク",
            kunyomi = "つ・げる",
            meaningBurmese = "အသိပေးသည်၊ ကြေညာသည်",
            meaningEnglish = "Tell, Inform, Announce",
            strokeCount = 7,
            examples = listOf(
                KanjiExample("広告", "こうこく", "ကြော်ငြာ"),
                KanjiExample("警告", "けいこく", "သတိပေးချက်"),
                KanjiExample("告げる", "つげる", "ပြောပြသည်/အသိပေးသည်")
            )
        ),
        KanjiItem(
            id = 18,
            kanji = "広",
            onyomi = "コウ",
            kunyomi = "ひろ・い, ひろ・がる",
            meaningBurmese = "ကျယ်ပြန့်သော၊ ဖြန့်ကျက်သည်",
            meaningEnglish = "Wide, Broad, Spacious",
            strokeCount = 5,
            examples = listOf(
                KanjiExample("広い", "ひろい", "ကျယ်ဝန်းသော"),
                KanjiExample("広場", "ひろば", "ရင်ပြင်"),
                KanjiExample("広告", "こうこく", "ကြော်ငြာ")
            )
        ),
        KanjiItem(
            id = 19,
            kanji = "求",
            onyomi = "キュウ",
            kunyomi = "もと・める",
            meaningBurmese = "တောင်းဆိုသည်၊ ရှာဖွေလိုလားသည်",
            meaningEnglish = "Request, Seek, Demand",
            strokeCount = 7,
            examples = listOf(
                KanjiExample("要求", "ようきゅう", "တောင်းဆိုချက်"),
                KanjiExample("請求", "せいきゅう", "ငွေတောင်းခံလွှာ"),
                KanjiExample("求める", "もとめる", "ရှာဖွေလိုလားသည်")
            )
        ),
        KanjiItem(
            id = 20,
            kanji = "協",
            onyomi = "キョウ",
            kunyomi = "-",
            meaningBurmese = "ပူးပေါင်းဆောင်ရွက်သည်၊ ညီညွတ်သည်",
            meaningEnglish = "Cooperation, Harmonize",
            strokeCount = 8,
            examples = listOf(
                KanjiExample("協力", "きょうりょく", "ပူးပေါင်းဆောင်ရွက်မှု"),
                KanjiExample("協会", "きょうかい", "အသင်းအဖွဲ့"),
                KanjiExample("妥協", "だきょう", "အပြန်အလှန်လိုက်လျောမှု")
            )
        ),
        KanjiItem(
            id = 21,
            kanji = "存",
            onyomi = "ゾン, ソン",
            kunyomi = "-",
            meaningBurmese = "တည်ရှိသည်၊ အသက်ရှင်သည်၊ သိရှိသည်",
            meaningEnglish = "Exist, Be aware of, Preserve",
            strokeCount = 6,
            examples = listOf(
                KanjiExample("存在", "そんざい", "တည်ရှိမှု"),
                KanjiExample("保存", "ほぞん", "သိမ်းဆည်းထိန်းသိမ်းမှု"),
                KanjiExample("ご存知", "ごぞんじ", "သိရှိတော်မူသော")
            )
        ),
        KanjiItem(
            id = 22,
            kanji = "認",
            onyomi = "ニン",
            kunyomi = "みと・める",
            meaningBurmese = "အသိအမှတ်ပြုသည်၊ ဝန်ခံလက်ခံသည်",
            meaningEnglish = "Recognize, Acknowledge, Approve",
            strokeCount = 14,
            examples = listOf(
                KanjiExample("確認", "かくにん", "စစ်ဆေးအတည်ပြုခြင်း"),
                KanjiExample("認める", "みとめる", "ဝန်ခံလက်ခံသည်"),
                KanjiExample("認識", "にんしき", "နားလည်သဘောပေါက်မှု")
            )
        ),
        KanjiItem(
            id = 23,
            kanji = "導",
            onyomi = "ドウ",
            kunyomi = "みちび・く",
            meaningBurmese = "လမ်းညွှန်သည်၊ မိတ်ဆက်ခေါ်ဆောင်သည်",
            meaningEnglish = "Guide, Lead, Conduct",
            strokeCount = 15,
            examples = listOf(
                KanjiExample("導入", "どうにゅう", "စတင်မိတ်ဆက်အသုံးပြုခြင်း"),
                KanjiExample("指導", "しどう", "လမ်းညွှန်သင်ကြားမှု"),
                KanjiExample("導く", "みちびく", "လမ်းပြခေါ်ဆောင်သည်")
            )
        ),
        KanjiItem(
            id = 24,
            kanji = "確",
            onyomi = "カク",
            kunyomi = "たし・か, たし・かめる",
            meaningBurmese = "သေချာခိုင်မာသော၊ စစ်ဆေးသည်",
            meaningEnglish = "Certain, Firm, Confirm",
            strokeCount = 15,
            examples = listOf(
                KanjiExample("確認", "かくにん", "အတည်ပြုစစ်ဆေးခြင်း"),
                KanjiExample("確かめる", "たしかめる", "သေချာအောင်စစ်ဆေးသည်"),
                KanjiExample("正確", "せいかく", "တိကျမှန်ကန်သော")
            )
        ),
        KanjiItem(
            id = 25,
            kanji = "実",
            onyomi = "ジツ",
            kunyomi = "み, みの・る",
            meaningBurmese = "အမှန်တကယ်၊ လက်တွေ့၊ အသီးအပွင့်",
            meaningEnglish = "Reality, Truth, Fruit",
            strokeCount = 8,
            examples = listOf(
                KanjiExample("実現", "じつげん", "လက်တွေ့အကောင်အထည်ဖော်ခြင်း"),
                KanjiExample("実験", "じっけん", "လက်တွေ့စမ်းသပ်မှု"),
                KanjiExample("実は", "じつは", "အမှန်အတိုင်းပြောရလျှင်")
            )
        ),
        KanjiItem(
            id = 26,
            kanji = "態",
            onyomi = "タイ",
            kunyomi = "-",
            meaningBurmese = "အခြေအနေ၊ အသွင်သဏ္ဌာန်၊ သဘောထား",
            meaningEnglish = "Condition, Situation, Appearance",
            strokeCount = 14,
            examples = listOf(
                KanjiExample("状態", "じょうたい", "အခြေအနေ"),
                KanjiExample("態度", "たいど", "သဘောထား/အပြုအမူ"),
                KanjiExample("事態", "じたい", "အခြေအနေဆိုး")
            )
        ),
        KanjiItem(
            id = 27,
            kanji = "規",
            onyomi = "キ",
            kunyomi = "-",
            meaningBurmese = "စည်းမျဉ်း၊ စံသတ်မှတ်ချက်",
            meaningEnglish = "Standard, Measure, Rule",
            strokeCount = 11,
            examples = listOf(
                KanjiExample("規則", "きそく", "စည်းမျဉ်းစည်းကမ်း"),
                KanjiExample("規定", "きてい", "ပြဋ္ဌာန်းချက်"),
                KanjiExample("定規", "じょうぎ", "ပေတံ")
            )
        ),
        KanjiItem(
            id = 28,
            kanji = "則",
            onyomi = "ソク",
            kunyomi = "-",
            meaningBurmese = "စည်းကမ်း၊ ဥပဒေသ",
            meaningEnglish = "Rule, Law, Follow",
            strokeCount = 9,
            examples = listOf(
                KanjiExample("規則", "きそく", "စည်းကမ်း"),
                KanjiExample("法則", "ほうそく", "သဘာဝဥပဒေသ"),
                KanjiExample("原則", "げんそく", "အခြေခံမူ")
            )
        ),
        KanjiItem(
            id = 29,
            kanji = "測",
            onyomi = "ソク",
            kunyomi = "はか・る",
            meaningBurmese = "တိုင်းတာသည်၊ ခန့်မှန်းသည်",
            meaningEnglish = "Measure, Survey, Fathom",
            strokeCount = 12,
            examples = listOf(
                KanjiExample("測定", "そくてい", "တိုင်းတာခြင်း"),
                KanjiExample("予測", "よそく", "ကြိုတင်ခန့်မှန်းချက်"),
                KanjiExample("測る", "はかる", "တိုင်းတာသည်")
            )
        ),
        KanjiItem(
            id = 30,
            kanji = "定",
            onyomi = "テイ, ジョウ",
            kunyomi = "さだ・める, さだ・まる",
            meaningBurmese = "သတ်မှတ်သည်၊ ဆုံးဖြတ်အတည်ပြုသည်",
            meaningEnglish = "Determine, Fix, Settle",
            strokeCount = 8,
            examples = listOf(
                KanjiExample("予定", "よてい", "အစီအစဉ်"),
                KanjiExample("決定", "けってい", "ဆုံးဖြတ်ချက်"),
                KanjiExample("定休日", "ていきゅうび", "ပုံမှန်ပိတ်ရက်")
            )
        ),
        KanjiItem(
            id = 31,
            kanji = "変",
            onyomi = "ヘン",
            kunyomi = "か・わる, か・える",
            meaningBurmese = "ပြောင်းလဲသည်၊ ထူးဆန်းသော",
            meaningEnglish = "Change, Unusual, Strange",
            strokeCount = 9,
            examples = listOf(
                KanjiExample("変化", "へんか", "ပြောင်းလဲမှု"),
                KanjiExample("変更", "へんこう", "ပြင်ဆင်ပြောင်းလဲခြင်း"),
                KanjiExample("大変", "たいへん", "ခက်ခဲပင်ပန်းသော/အလွန်")
            )
        ),
        KanjiItem(
            id = 32,
            kanji = "更",
            onyomi = "コウ",
            kunyomi = "ふ・ける, さら",
            meaningBurmese = "အသစ်လဲလှယ်သည်၊ ပြင်ဆင်သည်",
            meaningEnglish = "Renew, Change, Further",
            strokeCount = 7,
            examples = listOf(
                KanjiExample("変更", "へんこう", "ပြောင်းလဲခြင်း"),
                KanjiExample("更新", "こうしん", "သက်တမ်းတိုးခြင်း/အပ်ဒိတ်"),
                KanjiExample("今更", "いまさら", "ယခုမှစ၍")
            )
        ),
        KanjiItem(
            id = 33,
            kanji = "慎",
            onyomi = "シン",
            kunyomi = "つつし・む",
            meaningBurmese = "သတိထားဆင်ခြင်သည်၊ စေ့စပ်သည်",
            meaningEnglish = "Prudent, Careful, Discreet",
            strokeCount = 13,
            examples = listOf(
                KanjiExample("慎重", "しんちょう", "သတိရှိသော/စေ့စပ်သော"),
                KanjiExample("慎む", "つつしむ", "ထိန်းသိမ်းဆင်ခြင်သည်")
            )
        ),
        KanjiItem(
            id = 34,
            kanji = "重",
            onyomi = "ジュウ, チョウ",
            kunyomi = "おも・い, かさ・なる",
            meaningBurmese = "လေးလံသော၊ အရေးကြီးသော",
            meaningEnglish = "Heavy, Important, Pile up",
            strokeCount = 9,
            examples = listOf(
                KanjiExample("重要", "じゅうよう", "အရေးကြီးသော"),
                KanjiExample("体重", "たいじゅう", "ကိုယ်အလေးချိန်"),
                KanjiExample("重い", "おもい", "လေးလံသော")
            )
        ),
        KanjiItem(
            id = 35,
            kanji = "要",
            onyomi = "ヨウ",
            kunyomi = "い・る",
            meaningBurmese = "လိုအပ်သော၊ အရေးပါသော အဓိကအချက်",
            meaningEnglish = "Require, Essential, Need",
            strokeCount = 9,
            examples = listOf(
                KanjiExample("必要", "ひつよう", "လိုအပ်သော"),
                KanjiExample("重要", "じゅうよう", "အရေးပါသော"),
                KanjiExample("要約", "ようやく", "အကျဉ်းချုပ်")
            )
        ),
        KanjiItem(
            id = 36,
            kanji = "解",
            onyomi = "カイ, ゲ",
            kunyomi = "と・く, と・ける, ほど・く",
            meaningBurmese = "ဖြေရှင်းသည်၊ နားလည်သဘောပေါက်သည်",
            meaningEnglish = "Untie, Solve, Understand",
            strokeCount = 13,
            examples = listOf(
                KanjiExample("解決", "かいけつ", "ပြဿနာဖြေရှင်းခြင်း"),
                KanjiExample("理解", "りかい", "နားလည်သဘောပေါက်မှု"),
                KanjiExample("解答", "かいとう", "အဖြေပေးခြင်း")
            )
        ),
        KanjiItem(
            id = 37,
            kanji = "決",
            onyomi = "ケツ",
            kunyomi = "き・める, き・まる",
            meaningBurmese = "ဆုံးဖြတ်သည်၊ ဆုံးဖြတ်ပြီးစီးသည်",
            meaningEnglish = "Decide, Determine, Agree",
            strokeCount = 7,
            examples = listOf(
                KanjiExample("決定", "けってい", "ဆုံးဖြတ်ချက်"),
                KanjiExample("決める", "きめる", "ဆုံးဖြတ်သည်"),
                KanjiExample("決して", "けっして", "လုံးဝ/ဘယ်သောအခါမှ")
            )
        ),
        KanjiItem(
            id = 38,
            kanji = "論",
            onyomi = "ロン",
            kunyomi = "-",
            meaningBurmese = "ဆွေးနွေးငြင်းခုံသည်၊ စာတမ်း၊ သီအိုရီ",
            meaningEnglish = "Theory, Argument, Essay",
            strokeCount = 15,
            examples = listOf(
                KanjiExample("論文", "ろんぶん", "စာတမ်း/သုတေသန"),
                KanjiExample("議論", "ぎろん", "ဆွေးနွေးငြင်းခုံမှု"),
                KanjiExample("結論", "けつろん", "နိဂုံးချုပ်/ကောက်ချက်")
            )
        ),
        KanjiItem(
            id = 39,
            kanji = "議",
            onyomi = "ギ",
            kunyomi = "-",
            meaningBurmese = "ဆွေးနွေးတိုင်ပင်သည်၊ ညီလာခံ",
            meaningEnglish = "Deliberation, Consult, Debate",
            strokeCount = 20,
            examples = listOf(
                KanjiExample("会議", "かいぎ", "အစည်းအဝေး"),
                KanjiExample("議員", "ぎいん", "လွှတ်တော်အမတ်"),
                KanjiExample("不思議", "ふしぎ", "အံ့ဩဖွယ်ရာ/ထူးဆန်းသော")
            )
        ),
        KanjiItem(
            id = 40,
            kanji = "団",
            onyomi = "ダン, トン",
            kunyomi = "-",
            meaningBurmese = "အဖွဲ့အစည်း၊ အစုအဖွဲ့",
            meaningEnglish = "Group, Organization, Association",
            strokeCount = 6,
            examples = listOf(
                KanjiExample("団体", "だんたい", "အဖွဲ့အစည်း"),
                KanjiExample("集団", "しゅうだん", "လူစုလူဝေး"),
                KanjiExample("団結", "だんけつ", "စည်းလုံးညီညွတ်မှု")
            )
        )
    )

    val kotobaList = listOf(
        KotobaItem(
            id = 1,
            word = "政治",
            reading = "せいじ",
            romaji = "seiji",
            meaningBurmese = "နိုင်ငံရေး",
            meaningEnglish = "Politics, Government",
            partOfSpeech = "Noun",
            exampleJapanese = "日本の政治について学んでいます。",
            exampleBurmese = "ဂျပန်နိုင်ငံရေးအကြောင်းကို လေ့လာနေပါသည်။",
            category = "Society"
        ),
        KotobaItem(
            id = 2,
            word = "経済",
            reading = "けいざい",
            romaji = "keizai",
            meaningBurmese = "စီးပွားရေး",
            meaningEnglish = "Economy",
            partOfSpeech = "Noun",
            exampleJapanese = "世界経済の発展が期待されています。",
            exampleBurmese = "ကမ္ဘာ့စီးပွားရေး ဖွံ့ဖြိုးတိုးတက်မှုကို မျှော်လင့်ထားကြသည်။",
            category = "Business"
        ),
        KotobaItem(
            id = 3,
            word = "経験",
            reading = "けいけん",
            romaji = "keiken",
            meaningBurmese = "အတွေ့အကြုံ",
            meaningEnglish = "Experience",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "日本での生活は良い経験になります。",
            exampleBurmese = "ဂျပန်တွင် နေထိုင်ခြင်းသည် ကောင်းမွန်သော အတွေ့အကြုံတစ်ခု ဖြစ်စေသည်။",
            category = "Life"
        ),
        KotobaItem(
            id = 4,
            word = "準備",
            reading = "じゅんび",
            romaji = "junbi",
            meaningBurmese = "ကြိုတင်ပြင်ဆင်ခြင်း",
            meaningEnglish = "Preparation",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "試験の準備をしっかりしてください。",
            exampleBurmese = "စာမေးပွဲအတွက် ပြင်ဆင်မှုကို သေချာစွာ ပြုလုပ်ပါ။",
            category = "Study"
        ),
        KotobaItem(
            id = 5,
            word = "教育",
            reading = "きょういく",
            romaji = "kyouiku",
            meaningBurmese = "ပညာရေး",
            meaningEnglish = "Education",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "子供の教育はとても重要です。",
            exampleBurmese = "ကလေးများ၏ ပညာရေးသည် အလွန်အရေးကြီးပါသည်။",
            category = "Education"
        ),
        KotobaItem(
            id = 6,
            word = "文化",
            reading = "ぶんか",
            romaji = "bunka",
            meaningBurmese = "ယဉ်ကျေးမှု",
            meaningEnglish = "Culture",
            partOfSpeech = "Noun",
            exampleJapanese = "ミャンマーと日本の文化の違いを話しました。",
            exampleBurmese = "မြန်မာနှင့် ဂျပန်ယဉ်ကျေးမှု ကွာခြားချက်များကို ပြောဆိုခဲ့ကြသည်။",
            category = "Society"
        ),
        KotobaItem(
            id = 7,
            word = "環境",
            reading = "かんきょう",
            romaji = "kankyou",
            meaningBurmese = "ပတ်ဝန်းကျင်",
            meaningEnglish = "Environment",
            partOfSpeech = "Noun",
            exampleJapanese = "自然環境を守る活動に参加しました。",
            exampleBurmese = "သဘာဝပတ်ဝန်းကျင် ထိန်းသိမ်းရေး လှုပ်ရှားမှုတွင် ပါဝင်ခဲ့သည်။",
            category = "Nature"
        ),
        KotobaItem(
            id = 8,
            word = "複雑",
            reading = "ふくざつ",
            romaji = "fukuzatsu",
            meaningBurmese = "ရှုပ်ထွေးသော",
            meaningEnglish = "Complex, Complicated",
            partOfSpeech = "Na-Adjective",
            exampleJapanese = "この文法は少し複雑です。",
            exampleBurmese = "ဤသဒ္ဒါသည် အနည်းငယ် ရှုပ်ထွေးပါသည်။",
            category = "Study"
        ),
        KotobaItem(
            id = 9,
            word = "簡単",
            reading = "かんたん",
            romaji = "kantan",
            meaningBurmese = "ရိုးရှင်းလွယ်ကူသော",
            meaningEnglish = "Simple, Easy",
            partOfSpeech = "Na-Adjective",
            exampleJapanese = "簡単に自己紹介をしてください。",
            exampleBurmese = "ရိုးရှင်းလွယ်ကူစွာ ကိုယ့်ကိုယ်ကိုယ် မိတ်ဆက်ပေးပါ။",
            category = "Daily"
        ),
        KotobaItem(
            id = 10,
            word = "連絡",
            reading = "れんらく",
            romaji = "renraku",
            meaningBurmese = "အဆက်အသွယ်ပြုလုပ်သည်",
            meaningEnglish = "Contact, Communication",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "後でメールで連絡します。",
            exampleBurmese = "နောက်မှ အီးမေးလ်ဖြင့် ဆက်သွယ်ပါမည်။",
            category = "Work"
        ),
        KotobaItem(
            id = 11,
            word = "関係",
            reading = "かんけい",
            romaji = "kankei",
            meaningBurmese = "ပတ်သက်ဆက်နွယ်မှု",
            meaningEnglish = "Relationship, Connection",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "彼との関係はとても良好です。",
            exampleBurmese = "သူနှင့် ဆက်ဆံရေးသည် အလွန်ကောင်းမွန်ပါသည်။",
            category = "Society"
        ),
        KotobaItem(
            id = 12,
            word = "情報",
            reading = "じょうほう",
            romaji = "jouhou",
            meaningBurmese = "သတင်းအချက်အလက်",
            meaningEnglish = "Information, News",
            partOfSpeech = "Noun",
            exampleJapanese = "インターネットで最新の情報を調べました。",
            exampleBurmese = "အင်တာနက်မှ နောက်ဆုံးရ သတင်းအချက်အလက်များကို ရှာဖွေခဲ့သည်။",
            category = "Study"
        ),
        KotobaItem(
            id = 13,
            word = "報告",
            reading = "ほうこく",
            romaji = "houkoku",
            meaningBurmese = "အစီရင်ခံခြင်း",
            meaningEnglish = "Report, Briefing",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "会議の結果を部長に報告しました。",
            exampleBurmese = "အစည်းအဝေးရလဒ်ကို ဌာနမှူးထံ အစီရင်ခံတင်ပြခဲ့သည်။",
            category = "Work"
        ),
        KotobaItem(
            id = 14,
            word = "広告",
            reading = "こうこく",
            romaji = "koukoku",
            meaningBurmese = "ကြော်ငြာ",
            meaningEnglish = "Advertisement",
            partOfSpeech = "Noun",
            exampleJapanese = "駅前で新しい商品の広告を見ました。",
            exampleBurmese = "ဘူတာရုံရှေ့တွင် ကုန်ပစ္စည်းအသစ်၏ ကြော်ငြာကို တွေ့ခဲ့သည်။",
            category = "Business"
        ),
        KotobaItem(
            id = 15,
            word = "管理",
            reading = "かんり",
            romaji = "kanri",
            meaningBurmese = "စီမံခန့်ခွဲထိန်းသိမ်းမှု",
            meaningEnglish = "Management, Administration",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "健康管理に気をつけています。",
            exampleBurmese = "ကျန်းမာရေးထိန်းသိမ်းမှုကို ဂရုစိုက်နေပါသည်။",
            category = "Life"
        ),
        KotobaItem(
            id = 16,
            word = "解決",
            reading = "かいけつ",
            romaji = "kaiketsu",
            meaningBurmese = "ပြဿနာဖြေရှင်းခြင်း",
            meaningEnglish = "Solution, Resolution",
            partOfSpeech = "Noun / Suru Verb",
            exampleJapanese = "みんなで話し合って問題を解決しました。",
            exampleBurmese = "အားလုံးဆွေးနွေးတိုင်ပင်၍ ပြဿနာကို ဖြေရှင်းခဲ့ကြသည်။",
            category = "Work"
        )
    )

    // JLPT N3 Past Exam Questions (2014-2025)
    val pastQuestions = listOf(
        PastExamQuestion(
            year = "2024 July",
            question = "この問題は、もっと【慎重】に考えなければならない。",
            reading = "しんちょう",
            meaningBurmese = "သတိထားသော၊ စေ့စပ်သေချာသော",
            options = listOf("しんちょう", "しんじゅう", "じんちょう", "じんじゅう"),
            correctAnswer = "しんちょう"
        ),
        PastExamQuestion(
            year = "2023 Dec",
            question = "新しい制度が【導入】された。",
            reading = "どうにゅう",
            meaningBurmese = "စတင်မိတ်ဆက်အသုံးပြုခြင်း",
            options = listOf("どうにゅう", "とうにゅう", "どうにん", "とうにん"),
            correctAnswer = "どうにゅう"
        ),
        PastExamQuestion(
            year = "2022 July",
            question = "彼の意見はとても【具体的】だった。",
            reading = "ぐたいてき",
            meaningBurmese = "တိကျခိုင်မာသော",
            options = listOf("ぐたいてき", "こうたいてき", "くたいてき", "ごうたいてき"),
            correctAnswer = "ぐたいてき"
        ),
        PastExamQuestion(
            year = "2021 Dec",
            question = "書類の内容を【確認】してください。",
            reading = "かくにん",
            meaningBurmese = "စစ်ဆေးအတည်ပြုသည်",
            options = listOf("かくにん", "かっにん", "こうにん", "がくにん"),
            correctAnswer = "かくにん"
        ),
        PastExamQuestion(
            year = "2020 Dec",
            question = "計画を【変更】することになりました。",
            reading = "へんこう",
            meaningBurmese = "ပြောင်းလဲပြင်ဆင်ခြင်း",
            options = listOf("へんこう", "へんごう", "はんこう", "はんごう"),
            correctAnswer = "へんこう"
        )
    )
}

data class PastExamQuestion(
    val year: String,
    val question: String,
    val reading: String,
    val meaningBurmese: String,
    val options: List<String>,
    val correctAnswer: String
)
