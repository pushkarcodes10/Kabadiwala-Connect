# Kabadiwala Connect

**TL;DR:** Kabadiwala Connect is an intelligent, decentralized circular economy and scrap management platform for Android. We digitize India's informal waste collection ecosystem by pairing on-device computer vision with live Mandi commodity pricing, verified recycler discovery, and transparent doorstep pickup scheduling. Users don't just sell scrap; they receive instant AI-powered material classification, fair-market valuation, certified green recycler matchmaking, and quantifiable environmental impact telemetry across 28 native Indian languages.

---

## 🌍 Overview (For Non-Technical Readers)

Historically, selling household or industrial scrap (*kabadi*) in India has been a fragmented, opaque, and manual process: fluctuating unverified mandi prices, unreliable weighing scales, middleman cuts, hazardous mishandling of toxic e-waste, and an inability to find certified recycling facilities.

**Kabadiwala Connect solves this.**

We have built a professional-grade digital circular economy platform that bridges the gap between households, informal waste collectors (*kabadiwalas*), aggregators, and authorized recycling plants. When you have scrap to dispose of—ranging from daily newspapers and PET bottles to industrial brass, copper wiring, and e-waste—you simply snap a photo or select the material category.

Our on-device AI scrap scanner instantly inspects the scrap, identifies its material sub-grade, assesses condition and purity, and matches it with live Mandi market rates. You can discover nearby verified recyclers, compare live purchase rates, schedule doorstep pickups, generate digital transaction receipts, and track your environmental contribution (trees spared, carbon emissions prevented, water conserved).

To ensure zero barrier to entry across diverse demographics, the platform provides seamless runtime localization across **28 native Indian languages**.

---

## ⚙️ Architecture Deep Dive (For Technical Readers)

Kabadiwala Connect is engineered on a modern, reactive, offline-first Android architecture adhering strictly to Clean Architecture and MVVM principles. The core loop consists of:

$$\text{Photo / Input} \longrightarrow \text{Edge ML Vision Inference} \longrightarrow \text{Mandi Valuation Engine} \longrightarrow \text{Recycler Matchmaking} \longrightarrow \text{Doorstep Pickup Scheduling} \longrightarrow \text{ESG Ledger Update}$$

```
+---------------------------------------------------------------------------------------+
|                                    PRESENTATION LAYER                                 |
|               Jetpack Compose  *  Material 3  *  Navigation Compose                   |
+---------------------------------------------------------------------------------------+
        |                                       |                               |
        v                                       v                               v
+-----------------------+           +-----------------------+       +-------------------+
|  Home & Transactions  |           | Material Vision & Cart|       | Recycler Discovery|
|  (StateFlow / MVI)    |           | (CameraX / ML Kit)    |       | (Geo & Filtering) |
+-----------------------+           +-----------------------+       +-------------------+
        \                                       |                               /
         +--------------------------------------+------------------------------+
                                                |
                                                v
+---------------------------------------------------------------------------------------+
|                                      DOMAIN LAYER                                     |
|           KabadiwalaRepository  *  MaterialRecognitionService  *  LanguageManager     |
+---------------------------------------------------------------------------------------+
        |                                       |                               |
        v                                       v                               v
+-----------------------+           +-----------------------+       +-------------------+
|   Google ML Kit       |           |   Room ORM & Cache    |       | Dynamic Locale    |
|   (Image Labeler)     |           |   (Offline First)     |       | (28 Languages)    |
+-----------------------+           +-----------------------+       +-------------------+
```

### The 4 Core Pillars

#### 1. Reactive Presentation Layer (Jetpack Compose & Material 3)
A 100% declarative UI built with Jetpack Compose (BOM `2024.05.00`) and Material Design 3. The UI layer eliminates traditional Android XML layouts in favor of reactive composables bound to unidirectional data flows (`StateFlow` / `ViewModel`).
* **Micro-interactions & Aesthetics:** Custom animated surface containers, categorized scrap cards, glassmorphic cards, and dynamic theme tokens defined in `KabadiwalaColors`.
* **State Management:** Screens observe immutable UI states emitted by ViewModels (`HomeViewModel`, `MaterialEntryViewModel`, `RecyclerDiscoveryViewModel`, `TransactionViewModel`).
* **Permissions & Media:** Accompanist permissions integrate seamlessly with the camera pipeline, while Coil provides asynchronous, cached bitmap rendering.

