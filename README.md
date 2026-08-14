# Islamic Hub — Native Android

> Direct native Kotlin rebuild of the Islamic Hub Capacitor/WebView app.
> **No WebView. No Capacitor. No HTML-as-UI.** Pure Android SDK + Jetpack + Material 3.
>
> ইসলামিক হাব — সম্পূর্ণ নেটিভ Android (Kotlin) অ্যাপ। কোনো WebView/Capacitor নেই।

[![Build APK](https://github.com/khadembd/islamichub-native/actions/workflows/build-apk.yml/badge.svg)](https://github.com/khadembd/islamichub-native/actions/workflows/build-apk.yml)

---

## সংক্ষিপ্ত বিবরণ / Overview

এই প্রোজেক্টটি `islamic-hub-source.zip` (একটি Capacitor-based ইসলামিক ওয়েব অ্যাপ) কে সম্পূর্ণ নেটিভ Android অ্যাপে রূপান্তর করে। রূপান্তরটি `IslamicHub_Native_Android_Conversion_Plan.md`-এ বর্ণিত মাস্টার প্ল্যান অনুসরণ করে।

This project converts the original Capacitor-based Islamic Hub web app into a fully native Android application. The conversion follows the master plan documented in `IslamicHub_Native_Android_Conversion_Plan.md`.

### Key principles / মূলনীতি

- **No wrapper.** Final APK contains zero HTML/CSS/JS-as-UI. শেষ APK-তে কোনো HTML/CSS/JS-as-UI নেই।
- **Native data layer.** All JS data files converted to JSON and bundled in `app/src/main/assets/data/`. সব JS ডেটা JSON-এ রূপান্তরিত।
- **Native Android APIs.** Location → FusedLocationProviderClient; Camera → CameraX; Sensors → SensorManager; Audio → Media3; Notifications → AlarmManager + NotificationManager.
- **Feature parity.** All 26+ screens from the original app are mapped to native fragments. সব স্ক্রিন নেটিভ fragment হিসেবে তৈরি।

---

## Tech Stack / প্রযুক্তি

| Layer            | Library                                                                 |
|------------------|-------------------------------------------------------------------------|
| Language         | Kotlin 1.9.24                                                          |
| UI               | Material 3 (View-based) + RecyclerView + ConstraintLayout              |
| Navigation       | AndroidX Navigation 2.7.7                                              |
| DI               | Hilt 2.51.1                                                            |
| Database         | Room 2.6.1 (bookmarks, salah tracker, zikr sessions)                   |
| Preferences      | DataStore 1.1.1                                                        |
| Audio            | Media3 / ExoPlayer 1.4.0                                               |
| Camera           | CameraX 1.3.4                                                          |
| Location         | Google Play Services Location 21.3.0                                   |
| Network          | Retrofit 2.11 + OkHttp 4.12 (for AI Scholar / Hadith API)              |
| Serialization    | kotlinx.serialization 1.6.3                                            |
| Background work  | WorkManager 2.9.0 + Hilt-Work 1.2.0                                    |
| Biometric        | AndroidX Biometric 1.1.0                                               |
| Images           | Coil 2.6.0                                                             |
| Build            | Android Gradle Plugin 8.5.2, Gradle 8.8, JDK 17                       |
| Min SDK          | 24 (Android 7.0)                                                       |
| Target SDK       | 34 (Android 14)                                                        |

---

## Project structure / প্রোজেক্ট স্ট্রাকচার

```text
islamichub-native/
├── .github/workflows/build-apk.yml      # CI: builds debug + release APK
├── app/
│   ├── build.gradle.kts                 # App module config + dependencies
│   ├── proguard-rules.pro               # R8 keep rules for serialization + Hilt + Room
│   └── src/main/
│       ├── AndroidManifest.xml          # Permissions + receivers + activities
│       ├── assets/data/                 # Converted JSON (kalima, dua, hadith, ...)
│       │   ├── migration-report.json    # Per-file record counts + SHA-256
│       │   ├── kalima.json
│       │   ├── dua.json
│       │   ├── asmaul_husna.json
│       │   ├── hadith.json
│       │   ├── extended_hadith.json
│       │   ├── namaz.json
│       │   ├── namaz_extras.json
│       │   ├── namaz_shikkha.json
│       │   ├── questions.json
│       │   ├── answers.json
│       │   ├── misconceptions.json
│       │   ├── islamic_stories.json
│       │   └── locations.json
│       ├── java/com/islamichub/
│       │   ├── IslamicHubApplication.kt
│       │   ├── MainActivity.kt
│       │   ├── core/ui/                 # ContentCardAdapter, shared UI utilities
│       │   ├── data/
│       │   │   ├── local/               # Room DB, DAOs, entities
│       │   │   ├── model/               # Kotlinx @Serializable data classes
│       │   │   ├── preferences/         # DataStore wrapper
│       │   │   └── repository/          # AssetRepository (reads JSON)
│       │   ├── di/                      # Hilt modules
│       │   ├── feature/                 # One package per screen
│       │   │   ├── home/                # HomeFragment, MoreFragment
│       │   │   ├── quran/               # QuranFragment
│       │   │   ├── hadith/              # HadithFragment
│       │   │   ├── namaz/               # NamazFragment
│       │   │   ├── prayer/              # PrayerTimesFragment (computed locally)
│       │   │   ├── qibla/               # QiblaFragment (native SensorManager)
│       │   │   ├── dua/                 # DuaFragment
│       │   │   ├── zikr/                # ZikrFragment (tap counter)
│       │   │   ├── asmaulhusna/         # AsmaulHusnaFragment
│       │   │   ├── stories/             # StoriesFragment
│       │   │   ├── misconceptions/      # MisconceptionsFragment
│       │   │   ├── questions/           # QuestionsFragment
│       │   │   ├── search/              # SearchFragment
│       │   │   ├── bookmarks/           # BookmarksFragment (Room-backed)
│       │   │   └── profile/             # SettingsFragment
│       │   └── services/                # PrayerTimeCalculator, notification scheduler
│       └── res/
│           ├── drawable/                # Vector icons
│           ├── drawable-nodpi/          # All original images (60 files: WebP, PNG, SVG)
│           ├── mipmap-*/                # Launcher icons (legacy + adaptive)
│           ├── raw/                     # 15 Namaz MP3 audio files
│           ├── layout/                  # XML layouts
│           ├── values/                  # Colors, strings (en), themes
│           ├── values-bn/               # Bangla strings
│           ├── values-night/            # Dark theme
│           ├── menu/                    # Bottom navigation
│           ├── anim/                    # Activity transitions
│           ├── navigation/nav_graph.xml # Single-activity navigation graph
│           └── xml/                     # Backup rules
├── docs/                                # Original markdown plans (reference)
│   ├── IslamicHub_Native_Android_Conversion_Plan.md
│   ├── IslamicHub_Bug_Fix_Master_Plan.md
│   └── IslamicHub_UI_UX_Upgrade_Master_Plan.md
├── scripts/
│   ├── convert_data.js                  # JS → JSON data migration
│   └── copy_assets.sh                   # Image + audio → res/ copier
├── build.gradle.kts                     # Root: plugin versions
├── settings.gradle.kts
├── gradle.properties
├── gradlew, gradlew.bat
└── gradle/wrapper/
```

---

## Build & run / বিল্ড ও রান

### Prerequisites

- JDK 17+
- Android SDK (compileSdk 34, build-tools 34.0.0)
- Internet access for first-time Gradle dependency download

### Local build

```bash
# 1. Clone
git clone https://github.com/khadembd/islamichub-native.git
cd islamichub-native

# 2. (Optional) Re-convert data from original source
node scripts/convert_data.js /path/to/islamic-hub-source/islamichub app/src/main/assets/data

# 3. (Optional) Re-copy assets from original source
bash scripts/copy_assets.sh /path/to/islamic-hub-source/islamichub

# 4. Build debug APK
./gradlew assembleDebug

# 5. Install on connected device
./gradlew installDebug
#   OR
adb install app/build/outputs/apk/debug/app-debug.apk
```

### GitHub Actions build

The repo includes a workflow at `.github/workflows/build-apk.yml` that automatically:

1. Checks out the code
2. Sets up JDK 17 + Android SDK
3. Caches Gradle dependencies
4. Builds debug + release APK
5. Uploads APKs as GitHub Actions artifacts (30-day retention)

**Trigger types:**
- Push to `main` / `master`
- Pull request to `main`
- Manual dispatch via Actions tab → "Build APK" → Run workflow → pick `debug` / `release` / `both`

**To download the APK:**
1. Go to https://github.com/khadembd/islamichub-native/actions
2. Click the latest successful "Build APK" run
3. Scroll to "Artifacts" at the bottom
4. Download `islamichub-debug-apk` or `islamichub-release-apk-unsigned`
5. Unzip and install the `.apk` on your device (enable "Install unknown apps" if needed)

---

## Feature coverage / ফিচার কভারেজ

| # | Feature              | Status | Native implementation                                |
|---|----------------------|--------|------------------------------------------------------|
| 1 | Home / Hub           | ✅ v1   | `HomeFragment` — feature grid + next prayer + daily ayah |
| 2 | Quran (short surahs) | ✅ v1   | `QuranFragment` — list from `namaz_extras.json`      |
| 3 | Hadith               | ✅ v1   | `HadithFragment` — primary + extended merged         |
| 4 | Namaz learning       | ✅ v1   | `NamazFragment` — prayers + learning steps           |
| 5 | Prayer times         | ✅ v1   | `PrayerTimesFragment` — `PrayerTimeCalculator` (astronomy) |
| 6 | Qibla compass        | ✅ v1   | `QiblaFragment` — `SensorManager` + `FusedLocationProvider` |
| 7 | Dua                  | ✅ v1   | `DuaFragment`                                        |
| 8 | Zikr / Tasbih        | ✅ v1   | `ZikrFragment` — Room-backed session log             |
| 9 | Asmaul Husna         | ✅ v1   | `AsmaulHusnaFragment` — 99 names                     |
| 10| Stories              | ✅ v1   | `StoriesFragment` — prophets + khalifas + meraj      |
| 11| Misconceptions       | ✅ v1   | `MisconceptionsFragment`                             |
| 12| Questions & Answers  | ✅ v1   | `QuestionsFragment` — Q+A matched by category        |
| 13| Search               | ✅ v1   | `SearchFragment` — debounced multi-source search     |
| 14| Bookmarks            | ✅ v1   | `BookmarksFragment` — Room `BookmarkDao`             |
| 15| Settings             | ✅ v1   | `SettingsFragment` — theme + language + madhab + notifications |
| 16| Prayer notifications | ✅ v1   | `PrayerNotificationScheduler` — `AlarmManager` + boot receiver |
| 17| Salah tracker        | 🟡 partial | Room schema ready (`SalahTrackerDao`); UI pending v2 |
| 18| App lock (biometric) | 🟡 partial | `Biometric` dep added; UI pending v2                |
| 19| Audio playback       | 🟡 partial | Media3 dep added; player service pending v2         |
| 20| Vision scanner       | 🔲 v2   | CameraX dep added; scanner UI pending                |
| 21| Tajweed checker      | 🔲 v2   | RECORD_AUDIO perm declared; mic analysis pending     |
| 22| AI Scholar           | 🔲 v2   | Retrofit dep added; provider abstraction pending     |
| 23| Admin panel          | 🔲 v2   | Debug-only webkit dep; native admin pending          |
| 24| Daily content worker | 🔲 v2   | WorkManager dep added; daily refresh pending         |
| 25| Firebase sync        | 🔲 v2   | Disabled until google-services.json regenerated      |

---

## Migration report / মাইগ্রেশন রিপোর্ট

Run `node scripts/convert_data.js <source> <output>` to regenerate the data files. The script writes a `migration-report.json` next to the output JSONs with:

- Source filename
- Captured global variable name
- Output filename
- Record count
- Byte size
- SHA-256 checksum

This report is the parity validation gate between the original JS data and the bundled native JSON.

### Known data-quality fixes applied during migration

Per `IslamicHub_Native_Android_Conversion_Plan.md` §14–15, the following known bugs in the original JS source are corrected in the native Kotlin implementation:

1. **Asmaul Husna search** uses `transliteration` (not `bangla`).
2. **Extended Hadith search** uses `items` (not `hadiths`).
3. **Stories search** treats `prophets` and `khalifas` as arrays (not objects).
4. **Meraj** is included in story search categories.
5. **Q&A search** uses `questionData` / `ansData` (not `QUESTION_DATA`).
6. **Misconceptions search** uses both `items` array and `categories` map.
7. **Kalima search** checks the correct data shape.
8. **`namazData` global collision** between `namaz-data.js` and `namazshikkha-data.js` is resolved by separate Kotlin models (`NamazData` vs `NamazShikkhaData`).

---

## Security notes / নিরাপত্তা

- **No API keys** are hardcoded in the APK. AI Scholar / external APIs will use a backend proxy.
- **No Firebase config** is committed. When re-enabled, drop your own `app/google-services.json`.
- **Original Capacitor source** is NOT bundled in the APK. Only converted JSON + image/audio assets travel with the app.

---

## Roadmap

**v1.0** (current) — Foundation + 16 working screens + APK build pipeline
**v1.1** — Audio playback service (Media3) + Salah tracker UI + App lock UI
**v1.2** — Vision scanner (CameraX) + Tajweed checker (RECORD_AUDIO)
**v1.3** — AI Scholar (Retrofit + provider abstraction)
**v2.0** — Firebase sync + Admin panel + daily content worker + full 114-surah Quran

---

## License

This project continues the licensing of the original Islamic Hub source. See `docs/` for the original master plans.

---

## Credits

- Original Islamic Hub Capacitor source: provided as `islamic-hub-source.zip`
- Prayer times algorithm: based on PrayTimes.org (public domain)
- Migration plan: `IslamicHub_Native_Android_Conversion_Plan.md`
