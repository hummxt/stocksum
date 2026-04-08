package com.example.stocksum.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class HomeViewModel : ViewModel() {
    private val repository = StockRepository()

    private val _homeStocks = MutableStateFlow<UiState<List<MockStock>>>(UiState.Loading)
    val homeStocks: StateFlow<UiState<List<MockStock>>> = _homeStocks.asStateFlow()

    private val _searchResults = MutableStateFlow<UiState<List<MockStock>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<MockStock>>> = _searchResults.asStateFlow()

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
}
