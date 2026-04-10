package com.example.stocksum.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class PortfolioEntry(
    val ticker: String,
    val companyName: String,
    val sharesOwned: Double,
    val purchasePrice: Double,
    val dateAdded: Long = System.currentTimeMillis()
)

class PortfolioManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("stocksum_portfolio", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PORTFOLIO = "portfolio_entries"
    }

    fun addStock(entry: PortfolioEntry) {
        val entries = getPortfolio().toMutableList()
        // Update if already exists
        val existingIndex = entries.indexOfFirst { it.ticker == entry.ticker }
        if (existingIndex >= 0) {
            entries[existingIndex] = entry
        } else {
            entries.add(entry)
        }
        savePortfolio(entries)
    }

    fun removeStock(ticker: String) {
        val entries = getPortfolio().toMutableList()
        entries.removeAll { it.ticker == ticker }
        savePortfolio(entries)
    }

    fun updateStock(ticker: String, newShares: Double, newPurchasePrice: Double) {
        val entries = getPortfolio().toMutableList()
        val index = entries.indexOfFirst { it.ticker == ticker }
        if (index >= 0) {
            entries[index] = entries[index].copy(
                sharesOwned = newShares,
                purchasePrice = newPurchasePrice
            )
            savePortfolio(entries)
        }
    }

    fun getPortfolio(): List<PortfolioEntry> {
        val json = prefs.getString(KEY_PORTFOLIO, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                PortfolioEntry(
                    ticker = obj.getString("ticker"),
                    companyName = obj.optString("companyName", ""),
                    sharesOwned = obj.getDouble("sharesOwned"),
                    purchasePrice = obj.getDouble("purchasePrice"),
                    dateAdded = obj.optLong("dateAdded", System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isInPortfolio(ticker: String): Boolean {
        return getPortfolio().any { it.ticker == ticker }
    }

    private fun savePortfolio(entries: List<PortfolioEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            val obj = JSONObject().apply {
                put("ticker", entry.ticker)
                put("companyName", entry.companyName)
                put("sharesOwned", entry.sharesOwned)
                put("purchasePrice", entry.purchasePrice)
                put("dateAdded", entry.dateAdded)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PORTFOLIO, array.toString()).apply()
    }
}
