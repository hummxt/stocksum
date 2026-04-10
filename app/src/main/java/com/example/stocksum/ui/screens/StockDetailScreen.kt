package com.example.stocksum.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stocksum.data.AlertCondition
import com.example.stocksum.ui.MockData
import com.example.stocksum.ui.components.ActionButton
import com.example.stocksum.ui.components.PriceBadge
import com.example.stocksum.ui.components.SectionHeader
import com.example.stocksum.ui.components.SparklineChart
import com.example.stocksum.ui.components.tickerToColor
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
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
    val context = LocalContext.current

    val stocksState by viewModel.homeStocks.collectAsState()
    val loadedStocks = (stocksState as? UiState.Success<List<com.example.stocksum.ui.MockStock>>)?.data ?: emptyList()

    val searchState by viewModel.searchResults.collectAsState()
    val searchStocks = (searchState as? UiState.Success<List<com.example.stocksum.ui.MockStock>>)?.data ?: emptyList()

    val stock = loadedStocks.find { it.ticker == ticker }
        ?: searchStocks.find { it.ticker == ticker }
        ?: MockData.allStocks.find { it.ticker == ticker }
        ?: MockData.allStocks.first()

    val portfolioStocks by viewModel.portfolioStocks.collectAsState()
    val portfolioStock = portfolioStocks.find { it.ticker == ticker }
    val isInPortfolio = viewModel.isInPortfolio(ticker)
    val isInWatchlist by viewModel.watchlist.collectAsState()
    val watchlisted = isInWatchlist.contains(ticker)

    var selectedTimeFilter by remember { mutableIntStateOf(0) }
    val timeFilters = listOf("1D", "1W", "1M", "3M", "1Y")

    val isPositive = stock.changePercent >= 0
    val priceColor = if (isPositive) colors.textGain else colors.textLoss
    val lineColor = if (isPositive) colors.gain else colors.loss

    // Dialog states
    var showAlertDialog by remember { mutableStateOf(false) }
    var showPortfolioDialog by remember { mutableStateOf(false) }

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

    Box(modifier = Modifier.fillMaxSize()) {

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

        // Price
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

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    icon = Icons.Rounded.Notifications,
                    label = "Alert",
                    onClick = { showAlertDialog = true },
                    bgColor = colors.bgCard,
                    iconColor = colors.accent
                )
                ActionButton(
                    icon = if (isInPortfolio) Icons.Rounded.Check else Icons.Rounded.Folder,
                    label = if (isInPortfolio) "Edit" else "Portfolio",
                    onClick = { showPortfolioDialog = true },
                    bgColor = colors.bgCard,
                    iconColor = if (isInPortfolio) colors.gain else colors.textPrimary
                )
                ActionButton(
                    icon = Icons.Rounded.Share,
                    label = "Share",
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT,
                                "Check out ${stock.companyName} (${stock.ticker})! " +
                                "Current price: ${stock.currencySymbol}${"%.2f".format(stock.currentPrice)} " +
                                "(${if (isPositive) "+" else ""}${"%.2f".format(stock.changePercent)}%)"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share ${stock.ticker}"))
                    },
                    bgColor = colors.bgCard,
                    iconColor = colors.textPrimary
                )
                ActionButton(
                    icon = if (watchlisted) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                    label = if (watchlisted) "Saved" else "Watch",
                    onClick = { viewModel.toggleWatchlist(ticker) },
                    bgColor = colors.bgCard,
                    iconColor = if (watchlisted) colors.accent else colors.textSecondary
                )
            }
        }

        // Time Filters
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

        // Chart
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

        // Your Position (if in portfolio)
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

        // Key Stats
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

        // About
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
    
    // Alert Dialog overlay
    if (showAlertDialog) {
        AlertDialogContent(
            ticker = ticker,
            currentPrice = stock.currentPrice,
            currencySymbol = stock.currencySymbol,
            onDismiss = { showAlertDialog = false },
            onConfirm = { condition, targetPrice ->
                viewModel.addAlert(ticker, stock.companyName, condition, targetPrice)
                showAlertDialog = false
            }
        )
    }

    // Portfolio Dialog overlay
    if (showPortfolioDialog) {
        PortfolioDialogContent(
            ticker = ticker,
            companyName = stock.companyName,
            currentPrice = stock.currentPrice,
            currencySymbol = stock.currencySymbol,
            isEditing = isInPortfolio,
            existingShares = portfolioStock?.sharesOwned ?: 0.0,
            existingPrice = portfolioStock?.purchasePrice ?: stock.currentPrice,
            onDismiss = { showPortfolioDialog = false },
            onConfirm = { shares, price ->
                if (isInPortfolio) {
                    viewModel.updatePortfolioEntry(ticker, shares, price)
                } else {
                    viewModel.addToPortfolio(ticker, stock.companyName, shares, price)
                }
                showPortfolioDialog = false
            },
            onRemove = if (isInPortfolio) {
                {
                    viewModel.removeFromPortfolio(ticker)
                    showPortfolioDialog = false
                }
            } else null
        )
    }
    }
}

