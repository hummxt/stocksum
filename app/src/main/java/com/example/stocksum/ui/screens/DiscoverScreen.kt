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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import com.example.stocksum.ui.components.SectionHeader
import com.example.stocksum.ui.components.StockRow
import com.example.stocksum.ui.components.StocksumTextField
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import com.example.stocksum.ui.utils.InfiniteScrollHandler
import com.example.stocksum.ui.viewmodels.HomeViewModel
import com.example.stocksum.ui.viewmodels.UiState

@Composable
fun DiscoverScreen(
    viewModel: HomeViewModel,
    onStockClick: (String) -> Unit = {}
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    var searchQuery by remember { mutableStateOf("") }
    var selectedSection by remember { mutableIntStateOf(0) }

    val searchState by viewModel.searchResults.collectAsState()
    val homeStocksState by viewModel.homeStocks.collectAsState()

    val sections = listOf("Market View", "Gainers", "Losers", "Active")
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(top = Spacing.xxl, bottom = Spacing.xxl)
    ) {
        item {
            BasicText(
                text = "Discover",
                style = typography.headline.copy(color = colors.textPrimary),
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            StocksumTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.search(it)
                },
                placeholder = "Search stocks (e.g. AAPL)...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.lg)
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(
                    horizontal = Spacing.screenHorizontal,
                    vertical = Spacing.sm
                ),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(sections.size) { index ->
                    val isSelected = index == selectedSection
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.full))
                            .background(if (isSelected) colors.gain else colors.bgCard)
                            .clickable { selectedSection = index }
                            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = sections[index],
                            style = typography.label.copy(
                                color = if (isSelected) colors.bgBase else colors.textSecondary
                            )
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = if (searchQuery.length >= 2) "Search Results" else sections.getOrElse(selectedSection) { "Stocks" },
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        if (searchQuery.length >= 2) {
            when (val state = searchState) {
                is UiState.Loading -> {
                    item {
                        BasicText(
                            text = "Searching...",
                            style = typography.body.copy(color = colors.textSecondary),
                            modifier = Modifier.padding(Spacing.screenHorizontal)
                        )
                    }
                }
                is UiState.Error -> {
                    item {
                        BasicText(
                            text = state.message,
                            style = typography.body.copy(color = colors.loss),
                            modifier = Modifier.padding(Spacing.screenHorizontal)
                        )
                    }
                }
                is UiState.Success<*> -> {
                    val data = (state as? UiState.Success<List<com.example.stocksum.ui.MockStock>>)?.data ?: emptyList()
                    if (data.isEmpty()) {
                        item {
                            BasicText(
                                text = "No results found for \"$searchQuery\"",
                                style = typography.body.copy(color = colors.textSecondary),
                                modifier = Modifier.padding(Spacing.screenHorizontal)
                            )
                        }
                    } else {
                        items(data) { stock ->
                            StockRow(
                                ticker = stock.ticker,
                                companyName = stock.companyName,
                                exchange = stock.exchange,
                                currentPrice = stock.currentPrice,
                                changePercent = stock.changePercent,
                                currencySymbol = stock.currencySymbol,
                                logoUrl = stock.logoUrl,
                                onClick = { onStockClick(stock.ticker) }
                            )
                        }
                    }
                }
            }
        } else {
            val defaultState = homeStocksState
            val defaultData = (defaultState as? UiState.Success<List<com.example.stocksum.ui.MockStock>>)?.data ?: emptyList()
            
            if (defaultData.isEmpty()) {
                item {
                    BasicText(
                        text = "Loading market data...",
                        style = typography.body.copy(color = colors.textSecondary),
                        modifier = Modifier.padding(Spacing.screenHorizontal)
                    )
                }
            } else {
                val sortedStocks = when (selectedSection) {
                    1 -> defaultData.sortedByDescending { it.changePercent }
                    2 -> defaultData.sortedBy { it.changePercent }
                    else -> defaultData
                }
                
                items(sortedStocks) { stock ->
                    StockRow(
                        ticker = stock.ticker,
                        companyName = stock.companyName,
                        exchange = stock.exchange,
                        currentPrice = stock.currentPrice,
                        changePercent = stock.changePercent,
                        currencySymbol = stock.currencySymbol,
                        logoUrl = stock.logoUrl,
                        onClick = { onStockClick(stock.ticker) }
                    )
                }
            }
        }
    }

    // Infinite scroll handler for search results and market view
    val displayedStocks = when {
        searchQuery.isNotEmpty() -> (searchState as? UiState.Success)?.data ?: emptyList()
        else -> (homeStocksState as? UiState.Success)?.data ?: emptyList()
    }
    InfiniteScrollHandler(
        lazyListState = lazyListState,
        itemCount = displayedStocks.size,
        onLoadMore = { viewModel.loadMoreStocks() }
    )
}