#### 2. Edge ML Vision & Scrap Classifier (Google ML Kit + Vision Heuristics)
The application carries an edge computer vision pipeline for real-time scrap inspection without requiring an active internet connection.
* **On-Device Labeling:** Utilizes Google ML Kit Vision API (`ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)`) to extract visual primitives and classification tags.
* **Domain Scrap Categorization (`MaterialCategoryMapper`):** Custom mapping algorithms evaluate detected ML labels and RGB visual profiles to categorize scrap into 8 main streams: *Metals, Paper, Plastics, Electronics/E-Waste, Rubber, Glass, Textiles,* and *Packaging*.
* **Purity & Mandi Prediction:** Evaluates surface condition, yields confidence scores (78%–98%), and computes purity indexes (84%–98%) to produce dynamic mandi valuation estimates.

#### 3. Domain Repository & Offline-First Persistence (Room DB & Coroutines)
A robust data layer designed around the Repository pattern acting as the Single Source of Truth (SSOT).
* **Asynchronous Concurrency:** Dispatches IO-intensive tasks via Kotlin Coroutines (`Dispatchers.IO`) to guarantee zero frame drops on the main thread.
* **Data Serialization:** High-performance payload serialization powered by `kotlinx.serialization` and `Gson` for complex nested transaction records and pickup orders.
* **Dynamic Price Indexing:** Aggregates real-time daily mandi prices per kilogram across 40+ granular commodities with historical trends and volatility metrics.

#### 4. Pan-India Localization Engine (28 Indian Languages)
A zero-restart runtime localization framework supporting pan-India inclusivity:
* **Coverage:** Supports 28 languages, encompassing all 22 Eighth Schedule official languages (Hindi, Bengali, Marathi, Telugu, Tamil, Gujarati, Urdu, Kannada, Odia, Malayalam, Punjabi, Assamese, Maithili, Santali, Kashmiri, Nepali, Sindhi, Konkani, Dogri, Manipuri, Bodo, Sanskrit) plus English, English (India), Bhojpuri, Rajasthani, Chhattisgarhi, and Haryanvi.
* **Dynamic Context Injection:** Utilizes Android `ConfigurationContext` override coupled with Compose `CompositionLocalProvider` (`LocalAppStrings`, `LocalCurrentLanguage`).
* **Zero Activity Restart:** Switch languages on the fly through `LanguageSelectionDialog` without resetting current navigation backstack or in-flight user forms.

### Security, Safety & ESG Compliance Framework
* **Hazardous Scrap Segregation:** Intelligent warning triggers detect bio-hazards, damaged lithium batteries, and lead-acid battery plates. Hazardous items are locked out of regular pickups and routed strictly to authorized e-waste centers.
* **Transparent Verification:** All recyclers are vetted with verified badges, explicit operating permits, accepted scrap types, transparent per-kg pricing, and direct phone contact capabilities.
* **Environmental Impact Metrics:** Every closed transaction computes equivalent environmental offsets (metric tons of CO₂ emissions avoided, trees saved, water conserved, and electricity saved) presented via interactive telemetry cards.

---

## 🛠️ Technology Stack

| Domain | Technology / Library | Version / Details |
|---|---|---|
| **Platform** | Android OS | Min SDK 26 (Android 8.0) • Target SDK 34 (Android 14) |
| **Language** | Kotlin & Java | Kotlin 1.9.x • JVM Target 1.8 |
| **UI Framework** | Jetpack Compose | Compose BOM `2024.05.00` • Material 3 • Navigation Compose |
| **Edge AI / ML** | Google ML Kit Vision | `image-labeling:17.0.9` (On-device neural inference) |
| **Architecture** | MVVM + Clean Architecture | Unidirectional Data Flow, StateFlow, Coroutines 1.7.3 |
| **Local Storage** | AndroidX Room | Room Runtime & KTX `2.6.1` • SharedPreferences |
| **Image Loading** | Coil Compose | `io.coil-kt:coil-compose:2.5.0` |
| **Permissions** | Google Accompanist | `accompanist-permissions:0.32.0` |
| **Serialization** | Kotlinx Serialization & Gson | JSON serialization for transaction & material models |
| **Localization** | Custom Context Engine | 28 Indian languages with runtime dynamic locale switching |

---

## 🚀 Local Development Guide

### Prerequisites
* **Android Studio:** Hedgehog (2023.1.1) / Iguana (2023.2.1) / Ladybug or newer
* **Java Development Kit:** JDK 17 (recommended for Gradle 8.x)
* **Android SDK Platform:** API 34 (Upside Down Cake)
* **Physical Device or Android Emulator:** Camera and Location permissions enabled

