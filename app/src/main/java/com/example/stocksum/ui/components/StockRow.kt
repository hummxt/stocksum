package com.example.stocksum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import kotlin.math.absoluteValue

@Composable
fun StockRow(
    ticker: String,
    companyName: String,
    exchange: String,
    currentPrice: Double,
    changePercent: Double,
    pnlValue: Double? = null,
    sharesOwned: Double? = null,
    currencySymbol: String = "$",
    logoUrl: String? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val logoColor = tickerToColor(ticker)
    val initials = ticker.take(2)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .then(
                    if (logoUrl == null || logoUrl.isEmpty()) {
                        Modifier.background(logoColor)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (logoUrl != null && logoUrl.isNotEmpty()) {
                coil.compose.AsyncImage(
                    model = logoUrl,
                    contentDescription = "$companyName logo",
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            } else {
                BasicText(
                    text = initials,
                    style = typography.label.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.md),
            verticalArrangement = Arrangement.Center
        ) {
            BasicText(
                text = ticker,
                style = typography.title.copy(color = colors.textPrimary)
            )
            val subtitle = if (sharesOwned != null) {
                "$exchange · %.1f shares".format(sharesOwned)
            } else {
                companyName
            }
            BasicText(
                text = subtitle,
                style = typography.caption.copy(color = colors.textSecondary)
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            BasicText(
                text = "$currencySymbol%.2f".format(currentPrice),
                style = typography.title.copy(color = colors.textPrimary)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                if (pnlValue != null) {
                    val pnlColor = if (pnlValue >= 0) colors.textGain else colors.textLoss
                    val pnlSign = if (pnlValue >= 0) "+" else ""
                    BasicText(
                        text = "$pnlSign$currencySymbol%.2f".format(pnlValue.absoluteValue),
                        style = typography.label.copy(color = pnlColor)
                    )
                }
                PriceBadge(changePercent = changePercent)
            }
        }
    }
}

fun tickerToColor(ticker: String): Color {
    val hash = ticker.hashCode().absoluteValue
    val hue = (hash % 360).toFloat()
    return Color.hsl(hue, 0.6f, 0.4f)
}
