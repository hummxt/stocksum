package com.example.stocksum.ui.onboarding

enum class OnboardingStep {
    WELCOME,
    ADD_STOCK,
    SET_ALERT,
    MARKET_SUMMARY
}

data class OnboardingState(
    val isFirstTimeUser: Boolean = true,
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val isDismissed: Boolean = false,
    val demoPortfolioActive: Boolean = false,
    val demoCountdownSeconds: Int = 0
)
