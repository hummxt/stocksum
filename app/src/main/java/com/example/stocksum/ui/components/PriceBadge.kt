package com.example.stocksum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun PriceBadge(
    changePercent: Double,
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val isPositive = changePercent > 0
    val isNeutral = changePercent == 0.0
    val sign = if (isPositive) "+" else ""
    val displayText = "$sign%.2f%%".format(changePercent)

    val textColor = when {
        isNeutral -> colors.textSecondary
        isPositive -> colors.gain
        else -> colors.loss
    }
    val bgColor = when {
        isNeutral -> colors.bgElevated
        isPositive -> colors.gainBg
        else -> colors.lossBg
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(bgColor)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = displayText,
            style = typography.label.copy(color = textColor)
        )
    }
}
