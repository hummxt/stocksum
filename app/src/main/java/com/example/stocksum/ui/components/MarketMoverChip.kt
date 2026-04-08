package com.example.stocksum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun MarketMoverChip(
    ticker: String,
    price: String,
    changePercent: Double,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Column(
        modifier = modifier
            .size(width = 100.dp, height = 76.dp)
            .clip(RoundedCornerShape(Radius.md))
            .background(colors.bgCard)
            .clickable(onClick = onClick)
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            text = ticker,
            style = typography.label.copy(color = colors.textPrimary)
        )
        BasicText(
            text = price,
            style = typography.body.copy(color = colors.textPrimary)
        )
        PriceBadge(changePercent = changePercent)
    }
}
