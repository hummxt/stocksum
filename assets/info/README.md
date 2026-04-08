# Stocksum - Stock Tracking Android App

> A simple, powerful stock tracking app combining portfolio management, market insights, and price alerts.

**Status:** 🚀 Ready to Build  
**Build Time:** 4-6 weeks  
**Difficulty:** Medium  
**Platform:** Android (Kotlin)

---

## Table of Contents
- [Quick Overview](#quick-overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [API Setup](#api-setup)
- [Database Setup](#database-setup)
- [Development Workflow](#development-workflow)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

---

## Quick Overview

**Stocksum** is an all-in-one stock tracking app for individual investors.

**What you can do:**
- 📊 View daily market summary (gainers, losers, movers)
- 💼 Track your stock portfolio with live prices
- 📈 See real-time gains/losses on your investments
- 🔔 Get alerts when stocks hit your target prices
- 🌍 Track stocks from USA (NYSE/NASDAQ) and India (NSE/BSE)

**Who is it for:**
- Retail investors who want simple portfolio tracking
- People learning about stock market
- Anyone tracking specific stocks

**Why build this:**
- Real market demand (millions of investors need this)
- Multiple revenue opportunities (premium features, ads, data partnerships)
- Great learning project for Android development
- Scalable (add crypto, forex, options later)

---

## Features

### Phase 1: MVP (Weeks 1-3)
- ✅ **Portfolio Tracker** — Add/edit/delete stocks you own
- ✅ **Live Prices** — Real-time stock prices (updated every 5 mins)
- ✅ **P&L Calculation** — See gains/losses in rupees and percentage
- ✅ **Market Summary** — Top gainers, losers, most active stocks
- ✅ **Multi-Market Support** — USA & India stocks

### Phase 2: Alerts (Week 4)
- ✅ **Price Alerts** — Notify when price hits target
- ✅ **Push Notifications** — Get alerts even when app is closed
- ✅ **Alert Management** — View/edit/delete alerts

### Phase 3+: Advanced (Future)
- 📱 Cloud sync (use portfolio on multiple devices)
- 📊 Historical charts and analysis
- 📰 Stock news feed
- 👥 Social features (share portfolio)
- 🔐 User authentication
- 💰 Cryptocurrency support
- 💱 Forex tracking

---

## Getting Started

### Prerequisites

**You need:**
1. **Android Studio** (latest version)
2. **Android SDK** (API 24 or higher)
3. **Kotlin** (1.8+)
4. **Git** (for version control)
5. **API Keys:**
   - Alpha Vantage (free at alphavantage.co)
   - Finnhub (free at finnhub.io)
   - Firebase account (free at firebase.google.com)

**Knowledge needed:**
- Basic Kotlin programming
- Android fundamentals (Activities, Fragments, Services)
- REST API concepts
- SQL basics
- Git basics

---

## Installation

### Step 1: Clone or Create Project

```bash
# Create new Android project
mkdir Stocksum
cd Stocksum

# Or clone if you already have repo
git clone <your-repo-url>
cd Stocksum
```

### Step 2: Open in Android Studio

```bash
# Open Android Studio
android-studio .
```

### Step 3: Create Project Structure

```bash
# Inside app/src/main/java/com/yourname/Stocksum/

mkdir -p data/{api,database/{entities,dao},models,repository}
mkdir -p ui/{screens,viewmodels,components}
mkdir -p domain/usecases
mkdir -p workers
mkdir -p di
```

### Step 4: Add Dependencies

Edit `app/build.gradle`:

```gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.yourname.Stocksum"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
    }
}

dependencies {
    // Core Android
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.core:core-ktx:1.10.1'
    
    // Jetpack Compose (UI)
    implementation 'androidx.compose.ui:ui:1.5.0'
    implementation 'androidx.compose.material:material:1.5.0'
    implementation 'androidx.activity:activity-compose:1.7.2'
    
    // Lifecycle
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    
    // Room (Database)
    implementation 'androidx.room:room-runtime:2.5.2'
    kapt 'androidx.room:room-compiler:2.5.2'
    implementation 'androidx.room:room-ktx:2.5.2'
    
    // Retrofit (HTTP)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.3.1')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-messaging-ktx'
    
    // Hilt (Dependency Injection)
    implementation 'com.google.dagger:hilt-android:2.46'
    kapt 'com.google.dagger:hilt-compiler:2.46'
    implementation 'androidx.hilt:hilt-work:1.0.1'
    
    // WorkManager (Background Tasks)
    implementation 'androidx.work:work-runtime-ktx:2.8.1'
    
    // JSON Parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Charts
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

Then sync Gradle files.

### Step 5: Enable View Binding

In `app/build.gradle`:

```gradle
android {
    ...
    buildFeatures {
        viewBinding true
        compose true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}
```

### Step 6: Set Up Permissions

In `AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.yourname.Stocksum">

    <!-- Internet for API calls -->
    <uses-permission android:name="android.permission.INTERNET" />
    
    <!-- For background tasks -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
    <!-- For notifications -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.Stocksum">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

## API Setup

### Get Alpha Vantage API Key (FREE)

1. Visit https://www.alphavantage.co/
2. Click "GET FREE API KEY"
3. Enter your email
4. Check email for API key
5. Store it safely (add to local.properties)

### Get Finnhub API Key (FREE)

1. Visit https://finnhub.io/
2. Sign up (free account)
3. Go to Dashboard → API keys
4. Copy your key
5. Store it safely

### Store API Keys Securely

Create `local.properties` (in root directory):

```properties
ALPHA_VANTAGE_KEY=your_key_here
FINNHUB_KEY=your_key_here
```

**Never** commit this file to Git!

Access in code:

```kotlin
object ApiKeys {
    val alphaVantageKey: String
        get() = BuildConfig.ALPHA_VANTAGE_KEY
    
    val finnhubKey: String
        get() = BuildConfig.FINNHUB_KEY
}
```

### Set Up Firebase

1. Go to https://firebase.google.com/
2. Click "Get Started"
3. Create new project: "Stocksum"
4. Enable Firestore Database
5. Enable Cloud Messaging (for push notifications)
6. Download `google-services.json`
7. Place in `app/` directory
8. Enable plugin in `build.gradle`:

```gradle
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'com.google.gms.google-services'
}
```

---

## Database Setup

### Create Room Database

`data/database/AppDatabase.kt`:

```kotlin
@Database(
    entities = [User::class, Portfolio::class, StockPrice::class, PriceAlert::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun stockPriceDao(): StockPriceDao
    abstract fun alertDao(): AlertDao
    
    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stock_watch_db"
                ).build().also { instance = it }
            }
        }
    }
}
```

### Create Entities

`data/database/entities/Portfolio.kt`:

```kotlin
@Entity(tableName = "portfolio")
data class Portfolio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val quantity: Double,
    val purchasePrice: Double,
    val market: String, // NYSE, NASDAQ, NSE, BSE
    val purchaseDate: String
)
```

---

## Development Workflow

### Week 1: Database & API Setup

```bash
# Day 1-2: Create entities and DAOs
# Files to create:
#   - User.kt, Portfolio.kt, StockPrice.kt, PriceAlert.kt
#   - UserDao.kt, PortfolioDao.kt, etc.

# Day 3-4: Create API services
# Files to create:
#   - AlphaVantageApi.kt
#   - FinnhubApi.kt
#   - ApiModels.kt

# Day 5-7: Create repositories
# Files to create:
#   - StockRepository.kt
#   - PortfolioRepository.kt
#   - AlertRepository.kt
```

### Week 2: UI & ViewModels

```bash
# Day 1-3: Create screens
# Files to create:
#   - ui/screens/MarketSummaryScreen.kt
#   - ui/screens/PortfolioScreen.kt
#   - ui/screens/AlertsScreen.kt

# Day 4-7: Create ViewModels
# Files to create:
#   - ui/viewmodels/MarketViewModel.kt
#   - ui/viewmodels/PortfolioViewModel.kt
```

### Week 3: Features & Testing

```bash
# Day 1-3: Implement core logic
# Day 4-5: Test on emulator/device
# Day 6-7: Bug fixes and polish
```

### Week 4: Alerts (Optional)

```bash
# Day 1-4: Implement price alerts and notifications
# Day 5-7: Testing and refinement
```

### Building & Running

```bash
# Build APK
./gradlew build

# Run on emulator/device
./gradlew installDebug

# Build release APK
./gradlew bundleRelease

# View logs
adb logcat | grep "YourTag"
```

---

## Git Workflow

```bash
# Initialize git
git init

# Create .gitignore
echo "local.properties
.gradle/
build/
.DS_Store
*.apk" > .gitignore

# First commit
git add .
git commit -m "Initial project setup"

# Create branches for features
git checkout -b feature/portfolio-tracker
# ... make changes ...
git add .
git commit -m "Add portfolio tracker feature"
git checkout main
git merge feature/portfolio-tracker
```

---

## Testing

### Unit Tests

`src/test/java/com/yourname/Stocksum/PortfolioCalculationTest.kt`:

```kotlin
class PortfolioCalculationTest {
    @Test
    fun calculatePLCorrectly() {
        // Arrange
        val portfolio = Portfolio(
            symbol = "AAPL",
            quantity = 10.0,
            purchasePrice = 150.0
        )
        val currentPrice = 160.0
        
        // Act
        val pnl = (currentPrice - portfolio.purchasePrice) * portfolio.quantity
        val roi = (pnl / (portfolio.purchasePrice * portfolio.quantity)) * 100
        
        // Assert
        assertEquals(100.0, pnl)
        assertEquals(6.67, roi, 0.01)
    }
}
```

### UI Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class PortfolioScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun portfolioDisplaysCorrectly() {
        composeTestRule.setContent {
            PortfolioScreen()
        }
        
        composeTestRule.onNodeWithText("Portfolio").assertIsDisplayed()
    }
}
```

---

## Common Issues & Solutions

### Issue: API calls too slow
**Solution:** Add request timeout and caching
```kotlin
val timeout = 10L
httpClient.connectTimeout(timeout, TimeUnit.SECONDS)
httpClient.readTimeout(timeout, TimeUnit.SECONDS)
```

### Issue: Rate limits hit
**Solution:** Cache responses locally and reduce API calls
```kotlin
// Check if data is fresh (less than 5 mins old)
if (System.currentTimeMillis() - lastFetchTime < 5 * 60 * 1000) {
    return cachedData
}
```

### Issue: Large database causing crashes
**Solution:** Paginate queries and use pagination
```kotlin
@Query("SELECT * FROM portfolio LIMIT :limit OFFSET :offset")
suspend fun getPortfolioPaginated(limit: Int, offset: Int): List<Portfolio>
```

### Issue: Background work not triggering
**Solution:** Check WorkManager configuration
```kotlin
// Ensure battery optimization is off for your app
// Settings → Apps → Stocksum → Battery → Don't restrict
```

---

## Performance Tips

- **Minimize API calls:** Cache aggressively
- **Use coroutines:** Don't block main thread
- **Lazy load data:** Show UI first, load data second
- **Optimize database:** Index frequently queried columns
- **Compress images:** Use WebP format
- **Profile app:** Use Android Profiler in Android Studio

---

## Deployment Checklist

- [ ] App tested on Android 8.0+ devices
- [ ] All APIs tested and working
- [ ] No crashes or ANRs
- [ ] Battery drain acceptable (<5% per hour)
- [ ] Network usage reasonable
- [ ] Permissions minimized
- [ ] Privacy policy written
- [ ] Screenshots prepared (5-8)
- [ ] App description written
- [ ] Icon designed (512x512 PNG)
- [ ] Signed APK/AAB built
- [ ] Google Play Store listing created
- [ ] Content rating completed
- [ ] App submitted for review

---

## Marketing Ideas

**Before Launch:**
- Post on r/androiddev, r/stocks
- Share on Twitter #AndroidDevelopment
- Get early testers (friends, family)

**After Launch:**
- YouTube tutorial: "How to use Stocksum"
- Medium blog: "Building a Stock Tracker App"
- ProductHunt submission
- LinkedIn posts
- Reddit threads answering questions

---

## Resources

### Official Documentation
- Android Docs: https://developer.android.com
- Jetpack Compose: https://developer.android.com/jetpack/compose
- Room Database: https://developer.android.com/training/data-storage/room

### APIs
- Alpha Vantage: https://www.alphavantage.co/documentation/
- Finnhub: https://finnhub.io/docs/api
- Firebase: https://firebase.google.com/docs

### Learning
- Android Developers YouTube: https://www.youtube.com/c/AndroidDevelopers
- Kotlin Docs: https://kotlinlang.org/docs/
- Jetpack Compose Codelabs: https://developer.android.com/codelabs

### Community
- r/androiddev (Reddit)
- Stack Overflow (tag: android)
- Android Slack community
- GitHub discussions

---

## Next Steps

1. **Right now:** Read technical requirements document
2. **Day 1:** Set up Android Studio and create project
3. **Day 2:** Add all dependencies and set up database
4. **Day 3-4:** Create API integrations
5. **Day 5:** Start building UI and ViewModels
6. **Week 2+:** Complete features and launch MVP

---

## License

MIT License - Hummet Azim

