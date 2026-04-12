package com.example.stocksum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun StreakBadge(
    streak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography
    
    // Determine milestone
    val milestone = when {
        streak >= 100 -> "🎖️"
        streak >= 30 -> "🏆"
        streak >= 7 -> "⭐"
        else -> "🔥"
    }

    // Animate scale if milestone achieved
    val scale by animateFloatAsState(targetValue = 1.0f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.md))
            .background(colors.bgCard)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji
        BasicText(
            text = milestone,
            style = typography.headline,
            modifier = Modifier.scale(scale)
        )

        // Streak info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicText(
                text = "$streak",
                style = typography.title.copy(color = colors.accent)
            )
            BasicText(
                text = "day${if (streak != 1) "s" else ""}",
                style = typography.label.copy(color = colors.textSecondary)
            )
        }

        // Best streak indicator (if less than best)
        if (streak > 0 && streak < bestStreak) {
            BasicText(
                text = "• Best: $bestStreak",
                style = typography.caption.copy(color = colors.textSecondary)
            )
        }
    }
}
