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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.MockData
import com.example.stocksum.ui.components.PriceBadge
import com.example.stocksum.ui.components.PrimaryButton
import com.example.stocksum.ui.components.SectionHeader
import com.example.stocksum.ui.components.SparklineChart
import com.example.stocksum.ui.components.tickerToColor
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import androidx.compose.runtime.collectAsState
import com.example.stocksum.ui.viewmodels.HomeViewModel
import com.example.stocksum.ui.viewmodels.UiState

@Composable
fun StockDetailScreen(
    ticker: String,
    viewModel: HomeViewModel,
    onBack: () -> Unit = {}
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val stocksState by viewModel.homeStocks.collectAsState()
    val loadedStocks = (stocksState as? UiState.Success<List<com.example.stocksum.ui.MockStock>>)?.data ?: emptyList()
    
    val searchState by viewModel.searchResults.collectAsState()
    val searchStocks = (searchState as? UiState.Success<List<com.example.stocksum.ui.MockStock>>)?.data ?: emptyList()
    
    val stock = loadedStocks.find { it.ticker == ticker } 
        ?: searchStocks.find { it.ticker == ticker }
        ?: MockData.allStocks.find { it.ticker == ticker } 
        ?: MockData.allStocks.first()

    val portfolioStock = MockData.portfolioStocks.find { it.ticker == ticker }

    var selectedTimeFilter by remember { mutableIntStateOf(0) }
    val timeFilters = listOf("1D", "1W", "1M", "3M", "1Y")

    val isPositive = stock.changePercent >= 0
    val priceColor = if (isPositive) colors.textGain else colors.textLoss
    val lineColor = if (isPositive) colors.gain else colors.loss

    val chartData = remember(ticker, selectedTimeFilter) {
        val baseData = MockData.sparklineData
        val seed = ticker.hashCode().toLong() + selectedTimeFilter
        val random = java.util.Random(seed)
        
        when (selectedTimeFilter) {
            0 -> baseData.map { it * (0.98f + random.nextFloat() * 0.04f) } 
            1 -> baseData.mapIndexed { i, v -> v * (0.95f + (random.nextFloat() * 0.1f)) + (i * 10f) } 
            2 -> baseData.map { it * (0.9f + random.nextFloat() * 0.2f) } 
            3 -> baseData.reversed().map { it * (0.85f + random.nextFloat() * 0.3f) } 
            else -> baseData.map { it * (0.8f + random.nextFloat() * 0.4f) } 
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(top = Spacing.xxl, bottom = Spacing.xxl)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = "←",
                    style = typography.headline.copy(color = colors.textPrimary),
                    modifier = Modifier
                        .clickable(onClick = onBack)
                        .padding(end = Spacing.lg)
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (stock.logoUrl != null && stock.logoUrl.isNotEmpty()) {
                        coil.compose.AsyncImage(
                            model = stock.logoUrl,
                            contentDescription = "${stock.companyName} logo",
                            modifier = Modifier.size(48.dp).clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(tickerToColor(stock.ticker)),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicText(
                                text = stock.ticker.take(2),
                                style = typography.label.copy(
                                    color = androidx.compose.ui.graphics.Color.White,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(start = Spacing.md)) {
                    BasicText(
                        text = stock.ticker,
                        style = typography.title.copy(color = colors.textPrimary)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicText(
                            text = stock.companyName,
                            style = typography.caption.copy(color = colors.textSecondary)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.sm))
                                .background(colors.bgElevated)
                                .padding(horizontal = Spacing.xs, vertical = 2.dp)
                        ) {
                            BasicText(
                               text = stock.exchange,
                               style = typography.caption.copy(color = colors.textSecondary)
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.screenHorizontal)
                    .padding(top = Spacing.xl)
            ) {
                BasicText(
                    text = "${stock.currencySymbol}%.2f".format(stock.currentPrice),
                    style = typography.display.copy(color = colors.textPrimary)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.padding(top = Spacing.xs)
                ) {
                    val sign = if (isPositive) "+" else ""
                    val changeAmount = stock.currentPrice * stock.changePercent / 100
                    BasicText(
                        text = "$sign${stock.currencySymbol}%.2f".format(changeAmount),
                        style = typography.body.copy(color = priceColor)
                    )
                    PriceBadge(changePercent = stock.changePercent)
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                timeFilters.forEachIndexed { index, filter ->
                    val isSelected = index == selectedTimeFilter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Radius.sm))
                            .background(if (isSelected) colors.accentBg else colors.bgCard)
                            .clickable { selectedTimeFilter = index }
                            .padding(vertical = Spacing.sm),
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

        item {
            SparklineChart(
                data = chartData,
                lineColor = lineColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = Spacing.screenHorizontal)
            )
        }

        if (portfolioStock != null) {
            item {
                SectionHeader(
                    title = "Your Position",
                    modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenHorizontal)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.lg))
                        .background(colors.bgCard)
                        .padding(Spacing.lg)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("Shares", "%.1f".format(portfolioStock.sharesOwned))
                        StatItem("Avg Cost", "${stock.currencySymbol}%.2f".format(portfolioStock.purchasePrice))
                        StatItem("Value", "${stock.currencySymbol}%.2f".format(portfolioStock.currentPrice * portfolioStock.sharesOwned))
                        val pnlColor = if (portfolioStock.pnlValue >= 0) colors.textGain else colors.textLoss
                        StatItem(
                            "P&L",
                            "${if (portfolioStock.pnlValue >= 0) "+" else ""}${stock.currencySymbol}%.2f".format(portfolioStock.pnlValue),
                            pnlColor
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Key Stats",
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.screenHorizontal)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.lg))
                    .background(colors.bgCard)
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                StatRow("Market Cap", "$2.78T")
                StatRow("52W High", "${stock.currencySymbol}199.62")
                StatRow("52W Low", "${stock.currencySymbol}124.17")
                StatRow("Volume", "52.3M")
                StatRow("P/E Ratio", "28.4")
            }
        }

        item {
            SectionHeader(
                title = "About",
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = Spacing.screenHorizontal)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.lg))
                    .background(colors.bgCard)
                    .padding(Spacing.lg)
            ) {
                BasicText(
                    text = "${stock.companyName} is a multinational technology company that designs, develops, and sells consumer electronics, software, and services.",
                    style = typography.body.copy(color = colors.textSecondary)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color? = null
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BasicText(
            text = label,
            style = typography.caption.copy(color = colors.textSecondary)
        )
        BasicText(
            text = value,
            style = typography.label.copy(color = valueColor ?: colors.textPrimary),
            modifier = Modifier.padding(top = Spacing.xs)
        )
    }
}

@Composable
fun StatRow(label: String, value: String) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            text = label,
            style = typography.body.copy(color = colors.textSecondary)
        )
        BasicText(
            text = value,
            style = typography.body.copy(color = colors.textPrimary)
        )
    }
}
