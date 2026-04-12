package com.example.stocksum.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stocksum.data.repository.StockRepository

class MarketNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val notificationHelper = NotificationHelper(applicationContext)
            val repository = StockRepository()

            val timeOfDay = inputData.getString("time_of_day") ?: "open"
            
            when (timeOfDay) {
                "open" -> {
                    // Get popular tech stocks data
                    val techStocks = listOf(
                        "AAPL" to "Apple",
                        "GOOGL" to "Google",
                        "MSFT" to "Microsoft",
                        "TSLA" to "Tesla",
                        "AMZN" to "Amazon"
                    )
                    val marketData = repository.getQuotesForSymbols(techStocks)
                    val gainers = marketData.filter { it.changePercent > 0 }
                        .sortedByDescending { it.changePercent }
                        .take(3)
                    
                    val title = "📈 Market is Open!"
                    val body = if (gainers.isNotEmpty()) {
                        val topMover = gainers.first()
                        "${topMover.ticker} up ${String.format("%.2f", topMover.changePercent)}%"
                    } else {
                        "Check out today's market opportunities"
                    }
                    
                    notificationHelper.showMarketNotification(title, body)
                }
                "close" -> {
                    // Get popular tech stocks data
                    val techStocks = listOf(
                        "AAPL" to "Apple",
                        "GOOGL" to "Google",
                        "MSFT" to "Microsoft",
                        "TSLA" to "Tesla",
                        "AMZN" to "Amazon"
                    )
                    val marketData = repository.getQuotesForSymbols(techStocks)
                    val topGainers = marketData.filter { it.changePercent > 0 }
                        .sortedByDescending { it.changePercent }
                        .take(1)
                    
                    val title = "📊 Market Closed"
                    val body = if (topGainers.isNotEmpty()) {
                        val topStock = topGainers.first()
                        "Top gain: ${topStock.ticker} +${String.format("%.2f", topStock.changePercent)}%"
                    } else {
                        "See you tomorrow!"
                    }
                    
                    notificationHelper.showMarketNotification(title, body)
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
