# Smart Age & Zodiac Calculator

A production-grade Android application built with Kotlin, Jetpack Compose, Material 3, and `java.time` that provides exact chronological age calculations, 4 extensive structured tables, live birthday countdown rings, and a complete 12-sign bilingual (English & Bengali) Western Tropical Zodiac system.

## Key Features
1. **High-Precision Age Calculation Engine (`java.time`)**
   - Exact calendar years, months, and days (`Period.between`).
   - Exact total years, months, weeks, and days (`ChronoUnit`), plus clearly labeled date-midnight hour, minute, second, and decimal-year conversions.
   - Explicit February 29 leap-year handling: non-leap-year birthdays are consistently evaluated on February 28 with an informational banner.
   - Strict rejection of future birth dates.

2. **Extensive 4-Table Personal Age Dashboard**
   - **Table 1: Basic Age Information** (14 attributes)
   - **Table 2: Birthday Information** (8 attributes)
   - **Table 3: Calendar Information** (8 attributes)
   - **Table 4: Time Statistics** (7 attributes)
   - Individual row copy, **Copy All Information**, **Share Formatted Text**, and **Shareable High-Resolution Summary Card Image** via `FileProvider`.

3. **Bilingual 12-Sign Western Tropical Zodiac System (English & বাংলা)**
   - Complete profiles for all 12 signs (Aries through Pisces) with English & Bengali names, symbols, elements, ruling planets, modalities, lucky colors/days, strengths, weaknesses, and traits.
   - 11-row detailed Zodiac table + horizontally scrollable **12-Sign Comparison Table**.
   - Automatic boundary/cusp date advisory note and astrological disclaimer.

4. **Local History, Pinned Profiles & Settings (Jetpack DataStore)**
   - Save multiple DOB profiles, search history, sort by newest/oldest, and delete individual or all records offline.
   - Complete Light Mode, Dark Mode, System Default, and optional Rewarded-Ad unlockable **Celestial Gold** and **Cosmic Violet** themes.

## Build & Testing Instructions
- **Compile Debug APK:** `gradle assembleDebug`
- **Run Unit & Robolectric UI Tests:** `gradle :app:testDebugUnitTest`
- **AdMob Configuration:** Official Google Test Ad Unit IDs are configured by default in `.env.example` and injected via `BuildConfig`. Replace with production AdMob IDs via the Secrets panel before Play Store release.
