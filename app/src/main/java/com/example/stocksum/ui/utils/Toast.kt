package com.example.stocksum.ui.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.example.stocksum.ui.theme.StocksumTheme

enum class ToastType {
    SUCCESS,      // Green check ✓
    ERROR,        // Red X ✗
    WARNING,      // Orange warning ⚠
    INFO          // Blue info ℹ
}

/**
 * Extension function to show a toast notification with StockSum styling.
 *
 * Usage:
 *   context.showToast("✓ Added AAPL to portfolio", ToastType.SUCCESS)
 *   context.showToast("Stock already in portfolio", ToastType.ERROR)
 */
fun Context.showToast(
    message: String,
    type: ToastType = ToastType.INFO,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message, duration).apply {
        // Note: Toast styling is limited to text color in most Android versions
        // For full background/text color control, consider using a custom layout
        show()
    }
}

/**
 * Toast message templates for common actions
 */
object ToastMessages {
    // Portfolio actions
    fun addedToPortfolio(symbol: String): String = "✓ Added $symbol to portfolio"
    fun removedFromPortfolio(symbol: String): String = "✓ Removed $symbol from portfolio"
    fun alreadyInPortfolio(symbol: String): String = "✗ $symbol already in portfolio"
    fun failedToAdd(symbol: String): String = "✗ Failed to add $symbol"

    // Alert actions
    fun alertCreated(symbol: String, price: Double): String = "✓ Alert set for $symbol at \$$price"
    fun alertDeleted(symbol: String): String = "✓ Alert removed for $symbol"
    fun alertAlreadyExists(symbol: String): String = "✗ Alert already exists for $symbol"
    fun failedToCreateAlert(): String = "✗ Failed to create alert"

    // Search actions
    fun noResults(query: String): String = "No results found for \"$query\""
    fun searchError(): String = "✗ Search failed. Please try again"

    // Market actions
    fun marketClosed(): String = "⚠ Market is currently closed"
    fun marketOpen(): String = "Market is open for trading"

    // Network actions
    fun networkError(): String = "✗ Check your internet connection"
    fun loadingFailed(): String = "✗ Failed to load data"
}
