package com.example.stocksum.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun MarketMoodBar(
    value: Int,
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val clampedValue = value.coerceIn(0, 100)

    val moodLabel = when {
        clampedValue <= 30 -> "Fear"
        clampedValue <= 49 -> "Slight Fear"
        clampedValue == 50 -> "Neutral"
        clampedValue <= 70 -> "Slight Greed"
        else -> "Greed"
    }

    val moodColor = when {
        clampedValue <= 30 -> colors.loss
        clampedValue <= 49 -> colors.loss.copy(alpha = 0.7f)
        clampedValue == 50 -> colors.neutral
        clampedValue <= 70 -> colors.gain.copy(alpha = 0.7f)
        else -> colors.gain
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(colors.bgCard)
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        ) {
            val barWidth = size.width
            val barHeight = size.height

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(colors.loss, colors.neutral, colors.gain),
                    startX = 0f,
                    endX = barWidth
                ),
                cornerRadius = CornerRadius(barHeight / 2, barHeight / 2),
                size = Size(barWidth, barHeight)
            )

            val dotX = (clampedValue / 100f) * barWidth
            val dotRadius = 6.dp.toPx()

            drawCircle(
                color = colors.bgBase,
                radius = dotRadius + 2.dp.toPx(),
                center = Offset(dotX, barHeight / 2)
            )
            drawCircle(
                color = colors.textPrimary,
                radius = dotRadius,
                center = Offset(dotX, barHeight / 2)
            )
        }

        BasicText(
            text = "$clampedValue · $moodLabel",
            style = typography.label.copy(color = moodColor),
            modifier = Modifier.padding(top = Spacing.sm)
        )
    }
}
