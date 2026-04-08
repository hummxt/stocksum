# Skills Checklist & Learning Path

> Everything you need to know (or learn) to build Stocksum

---

## Table of Contents
1. [Prerequisites Check](#prerequisites-check)
2. [Android Development Skills](#android-development-skills)
3. [Backend Skills (Optional)](#backend-skills-optional)
4. [Learning Path](#learning-path)
5. [Resources by Topic](#resources-by-topic)
6. [Estimated Learning Time](#estimated-learning-time)

---

## Prerequisites Check

Before starting, honestly assess where you are:

### Question 1: Have you built Android apps before?
- [ ] **No** → You need 2-3 weeks learning before building this
- [ ] **Yes, simple apps** → You need 1 week refresher
- [ ] **Yes, complex apps** → You can start immediately

### Question 2: Do you know Kotlin?
- [ ] **No, I know Java** → 2-3 days learning
- [ ] **No, I don't know either** → 1-2 weeks learning
- [ ] **Yes, intermediate level** → You're good, continue
- [ ] **Yes, advanced level** → You're great, continue

### Question 3: Do you understand REST APIs?
- [ ] **No** → 2-3 days learning
- [ ] **Yes, basics** → 1 day refresher
- [ ] **Yes, experienced** → Continue

### Question 4: Database knowledge?
- [ ] **No SQL experience** → 3-4 days learning
- [ ] **Know SQL basics** → 1-2 days learning Room
- [ ] **Expert** → Continue

---

## Android Development Skills

### Must-Have Skills

#### 1. Kotlin Programming
**What you need to know:**
- Variables (val, var)
- Functions and lambdas
- Classes and objects
- Extension functions
- Coroutines basics
- Collections (List, Map, Set)
- Null safety

**Test yourself:**
```kotlin
// Can you write this code?

// 1. Create a data class
data class Stock(val symbol: String, val price: Double)

// 2. Use coroutines
viewModelScope.launch {
    val stocks = fetchStocks()
    updateUI(stocks)
}

// 3. Filter list
val gainers = stocks.filter { it.price > 100 }

// 4. Lambda with receiver
stocks.forEach { 
    println("${it.symbol}: ${it.price}")
}
```

**If you can do these → You know enough Kotlin**

**Resources:**
- Kotlin Basics: https://kotlinlang.org/docs/basic-syntax.html (2 hours)
- Kotlin Coroutines: https://kotlinlang.org/docs/coroutines-overview.html (3 hours)
- Kotlin Collections: https://kotlinlang.org/docs/collections-overview.html (2 hours)

---

#### 2. Android Lifecycle
**What you need to know:**
- Activity lifecycle (onCreate, onStart, onResume, etc.)
- Fragment lifecycle
- ViewModel lifecycle
- Service lifecycle
- When to use each

**Test yourself:**
```kotlin
// Can you answer these?

// 1. When should I fetch API data - onCreate or onStart?
// 2. What's the difference between Activity and Fragment?
// 3. Why use ViewModel instead of storing data in Activity?
// 4. When does ViewModel survive screen rotation?
// 5. How to prevent API call on every Activity.onCreate?
```

**Resources:**
- Activity Lifecycle: https://developer.android.com/guide/components/activities/activity-lifecycle (1 hour)
- Fragment Lifecycle: https://developer.android.com/guide/fragments/lifecycle (1 hour)
- ViewModel: https://developer.android.com/topic/libraries/architecture/viewmodel (1 hour)

---

#### 3. Jetpack Compose or XML Layouts
**What you need to know:**
Choose ONE (Compose is modern, XML is traditional):

**Option A: Jetpack Compose (Recommended)**
- Composable functions
- State management (remember, mutableState)
- Layouts (Column, Row, Box)
- Lists (LazyColumn, LazyRow)
- Navigation

**Option B: XML Layouts (Traditional)**
- XML layout files
- View hierarchy
- ViewBinding
- RecyclerView for lists
- Fragment navigation

**I recommend Compose** (more future-proof, easier to learn)

**Resources:**
- Compose Basics: https://developer.android.com/jetpack/compose/documentation (2 hours)
- Compose State: https://developer.android.com/jetpack/compose/state (2 hours)
- Compose Navigation: https://developer.android.com/jetpack/compose/navigation (1 hour)

---

#### 4. Room Database
**What you need to know:**
- Entities (data classes)
- DAOs (Data Access Objects)
- Database class
- Queries (@Query)
- Migrations (schema changes)
- Async operations (suspend functions)

**Test yourself:**
```kotlin
// Can you write this?

// 1. Create a simple Entity
@Entity
data class Portfolio(val symbol: String, val quantity: Double)

// 2. Create DAO
@Dao
interface PortfolioDao {
    @Insert
    suspend fun insert(portfolio: Portfolio)
    
    @Query("SELECT * FROM portfolio WHERE symbol = :symbol")
    suspend fun getPortfolio(symbol: String): Portfolio?
}

// 3. Create Database
@Database(entities = [Portfolio::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
}
```

**Resources:**
- Room Basics: https://developer.android.com/training/data-storage/room (2 hours)
- Room Advanced: https://developer.android.com/training/data-storage/room/relationships (1 hour)

---

#### 5. Retrofit (HTTP Requests)
**What you need to know:**
- Creating API interfaces
- GET/POST requests
- Request/response models
- Error handling
- Interceptors

**Test yourself:**
```kotlin
// Can you write this?

interface StockApi {
    @GET("query")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("apikey") key: String
    ): QuoteResponse
}

data class QuoteResponse(
    @SerializedName("Global Quote")
    val quote: Quote
)

data class Quote(
    @SerializedName("05. price")
    val price: String
)
```

**Resources:**
- Retrofit Guide: https://square.github.io/retrofit/ (2 hours)
- API Design: https://developer.android.com/training/volley/request (1 hour)

---

#### 6. Firebase Setup
**What you need to know:**
- Firebase project setup
- Authentication
- Firestore database
- Cloud Messaging (FCM)
- Rules and permissions

**Resources:**
- Firebase Basics: https://firebase.google.com/docs/guides (2 hours)
- Firebase Auth: https://firebase.google.com/docs/auth (1 hour)
- Firestore: https://firebase.google.com/docs/firestore (2 hours)
- FCM: https://firebase.google.com/docs/cloud-messaging (1 hour)

---

#### 7. Background Work (WorkManager)
**What you need to know:**
- Creating Workers
- Scheduling work
- Constraints (network, battery, etc.)
- Unique work

**Test yourself:**
```kotlin
// Can you write this?

class PriceCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Fetch prices
        // Check alerts
        // Trigger notifications
        return Result.success()
    }
}

// Schedule it
val priceCheckWork = PeriodicWorkRequestBuilder<PriceCheckWorker>(
    15, TimeUnit.MINUTES
).build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "price_check",
    ExistingPeriodicWorkPolicy.REPLACE,
    priceCheckWork
)
```

**Resources:**
- WorkManager: https://developer.android.com/topic/libraries/architecture/workmanager (2 hours)

---

#### 8. Push Notifications (FCM)
**What you need to know:**
- Setting up FCM
- Receiving notifications
- Notification channels
- Notification actions

**Resources:**
- FCM Setup: https://firebase.google.com/docs/cloud-messaging/android/client (2 hours)

---

### Nice-to-Have Skills

- [ ] Material Design principles
- [ ] Unit testing (JUnit)
- [ ] UI testing (Espresso)
- [ ] Dependency Injection (Hilt)
- [ ] Git version control
- [ ] App performance optimization
- [ ] Security best practices

---

## Backend Skills (Optional)

If you want to handle alerts server-side or have multiple users:

### Must-Have Backend Skills

#### 1. Node.js / JavaScript
- JavaScript basics
- Node.js runtime
- NPM packages
- Async/await
- REST APIs

#### 2. Express.js
- Creating routes
- Middleware
- Error handling
- CORS

#### 3. Database
- SQL (PostgreSQL)
- OR Firebase Firestore
- Schema design
- Queries and indexing

#### 4. APIs
- REST principles
- HTTP methods
- Status codes
- Error handling

---

## Learning Path

### Scenario A: Completely New to Android (8 weeks total)

**Week 1-2: Kotlin Fundamentals**
- Day 1-2: Syntax basics
- Day 3-4: OOP (classes, interfaces)
- Day 5-6: Coroutines
- Day 7-10: Collections and functional programming
- Day 11-14: Practice (build 3 small Kotlin programs)

**Week 3: Android Basics**
- Day 1-2: Android project setup
- Day 3: Activities and Lifecycle
- Day 4: Fragments
- Day 5-7: Build a simple UI

**Week 4: Databases**
- Day 1-2: SQL basics
- Day 3-4: Room setup
- Day 5-7: Create and query database

**Week 5: APIs & Networking**
- Day 1-2: Retrofit setup
- Day 3-4: Make API calls
- Day 5-7: Handle responses

**Week 6: Firebase & Notifications**
- Day 1-2: Firebase setup
- Day 3-4: Push notifications
- Day 5-7: Test notifications

**Week 7-8: Build Stocksum**
- Start building the app following the technical requirements

---

### Scenario B: Know Android Basics (2 weeks to start)

**Week 1: Refresh & Setup**
- Day 1-2: Kotlin refresh (coroutines)
- Day 3: Room database refresh
- Day 4: Retrofit refresh
- Day 5: Firebase setup
- Day 6-7: WorkManager & FCM setup

**Week 2: Quick Practice**
- Day 1-3: Small practice project
- Day 4-7: Start Stocksum MVP

---

### Scenario C: Android Expert (Start Immediately)
Just jump into Stocksum development!

---

## Resources by Topic

### Kotlin
- **Official Docs:** https://kotlinlang.org/docs/
- **Coroutines:** https://kotlinlang.org/docs/coroutines-overview.html
- **YouTube:** "Kotlin Fundamentals" by Android Developers (4 hours)
- **Practice:** LeetCode, HackerRank (Kotlin solutions)

### Android Development
- **Official Documentation:** https://developer.android.com
- **Jetpack Compose:** https://developer.android.com/jetpack/compose
- **Codelabs:** https://codelabs.developers.google.com/?cat=android
- **YouTube:** Android Developers channel (official, updated regularly)

### Database
- **SQL Basics:** https://www.w3schools.com/sql/
- **Room Database:** https://developer.android.com/training/data-storage/room
- **Firebase Firestore:** https://firebase.google.com/docs/firestore

### APIs
- **REST Fundamentals:** https://www.restfulapi.net/
- **Retrofit:** https://square.github.io/retrofit/
- **OkHttp:** https://square.github.io/okhttp/

### Firebase
- **Firebase Console:** https://console.firebase.google.com
- **Documentation:** https://firebase.google.com/docs
- **FCM Setup:** https://firebase.google.com/docs/cloud-messaging
- **Firestore:** https://firebase.google.com/docs/firestore

### Books
- **"Android Programming: The Big Nerd Ranch Guide"** - Best for beginners
- **"Kotlin in Action"** - Comprehensive Kotlin book
- **"Clean Code in Android"** - Best practices

### Communities
- **r/androiddev** (Reddit) - Active Android developer community
- **Stack Overflow** - Tag: android
- **GitHub** - Browse open-source Android projects
- **Twitter:** #AndroidDevelopment

---

## Estimated Learning Time

### If you know NOTHING about Android
- Kotlin: 2-3 weeks
- Android Basics: 1-2 weeks
- Databases: 1 week
- APIs: 1 week
- Firebase: 3-4 days
- **Total: 6-8 weeks before building**

### If you know some Android/Kotlin
- Refresh Kotlin: 2-3 days
- Room Database: 3-4 days
- Retrofit: 2-3 days
- Firebase: 2-3 days
- **Total: 1-2 weeks before building**

### If you're an Android expert
- Just build! 4-6 weeks to complete Stocksum

---

## Self-Assessment Quiz

Be honest with yourself:

**Question 1: Kotlin**
```kotlin
// What does this do?
val numbers = listOf(1, 2, 3, 4, 5)
val doubled = numbers.map { it * 2 }.filter { it > 5 }
println(doubled)  // Output: [6, 8, 10]
```
- [ ] I don't understand this
- [ ] I understand it but can't write similar code
- [ ] I can write similar code
- [ ] I can write and explain advanced Kotlin

**Question 2: ViewModel**
```kotlin
// Is this correct?
class MyActivity : AppCompatActivity() {
    val viewModel = MyViewModel()  // Created here
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.data.observe(this) { data ->
            updateUI(data)
        }
    }
}
```
- [ ] I don't know what's wrong
- [ ] I know something's wrong but can't explain
- [ ] I can explain the issue and fix it
- [ ] I can explain why and design better architecture

**Question 3: Retrofit**
Can you write code to:
- [ ] No idea where to start
- [ ] I can copy-paste from documentation
- [ ] I can write it from memory
- [ ] I can write advanced patterns

**Question 4: Database Queries**
Can you write Room queries using:
- [ ] No
- [ ] With documentation help
- [ ] From memory
- [ ] Complex queries with joins

---

## Your Next Steps

1. **Take this self-assessment seriously** — Be honest about your level
2. **Based on your level, follow the learning path** — Don't skip steps
3. **Do hands-on practice** — Don't just watch tutorials
4. **Build small projects first** — Before building Stocksum
5. **Join communities** — Ask questions on r/androiddev or Stack Overflow
6. **Start building when ready** — Not before

---

## Red Flags - Don't Start Yet If...

🚫 You haven't written any Kotlin code yet  
🚫 You don't understand Activity/Fragment lifecycle  
🚫 You've never used a REST API  
🚫 You don't understand SQL basics  
🚫 You can't debug Android errors  

**If any apply to you:** Spend 1-2 weeks learning first. It will save you frustration later!

---

## Green Lights - You're Ready If...

✅ You've built at least 2 Android apps  
✅ You're comfortable with Kotlin  
✅ You understand REST APIs  
✅ You know how to use Room or SQLite  
✅ You can read and understand Android documentation  

---

## Getting Help

**Stuck on something?**

1. **Google the error message** (90% of the time, someone has solved it)
2. **Read Stack Overflow** (search the exact error)
3. **Check official documentation** (Android Developers site)
4. **Ask on r/androiddev** (provide code, error message, what you tried)
5. **Join Android Slack communities** (real-time help)

**Remember:** Everyone gets stuck. It's normal. The key is knowing how to debug and find solutions.

---

## Recommended Learning Order

**For Beginners:**
1. Kotlin fundamentals
2. Android basics (Activities, Fragments)
3. XML layouts (or Jetpack Compose)
4. RecyclerView (lists)
5. Room database
6. Retrofit API calls
7. Firebase setup
8. Start Stocksum

**For Intermediate:**
1. Quick Kotlin refresh
2. Jetpack Compose
3. Room advanced features
4. Retrofit + interceptors
5. Firebase + Notifications
6. WorkManager
7. Start Stocksum

**For Advanced:**
Just build Stocksum!

---

## Final Checklist Before Starting

Before you start coding Stocksum:

- [ ] I've written working Kotlin code
- [ ] I can build and run an Android app
- [ ] I understand Activity/Fragment lifecycle
- [ ] I've used Retrofit to call an API
- [ ] I've used Room to save and retrieve data
- [ ] I have Firebase account set up
- [ ] I have Android Studio installed and working
- [ ] I have Alpha Vantage and Finnhub API keys
- [ ] I understand the technical requirements document
- [ ] I've read the README and project overview

**If you checked all boxes → You're ready!**  
**If you missed some → Spend 1-2 weeks learning those areas first**

---

**Good luck! The journey of 1000 miles begins with a single commit.** 🚀

