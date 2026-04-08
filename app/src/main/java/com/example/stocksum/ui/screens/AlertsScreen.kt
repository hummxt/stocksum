package com.example.stocksum.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.components.AlertRow
import com.example.stocksum.ui.components.AlertStatus
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

data class MockAlert(
    val ticker: String,
    val condition: String,
    val targetPrice: String,
    val currentPrice: String,
    val timeAgo: String,
    val status: AlertStatus
)

@Composable
fun AlertsScreen() {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Triggered", "Paused")

    val alerts = listOf(
        MockAlert("AAPL", "Above", "$200.00", "$178.90", "3d ago", AlertStatus.ACTIVE),
        MockAlert("TSLA", "Below", "$150.00", "$175.20", "1w ago", AlertStatus.ACTIVE),
        MockAlert("NVDA", "Above", "$900.00", "$892.40", "2d ago", AlertStatus.ACTIVE),
        MockAlert("GOOGL", "Below", "$140.00", "$155.72", "5d ago", AlertStatus.ACTIVE),
        MockAlert("MSFT", "Above", "$400.00", "$415.60", "1d ago", AlertStatus.TRIGGERED),
        MockAlert("AMD", "Above", "$160.00", "$168.50", "4d ago", AlertStatus.TRIGGERED),
        MockAlert("META", "Below", "$480.00", "$505.20", "2w ago", AlertStatus.PAUSED)
    )

    val filteredAlerts = alerts.filter {
        when (selectedTab) {
            0 -> it.status == AlertStatus.ACTIVE
            1 -> it.status == AlertStatus.TRIGGERED
            2 -> it.status == AlertStatus.PAUSED
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(top = Spacing.xxl, bottom = Spacing.xxl)
    ) {
        item {
            BasicText(
                text = "Alerts",
                style = typography.headline.copy(color = colors.textPrimary),
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selectedTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Radius.sm))
                            .background(if (isSelected) colors.accentBg else colors.bgCard)
                            .clickable { selectedTab = index }
                            .padding(vertical = Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = tab,
                            style = typography.label.copy(
                                color = if (isSelected) colors.accent else colors.textSecondary
                            )
                        )
                    }
                }
            }
        }

        if (filteredAlerts.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BasicText(
                        text = when (selectedTab) {
                            0 -> "No active alerts"
                            1 -> "No alerts have triggered yet"
                            else -> "No paused alerts"
                        },
                        style = typography.title.copy(color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    BasicText(
                        text = if (selectedTab == 0) "Create an alert to get notified" else "",
                        style = typography.caption.copy(color = colors.textTertiary)
                    )
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenHorizontal)
                        .clip(RoundedCornerShape(Radius.lg))
                        .background(colors.bgCard)
                ) {
                    filteredAlerts.forEachIndexed { index, alert ->
                        AlertRow(
                            ticker = alert.ticker,
                            condition = alert.condition,
                            targetPrice = alert.targetPrice,
                            currentPrice = alert.currentPrice,
                            timeAgo = alert.timeAgo,
                            status = alert.status
                        )
                        if (index < filteredAlerts.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Spacing.lg)
                                    .height(0.5.dp)
                                    .background(colors.borderSubtle)
                            )
                        }
                    }
                }
            }
        }
    }
}