### Step-by-Step Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/pushkarcodes10/Kabadiwala-Connect.git
cd "Kabadiwala Connect"
```

#### 2. Open Project & Sync Gradle
Open the project folder inside Android Studio. Allow Gradle to download dependencies and build the index. Alternatively, build via CLI:

```bash
# On Windows (PowerShell / Command Prompt)
.\gradlew.bat assembleDebug

# On Linux / macOS
./gradlew assembleDebug
```

#### 3. Run on Emulator or Physical Device
Ensure ADB is enabled and your device is connected (`adb devices`):

```bash
# Install and launch debug APK on connected device
.\gradlew.bat installDebug
```

#### 4. Run Automated Tests
```bash
# Run local unit tests
.\gradlew.bat testDebugUnitTest

# Run connected Android instrumentation tests
.\gradlew.bat connectedDebugAndroidTest
```

---

## 📂 Directory Structure

```
Kabadiwala-Connect/
├── .idea/                                 # Android Studio workspace settings
├── gradle/                                # Gradle wrapper binaries and definitions
├── app/
│   ├── build.gradle.kts                   # App module build configuration & dependencies
│   ├── proguard-rules.pro                 # R8 / ProGuard obfuscation rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml        # Permissions, providers, and main activity
│       │   ├── assets/                    # Static catalogs and configuration assets
│       │   ├── java/com/kabadiwalaconnect/
│       │   │   ├── KabadiwalaApplication.kt # Application lifecycle & repository init
│       │   │   ├── MainActivity.kt        # Root Activity & CompositionLocal setup
│       │   │   ├── data/
│       │   │   │   ├── model/             # Domain data models
│       │   │   │   │   ├── Material.kt    # Scrap categories, prices & image resources
│       │   │   │   │   ├── Recycler.kt    # Recycler profiles, ratings, addresses
│       │   │   │   │   ├── Transaction.kt # Orders, pickup tracking, bills
│       │   │   │   │   ├── MarketPrice.kt # Live Mandi commodity rates
│       │   │   │   │   ├── Safety.kt      # Hazard guidelines & handling instructions
│       │   │   │   │   └── Earnings.kt    # Revenue ledger & carbon offset metrics
│       │   │   │   ├── repository/        # Repository interface & implementation
│       │   │   │   │   ├── KabadiwalaRepository.kt
│       │   │   │   │   └── KabadiwalaRepositoryImpl.kt
│       │   │   │   └── service/           # Device services & scanner engine
│       │   │   │       └── MaterialRecognitionService.kt
│       │   │   ├── ml/                    # Edge Machine Learning
│       │   │   │   ├── MaterialCategoryMapper.kt # ML Kit label to scrap category mapper
│       │   │   │   └── PhotoPriceScanner.kt      # Scanner orchestration
│       │   │   ├── navigation/            # Navigation routing & screen graphs
│       │   │   │   └── Navigation.kt      # Type-safe destinations & deep links
│       │   │   ├── language/              # Pan-India Localization System
│       │   │   │   ├── LanguageManager.kt # 28-language registry & configuration wrapper
│       │   │   │   ├── LanguageViewModel.kt
│       │   │   │   ├── AppStrings.kt      # Core UI strings
│       │   │   │   ├── UiTranslations.kt  # Extended UI dictionaries
│       │   │   │   └── EntityTranslations.kt # Translated material & category names
│       │   │   ├── ui/
│       │   │   │   ├── theme/             # Color tokens, Typography & Material 3 Theme
│       │   │   │   ├── components/        # Reusable UI widgets (cards, dialogs, navbars)
│       │   │   │   └── screens/           # Feature screens
│       │   │   │       ├── HomeScreen.kt
│       │   │   │       ├── MaterialScannerScreen.kt
│       │   │   │       ├── MaterialEntryScreen.kt
│       │   │   │       ├── RecyclerDiscoveryScreen.kt
│       │   │   │       ├── RecyclerDetailScreen.kt
│       │   │   │       ├── MarketPricesScreen.kt
│       │   │   │       ├── TransactionScreen.kt
│       │   │   │       ├── TransactionDetailScreen.kt
│       │   │   │       ├── EarningsScreen.kt
│       │   │   │       ├── SafetyScreen.kt
│       │   │   │       └── SplashIntroFlow.kt
│       │   │   └── viewmodel/             # ViewModels & ViewModelFactory
│       │   └── res/                       # Drawables, mipmaps, strings, XML configs
├── build.gradle.kts                       # Top-level Gradle build configuration
├── settings.gradle.kts                    # Module declarations & plugin repositories
├── gradle.properties                      # JVM args and AndroidX configuration
└── Readme.md                              # Project documentation
```
