package com.example.stocksum.ui

data class MockStock(
    val ticker: String,
    val companyName: String,
    val exchange: String,
    val currentPrice: Double,
    val changePercent: Double,
    val pnlValue: Double = 0.0,
    val sharesOwned: Double = 0.0,
    val purchasePrice: Double = 0.0,
    val currencySymbol: String = "$",
    val logoUrl: String? = null
)

object MockData {

    val portfolioStocks = listOf(
        MockStock("AAPL", "Apple Inc.", "NASDAQ", 178.90, 1.42, 289.0, 10.0, 150.0),
        MockStock("NVDA", "NVIDIA Corp.", "NASDAQ", 892.40, 3.24, 1962.0, 5.0, 500.0),
        MockStock("GOOGL", "Alphabet Inc.", "NASDAQ", 155.72, -0.38, -14.0, 8.0, 157.47),
        MockStock("MSFT", "Microsoft Corp.", "NASDAQ", 415.60, 0.87, 156.0, 3.0, 363.60),
        MockStock("TSLA", "Tesla Inc.", "NASDAQ", 175.20, -2.10, -248.0, 4.0, 237.20),
        MockStock("AMZN", "Amazon.com Inc.", "NASDAQ", 186.50, 1.05, 115.0, 6.0, 167.33)
    )

    val marketGainers = listOf(
        MockStock("NVDA", "NVIDIA Corp.", "NASDAQ", 892.40, 3.24),
        MockStock("AMD", "Advanced Micro", "NASDAQ", 168.50, 2.87),
        MockStock("META", "Meta Platforms", "NASDAQ", 505.20, 2.15),
        MockStock("AAPL", "Apple Inc.", "NASDAQ", 178.90, 1.42),
        MockStock("AMZN", "Amazon.com", "NASDAQ", 186.50, 1.05)
    )

    val marketLosers = listOf(
        MockStock("TSLA", "Tesla Inc.", "NASDAQ", 175.20, -2.10),
        MockStock("INTC", "Intel Corp.", "NASDAQ", 31.40, -1.85),
        MockStock("BA", "Boeing Co.", "NYSE", 178.60, -1.52),
        MockStock("DIS", "Walt Disney", "NYSE", 112.30, -1.20),
        MockStock("PYPL", "PayPal Holdings", "NASDAQ", 63.80, -0.95)
    )

    val allStocks = listOf(
        MockStock("AAPL", "Apple Inc.", "NASDAQ", 178.90, 1.42),
        MockStock("NVDA", "NVIDIA Corp.", "NASDAQ", 892.40, 3.24),
        MockStock("GOOGL", "Alphabet Inc.", "NASDAQ", 155.72, -0.38),
        MockStock("MSFT", "Microsoft Corp.", "NASDAQ", 415.60, 0.87),
        MockStock("TSLA", "Tesla Inc.", "NASDAQ", 175.20, -2.10),
        MockStock("AMZN", "Amazon.com Inc.", "NASDAQ", 186.50, 1.05),
        MockStock("META", "Meta Platforms", "NASDAQ", 505.20, 2.15),
        MockStock("AMD", "Advanced Micro", "NASDAQ", 168.50, 2.87),
        MockStock("NFLX", "Netflix Inc.", "NASDAQ", 628.40, 0.62),
        MockStock("INTC", "Intel Corp.", "NASDAQ", 31.40, -1.85)
    )

    val sparklineData = listOf(
        24200f, 24350f, 24100f, 24500f, 24450f,
        24600f, 24550f, 24800f, 24750f, 24812f
    )

    val totalPortfolioValue = "24,812.50"
    val todayChange = "+\$348.20 today"
    val todayPercent = 1.42

    val moodValue = 52
}
