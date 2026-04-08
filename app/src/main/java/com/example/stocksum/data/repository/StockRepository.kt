package com.example.stocksum.data.repository

import android.util.Log
import com.example.stocksum.BuildConfig
import com.example.stocksum.data.network.FinnhubApi
import com.example.stocksum.ui.MockStock
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class StockRepository {
    private val api: FinnhubApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        api = retrofit.create(FinnhubApi::class.java)
    }

    suspend fun getQuotesForSymbols(symbols: List<Pair<String, String>>): List<MockStock> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.FINNHUB_API_KEY
        if (apiKey.isEmpty()) {
            return@withContext emptyList()
        }

        val deferredQuotes = symbols.map { (ticker, name) ->
            async {
                try {
                    val quoteDeferred = async { api.getQuote(ticker, apiKey) }
                    val profileDeferred = async { 
                        try { api.getProfile(ticker, apiKey) } catch (e: Exception) { null } 
                    }
                    
                    val response = quoteDeferred.await()
                    val profile = profileDeferred.await()
                    
                    if (response.currentPrice == 0.0 && response.previousClose == 0.0) {
                        return@async null
                    }
                    
                    val changePct = response.percentChange ?: if (response.previousClose > 0) {
                        ((response.currentPrice - response.previousClose) / response.previousClose) * 100
                    } else {
                        0.0
                    }
                    
                    MockStock(
                        ticker = ticker,
                        companyName = name,
                        exchange = "US",
                        currentPrice = response.currentPrice,
                        changePercent = changePct,
                        logoUrl = profile?.logoUrl
                    )
                } catch (e: Exception) {
                    Log.e("StockRepository", "Failed to fetch $ticker: ${e.message}")
                    null
                }
            }
        }
        deferredQuotes.awaitAll().filterNotNull()
    }
    
    suspend fun searchStocks(query: String): List<MockStock> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.FINNHUB_API_KEY
        if (apiKey.isEmpty() || query.length < 2) return@withContext emptyList()
        
        try {
            val response = api.searchSymbol(query, apiKey)
            val topSymbols = response.result
                .filter { !it.symbol.contains(".") }
                .take(5)
                
            getQuotesForSymbols(topSymbols.map { Pair(it.symbol, it.description) })
        } catch (e: Exception) {
            Log.e("StockRepository", "Search failed: ${e.message}")
            emptyList()
        }
    }
}
