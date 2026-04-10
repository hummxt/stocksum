package com.example.stocksum.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.TimeZone

class MarketReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val alertManager = AlertManager(applicationContext)

        // Check if market reminders are still enabled
        if (!alertManager.areMarketRemindersEnabled()) {
            return Result.success()
        }

        val notificationHelper = NotificationHelper(applicationContext)
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Only run on weekdays
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return Result.success()
        }

        // Market opens at 9:30 AM ET — notify between 9:15-9:30
        if (hour == 9 && minute in 15..30) {
            notificationHelper.showMarketReminderNotification(
                "📈 US Stock Market opens in 15 minutes! Get ready for today's trading session."
            )
        }

        // Market closes at 4:00 PM ET — notify between 3:45-4:00
        if (hour == 15 && minute in 45..59) {
            notificationHelper.showMarketReminderNotification(
                "📉 US Stock Market closes in 15 minutes! Review your positions before the bell."
            )
        }

        return Result.success()
    }
}
