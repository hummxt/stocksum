# Week-by-Week Development Roadmap

> Your day-by-day guide to build Stocksum in 4-6 weeks

---

## Table of Contents
1. [Week 1: Setup & Database](#week-1-setup--database)
2. [Week 2: API Integration](#week-2-api-integration)
3. [Week 3: UI & ViewModels](#week-3-ui--viewmodels)
4. [Week 4: Portfolio Features](#week-4-portfolio-features)
5. [Week 5: Alerts & Notifications](#week-5-alerts--notifications)
6. [Week 6: Polish & Launch](#week-6-polish--launch)

---

## Week 1: Setup & Database

**Goal:** Get project structure set up and database working

### Day 1: Project Initialization

**Morning (2 hours):**
- [ ] Open Android Studio
- [ ] Create new project: "Stocksum"
- [ ] Choose Empty Activity template
- [ ] Set Package name: `com.yourname.Stocksum`
- [ ] Target SDK: 34, Min SDK: 24

**Afternoon (2 hours):**
- [ ] Explore project structure
- [ ] Update `build.gradle` (app level) with dependencies
- [ ] Sync Gradle files
- [ ] Test run app on emulator (should show "Hello World")

**Code to add to `build.gradle`:**
```gradle
dependencies {
    // Kotlin & Core
    implementation 'androidx.core:core-ktx:1.10.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    
    // Jetpack Compose
    implementation 'androidx.compose.ui:ui:1.5.0'
    implementation 'androidx.compose.material:material:1.5.0'
    implementation 'androidx.activity:activity-compose:1.7.2'
    
    // Room Database
    implementation 'androidx.room:room-runtime:2.5.2'
    kapt 'androidx.room:room-compiler:2.5.2'
    implementation 'androidx.room:room-ktx:2.5.2'
    
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.3.1')
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-messaging-ktx'
}
```

**Deliverable:** ✅ App compiles and runs on emulator

---

### Day 2: Create Database Entities

**Morning (2 hours):**
- [ ] Create folder: `data/database/entities/`
- [ ] Create `Portfolio.kt` entity
- [ ] Create `StockPrice.kt` entity

**Afternoon (2 hours):**
- [ ] Create `PriceAlert.kt` entity
- [ ] Create `User.kt` entity
- [ ] Test that entities compile

**Code:**

`data/database/entities/Portfolio.kt`:
```kotlin
package com.yourname.Stocksum.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolio")
data class Portfolio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val symbol: String,
    val quantity: Double,
    val purchasePrice: Double,
    val purchaseDate: Long,
    val market: String, // NYSE, NASDAQ, NSE, BSE
    val addedAt: Long = System.currentTimeMillis()
)
```

`data/database/entities/StockPrice.kt`:
```kotlin
@Entity(tableName = "stock_price")
data class StockPrice(
    @PrimaryKey
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val market: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
```

`data/database/entities/PriceAlert.kt`:
```kotlin
@Entity(tableName = "price_alert")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val symbol: String,
    val targetPrice: Double,
    val alertType: String, // "ABOVE" or "BELOW"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null
)
```

**Deliverable:** ✅ All entities created and compiling

---

### Day 3: Create DAOs

**Morning (2 hours):**
- [ ] Create folder: `data/database/dao/`
- [ ] Create `PortfolioDao.kt`
- [ ] Create `StockPriceDao.kt`

**Afternoon (2 hours):**
- [ ] Create `AlertDao.kt`
- [ ] Test that DAOs compile

**Code:**

`data/database/dao/PortfolioDao.kt`:
```kotlin
package com.yourname.Stocksum.data.database.dao

import androidx.room.*
import com.yourname.Stocksum.data.database.entities.Portfolio
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    @Insert
    suspend fun insert(portfolio: Portfolio)
    
    @Update
    suspend fun update(portfolio: Portfolio)
    
    @Delete
    suspend fun delete(portfolio: Portfolio)
    
    @Query("SELECT * FROM portfolio WHERE userId = :userId")
    fun getUserPortfolio(userId: String): Flow<List<Portfolio>>
    
    @Query("SELECT * FROM portfolio WHERE symbol = :symbol AND userId = :userId")
    suspend fun getBySymbol(symbol: String, userId: String): Portfolio?
    
    @Query("DELETE FROM portfolio WHERE id = :id")
    suspend fun deleteById(id: Int)
}
```

`data/database/dao/StockPriceDao.kt`:
```kotlin
@Dao
interface StockPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(price: StockPrice)
    
    @Query("SELECT * FROM stock_price WHERE symbol = :symbol")
    suspend fun getPrice(symbol: String): StockPrice?
    
    @Query("SELECT * FROM stock_price ORDER BY changePercent DESC LIMIT :limit")
    suspend fun getTopGainers(limit: Int): List<StockPrice>
    
    @Query("SELECT * FROM stock_price ORDER BY changePercent ASC LIMIT :limit")
    suspend fun getTopLosers(limit: Int): List<StockPrice>
    
    @Query("DELETE FROM stock_price WHERE lastUpdated < :beforeTime")
    suspend fun deleteOldPrices(beforeTime: Long)
}
```

`data/database/dao/AlertDao.kt`:
```kotlin
@Dao
interface AlertDao {
    @Insert
    suspend fun insert(alert: PriceAlert)
    
    @Delete
    suspend fun delete(alert: PriceAlert)
    
    @Update
    suspend fun update(alert: PriceAlert)
    
    @Query("SELECT * FROM price_alert WHERE userId = :userId")
    fun getUserAlerts(userId: String): Flow<List<PriceAlert>>
    
    @Query("SELECT * FROM price_alert WHERE isActive = 1")
    suspend fun getActiveAlerts(): List<PriceAlert>
}
```

**Deliverable:** ✅ All DAOs created

---

### Day 4: Create Room Database

**Morning (2 hours):**
- [ ] Create `data/database/AppDatabase.kt`
- [ ] Test database creation

**Afternoon (2 hours):**
- [ ] Enable view binding in `build.gradle`
- [ ] Test database instance creation
- [ ] Commit code to git

**Code:**

`data/database/AppDatabase.kt`:
```kotlin
package com.yourname.Stocksum.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yourname.Stocksum.data.database.dao.AlertDao
import com.yourname.Stocksum.data.database.dao.PortfolioDao
import com.yourname.Stocksum.data.database.dao.StockPriceDao
import com.yourname.Stocksum.data.database.entities.Portfolio
import com.yourname.Stocksum.data.database.entities.PriceAlert
import com.yourname.Stocksum.data.database.entities.StockPrice

@Database(
    entities = [Portfolio::class, StockPrice::class, PriceAlert::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
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

**Deliverable:** ✅ Database set up and working

---

### Day 5-7: API Models & Repositories

**Day 5 (4 hours):**
- [ ] Create folder: `data/models/`
- [ ] Create `ApiResponse.kt` for API response models
- [ ] Create `AlphaVantageResponse.kt`

**Code:**

`data/models/ApiResponse.kt`:
```kotlin
package com.yourname.Stocksum.data.models

data class QuoteResponse(
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val market: String
)

data class MarketSummary(
    val gainers: List<QuoteResponse>,
    val losers: List<QuoteResponse>,
    val mostActive: List<QuoteResponse>
)
```

**Day 6 (4 hours):**
- [ ] Create folder: `data/repository/`
- [ ] Create `StockRepository.kt`
- [ ] Create `PortfolioRepository.kt`

**Code:**

`data/repository/StockRepository.kt`:
```kotlin
package com.yourname.Stocksum.data.repository

import com.yourname.Stocksum.data.database.dao.StockPriceDao
import com.yourname.Stocksum.data.database.entities.StockPrice
import kotlinx.coroutines.flow.Flow

class StockRepository(
    private val stockPriceDao: StockPriceDao,
    private val stockApi: StockApi
) {
    suspend fun getQuote(symbol: String): QuoteResponse {
        return try {
            val response = stockApi.getQuote(symbol)
            // Save to local database
            val price = StockPrice(
                symbol = symbol,
                price = response.price,
                change = response.change,
                changePercent = response.changePercent,
                market = "NYSE"
            )
            stockPriceDao.insert(price)
            response
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun getTopGainers(limit: Int = 5): List<StockPrice> {
        return stockPriceDao.getTopGainers(limit)
    }
    
    suspend fun getTopLosers(limit: Int = 5): List<StockPrice> {
        return stockPriceDao.getTopLosers(limit)
    }
}
```

`data/repository/PortfolioRepository.kt`:
```kotlin
package com.yourname.Stocksum.data.repository

import com.yourname.Stocksum.data.database.dao.PortfolioDao
import com.yourname.Stocksum.data.database.entities.Portfolio
import kotlinx.coroutines.flow.Flow

class PortfolioRepository(
    private val portfolioDao: PortfolioDao
) {
    fun getUserPortfolio(userId: String): Flow<List<Portfolio>> {
        return portfolioDao.getUserPortfolio(userId)
    }
    
    suspend fun addStock(portfolio: Portfolio) {
        portfolioDao.insert(portfolio)
    }
    
    suspend fun updateStock(portfolio: Portfolio) {
        portfolioDao.update(portfolio)
    }
    
    suspend fun deleteStock(portfolio: Portfolio) {
        portfolioDao.delete(portfolio)
    }
}
```

**Day 7 (4 hours):**
- [ ] Create first test
- [ ] Test database operations
- [ ] Commit all code

**Test:**
```kotlin
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: PortfolioDao
    
    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).build()
        dao = db.portfolioDao()
    }
    
    @Test
    fun insertAndRetrievePortfolio() {
        val portfolio = Portfolio(
            symbol = "AAPL",
            quantity = 10.0,
            purchasePrice = 150.0,
            purchaseDate = System.currentTimeMillis(),
            market = "NASDAQ"
        )
        
        runBlocking {
            dao.insert(portfolio)
            val retrieved = dao.getBySymbol("AAPL", "user1")
            assertEquals("AAPL", retrieved?.symbol)
        }
    }
    
    @After
    fun teardown() {
        db.close()
    }
}
```

**Deliverable:** ✅ Database fully working with repositories

---

## Week 2: API Integration

**Goal:** Fetch real stock data from APIs

### Day 1-2: Retrofit Setup

**Morning (2 hours):**
- [ ] Create `data/api/` folder
- [ ] Create API interfaces
- [ ] Get Alpha Vantage and Finnhub API keys

**Code:**

`data/api/AlphaVantageApi.kt`:
```kotlin
package com.yourname.Stocksum.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {
    @GET("query")
    suspend fun getQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): GlobalQuoteResponse
    
    companion object {
        const val BASE_URL = "https://www.alphavantage.co/"
    }
}

data class GlobalQuoteResponse(
    @SerializedName("Global Quote")
    val globalQuote: GlobalQuote
)

data class GlobalQuote(
    @SerializedName("01. symbol")
    val symbol: String = "",
    
    @SerializedName("05. price")
    val price: String = "0",
    
    @SerializedName("09. change")
    val change: String = "0",
    
    @SerializedName("10. change percent")
    val changePercent: String = "0%"
)
```

**Afternoon (2 hours):**
- [ ] Create Retrofit instance builder
- [ ] Add interceptor for API keys
- [ ] Test API calls

**Code:**

`data/api/RetrofitClient.kt`:
```kotlin
package com.yourname.Stocksum.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(AlphaVantageApi.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val alphaVantageApi: AlphaVantageApi = retrofit.create(AlphaVantageApi::class.java)
}
```

**Deliverable:** ✅ Retrofit working, can fetch prices

---

### Day 3-4: Test API Calls

**Morning (2 hours):**
- [ ] Create test to verify API responses
- [ ] Test parsing responses
- [ ] Handle errors

**Afternoon (2 hours):**
- [ ] Save API responses to database
- [ ] Test database persistence
- [ ] Debug any issues

**Test Code:**
```kotlin
@RunWith(AndroidJUnit4::class)
class ApiIntegrationTest {
    private lateinit var api: AlphaVantageApi
    
    @Before
    fun setup() {
        api = RetrofitClient.alphaVantageApi
    }
    
    @Test
    fun fetchAaplQuote() {
        runBlocking {
            val response = api.getQuote(
                symbol = "AAPL",
                apiKey = BuildConfig.ALPHA_VANTAGE_KEY
            )
            
            assertNotNull(response)
            assertTrue(response.globalQuote.symbol.isNotEmpty())
        }
    }
}
```

**Deliverable:** ✅ APIs integrated and tested

---

### Day 5: Create API Service Layer

**Morning & Afternoon (4 hours):**
- [ ] Create service layer to abstract API calls
- [ ] Add caching logic
- [ ] Implement retry logic

**Code:**

`data/service/StockApiService.kt`:
```kotlin
package com.yourname.Stocksum.data.service

import com.yourname.Stocksum.data.api.AlphaVantageApi
import com.yourname.Stocksum.data.models.QuoteResponse
import kotlinx.coroutines.delay

class StockApiService(
    private val api: AlphaVantageApi,
    private val apiKey: String
) {
    private val cache = mutableMapOf<String, Pair<QuoteResponse, Long>>()
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 minutes
    
    suspend fun getQuote(symbol: String): QuoteResponse {
        // Check cache first
        val cached = cache[symbol]
        if (cached != null && System.currentTimeMillis() - cached.second < CACHE_DURATION) {
            return cached.first
        }
        
        // Fetch from API with retry
        var retries = 3
        while (retries > 0) {
            try {
                val response = api.getQuote(
                    symbol = symbol,
                    apiKey = apiKey
                )
                
                val quote = QuoteResponse(
                    symbol = response.globalQuote.symbol,
                    price = response.globalQuote.price.toDoubleOrNull() ?: 0.0,
                    change = response.globalQuote.change.toDoubleOrNull() ?: 0.0,
                    changePercent = response.globalQuote.changePercent
                        .replace("%", "")
                        .toDoubleOrNull() ?: 0.0,
                    market = "NYSE"
                )
                
                // Cache result
                cache[symbol] = Pair(quote, System.currentTimeMillis())
                return quote
            } catch (e: Exception) {
                retries--
                if (retries > 0) {
                    delay(1000) // Wait 1 second before retry
                } else {
                    throw e
                }
            }
        }
        
        throw Exception("Failed to fetch quote after retries")
    }
}
```

**Deliverable:** ✅ API service with caching and retry

---

### Day 6-7: Market Summary Screen

**Morning (2 hours):**
- [ ] Create ViewModel for market data
- [ ] Implement use case for fetching market summary

**Code:**

`domain/usecases/GetMarketSummaryUseCase.kt`:
```kotlin
package com.yourname.Stocksum.domain.usecases

import com.yourname.Stocksum.data.repository.StockRepository
import com.yourname.Stocksum.data.models.MarketSummary

class GetMarketSummaryUseCase(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): MarketSummary {
        return try {
            val gainers = repository.getTopGainers(5)
            val losers = repository.getTopLosers(5)
            val mostActive = repository.getTopGainers(5)  // Simplified - should be by volume
            
            MarketSummary(
                gainers = gainers.map { it.toQuoteResponse() },
                losers = losers.map { it.toQuoteResponse() },
                mostActive = mostActive.map { it.toQuoteResponse() }
            )
        } catch (e: Exception) {
            throw e
        }
    }
}
```

**Afternoon (2 hours):**
- [ ] Create ViewModel
- [ ] Set up state management

**Code:**

`ui/viewmodels/MarketViewModel.kt`:
```kotlin
package com.yourname.Stocksum.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.Stocksum.data.models.MarketSummary
import com.yourname.Stocksum.domain.usecases.GetMarketSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketViewModel(
    private val getMarketSummaryUseCase: GetMarketSummaryUseCase
) : ViewModel() {
    private val _marketSummary = MutableStateFlow<MarketSummary?>(null)
    val marketSummary: StateFlow<MarketSummary?> = _marketSummary
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun fetchMarketSummary() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val summary = getMarketSummaryUseCase()
                _marketSummary.value = summary
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

**Evening (2 hours):**
- [ ] Commit week's work
- [ ] Prepare for Week 3

**Deliverable:** ✅ Market data loading and ViewModel ready

---

## Week 3: UI & ViewModels

**Goal:** Build user interface with Jetpack Compose

### Day 1-2: Main Screen Setup

**Morning (2 hours):**
- [ ] Create Compose screens structure
- [ ] Set up navigation

**Code:**

`ui/screens/HomeScreen.kt`:
```kotlin
package com.yourname.Stocksum.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourname.Stocksum.ui.viewmodels.MarketViewModel

@Composable
fun HomeScreen(
    marketViewModel: MarketViewModel,
    onNavigateToPortfolio: () -> Unit,
    onNavigateToAlerts: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stocksum") },
                elevation = 4.dp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Market") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Portfolio") }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Alerts") }
                )
            }
            
            when (selectedTabIndex) {
                0 -> MarketSummaryTab(marketViewModel)
                1 -> PortfolioTab(onNavigateToPortfolio)
                2 -> AlertsTab(onNavigateToAlerts)
            }
        }
    }
}

@Composable
fun MarketSummaryTab(viewModel: MarketViewModel) {
    val marketSummary by viewModel.marketSummary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.fetchMarketSummary()
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            marketSummary?.let { summary ->
                Text("Top Gainers", style = MaterialTheme.typography.h6)
                LazyColumn {
                    items(summary.gainers.size) { index ->
                        StockCard(summary.gainers[index])
                    }
                }
            }
        }
    }
}

@Composable
fun StockCard(stock: QuoteResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(stock.symbol, style = MaterialTheme.typography.h6)
                Text("$${stock.price}", style = MaterialTheme.typography.body2)
            }
            Text(
                "${stock.changePercent}%",
                style = MaterialTheme.typography.body2
            )
        }
    }
}
```

**Afternoon (2 hours):**
- [ ] Test UI compiles
- [ ] Preview in Compose Preview
- [ ] Add basic theming

**Deliverable:** ✅ Home screen with tabs

---

### Day 3-4: Portfolio Screen

**Morning (2 hours):**
- [ ] Create Portfolio ViewModel
- [ ] Implement portfolio calculations

**Code:**

`ui/viewmodels/PortfolioViewModel.kt`:
```kotlin
package com.yourname.Stocksum.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.Stocksum.data.database.entities.Portfolio
import com.yourname.Stocksum.data.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PortfolioState(
    val holdings: List<Portfolio> = emptyList(),
    val totalValue: Double = 0.0,
    val totalGain: Double = 0.0,
    val totalGainPercent: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PortfolioViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PortfolioState())
    val state: StateFlow<PortfolioState> = _state
    
    fun loadPortfolio(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getUserPortfolio(userId).collect { holdings ->
                    val totalValue = holdings.sumOf { it.quantity * it.purchasePrice }
                    val totalGain = holdings.sumOf { 
                        (it.quantity * it.purchasePrice) * 0.1  // Simplified - should fetch current prices
                    }
                    
                    _state.value = PortfolioState(
                        holdings = holdings,
                        totalValue = totalValue,
                        totalGain = totalGain,
                        totalGainPercent = (totalGain / totalValue) * 100,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    
    fun addStock(portfolio: Portfolio) {
        viewModelScope.launch {
            repository.addStock(portfolio)
        }
    }
}
```

**Afternoon (2 hours):**
- [ ] Create Portfolio UI screen
- [ ] Add/Edit stock dialogs

**Code:**

`ui/screens/PortfolioScreen.kt`:
```kotlin
package com.yourname.Stocksum.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourname.Stocksum.ui.viewmodels.PortfolioViewModel

@Composable
fun PortfolioScreen(
    viewModel: PortfolioViewModel,
    userId: String
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadPortfolio(userId)
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Stock")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Portfolio Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Portfolio Value: $${state.totalValue}")
                    Text("Total Gain: $${state.totalGain} (${state.totalGainPercent}%)")
                }
            }
            
            // Holdings List
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(state.holdings.size) { index ->
                        HoldingCard(state.holdings[index])
                    }
                }
            }
            
            if (showAddDialog) {
                AddStockDialog(
                    onDismiss = { showAddDialog = false },
                    onAdd = { portfolio ->
                        viewModel.addStock(portfolio)
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun HoldingCard(holding: Portfolio) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(holding.symbol, style = MaterialTheme.typography.h6)
                Text("${holding.quantity} @ $${holding.purchasePrice}")
            }
            Text("$${holding.quantity * holding.purchasePrice}")
        }
    }
}
```

**Deliverable:** ✅ Portfolio screen with add functionality

---

### Day 5-7: Alerts Screen & Polish

**Morning (2 hours):**
- [ ] Create Alerts ViewModel
- [ ] Create Alerts UI

**Afternoon (2 hours):**
- [ ] Set alert dialogs
- [ ] Test all screens work together

**Evening (2 hours):**
- [ ] UI polish and styling
- [ ] Test on multiple screen sizes
- [ ] Commit week's work

**Deliverable:** ✅ All three main screens functional

---

## Week 4: Portfolio Features & Data

**Goal:** Complete portfolio tracking with real price updates

### Tasks:
- [ ] Integrate stock price fetching in portfolio
- [ ] Show real-time P&L
- [ ] Add edit/delete functionality
- [ ] Implement portfolio calculation use cases
- [ ] Add data persistence
- [ ] Test end-to-end flow

**Deliverable:** ✅ Complete portfolio tracker

---

## Week 5: Alerts & Notifications

**Goal:** Implement price alerts and push notifications

### Tasks:
- [ ] Set up Firebase Messaging
- [ ] Create alert trigger logic
- [ ] Implement WorkManager background service
- [ ] Test notifications
- [ ] Add alert management UI
- [ ] Handle permissions

**Deliverable:** ✅ Working alerts system

---

## Week 6: Polish & Launch

**Goal:** Prepare for Google Play Store

### Tasks:
- [ ] Test on multiple devices
- [ ] Fix bugs and crashes
- [ ] Optimize performance
- [ ] Create app icon
- [ ] Write app description
- [ ] Prepare screenshots
- [ ] Create privacy policy
- [ ] Build release APK/AAB
- [ ] Submit to Google Play Store

**Deliverable:** ✅ Live on Google Play Store!

---

## Daily Standup Template

Each day, answer these:
- [ ] What did I accomplish?
- [ ] What's blocking me?
- [ ] What's next?

Example:
```
Monday:
- Accomplished: Set up database, created all entities
- Blocking: Waiting for API keys (got them!)
- Next: Create DAOs tomorrow
```

---

## Git Commit Strategy

Commit daily with meaningful messages:

```bash
# Day 1
git add .
git commit -m "feat: create Portfolio, StockPrice, Alert entities"

# Day 2
git add .
git commit -m "feat: create database DAOs"

# Day 3
git add .
git commit -m "feat: create AppDatabase with Room"

# etc.
```

---

## When You Get Stuck

1. **Read the error carefully** - 90% of solutions are in the error message
2. **Google the error** - Someone has solved it before
3. **Check Stack Overflow** - Tag: android
4. **Read official docs** - developer.android.com
5. **Ask on r/androiddev** - Post code, error, what you tried
6. **Take a break** - Seriously, often helps

---

## Success Indicators

✅ Week 1: Database working, can insert/retrieve data  
✅ Week 2: APIs returning real stock data  
✅ Week 3: All UI screens displaying correctly  
✅ Week 4: Portfolio calculations accurate  
✅ Week 5: Alerts triggering and notifications working  
✅ Week 6: App ready for launch!

---

**You got this! Build one feature at a time, test, then move on.** 🚀

