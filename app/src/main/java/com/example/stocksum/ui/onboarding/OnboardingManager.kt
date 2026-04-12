package com.example.stocksum.ui.onboarding

import android.content.Context
import android.content.SharedPreferences

class OnboardingManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "stocksum_onboarding",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_IS_FIRST_TIME = "is_first_time_user"
        private const val KEY_ONBOARDING_DISMISSED = "onboarding_dismissed"
        private const val KEY_DEMO_COMPLETED = "demo_completed"
    }

    fun isFirstTimeUser(): Boolean {
        return prefs.getBoolean(KEY_IS_FIRST_TIME, true)
    }

    fun markFirstTimeUserComplete() {
        prefs.edit().putBoolean(KEY_IS_FIRST_TIME, false).apply()
    }

    fun isOnboardingDismissed(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_DISMISSED, false)
    }

    fun dismissOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_DISMISSED, true).apply()
        markFirstTimeUserComplete()
    }

    fun isDemoCompleted(): Boolean {
        return prefs.getBoolean(KEY_DEMO_COMPLETED, false)
    }

    fun markDemoCompleted() {
        prefs.edit().putBoolean(KEY_DEMO_COMPLETED, true).apply()
    }

    fun resetOnboarding() {
        prefs.edit()
            .remove(KEY_IS_FIRST_TIME)
            .remove(KEY_ONBOARDING_DISMISSED)
            .remove(KEY_DEMO_COMPLETED)
            .apply()
    }
}
