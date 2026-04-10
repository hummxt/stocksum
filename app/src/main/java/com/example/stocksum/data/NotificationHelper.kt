package com.example.stocksum.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.stocksum.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ALERTS = "stock_alerts"
        const val CHANNEL_REMINDERS = "market_reminders"
        private var notificationId = 1000
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alertChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Stock Price Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when stock prices hit your target"
                enableVibration(true)
            }

            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Market Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for market open and close times"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(alertChannel)
            manager.createNotificationChannel(reminderChannel)
        }
    }

    fun showAlertNotification(ticker: String, condition: String, targetPrice: Double, currentPrice: Double) {
        val title = "$ticker Alert Triggered!"
        val conditionText = if (condition == "ABOVE") "risen above" else "fallen below"
        val body = "$ticker has $conditionText your target of $${"%.2f".format(targetPrice)}. Current price: $${"%.2f".format(currentPrice)}"

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId++, notification)
    }

    fun showMarketReminderNotification(message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Market Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId++, notification)
    }
}
