package com.example.ui.components

enum class FlashcardGestureStyle(
    val title: String,
    val burmeseLabel: String,
    val description: String
) {
    SWIPE_REVIEW(
        title = "Swipe SRS",
        burmeseLabel = "👉 ပွတ်ဆွဲ (SRS)",
        description = "ညာဘက်ပွတ်ဆွဲလျှင် မှတ်မိသည် (Good)၊ ဘယ်ဘက်ပွတ်ဆွဲလျှင် ပြန်လေ့လာ (Again)၊ နှိပ်လျှင် သို့မဟုတ် အပေါ်ပွတ်ဆွဲလျှင် အဖြေလှန်ပါမည်။"
    ),
    SLIDE_CAROUSEL(
        title = "Slide Carousel",
        burmeseLabel = "↔️ ဘေးသို့ ပွတ်ဆွဲ",
        description = "ကတ်ပြားများကို ဘယ်/ညာ ပွတ်ဆွဲ၍ ရှေ့နောက် ကူးပြောင်းနိုင်ပါသည်။ ကတ်ကို နှိပ်လျှင် အဖြေလှန်ပါမည်။"
    ),
    VERTICAL_FLIP(
        title = "Vertical Flip",
        burmeseLabel = "↕️ အပေါ်/အောက် လှန်",
        description = "ကတ်ပြားကို အပေါ်/အောက် ပွတ်ဆွဲ၍ အဖြေလှန်နိုင်ပြီး၊ ဘေးသို့ ပွတ်ဆွဲ၍ နောက်ကတ်သို့ ကူးပြောင်းနိုင်ပါသည်။"
    ),
    TAP_ONLY(
        title = "Tap & Buttons",
        burmeseLabel = "👆 နှိပ်ရုံသီးသန့်",
        description = "ကတ်ပြားကို နှိပ်ရုံဖြင့် အဖြေလှန်နိုင်ပြီး အောက်ခြေ ခလုတ်များဖြင့် အဆင်ပြေစွာ ကူးပြောင်းနိုင်ပါသည်။"
    )
}
