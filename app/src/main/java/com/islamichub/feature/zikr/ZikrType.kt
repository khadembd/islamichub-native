package com.islamichub.feature.zikr

/**
 * ZikrType — per source zikr-counter.js ZIKR_LIST (6 presets)
 * Each has: name, arabic, transliteration, meaning, target, color
 */
enum class ZikrType(
    val displayName: String,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val target: Int,
    val colorHex: String
) {
    SUBHANALLAH(
        "সুবহানআল্লাহ",
        "سُبْحَانَ اللَّهِ",
        "Subhanallah",
        "আল্লাহ পবিত্র",
        33,
        "#FF6D45C7"
    ),
    ALHAMDULILLAH(
        "আলহামদুলিল্লাহ",
        "الْحَمْدُ لِلَّهِ",
        "Alhamdulillah",
        "সমস্ত প্রশংসা আল্লাহর",
        33,
        "#FF7C3AED"
    ),
    ALLAHU_AKBAR(
        "আল্লাহু আকবার",
        "اللَّهُ أَكْبَرُ",
        "Allahu Akbar",
        "আল্লাহ মহান",
        34,
        "#FF0369A1"
    ),
    LA_ILAHA(
        "লা ইলাহা ইল্লাল্লাহ",
        "لَا إِلٰهَ إِلَّا اللَّهُ",
        "La ilaha illallah",
        "আল্লাহ ছাড়া কোনো ইলাহ নেই",
        100,
        "#FFC2410C"
    ),
    ASTAGHFIRULLAH(
        "আস্তাগফিরুল্লাহ",
        "أَسْتَغْفِرُ اللَّهَ",
        "Astaghfirullah",
        "আমি আল্লাহর কাছে ক্ষমা চাই",
        100,
        "#FF0F766E"
    ),
    DAROOD(
        "দরূদ শরীফ",
        "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ",
        "Allahumma salli ala Muhammad",
        "হে আল্লাহ, মুহাম্মদের উপর রহমত বর্ষণ করুন",
        100,
        "#FFB45309"
    )
}
