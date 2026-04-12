package com.example.stocksum.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import com.example.stocksum.ui.MockData
import com.example.stocksum.ui.components.PortfolioHeroCard
import com.example.stocksum.ui.components.StockRow
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import kotlinx.coroutines.delay

@Composable
fun DemoPortfolioView(
    onDemoComplete: () -> Unit
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography
    var countdownSeconds by remember { mutableIntStateOf(30) }

    LaunchedEffect(Unit) {
        for (i in 30 downTo 1) {
            delay(1000)
            countdownSeconds = i - 1
        }
        onDemoComplete()
    }

    val progressPercentage = countdownSeconds / 30f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(top = Spacing.xxl, bottom = Spacing.xxl)
    ) {
        // Header with countdown
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        text = "Demo Portfolio",
                        style = typography.headline.copy(color = colors.textPrimary)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.md))
                            .background(colors.bgCard)
                            .padding(Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = "⏱️ ${countdownSeconds}s",
                            style = typography.label.copy(color = colors.accent)
                        )
                    }
                }

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(colors.borderSubtle)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressPercentage)
                            .height(2.dp)
                            .background(colors.accent)
                    )
                }

                BasicText(
                    text = "This is a preview of how your portfolio will look. Explore and get started!",
                    style = typography.caption.copy(color = colors.textSecondary)
                )
            }
        }

        // Demo hero card
        item {
            PortfolioHeroCard(
                totalValue = MockData.totalPortfolioValue,
                todayChange = MockData.todayChange,
                todayPercent = MockData.todayPercent,
                sparklineData = MockData.sparklineData ?: emptyList(),
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        // Demo stocks
        item {
            BasicText(
                text = "Your Holdings",
                style = typography.title.copy(color = colors.textPrimary),
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        items(MockData.portfolioStocks) { stock ->
            StockRow(
                ticker = stock.ticker,
                companyName = stock.companyName,
                exchange = "NASDAQ",
                currentPrice = stock.currentPrice,
                changePercent = stock.changePercent,
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        // Footer info
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal)
                    .clip(RoundedCornerShape(Radius.md))
                    .background(colors.accentBg)
                    .padding(Spacing.lg)
            ) {
                BasicText(
                    text = "💡 Tip: Go to Stock Detail screen to add real stocks to your portfolio!",
                    style = typography.body.copy(color = colors.accent)
                )
            }
        }
    }
}