// --- Alert Dialog ---

@Composable
fun AlertDialogContent(
    ticker: String,
    companyName: String,
    currentPrice: Double,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (AlertCondition, Double) -> Unit
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    var selectedCondition by remember { mutableIntStateOf(0) }
    var targetPriceText by remember { mutableStateOf("%.2f".format(currentPrice)) }
    val conditions = listOf("Above", "Below")

    // Full-screen overlay dialog
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
                .clickable(enabled = false, onClick = {}) // prevent dismiss on card click
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            BasicText(
                text = "Set Alert for $ticker",
                style = typography.title.copy(color = colors.textPrimary)
            )
            BasicText(
                text = "Current: $currencySymbol${"%.2f".format(currentPrice)}",
                style = typography.caption.copy(color = colors.textSecondary)
            )

            // Condition selector
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
                text = "Target Price ($currencySymbol)",
                style = typography.label.copy(color = colors.textSecondary)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(colors.bgInput)
                    .padding(Spacing.md)
            ) {
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
                            if (price != null && price > 0) {
                                val condition = if (selectedCondition == 0) AlertCondition.ABOVE else AlertCondition.BELOW
                                onConfirm(condition, price)
                            }
                        }
                        .padding(vertical = Spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Set Alert",
                        style = typography.label.copy(color = colors.bgBase)
                    )
                }
            }
        }
    }
}

// --- Portfolio Dialog ---

@Composable
fun PortfolioDialogContent(
    ticker: String,
    companyName: String,
    currentPrice: Double,
    currencySymbol: String,
    isEditing: Boolean,
    existingShares: Double,
    existingPrice: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit,
    onRemove: (() -> Unit)?
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    var sharesText by remember { mutableStateOf(if (isEditing) "%.1f".format(existingShares) else "") }
    var priceText by remember { mutableStateOf(if (isEditing) "%.2f".format(existingPrice) else "%.2f".format(currentPrice)) }

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
                text = if (isEditing) "Edit $ticker Position" else "Add $ticker to Portfolio",
                style = typography.title.copy(color = colors.textPrimary)
            )
            BasicText(
                text = "Current price: $currencySymbol${"%.2f".format(currentPrice)}",
                style = typography.caption.copy(color = colors.textSecondary)
            )

            // Shares input
            BasicText(
                text = "Number of Shares",
                style = typography.label.copy(color = colors.textSecondary)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(colors.bgInput)
                    .padding(Spacing.md)
            ) {
                if (sharesText.isEmpty()) {
                    BasicText(
                        text = "e.g. 10",
                        style = typography.body.copy(color = colors.textTertiary)
                    )
                }
                BasicTextField(
                    value = sharesText,
                    onValueChange = { sharesText = it },
                    textStyle = typography.title.copy(color = colors.textPrimary),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Purchase price input
            BasicText(
                text = "Average Purchase Price ($currencySymbol)",
                style = typography.label.copy(color = colors.textSecondary)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(colors.bgInput)
                    .padding(Spacing.md)
            ) {
                BasicTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
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
                        .background(colors.gain)
                        .clickable {
                            val shares = sharesText.toDoubleOrNull()
                            val price = priceText.toDoubleOrNull()
                            if (shares != null && shares > 0 && price != null && price > 0) {
                                onConfirm(shares, price)
                            }
                        }
                        .padding(vertical = Spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = if (isEditing) "Update" else "Add",
                        style = typography.label.copy(color = colors.bgBase)
                    )
                }
            }

            // Remove button (only when editing)
            if (onRemove != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(colors.lossBg)
                        .clickable(onClick = onRemove)
                        .padding(vertical = Spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Remove from Portfolio",
                        style = typography.label.copy(color = colors.loss)
                    )
                }
            }
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
