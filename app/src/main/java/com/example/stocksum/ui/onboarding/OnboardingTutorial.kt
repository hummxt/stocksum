package com.example.stocksum.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import kotlinx.coroutines.delay

@Composable
fun OnboardingTutorial(
    onDismiss: () -> Unit,
    onTryDemo: () -> Unit,
    onSkip: () -> Unit
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        OnboardingStepData(
            emoji = "📈",
            title = "Welcome to StockSum",
            subtitle = "Track your stocks with ease",
            description = "Monitor your portfolio, set alerts, and discover investment opportunities in one place."
        ),
        OnboardingStepData(
            emoji = "➕",
            title = "Add Your First Stock",
            subtitle = "Build your portfolio",
            description = "Go to Stock Detail screen and tap the Portfolio button to add stocks you own."
        ),
        OnboardingStepData(
            emoji = "🔔",
            title = "Set Price Alerts",
            subtitle = "Stay updated",
            description = "Get notified when stocks hit your target prices. Never miss an opportunity again!"
        ),
        OnboardingStepData(
            emoji = "📊",
            title = "Check Market Summary",
            subtitle = "Discover trends",
            description = "See top gainers, losers, and most active stocks to find your next investment."
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(colors.bgCard)
            .padding(Spacing.lg)
    ) {
        Column {
            // Close button
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(24.dp))

                // Step indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(steps.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index <= currentStep) colors.accent
                                    else colors.borderSubtle
                                )
                        )
                    }
                }

                // Close X button
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable { onSkip() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Step content with animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300)) + scaleIn(
                    tween(300),
                    initialScale = 0.9f
                ),
                exit = fadeOut(tween(200))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Emoji icon
                    BasicText(
                        text = steps[currentStep].emoji,
                        style = typography.display.copy(fontSize = 48.sp)
                    )

                    // Title
                    BasicText(
                        text = steps[currentStep].title,
                        style = typography.headline.copy(color = colors.textPrimary),
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )

                    // Subtitle
                    BasicText(
                        text = steps[currentStep].subtitle,
                        style = typography.label.copy(color = colors.accent),
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )

                    // Description
                    BasicText(
                        text = steps[currentStep].description,
                        style = typography.body.copy(color = colors.textSecondary),
                        modifier = Modifier.padding(horizontal = Spacing.md)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                if (currentStep == steps.size - 1) {
                    // Last step: Show demo and start buttons
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Radius.md))
                            .background(colors.bgElevated)
                            .clickable { onTryDemo() }
                            .padding(Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "Try Demo",
                            style = typography.label.copy(color = colors.accent)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Radius.md))
                            .background(colors.accent)
                            .clickable { onDismiss() }
                            .padding(Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "Start",
                            style = typography.label.copy(color = colors.bgBase)
                        )
                    }
                } else {
                    // Other steps: Next button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Radius.md))
                            .background(colors.accent)
                            .clickable {
                                if (currentStep < steps.size - 1) {
                                    currentStep++
                                }
                            }
                            .padding(Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BasicText(
                                text = "Next",
                                style = typography.label.copy(color = colors.bgBase)
                            )
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = "Next",
                                tint = colors.bgBase,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class OnboardingStepData(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val description: String
)
