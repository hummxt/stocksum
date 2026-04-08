package com.example.stocksum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

enum class AlertStatus { ACTIVE, TRIGGERED, PAUSED }

@Composable
fun AlertRow(
    ticker: String,
    condition: String,
    targetPrice: String,
    currentPrice: String,
    timeAgo: String,
    status: AlertStatus,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val dotColor = when (status) {
        AlertStatus.ACTIVE -> colors.accent
        AlertStatus.TRIGGERED -> colors.gain
        AlertStatus.PAUSED -> colors.textSecondary
    }

    val statusText = when (status) {
        AlertStatus.ACTIVE -> "Active"
        AlertStatus.TRIGGERED -> "Triggered"
        AlertStatus.PAUSED -> "Paused"
    }

    val statusBgColor = when (status) {
        AlertStatus.ACTIVE -> colors.accentBg
        AlertStatus.TRIGGERED -> colors.gainBg
        AlertStatus.PAUSED -> colors.bgElevated
    }

    val statusTextColor = when (status) {
        AlertStatus.ACTIVE -> colors.accent
        AlertStatus.TRIGGERED -> colors.gain
        AlertStatus.PAUSED -> colors.textSecondary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.md),
            verticalArrangement = Arrangement.Center
        ) {
            BasicText(
                text = "$ticker · $condition $targetPrice",
                style = typography.title.copy(color = colors.textPrimary)
            )
            BasicText(
                text = "Current: $currentPrice · Set $timeAgo",
                style = typography.caption.copy(color = colors.textSecondary)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.sm))
                .background(statusBgColor)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
        ) {
            BasicText(
                text = statusText,
                style = typography.label.copy(color = statusTextColor)
            )
        }
    }
}
