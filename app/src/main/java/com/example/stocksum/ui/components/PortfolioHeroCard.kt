package com.example.stocksum.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun PortfolioHeroCard(
    totalValue: String,
    todayChange: String,
    todayPercent: Double,
    sparklineData: List<Float>,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val isPositive = todayPercent >= 0
    val lineColor = if (isPositive) colors.gain else colors.loss
    val changeColor = if (isPositive) colors.textGain else colors.textLoss

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.xl))
            .background(colors.heroCardBg)
            .padding(Spacing.xl)
    ) {
        BasicText(
            text = "Portfolio value",
            style = typography.caption.copy(color = colors.textSecondary)
        )

        BasicText(
            text = "$currencySymbol$totalValue",
            style = typography.display.copy(color = colors.textPrimary),
            modifier = Modifier.padding(top = Spacing.xs)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.padding(top = Spacing.xs)
        ) {
            BasicText(
                text = todayChange,
                style = typography.body.copy(color = changeColor)
            )
            PriceBadge(changePercent = todayPercent)
        }

        if (sparklineData.isNotEmpty()) {
            SparklineChart(
                data = sparklineData,
                lineColor = lineColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(top = Spacing.lg)
            )
        }
    }
}

@Composable
fun SparklineChart(
    data: List<Float>,
    lineColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    if (data.size < 2) return

    val fadedColor = lineColor.copy(alpha = 0.2f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val minVal = data.min()
        val maxVal = data.max()
        val range = (maxVal - minVal).coerceAtLeast(0.01f)

        val stepX = width / (data.size - 1)

        val linePath = Path()
        val fillPath = Path()

        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - minVal) / range) * height

            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(width, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fadedColor, fadedColor.copy(alpha = 0f)),
                startY = 0f,
                endY = height
            ),
            style = Fill
        )

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
