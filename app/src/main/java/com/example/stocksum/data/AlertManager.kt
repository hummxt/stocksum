package com.example.stocksum.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

enum class AlertCondition { ABOVE, BELOW }
enum class AlertState { ACTIVE, TRIGGERED, PAUSED }

data class StockAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val ticker: String,
    val companyName: String = "",
    val condition: AlertCondition,
    val targetPrice: Double,
    val state: AlertState = AlertState.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val triggeredAt: Long? = null
)

class AlertManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("stocksum_alerts", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ALERTS = "alert_entries"
        private const val KEY_ALERTS_ENABLED = "alerts_enabled"
        private const val KEY_MARKET_REMINDERS = "market_reminders_enabled"
    }

    fun addAlert(alert: StockAlert) {
        val alerts = getAlerts().toMutableList()
        alerts.add(alert)
        saveAlerts(alerts)
    }

    fun removeAlert(alertId: String) {
        val alerts = getAlerts().toMutableList()
        alerts.removeAll { it.id == alertId }
        saveAlerts(alerts)
    }

    fun toggleAlert(alertId: String) {
        val alerts = getAlerts().toMutableList()
        val index = alerts.indexOfFirst { it.id == alertId }
        if (index >= 0) {
            val current = alerts[index]
            val newState = when (current.state) {
                AlertState.ACTIVE -> AlertState.PAUSED
                AlertState.PAUSED -> AlertState.ACTIVE
                AlertState.TRIGGERED -> AlertState.ACTIVE
            }
            alerts[index] = current.copy(state = newState)
            saveAlerts(alerts)
        }
    }

    fun triggerAlert(alertId: String) {
        val alerts = getAlerts().toMutableList()
        val index = alerts.indexOfFirst { it.id == alertId }
        if (index >= 0) {
            alerts[index] = alerts[index].copy(
                state = AlertState.TRIGGERED,
                triggeredAt = System.currentTimeMillis()
            )
            saveAlerts(alerts)
        }
    }

    fun getAlerts(): List<StockAlert> {
        val json = prefs.getString(KEY_ALERTS, null) ?: return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                StockAlert(
                    id = obj.getString("id"),
                    ticker = obj.getString("ticker"),
                    companyName = obj.optString("companyName", ""),
                    condition = AlertCondition.valueOf(obj.getString("condition")),
                    targetPrice = obj.getDouble("targetPrice"),
                    state = AlertState.valueOf(obj.getString("state")),
                    createdAt = obj.getLong("createdAt"),
                    triggeredAt = if (obj.has("triggeredAt") && !obj.isNull("triggeredAt"))
                        obj.getLong("triggeredAt") else null
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getActiveAlertCount(): Int {
        return getAlerts().count { it.state == AlertState.ACTIVE }
    }

    fun getTriggeredUncheckedCount(): Int {
        return getAlerts().count { it.state == AlertState.TRIGGERED }
    }

    fun setAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALERTS_ENABLED, enabled).apply()
    }

    fun areAlertsEnabled(): Boolean {
        return prefs.getBoolean(KEY_ALERTS_ENABLED, true)
    }

    fun setMarketRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MARKET_REMINDERS, enabled).apply()
    }

    fun areMarketRemindersEnabled(): Boolean {
        return prefs.getBoolean(KEY_MARKET_REMINDERS, false)
    }

    /**
     * Check alerts against current prices and return list of newly triggered alert IDs.
     */
    fun checkAlerts(currentPrices: Map<String, Double>): List<StockAlert> {
        val alerts = getAlerts()
        val triggered = mutableListOf<StockAlert>()

        alerts.filter { it.state == AlertState.ACTIVE }.forEach { alert ->
            val currentPrice = currentPrices[alert.ticker] ?: return@forEach
            val shouldTrigger = when (alert.condition) {
                AlertCondition.ABOVE -> currentPrice >= alert.targetPrice
                AlertCondition.BELOW -> currentPrice <= alert.targetPrice
            }
            if (shouldTrigger) {
                triggerAlert(alert.id)
                triggered.add(alert.copy(state = AlertState.TRIGGERED))
            }
        }

        return triggered
    }

    private fun saveAlerts(alerts: List<StockAlert>) {
        val array = JSONArray()
        alerts.forEach { alert ->
            val obj = JSONObject().apply {
                put("id", alert.id)
                put("ticker", alert.ticker)
                put("companyName", alert.companyName)
                put("condition", alert.condition.name)
                put("targetPrice", alert.targetPrice)
                put("state", alert.state.name)
                put("createdAt", alert.createdAt)
                if (alert.triggeredAt != null) put("triggeredAt", alert.triggeredAt)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_ALERTS, array.toString()).apply()
    }
}
