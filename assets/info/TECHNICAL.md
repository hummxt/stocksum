# Stock Tracking App - Technical Requirements & Documentation

## Project Overview

**App Name:** Stocksum (or similar)

**Concept:** A unified stock tracking application combining:
1. **Daily Market Summary** — Top gainers, losers, most active stocks
2. **Price Alerts** — Notify users when stocks hit target prices
3. **Portfolio Tracker** — Track owned stocks with live P&L calculations

**Target Markets:** USA (NYSE/NASDAQ), India (NSE/BSE)

**Build Time Estimate:** 4-6 weeks

---

## Table of Contents
1. [App Features](#app-features)
2. [Technical Architecture](#technical-architecture)
3. [Required Skills](#required-skills)
4. [Tech Stack](#tech-stack)
5. [Database Design](#database-design)
6. [API Integration](#api-integration)
7. [Development Phases](#development-phases)
8. [Deployment & Launch](#deployment--launch)

---

## App Features

### Feature 1: Daily Market Summary
**Description:** Home screen showing real-time market overview

**User Story:**
- User opens app
- See top 5 gainers (stocks up most %)
- See top 5 losers (stocks down most %)
- See top 5 most active (highest volume)
- Each stock shows: symbol, price, change %, change amount
- Click on stock → detailed view

**Technical Requirements:**
- Fetch market data every 5-10 minutes
- Cache data locally (if offline, show cached version)
- Display charts/sparklines for each stock
- Support filtering by market (USA/India)

---

### Feature 2: Portfolio Tracker
**Description:** Users add stocks they own and track performance

**User Story:**
- User clicks "Add Stock" button
- Search for stock symbol (AAPL, TCS, etc.)
- Enter quantity owned & purchase price
- See live portfolio value
- See total gain/loss in rupees and percentage
- See individual stock P&L
- Edit/delete holdings

**Technical Requirements:**
- Store user portfolio in local database + cloud sync
- Calculate: Current Value = Quantity × Current Price
- Calculate: Gain/Loss = (Current Price - Purchase Price) × Quantity
- Calculate: ROI % = (Gain/Loss / Investment) × 100
- Real-time price updates
- Data persistence (survive app restart)

---

### Feature 3: Price Alerts
**Description:** Notify users when stock reaches target price

**User Story:**
- User clicks "Set Alert" on any stock
- Enter target price & alert type:
  - "Alert me when price goes ABOVE $150"
  - "Alert me when price goes BELOW $150"
- Turn on/off notifications
- See list of active alerts
- Delete alerts

**Technical Requirements:**
- Background service checking prices
- Push notifications (FCM - Firebase Cloud Messaging)
- Alert history (when alert triggered?)
- One-time or recurring alerts
- Sound/vibration options

---

## Technical Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Android App                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │  UI Layer (Jetpack Compose or XML layouts)        │ │
│  │  - Market Summary Screen                          │ │
│  │  - Portfolio Screen                               │ │
│  │  - Alerts Screen                                  │ │
│  └────────────────────────────────────────────────────┘ │
│                          ↓                               │
│  ┌────────────────────────────────────────────────────┐ │
│  │  Business Logic Layer (ViewModels, Repositories)  │ │
│  │  - Portfolio calculations                         │ │
│  │  - Alert management                               │ │
│  │  - Data formatting                                │ │
│  └────────────────────────────────────────────────────┘ │
│                          ↓                               │
│  ┌────────────────────────────────────────────────────┐ │
│  │  Local Database (Room)                            │ │
│  │  - User portfolios                                │ │
│  │  - Alert preferences                              │ │
│  │  - Cached stock data                              │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
         ↓                              ↑
         └──────────────────────────────┘
              (Network Requests)
                      ↓
┌─────────────────────────────────────────────────────────┐
│              Backend Services (Node.js/Firebase)        │
│  ┌────────────────────────────────────────────────────┐ │
│  │  API Server                                        │ │
│  │  - User authentication                            │ │
│  │  - Portfolio sync                                 │ │
│  │  - Alert trigger logic                            │ │
│  │  - Push notifications                             │ │
│  └────────────────────────────────────────────────────┘ │
│                          ↓                               │
│  ┌────────────────────────────────────────────────────┐ │
│  │  Database (Firebase Firestore or PostgreSQL)      │ │
│  │  - User data                                       │ │
│  │  - Portfolios                                      │ │
│  │  - Alerts                                          │ │
│  └────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
         ↓
┌─────────────────────────────────────────────────────────┐
│           External Stock APIs                           │
│  - Alpha Vantage (USA stocks)                          │
│  - Finnhub (Global + India)                            │
│  - NSE/BSE APIs (India specific)                       │
└─────────────────────────────────────────────────────────┘
```

### Data Flow

**On App Launch:**
1. Check internet connection
2. Fetch latest market data from stock APIs
3. Update local cache
4. Calculate portfolio values
5. Check if any alerts should trigger
6. Display UI with fresh data

**On Background (Every 5-10 mins):**
1. Fetch stock prices
2. Compare with alert thresholds
3. Trigger notifications if alert met
4. Sync portfolio to cloud (if user has account)

---

## Required Skills

### Android Development Skills
- **Kotlin** (main language) or Java
- **Android Jetpack** libraries:
  - Room (local database)
  - ViewModel & LiveData
  - Repository pattern
- **Retrofit** (HTTP requests)
- **Firebase** (authentication, push notifications, cloud storage)
- **Jetpack Compose** or XML layouts (UI)

### Backend Skills (Optional for MVP)
- **Node.js** or **Firebase Functions** (serverless)
- **Express.js** (if using Node)
- **PostgreSQL** or **Firebase Firestore** (database)
- **REST API design**

### Other Skills
- **API integration** (consuming third-party APIs)
- **Background services** & scheduling (WorkManager)
- **Data caching** strategies
- **Git** & version control

---

## Tech Stack

### Frontend (Android)
```
Language:           Kotlin
UI Framework:       Jetpack Compose (modern) OR XML Layouts (traditional)
Architecture:       MVVM (Model-View-ViewModel)
Database:           Room Database
HTTP Client:        Retrofit
JSON Parsing:       Gson or Kotlinx Serialization
Background Tasks:   WorkManager
Push Notifications: Firebase Cloud Messaging (FCM)
Authentication:     Firebase Auth
Cloud Storage:      Firebase Firestore / Realtime Database
Charting:           MPAndroidChart or AnyChart
Build Tool:         Gradle
```

### Backend (Optional)
```
Runtime:            Node.js
Framework:          Express.js or Firebase Functions
Database:           Firebase Firestore or PostgreSQL
Task Scheduler:     Node-cron or Google Cloud Scheduler
Push Notifications: Firebase Cloud Messaging Admin SDK
Hosting:            Firebase Hosting or AWS/GCP
```

### External APIs
```
Stock Data:         Alpha Vantage (free tier: 5 req/min)
                    Finnhub (free tier: 60 req/min)
                    IEX Cloud
India Stocks:       NSE/BSE direct APIs
                    Upstox/Zerodha APIs (if partnership)
```

---

## Database Design

### Local Database (Room - Android)

**Table: User**
```sql
CREATE TABLE User (
    userId TEXT PRIMARY KEY,
    email TEXT NOT NULL,
    name TEXT,
    createdAt TIMESTAMP,
    country TEXT  -- 'USA' or 'INDIA'
);
```

**Table: Portfolio**
```sql
CREATE TABLE Portfolio (
    portfolioId TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    stockSymbol TEXT NOT NULL,
    quantity REAL NOT NULL,
    purchasePrice REAL NOT NULL,
    purchaseDate DATE,
    market TEXT,  -- 'NYSE', 'NASDAQ', 'NSE', 'BSE'
    FOREIGN KEY(userId) REFERENCES User(userId)
);
```

**Table: StockPrice (Cache)**
```sql
CREATE TABLE StockPrice (
    symbol TEXT PRIMARY KEY,
    price REAL NOT NULL,
    change REAL,
    changePercent REAL,
    market TEXT,
    lastUpdated TIMESTAMP
);
```

**Table: PriceAlert**
```sql
CREATE TABLE PriceAlert (
    alertId TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    symbol TEXT NOT NULL,
    targetPrice REAL NOT NULL,
    alertType TEXT,  -- 'ABOVE' or 'BELOW'
    isActive BOOLEAN,
    createdAt TIMESTAMP,
    triggeredAt TIMESTAMP NULL,
    FOREIGN KEY(userId) REFERENCES User(userId)
);
```

### Cloud Database (Firestore)

**Collection: users/**
```json
{
  "userId": "user123",
  "email": "user@example.com",
  "portfolio": [
    {
      "symbol": "AAPL",
      "quantity": 10,
      "purchasePrice": 150.00,
      "market": "NASDAQ"
    }
  ],
  "alerts": [
    {
      "alertId": "alert1",
      "symbol": "AAPL",
      "targetPrice": 160.00,
      "type": "ABOVE"
    }
  ]
}
```

---

## API Integration

### Stock Data APIs

#### Option 1: Alpha Vantage (Recommended for beginners)
```
API Key:        Free (get from alphavantage.co)
Rate Limit:     5 requests/minute (free tier)
Endpoints:
  - GLOBAL_QUOTE: Get current price
  - TIME_SERIES_DAILY: Historical data
  - SECTOR_PERFORMANCE: Market sectors

Example Request:
https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=AAPL&apikey=YOUR_KEY

Example Response:
{
  "Global Quote": {
    "01. symbol": "AAPL",
    "02. price": "175.43",
    "09. change": "+2.15",
    "10. change percent": "+1.24%"
  }
}
```

#### Option 2: Finnhub (Better for production)
```
API Key:        Free (get from finnhub.io)
Rate Limit:     60 requests/minute (free tier)
Endpoints:
  - quote: Real-time quote
  - movers: Top gainers/losers
  - profile: Company info

Example Request:
https://finnhub.io/api/v1/quote?symbol=AAPL&token=YOUR_KEY

Example Response:
{
  "c": 175.43,      // Current price
  "d": 2.15,        // Change
  "dp": 1.24,       // Change percent
  "h": 176.50,      // High
  "l": 174.20,      // Low
  "o": 174.95,      // Open
  "pc": 173.28,     // Previous close
  "t": 1234567890   // Timestamp
}
```

#### Option 3: India Stocks (NSE/BSE)
```
Option A: NSE Direct API
  - Rate Limit: Varies
  - Endpoint: https://www.nseindia.com/api/quote-equity?symbol=TCS

Option B: Upstox/Zerodha (Partner APIs)
  - More reliable
  - Requires API key
  - Better data quality

Example Response (NSE):
{
  "symbol": "TCS",
  "ltp": 3450.50,
  "change": 25.00,
  "changePercent": 0.73,
  "high": 3455.00,
  "low": 3420.00
}
```

### Implementation Strategy
```
1. Start with Alpha Vantage (free, simple, global)
2. Add Finnhub for better rate limits
3. Add NSE/BSE APIs for India support
4. Cache responses locally to minimize API calls
5. Implement fallback (cached data if API fails)
```

---

## Development Phases

### Phase 1: MVP (2-3 weeks) - Launch Ready
**Goal:** Get app on Play Store

**Features:**
- ✅ Portfolio tracker (add/edit/delete stocks)
- ✅ Real-time price display
- ✅ Portfolio P&L calculation
- ✅ Daily market summary
- ❌ Price alerts (defer to Phase 2)

**Deliverables:**
- Working Android app
- Local database setup
- API integration (Alpha Vantage)
- Basic UI

---

### Phase 2: Alerts & Notifications (1-2 weeks)
**Goal:** Add core alert functionality

**Features:**
- ✅ Set price alerts
- ✅ Push notifications
- ✅ Alert management UI
- ✅ Background service for checking

**Deliverables:**
- Background worker service
- Firebase FCM setup
- Alert UI screens

---

### Phase 3: Cloud Sync & Auth (1-2 weeks)
**Goal:** Multi-device support

**Features:**
- ✅ User authentication
- ✅ Cloud sync (Firestore)
- ✅ Portfolio sync across devices
- ✅ Alert history

---

### Phase 4: Enhanced Features (Ongoing)
**Features to add later:**
- Watchlist without purchase details
- Charts & historical analysis
- News feed integration
- Social features (share portfolio)
- Crypto support
- Forex tracking

---

## Step-by-Step Implementation Guide

### Week 1: Setup & Basic UI
```
Day 1-2: Android Project Setup
  - Create new Android project (Kotlin)
  - Set up Jetpack libraries
  - Create folder structure (data, ui, domain)

Day 3-4: Database Setup
  - Create Room database schema
  - Create entities (User, Portfolio, StockPrice, Alert)
  - Create DAOs (Data Access Objects)

Day 5-7: Basic UI
  - Create main screens (Portfolio, Market, Alerts)
  - Create XML layouts or Compose functions
  - Add navigation between screens
```

### Week 2: API Integration
```
Day 1-3: API Setup
  - Add Retrofit dependency
  - Create API services for Alpha Vantage
  - Create data models for API responses

Day 4-5: Market Summary
  - Fetch top gainers/losers
  - Display on home screen
  - Implement refresh mechanism

Day 6-7: Portfolio Display
  - Fetch prices for user's holdings
  - Calculate P&L
  - Display portfolio value
```

### Week 3: Core Features
```
Day 1-3: Add/Edit Portfolio
  - Implement add stock UI
  - Stock search functionality
  - Store in local database
  - Display portfolio list

Day 4-7: Calculations & Display
  - Calculate individual stock P&L
  - Calculate total portfolio value
  - Format data nicely
  - Add charts/visualizations
```

### Week 4: Alerts (if time permits)
```
Day 1-4: Backend Alert Logic
  - Create background worker
  - Implement alert checking service
  - Set up Firebase notifications

Day 5-7: UI & Polish
  - Alert management screens
  - Test notifications
  - Bug fixes
```

---

## File Structure

```
Stocksum/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yourname/Stocksum/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── AlphaVantageApi.kt
│   │   │   │   │   │   └── FinnhubApi.kt
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── entities/
│   │   │   │   │   │   │   ├── User.kt
│   │   │   │   │   │   │   ├── Portfolio.kt
│   │   │   │   │   │   │   ├── StockPrice.kt
│   │   │   │   │   │   │   └── PriceAlert.kt
│   │   │   │   │   │   └── dao/
│   │   │   │   │   │       ├── UserDao.kt
│   │   │   │   │   │       ├── PortfolioDao.kt
│   │   │   │   │   │       └── AlertDao.kt
│   │   │   │   │   ├── models/
│   │   │   │   │   │   ├── StockData.kt
│   │   │   │   │   │   └── MarketSummary.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── StockRepository.kt
│   │   │   │   │       ├── PortfolioRepository.kt
│   │   │   │   │       └── AlertRepository.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── MarketSummaryScreen.kt
│   │   │   │   │   │   ├── PortfolioScreen.kt
│   │   │   │   │   │   └── AlertsScreen.kt
│   │   │   │   │   ├── viewmodels/
│   │   │   │   │   │   ├── MarketViewModel.kt
│   │   │   │   │   │   ├── PortfolioViewModel.kt
│   │   │   │   │   │   └── AlertViewModel.kt
│   │   │   │   │   └── components/
│   │   │   │   │       ├── StockCard.kt
│   │   │   │   │       └── PriceChart.kt
│   │   │   │   ├── domain/
│   │   │   │   │   ├── usecases/
│   │   │   │   │   │   ├── GetMarketSummaryUseCase.kt
│   │   │   │   │   │   ├── CalculatePortfolioUseCase.kt
│   │   │   │   │   │   └── SetAlertUseCase.kt
│   │   │   │   ├── workers/
│   │   │   │   │   ├── PriceCheckWorker.kt
│   │   │   │   │   └── AlertTriggerWorker.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── MyApplication.kt
│   │   │   │   └── di/
│   │   │   │       └── AppModule.kt (Dependency Injection)
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── drawable/
│   │   │       └── values/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle (root)
└── README.md
```

---

## Dependencies (build.gradle)

```gradle
dependencies {
    // Kotlin
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.9.0'
    
    // Jetpack
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    
    // Jetpack Compose (UI)
    implementation 'androidx.compose.ui:ui:1.5.0'
    implementation 'androidx.compose.material:material:1.5.0'
    implementation 'androidx.activity:activity-compose:1.7.2'
    
    // Room (Database)
    implementation 'androidx.room:room-runtime:2.5.2'
    kapt 'androidx.room:room-compiler:2.5.2'
    implementation 'androidx.room:room-ktx:2.5.2'
    
    // Retrofit (HTTP Client)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Firebase
    implementation 'com.google.firebase:firebase-auth-ktx:22.1.1'
    implementation 'com.google.firebase:firebase-firestore-ktx:24.7.1'
    implementation 'com.google.firebase:firebase-messaging-ktx:23.2.1'
    
    // WorkManager (Background Tasks)
    implementation 'androidx.work:work-runtime-ktx:2.8.1'
    
    // Charts
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    
    // JSON Parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'
    
    // Hilt (Dependency Injection)
    implementation 'com.google.dagger:hilt-android:2.46'
    kapt 'com.google.dagger:hilt-compiler:2.46'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

---

## Deployment & Launch

### Pre-Launch Checklist

**Code Quality**
- [ ] All features tested on multiple devices
- [ ] No crashes or major bugs
- [ ] Performance optimized (app loads in <2 seconds)
- [ ] Battery usage reasonable
- [ ] Network requests cached properly

**Security**
- [ ] API keys not hardcoded
- [ ] User data encrypted
- [ ] No sensitive data in logs
- [ ] Password/auth implemented securely

**Documentation**
- [ ] Code commented
- [ ] API integration documented
- [ ] Database schema documented
- [ ] Build instructions clear

**Play Store Preparation**
- [ ] App icon (512x512 PNG)
- [ ] Screenshots (5-8 screenshots)
- [ ] App description & short description
- [ ] Privacy policy (required)
- [ ] Terms of service
- [ ] Permissions explained in description

### Google Play Store Launch Steps

1. **Create Google Play Developer Account** ($25 one-time)
2. **Sign APK/AAB** (Android Package Bundle)
3. **Create App Listing** on Play Console
4. **Upload build** (AAB format preferred)
5. **Fill in metadata**:
   - Title
   - Description
   - Screenshots
   - Category
   - Content rating
6. **Review policy compliance** (privacy, permissions)
7. **Submit for review** (24-48 hours typically)
8. **Staged rollout** (release to 10% → 50% → 100%)

### Marketing Strategy

**Pre-Launch**
- Share on Twitter/Reddit dev communities
- Post on ProductHunt
- Share on LinkedIn

**Post-Launch**
- Ask for reviews/ratings
- Fix bugs quickly (shows responsiveness)
- Regular updates with new features
- YouTube tutorial videos
- Blog posts about stock market investing

---

## Common Pitfalls & How to Avoid

| Pitfall | Impact | Solution |
|---------|--------|----------|
| Not caching API data | High battery drain, rate limits hit | Implement local cache, sync every 10 mins |
| Hardcoding API keys | Security breach | Use BuildConfig or secure remote config |
| No error handling | App crashes on network fail | Implement try-catch, show user-friendly errors |
| Too many features at launch | Bugs, slow development | Start with MVP, iterate |
| Poor offline support | Crashes without internet | Cache critical data locally |
| No background work | Users forget to open app | Use WorkManager for daily updates |
| Ugly UI | Low ratings, low adoption | Invest in design/UX, study Material Design |

---

## Success Metrics

Track these to measure if your app is successful:

- **Downloads** — Target: 1,000+ in first month
- **DAU (Daily Active Users)** — Target: 20% of downloads
- **Retention** — Target: 30% after 7 days
- **Crash Rate** — Target: <0.1%
- **Rating** — Target: 4.0+ stars
- **Session Length** — Target: 3+ minutes average

---

## Next Steps

1. **Set up Android development environment** (Android Studio)
2. **Create Firebase project** (for cloud features)
3. **Get API keys** (Alpha Vantage, Finnhub)
4. **Create project folder structure**
5. **Start with Phase 1** (Portfolio + Market Summary)
6. **Build, test, launch**

---

## Resources & Learning

### Android Development
- Official Android Docs: https://developer.android.com
- Jetpack Compose: https://developer.android.com/jetpack/compose
- Room Database: https://developer.android.com/training/data-storage/room

### APIs
- Alpha Vantage: https://www.alphavantage.co
- Finnhub: https://finnhub.io
- Firebase: https://firebase.google.com

### Tutorials & Courses
- Google Codelabs: https://codelabs.developers.google.com
- Kotlin Tutorial: https://kotlinlang.org/docs
- YouTube: "Android Development Tutorial" (search official Android Developers channel)

### Community
- r/androiddev (Reddit)
- Stack Overflow (tag: android)
- Android Dev Summit (annual event)

---

## Questions & Support

**Before building, ask yourself:**
1. Do you have Android development experience?
2. Can you commit 4-6 weeks full-time?
3. Do you have API keys ready?
4. Have you tested the APIs?
5. Do you want to monetize this app?

**If stuck:**
- Check Stack Overflow
- Read error logs carefully
- Search Android documentation
- Ask on r/androiddev

---

**Good luck! 🚀**

