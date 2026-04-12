package com.example.stocksum.data

import android.content.Context
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleMarketNotifications() {
        scheduleMarketOpenNotification()
        scheduleMarketCloseNotification()
    }

    private fun scheduleMarketOpenNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }

        val now = System.currentTimeMillis()
        val notificationTime = calendar.timeInMillis

        // Calculate initial delay
        var initialDelayMinutes = ((notificationTime - now) / (1000 * 60)).toInt()
        if (initialDelayMinutes < 0) {
            initialDelayMinutes += (24 * 60) // Next day
        }

        val inputData = Data.Builder()
            .putString("time_of_day", "open")
            .build()

        val marketOpenWork = PeriodicWorkRequest.Builder(
            MarketNotificationWorker::class.java,
            24,
            TimeUnit.HOURS
        )
            .setInitialDelay(initialDelayMinutes.toLong(), TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag("market_open_notification")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "market_open_notification",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            marketOpenWork
        )
    }

    private fun scheduleMarketCloseNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val now = System.currentTimeMillis()
        val notificationTime = calendar.timeInMillis

        // Calculate initial delay
        var initialDelayMinutes = ((notificationTime - now) / (1000 * 60)).toInt()
        if (initialDelayMinutes < 0) {
            initialDelayMinutes += (24 * 60) // Next day
        }

        val inputData = Data.Builder()
            .putString("time_of_day", "close")
            .build()

        val marketCloseWork = PeriodicWorkRequest.Builder(
            MarketNotificationWorker::class.java,
            24,
            TimeUnit.HOURS
        )
            .setInitialDelay(initialDelayMinutes.toLong(), TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag("market_close_notification")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "market_close_notification",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            marketCloseWork
        )
    }

    fun cancelMarketNotifications() {
        workManager.cancelAllWorkByTag("market_open_notification")
        workManager.cancelAllWorkByTag("market_close_notification")
    }
}
