# Smart Age & Zodiac Calculator — Modern Language Switcher, Zoomed Logo & Warning Fixes

This update refines the visual polish of the **Language Switcher buttons**, zooms in and enlarges the **App Logo** so the celestial emblem fills its frame prominently, and eliminates all **Gradle & Kotlin compiler warnings**.

### User Review & Critical Decisions

> [!IMPORTANT]
> Summary of the three targeted improvements included in this update:

- **Modern Segmented Language Switcher (`EN | বাংলা | हिन्दी`)**: Replaces the basic `FilterChip` row on both the **Home Screen Header** and **Settings Screen** with a sleek, custom **Segmented Pill Switcher** featuring script badges (`EN`, `বাং`, `हिं`), smooth active gradient highlight, subtle elevation, and crisp typography.
- **Zoomed-In & Enlarged App Logo**: Center-crops the outer dark margin of the generated celestial emblem (`img_launcher_icon_fg`) by ~28% so the golden astrolabe/hourglass icon fills the frame boldly, increases the Home Header logo container from `46.dp` to `56.dp` with a subtle gold border and `1.22x` visual scale zoom, and updates the adaptive launcher icon (`76.dp` foreground safe zone) and mipmap icons.
- **Zero Build & Compiler Warnings**:
  - Removes the unused `google-services` and `ksp` plugin triggers in `app/build.gradle.kts` that caused the `"File google-services.json is missing"` and skipped task warnings.
  - Cleans up unused imports and deprecated API calls across all Kotlin source files.

---

### 1. Overview & Core Concept

- **What It Does**: Elevates the header and settings aesthetics with a modern segmented language control, makes the brand logo visually bold and unmistakably legible, and ensures a clean, warning-free Gradle build pipeline.
- **Key Value**: Delivers a cleaner, Play-Store-ready UI and a zero-warning compilation log.

---

### 2. User Experience & Visual Design

- **Modern Language Switcher Component (`ModernLanguageSwitcher`)**:
  - Contained inside a rounded pill track (`RoundedCornerShape(16.dp)`) with a subtle tonal border (`outlineVariant`).
  - Each of the 3 language options (**English**, **বাংলা**, **हिन्दी**) displays a circular script monogram badge (`EN`, `বাং`, `हिं`) next to its label.
  - The active language is highlighted with a vibrant primary/secondary gradient pill, bold text, and a smooth transition.
- **Enlarged & Zoomed Logo**:
  - The Home Header logo badge is enlarged to `56.dp` with a `1.5.dp` golden-indigo accent border, and the source image is center-cropped and scaled (`graphicsLayer(scaleX = 1.22f, scaleY = 1.22f)`) so the golden celestial emblem appears large, sharp, and edge-to-edge.

---

### 3. Technical Architecture & Data Strategy *(Technical Reference)*

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      VISUAL & BUILD POLISH UPDATES                      │
│                                                                         │
│  ┌─────────────────────────────────┐  ┌──────────────────────────────┐  │
│  │     ModernLanguageSwitcher      │  │   Zoomed & Enlarged Logo     │  │
│  │ • Segmented Pill Track          │  │ • Center-cropped emblem asset│  │
│  │ • Script Badges (EN / বাং / हिं) │  │ • 56.dp Home Header container│  │
│  │ • Used in HomeHeader & Settings │  │   with 1.22x scale zoom      │  │
│  └─────────────────────────────────┘  └──────────────────────────────┘  │
│                                                                         │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │                   Gradle & Compiler Warning Fixes                 │  │
│  │ • Comment out unused google-services plugin in app/build.gradle   │  │
│  │ • Remove unused imports and deprecations across Kotlin files      │  │
│  └───────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────┘
```
