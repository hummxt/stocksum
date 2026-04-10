package com.example.stocksum.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.stocksum.ui.MockData
import com.example.stocksum.ui.components.MarketMoodBar
import com.example.stocksum.ui.components.MarketMoverChip
import com.example.stocksum.ui.components.PortfolioHeroCard
import com.example.stocksum.ui.components.SectionHeader
import com.example.stocksum.ui.components.SkeletonHeroCard
import com.example.stocksum.ui.components.SkeletonStockRow
import com.example.stocksum.ui.components.StockRow
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import com.example.stocksum.ui.viewmodels.HomeViewModel
import com.example.stocksum.ui.viewmodels.UiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStockClick: (String) -> Unit = {},
    onSeeAllHoldings: () -> Unit = {},
    onSeeAllMovers: () -> Unit = {}
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val stocksState by viewModel.homeStocks.collectAsState()
    val portfolioStocks by viewModel.portfolioStocks.collectAsState()

    val totalValue = portfolioStocks.sumOf { it.sharesOwned * it.currentPrice }
    val todayChangeAmount = portfolioStocks.sumOf {
        it.sharesOwned * (it.currentPrice - (it.currentPrice / (1 + (it.changePercent / 100))))
    }
    val oldTotalValue = totalValue - todayChangeAmount
    val todayPercent = if (oldTotalValue > 0) (todayChangeAmount / oldTotalValue) * 100 else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(
            top = Spacing.xxl,
            bottom = Spacing.xxl
        )
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    BasicText(
                        text = "Good evening",
                        style = typography.headline.copy(color = colors.textPrimary)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.full))
                        .background(colors.accent)
                        .padding(Spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "HA",
                        style = typography.label.copy(color = colors.textPrimary)
                    )
                }
            }
        }

        item {
            PortfolioHeroCard(
                totalValue = "%.2f".format(totalValue),
                todayChange = "${if (todayChangeAmount >= 0) "+" else ""}$%.2f".format(todayChangeAmount),
                todayPercent = todayPercent,
                sparklineData = MockData.sparklineData,
                modifier = Modifier
                    .padding(horizontal = Spacing.screenHorizontal)
                    .padding(top = Spacing.xl)
            )
        }

        when (stocksState) {
            is UiState.Loading -> {
                item {
                    Column(modifier = Modifier.padding(top = Spacing.xl)) {
                        repeat(5) {
                            SkeletonStockRow(modifier = Modifier.padding(bottom = Spacing.md))
                        }
                    }
                }
            }
            is UiState.Error -> {
                item {
                    BasicText(
                        text = (stocksState as UiState.Error).message,
                        style = typography.title.copy(color = colors.loss),
                        modifier = Modifier.padding(Spacing.screenHorizontal).padding(top = Spacing.xl)
                    )
                }
            }
            is UiState.Success -> {
                val stocks = (stocksState as UiState.Success).data
                val gainers = stocks.sortedByDescending { it.changePercent }.take(6)
                
                item {
                    SectionHeader(
                        title = "Market Movers",
                        actionText = "See all →",
                        onActionClick = onSeeAllMovers,
                        modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Spacing.screenHorizontal),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(gainers) { stock ->
                            MarketMoverChip(
                                ticker = stock.ticker,
                                price = "${stock.currencySymbol}%.2f".format(stock.currentPrice),
                                changePercent = stock.changePercent,
                                onClick = { onStockClick(stock.ticker) }
                            )
                        }
                    }
                }

                item {
                    MarketMoodBar(
                        value = MockData.moodValue,
                        modifier = Modifier
                            .padding(horizontal = Spacing.screenHorizontal)
                            .padding(top = Spacing.lg)
                    )
                }

                item {
                    SectionHeader(
                        title = "Watchlist (Live)",
                        actionText = "See all →",
                        onActionClick = onSeeAllHoldings,
                        modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = Spacing.screenHorizontal)
                            .clip(RoundedCornerShape(Radius.lg))
                            .background(colors.bgCard)
                    ) {
                        stocks.forEachIndexed { index, stock ->
                            if (index == stocks.lastIndex) {
                                viewModel.loadMoreStocks()
                            }
                            
                            StockRow(
                                ticker = stock.ticker,
                                companyName = stock.companyName,
                                exchange = stock.exchange,
                                currentPrice = stock.currentPrice,
                                changePercent = stock.changePercent,
                                logoUrl = stock.logoUrl,
                                onClick = { onStockClick(stock.ticker) }
                            )
                            if (index < stocks.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = Spacing.lg)
                                        .height(Spacing.xs / 2)
                                        .background(colors.borderSubtle)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}
