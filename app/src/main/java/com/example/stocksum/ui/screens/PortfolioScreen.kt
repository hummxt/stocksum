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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.MockData
import com.example.stocksum.ui.MockStock
import com.example.stocksum.ui.components.SparklineChart
import com.example.stocksum.ui.components.StockRow
import com.example.stocksum.ui.components.PortfolioHeroCard
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import com.example.stocksum.ui.viewmodels.HomeViewModel

@Composable
fun PortfolioScreen(
    viewModel: HomeViewModel,
    onStockClick: (String) -> Unit = {}
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography
    var selectedTimeFilter by remember { mutableIntStateOf(1) }
    val timeFilters = listOf("1D", "1W", "1M", "3M", "1Y")

    val portfolioStocks by viewModel.portfolioStocks.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }

    val totalValue = portfolioStocks.sumOf { it.sharesOwned * it.currentPrice }
    val totalGain = portfolioStocks.sumOf { it.pnlValue }
    val todayChange = portfolioStocks.sumOf {
        it.sharesOwned * (it.currentPrice - (it.currentPrice / (1 + (it.changePercent / 100))))
    }
    val oldTotalValue = totalValue - todayChange
    val todayPercent = if (oldTotalValue > 0) (todayChange / oldTotalValue) * 100 else 0.0

    val portfolioChartData = remember(selectedTimeFilter) {
        val baseData = MockData.sparklineData
        val seed = 42L + selectedTimeFilter
        val random = java.util.Random(seed)

        when (selectedTimeFilter) {
            0 -> baseData.map { it * (0.98f + random.nextFloat() * 0.04f) }
            1 -> baseData
            2 -> baseData.map { it * (0.95f + random.nextFloat() * 0.1f) }
            3 -> baseData.reversed().map { it * (0.9f + random.nextFloat() * 0.2f) }
            else -> baseData.map { it * (0.85f + random.nextFloat() * 0.3f) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(top = Spacing.xxl, bottom = Spacing.xxl)
    ) {
        // Header with Edit toggle
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = "Portfolio",
                    style = typography.headline.copy(color = colors.textPrimary)
                )
                if (portfolioStocks.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.sm))
                            .background(if (isEditMode) colors.lossBg else colors.bgCard)
                            .clickable { isEditMode = !isEditMode }
                            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                    ) {
                        BasicText(
                            text = if (isEditMode) "Done" else "Edit",
                            style = typography.label.copy(
                                color = if (isEditMode) colors.loss else colors.accent
                            )
                        )
                    }
                }
            }
        }

        if (portfolioStocks.isEmpty()) {
            // Empty state
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xxl + Spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(colors.accentBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Folder,
                            contentDescription = "Portfolio",
                            tint = colors.accent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    BasicText(
                        text = "No stocks yet",
                        style = typography.title.copy(color = colors.textPrimary)
                    )
                    BasicText(
                        text = "Add stocks from the stock detail screen",
                        style = typography.caption.copy(color = colors.textSecondary)
                    )
                    BasicText(
                        text = "Tap any stock → Portfolio button",
                        style = typography.caption.copy(color = colors.textTertiary)
                    )
                }
            }
        } else {
            // Hero card
            item {
                PortfolioHeroCard(
                    totalValue = "%.2f".format(totalValue),
                    todayChange = "${if (todayChange >= 0) "+" else ""}$%.2f".format(todayChange),
                    todayPercent = todayPercent,
                    sparklineData = portfolioChartData,
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenHorizontal)
                        .padding(top = Spacing.xl)
                )
            }

            // Total P&L summary
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.md)
                        .clip(RoundedCornerShape(Radius.lg))
                        .background(colors.bgCard)
                        .padding(Spacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicText(
                            text = "Total Value",
                            style = typography.caption.copy(color = colors.textSecondary)
                        )
                        BasicText(
                            text = "$%.2f".format(totalValue),
                            style = typography.title.copy(color = colors.textPrimary)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicText(
                            text = "Total P&L",
                            style = typography.caption.copy(color = colors.textSecondary)
                        )
                        val pnlColor = if (totalGain >= 0) colors.textGain else colors.textLoss
                        BasicText(
                            text = "${if (totalGain >= 0) "+" else ""}$%.2f".format(totalGain),
                            style = typography.title.copy(color = pnlColor)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicText(
                            text = "Holdings",
                            style = typography.caption.copy(color = colors.textSecondary)
                        )
                        BasicText(
                            text = "${portfolioStocks.size}",
                            style = typography.title.copy(color = colors.textPrimary)
                        )
                    }
                }
            }

            // Time filters
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    timeFilters.forEachIndexed { index, filter ->
                        val isSelected = index == selectedTimeFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.sm))
                                .background(if (isSelected) colors.accentBg else colors.bgCard)
                                .clickable { selectedTimeFilter = index }
                                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicText(
                                text = filter,
                                style = typography.label.copy(
                                    color = if (isSelected) colors.accent else colors.textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Table header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenHorizontal)
                        .clip(RoundedCornerShape(topStart = Radius.lg, topEnd = Radius.lg))
                        .background(colors.bgCard)
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicText(text = "Stock", style = typography.caption.copy(color = colors.textSecondary))
                    BasicText(text = "Shares", style = typography.caption.copy(color = colors.textSecondary))
                    BasicText(text = "Price", style = typography.caption.copy(color = colors.textSecondary))
                    BasicText(text = "P&L", style = typography.caption.copy(color = colors.textSecondary))
                }
            }

            // Stock rows
            items(portfolioStocks) { stock ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenHorizontal)
                        .background(colors.bgCard)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            StockRow(
                                ticker = stock.ticker,
                                companyName = stock.companyName,
                                exchange = stock.exchange,
                                currentPrice = stock.currentPrice,
                                changePercent = stock.changePercent,
                                pnlValue = stock.pnlValue,
                                sharesOwned = stock.sharesOwned,
                                logoUrl = stock.logoUrl,
                                onClick = { onStockClick(stock.ticker) }
                            )
                        }
                        if (isEditMode) {
                            Box(
                                modifier = Modifier
                                    .padding(end = Spacing.md)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colors.lossBg)
                                    .clickable { viewModel.removeFromPortfolio(stock.ticker) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Remove",
                                    tint = colors.loss,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg)
                            .height(0.5.dp)
                            .background(colors.borderSubtle)
                    )
                }
            }

            // Bottom rounded corner
            item {
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenHorizontal)
                        .fillMaxWidth()
                        .height(Spacing.lg)
                        .clip(RoundedCornerShape(bottomStart = Radius.lg, bottomEnd = Radius.lg))
                        .background(colors.bgCard)
                )
            }
        }
    }
}
