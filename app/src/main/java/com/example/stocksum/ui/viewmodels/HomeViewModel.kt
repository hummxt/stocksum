package com.example.stocksum.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stocksum.data.AlertCondition
import com.example.stocksum.data.AlertManager
import com.example.stocksum.data.AlertState
import com.example.stocksum.data.NotificationHelper
import com.example.stocksum.data.PortfolioEntry
import com.example.stocksum.data.PortfolioManager
import com.example.stocksum.data.StockAlert
import com.example.stocksum.data.repository.StockRepository
import com.example.stocksum.ui.MockStock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StockRepository()
    val portfolioManager = PortfolioManager(application)
    val alertManager = AlertManager(application)
    private val notificationHelper = NotificationHelper(application)

    private val _homeStocks = MutableStateFlow<UiState<List<MockStock>>>(UiState.Loading)
    val homeStocks: StateFlow<UiState<List<MockStock>>> = _homeStocks.asStateFlow()

    private val _searchResults = MutableStateFlow<UiState<List<MockStock>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<MockStock>>> = _searchResults.asStateFlow()

    private val _portfolioStocks = MutableStateFlow<List<MockStock>>(emptyList())
    val portfolioStocks: StateFlow<List<MockStock>> = _portfolioStocks.asStateFlow()

    private val _alerts = MutableStateFlow<List<StockAlert>>(emptyList())
    val alerts: StateFlow<List<StockAlert>> = _alerts.asStateFlow()

    private val _alertBadgeCount = MutableStateFlow(0)
    val alertBadgeCount: StateFlow<Int> = _alertBadgeCount.asStateFlow()

    private val _watchlist = MutableStateFlow<Set<String>>(emptySet())
    val watchlist: StateFlow<Set<String>> = _watchlist.asStateFlow()

    private var currentPage = 0
    private var isPaginating = false
    private var loadedStocks = mutableListOf<MockStock>()

    private val allSymbolsPool = listOf(
        Pair("AAPL", "Apple Inc."), Pair("NVDA", "NVIDIA Corp."), Pair("MSFT", "Microsoft Corp."),
        Pair("TSLA", "Tesla Inc."), Pair("AMZN", "Amazon.com Inc."), Pair("META", "Meta Platforms"),
        Pair("AMD", "Advanced Micro"), Pair("NFLX", "Netflix Inc."), Pair("DIS", "Walt Disney"),
        Pair("JPM", "JPMorgan Chase"), Pair("V", "Visa Inc."), Pair("WMT", "Walmart Inc."),
        Pair("XOM", "Exxon Mobil"), Pair("SBUX", "Starbucks Corp."), Pair("NKE", "NIKE Inc."),
        Pair("KO", "Coca-Cola Co."), Pair("PEP", "PepsiCo Inc."), Pair("MCD", "McDonald's Corp."),
        Pair("INTC", "Intel Corp."), Pair("BA", "Boeing Co."), Pair("CRM", "Salesforce Inc."),
        Pair("CSCO", "Cisco Systems"), Pair("PFE", "Pfizer Inc."), Pair("T", "AT&T Inc."),
        Pair("VZ", "Verizon"), Pair("ADBE", "Adobe Inc."), Pair("ABT", "Abbott Labs")
    )

    init {
        fetchDashboardStocks()
        refreshAlerts()
        refreshPortfolio()
        loadWatchlist()
    }

    private fun fetchDashboardStocks() {
        viewModelScope.launch {
            _homeStocks.value = UiState.Loading
            try {
                currentPage = 0
                val batch = allSymbolsPool.take(10)
                val stocks = repository.getQuotesForSymbols(batch)
                if (stocks.isEmpty()) {
                    _homeStocks.value = UiState.Error("No data or API key missing.")
                } else {
                    loadedStocks.clear()
                    loadedStocks.addAll(stocks)
                    _homeStocks.value = UiState.Success(loadedStocks.toList())
                    checkAlertsAgainstPrices()
                    refreshPortfolio()
                }
            } catch (e: Exception) {
                _homeStocks.value = UiState.Error("Failed to load stocks.")
            }
        }
    }

    fun loadMoreStocks() {
        if (isPaginating) return

        val startIndex = 10 + (currentPage * 5)
        if (startIndex >= allSymbolsPool.size) return

        isPaginating = true
        val batch = allSymbolsPool.subList(startIndex, minOf(startIndex + 5, allSymbolsPool.size))

        viewModelScope.launch {
            try {
                val newStocks = repository.getQuotesForSymbols(batch)
                if (newStocks.isNotEmpty()) {
                    loadedStocks.addAll(newStocks)
                    _homeStocks.value = UiState.Success(loadedStocks.toList())
                    currentPage++
                    checkAlertsAgainstPrices()
                }
            } catch (e: Exception) {
            } finally {
                isPaginating = false
            }
        }
    }

    fun search(query: String) {
        if (query.length < 2) {
            _searchResults.value = UiState.Success(emptyList())
            return
        }
        viewModelScope.launch {
            _searchResults.value = UiState.Loading
            try {
                val results = repository.searchStocks(query)
                _searchResults.value = UiState.Success(results)
            } catch (e: Exception) {
                _searchResults.value = UiState.Error("Search failed")
            }
        }
    }

    // --- Portfolio Management ---

    fun addToPortfolio(ticker: String, companyName: String, shares: Double, purchasePrice: Double) {
        portfolioManager.addStock(
            PortfolioEntry(
                ticker = ticker,
                companyName = companyName,
                sharesOwned = shares,
                purchasePrice = purchasePrice
            )
        )
        refreshPortfolio()
    }

    fun removeFromPortfolio(ticker: String) {
        portfolioManager.removeStock(ticker)
        refreshPortfolio()
    }

    fun updatePortfolioEntry(ticker: String, shares: Double, purchasePrice: Double) {
        portfolioManager.updateStock(ticker, shares, purchasePrice)
        refreshPortfolio()
    }

    fun isInPortfolio(ticker: String): Boolean {
        return portfolioManager.isInPortfolio(ticker)
    }

    fun refreshPortfolio() {
        val entries = portfolioManager.getPortfolio()
        val currentStocks = loadedStocks.toList()

        val portfolioWithPrices = entries.map { entry ->
            val liveStock = currentStocks.find { it.ticker == entry.ticker }
            val currentPrice = liveStock?.currentPrice ?: entry.purchasePrice
            val changePercent = liveStock?.changePercent ?: 0.0
            val pnlValue = (currentPrice - entry.purchasePrice) * entry.sharesOwned

            MockStock(
                ticker = entry.ticker,
                companyName = entry.companyName,
                exchange = liveStock?.exchange ?: "US",
                currentPrice = currentPrice,
                changePercent = changePercent,
                pnlValue = pnlValue,
                sharesOwned = entry.sharesOwned,
                purchasePrice = entry.purchasePrice,
                logoUrl = liveStock?.logoUrl
            )
        }

        _portfolioStocks.value = portfolioWithPrices
    }

    // --- Alert Management ---

    fun addAlert(ticker: String, companyName: String, condition: AlertCondition, targetPrice: Double) {
        alertManager.addAlert(
            StockAlert(
                ticker = ticker,
                companyName = companyName,
                condition = condition,
                targetPrice = targetPrice
            )
        )
        refreshAlerts()
    }

    fun removeAlert(alertId: String) {
        alertManager.removeAlert(alertId)
        refreshAlerts()
    }

    fun toggleAlert(alertId: String) {
        alertManager.toggleAlert(alertId)
        refreshAlerts()
    }

    fun refreshAlerts() {
        _alerts.value = alertManager.getAlerts()
        _alertBadgeCount.value = alertManager.getTriggeredUncheckedCount()
    }

    private fun checkAlertsAgainstPrices() {
        if (!alertManager.areAlertsEnabled()) return

        val prices = loadedStocks.associate { it.ticker to it.currentPrice }
        val triggered = alertManager.checkAlerts(prices)

        triggered.forEach { alert ->
            val currentPrice = prices[alert.ticker] ?: return@forEach
            notificationHelper.showAlertNotification(
                ticker = alert.ticker,
                condition = alert.condition.name,
                targetPrice = alert.targetPrice,
                currentPrice = currentPrice
            )
        }

        if (triggered.isNotEmpty()) {
            refreshAlerts()
        }
    }

    // --- Watchlist Management ---

    private fun loadWatchlist() {
        val prefs = getApplication<Application>().getSharedPreferences("stocksum_watchlist", 0)
        val saved = prefs.getStringSet("watchlist_tickers", emptySet()) ?: emptySet()
        _watchlist.value = saved
    }

    fun toggleWatchlist(ticker: String) {
        val current = _watchlist.value.toMutableSet()
        if (current.contains(ticker)) {
            current.remove(ticker)
        } else {
            current.add(ticker)
        }
        _watchlist.value = current

        val prefs = getApplication<Application>().getSharedPreferences("stocksum_watchlist", 0)
        prefs.edit().putStringSet("watchlist_tickers", current).apply()
    }

    fun isInWatchlist(ticker: String): Boolean {
        return _watchlist.value.contains(ticker)
    }
}
