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
import androidx.compose.foundation.lazy.items
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
import com.example.stocksum.ui.MockData
import com.example.stocksum.ui.components.SparklineChart
import com.example.stocksum.ui.components.StockRow
import com.example.stocksum.ui.components.PortfolioHeroCard
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import androidx.compose.runtime.collectAsState
import com.example.stocksum.ui.viewmodels.HomeViewModel
import com.example.stocksum.ui.viewmodels.UiState

@Composable
fun PortfolioScreen(
    viewModel: HomeViewModel,
    onStockClick: (String) -> Unit = {}
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography
    var selectedTimeFilter by remember { mutableIntStateOf(1) }
    val timeFilters = listOf("1D", "1W", "1M", "3M", "1Y")

    val stocksState by viewModel.homeStocks.collectAsState()
    val loadedStocks = if (stocksState is UiState.Success) (stocksState as UiState.Success).data else emptyList()
    
    val portfolioStocks = remember(loadedStocks) {
        if (loadedStocks.isEmpty()) emptyList<com.example.stocksum.ui.MockStock>()
        else loadedStocks.take(6).mapIndexed { index, stock ->
            val shares = (index + 2) * 5.0
            val purchasePrice = stock.currentPrice * (0.85 + (index * 0.05))
            val pnlValue = (stock.currentPrice - purchasePrice) * shares
            
            stock.copy(
                sharesOwned = shares,
                purchasePrice = purchasePrice,
                pnlValue = pnlValue
            )
        }
    }

    val totalValue = portfolioStocks.sumOf { it.sharesOwned * it.currentPrice }
    val totalGain = portfolioStocks.sumOf { it.pnlValue }
    val todayChange = portfolioStocks.sumOf { it.sharesOwned * (it.currentPrice - (it.currentPrice / (1 + (it.changePercent / 100)))) }

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
        item {
            BasicText(
                text = "Portfolio",
                style = typography.headline.copy(color = colors.textPrimary),
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            PortfolioHeroCard(
                totalValue = "%.2f".format(totalValue),
                todayChange = "${if (todayChange >= 0) "+" else ""}$%.2f".format(todayChange),
                todayPercent = 1.42, 
                sparklineData = portfolioChartData,
                modifier = Modifier
                    .padding(horizontal = Spacing.screenHorizontal)
                    .padding(top = Spacing.xl)
            )
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

        items(portfolioStocks) { stock ->
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.screenHorizontal)
                    .background(colors.bgCard)
            ) {
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                        .height(0.5.dp)
                        .background(colors.borderSubtle)
                )
            }
        }

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
