package com.example.stocksum.ui.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class StreakData(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastOpenDate: Long = 0
)

class StreakViewModel(private val sharedPrefs: SharedPreferences) : ViewModel() {

    private val _streakData = MutableStateFlow(StreakData())
    val streakData: StateFlow<StreakData> = _streakData.asStateFlow()

    init {
        loadStreakData()
    }

    private fun loadStreakData() {
        val currentStreak = sharedPrefs.getInt("current_streak", 0)
        val bestStreak = sharedPrefs.getInt("best_streak", 0)
        val lastOpenDate = sharedPrefs.getLong("last_open_date", 0)
        
        _streakData.value = StreakData(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            lastOpenDate = lastOpenDate
        )
    }

    fun updateStreakOnAppOpen() {
        val current = _streakData.value
        val lastOpenCalendar = Calendar.getInstance().apply {
            timeInMillis = current.lastOpenDate
        }
        val todayCalendar = Calendar.getInstance()

        val lastOpenDay = lastOpenCalendar.get(Calendar.DAY_OF_YEAR)
        val todayDay = todayCalendar.get(Calendar.DAY_OF_YEAR)
        val lastOpenYear = lastOpenCalendar.get(Calendar.YEAR)
        val todayYear = todayCalendar.get(Calendar.YEAR)

        val newStreak = when {
            // Same day - no change
            lastOpenDay == todayDay && lastOpenYear == todayYear -> current.currentStreak
            // Consecutive day (yesterday) - increment streak
            (todayDay - lastOpenDay == 1 || (todayDay == 1 && lastOpenDay > 20)) && 
            lastOpenYear == todayYear -> current.currentStreak + 1
            // New year crossing
            lastOpenYear < todayYear && lastOpenDay > 20 && todayDay < 5 -> current.currentStreak + 1
            // Day skipped - reset streak
            else -> 1
        }

        val newBest = maxOf(newStreak, current.bestStreak)

        val newStreakData = StreakData(
            currentStreak = newStreak,
            bestStreak = newBest,
            lastOpenDate = System.currentTimeMillis()
        )

        _streakData.value = newStreakData
        saveStreakData(newStreakData)
    }

    private fun saveStreakData(streakData: StreakData) {
        sharedPrefs.edit().apply {
            putInt("current_streak", streakData.currentStreak)
            putInt("best_streak", streakData.bestStreak)
            putLong("last_open_date", streakData.lastOpenDate)
            apply()
        }
    }

    fun resetStreak() {
        val newStreakData = StreakData()
        _streakData.value = newStreakData
        saveStreakData(newStreakData)
    }
}
