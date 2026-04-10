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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.stocksum.data.AlertCondition
import com.example.stocksum.data.AlertState
import com.example.stocksum.data.StockAlert
import com.example.stocksum.ui.components.AlertRow
import com.example.stocksum.ui.components.AlertStatus
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import com.example.stocksum.ui.viewmodels.HomeViewModel

@Composable
fun AlertsScreen(
    viewModel: HomeViewModel,
    onStockClick: (String) -> Unit = {}
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Triggered", "Paused")

    val allAlerts by viewModel.alerts.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredAlerts = allAlerts.filter {
        when (selectedTab) {
            0 -> it.state == AlertState.ACTIVE
            1 -> it.state == AlertState.TRIGGERED
            2 -> it.state == AlertState.PAUSED
            else -> true
        }
    }

    // Create alert dialog
    if (showCreateDialog) {
        QuickAlertDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { ticker, condition, targetPrice ->
                viewModel.addAlert(ticker, "", condition, targetPrice)
                showCreateDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(top = Spacing.xxl, bottom = Spacing.xxl)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = "Alerts",
                    style = typography.headline.copy(color = colors.textPrimary)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(colors.accentBg)
                        .clickable { showCreateDialog = true }
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                ) {
                    BasicText(
                        text = "+ New Alert",
                        style = typography.label.copy(color = colors.accent)
                    )
                }
            }
        }

        // Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selectedTab
                    val count = allAlerts.count {
                        when (index) {
                            0 -> it.state == AlertState.ACTIVE
                            1 -> it.state == AlertState.TRIGGERED
                            2 -> it.state == AlertState.PAUSED
                            else -> false
                        }
                    }
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
                            text = "$tab ($count)",
                            style = typography.label.copy(
                                color = if (isSelected) colors.accent else colors.textSecondary
                            )
                        )
                    }
                }
            }
        }

        if (filteredAlerts.isEmpty()) {
            // Empty state
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(colors.accentBg),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "🔔",
                            style = typography.headline.copy(color = colors.accent)
                        )
                    }
                    Spacer(modifier = Modifier.height(Spacing.md))
                    BasicText(
                        text = when (selectedTab) {
                            0 -> "No active alerts"
                            1 -> "No triggered alerts"
                            else -> "No paused alerts"
                        },
                        style = typography.title.copy(color = colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    BasicText(
                        text = if (selectedTab == 0) "Tap '+ New Alert' to create one" else "",
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
                        val status = when (alert.state) {
                            AlertState.ACTIVE -> AlertStatus.ACTIVE
                            AlertState.TRIGGERED -> AlertStatus.TRIGGERED
                            AlertState.PAUSED -> AlertStatus.PAUSED
                        }
                        val timeAgo = getTimeAgo(alert.createdAt)
                        val conditionText = when (alert.condition) {
                            AlertCondition.ABOVE -> "Above"
                            AlertCondition.BELOW -> "Below"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                AlertRow(
                                    ticker = alert.ticker,
                                    condition = conditionText,
                                    targetPrice = "$%.2f".format(alert.targetPrice),
                                    currentPrice = "",
                                    timeAgo = timeAgo,
                                    status = status,
                                    onClick = {
                                        // Toggle alert state on tap
                                        viewModel.toggleAlert(alert.id)
                                    }
                                )
                            }
                            // Delete button
                            Box(
                                modifier = Modifier
                                    .padding(end = Spacing.md)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(colors.lossBg)
                                    .clickable { viewModel.removeAlert(alert.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                BasicText(
                                    text = "✕",
                                    style = typography.caption.copy(color = colors.loss)
                                )
                            }
                        }

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

// Quick alert creation dialog
@Composable
fun QuickAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, AlertCondition, Double) -> Unit
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    var tickerText by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableIntStateOf(0) }
    var targetPriceText by remember { mutableStateOf("") }
    val conditions = listOf("Above", "Below")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase.copy(alpha = 0.85f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = Spacing.xxl)
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.xl))
                .background(colors.bgCard)
                .clickable(enabled = false, onClick = {})
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            BasicText(
                text = "Create New Alert",
                style = typography.title.copy(color = colors.textPrimary)
            )

            // Ticker input
            BasicText(
                text = "Stock Ticker",
                style = typography.label.copy(color = colors.textSecondary)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(colors.bgInput)
                    .padding(Spacing.md)
            ) {
                if (tickerText.isEmpty()) {
                    BasicText(
                        text = "e.g. AAPL",
                        style = typography.body.copy(color = colors.textTertiary)
                    )
                }
                BasicTextField(
                    value = tickerText,
                    onValueChange = { tickerText = it.uppercase() },
                    textStyle = typography.title.copy(color = colors.textPrimary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Condition
            BasicText(
                text = "Condition",
                style = typography.label.copy(color = colors.textSecondary)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                conditions.forEachIndexed { index, label ->
                    val isSelected = index == selectedCondition
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.sm))
                            .background(if (isSelected) colors.accentBg else colors.bgElevated)
                            .clickable { selectedCondition = index }
                            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = label,
                            style = typography.label.copy(
                                color = if (isSelected) colors.accent else colors.textSecondary
                            )
                        )
                    }
                }
            }

            // Target price
            BasicText(
                text = "Target Price ($)",
                style = typography.label.copy(color = colors.textSecondary)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(colors.bgInput)
                    .padding(Spacing.md)
            ) {
                if (targetPriceText.isEmpty()) {
                    BasicText(
                        text = "e.g. 200.00",
                        style = typography.body.copy(color = colors.textTertiary)
                    )
                }
                BasicTextField(
                    value = targetPriceText,
                    onValueChange = { targetPriceText = it },
                    textStyle = typography.title.copy(color = colors.textPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(colors.bgElevated)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = Spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Cancel",
                        style = typography.label.copy(color = colors.textSecondary)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(colors.accent)
                        .clickable {
                            val price = targetPriceText.toDoubleOrNull()
                            if (tickerText.isNotBlank() && price != null && price > 0) {
                                val condition = if (selectedCondition == 0) AlertCondition.ABOVE else AlertCondition.BELOW
                                onConfirm(tickerText.trim(), condition, price)
                            }
                        }
                        .padding(vertical = Spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Create",
                        style = typography.label.copy(color = colors.bgBase)
                    )
                }
            }
        }
    }
}

private fun getTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        days < 30 -> "${days / 7}w ago"
        else -> "${days / 30}mo ago"
    }
}
