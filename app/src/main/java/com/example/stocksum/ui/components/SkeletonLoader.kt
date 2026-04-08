package com.example.stocksum.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun ShimmerBox(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            colors.bgElevated,
            colors.bgElevated.copy(alpha = 0.5f),
            colors.bgElevated
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 400f, 0f)
    )

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(Radius.sm))
            .background(shimmerBrush)
    )
}

@Composable
fun SkeletonStockRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(width = 36.dp, height = 36.dp, modifier = Modifier.clip(CircleShape))
        Column(modifier = Modifier.weight(1f).padding(start = Spacing.md)) {
            ShimmerBox(width = 60.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(width = 100.dp, height = 10.dp)
        }
        Column(horizontalAlignment = Alignment.End) {
            ShimmerBox(width = 70.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            ShimmerBox(width = 50.dp, height = 10.dp)
        }
    }
}

@Composable
fun SkeletonHeroCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.xl))
            .background(StocksumTheme.colors.heroCardBg)
            .padding(Spacing.xl)
    ) {
        ShimmerBox(width = 100.dp, height = 12.dp)
        Spacer(modifier = Modifier.height(Spacing.sm))
        ShimmerBox(width = 180.dp, height = 28.dp)
        Spacer(modifier = Modifier.height(Spacing.sm))
        ShimmerBox(width = 120.dp, height = 14.dp)
        Spacer(modifier = Modifier.height(Spacing.lg))
        ShimmerBox(width = 300.dp, height = 48.dp)
    }
}
