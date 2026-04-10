package com.example.stocksum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

enum class NavTab(val label: String, val icon: String) {
    HOME("Home", "⌂"),
    PORTFOLIO("Portfolio", "▦"),
    DISCOVER("Discover", "⌕"),
    ALERTS("Alerts", "♪"),
    PROFILE("Profile", "⊙")
}

@Composable
fun BottomNavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    alertBadgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgElevated)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(colors.border)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val targetColor = if (isSelected) colors.gain else colors.textSecondary
                val iconColor by animateColorAsState(targetColor)
                val labelColor by animateColorAsState(targetColor)
                val indicatorWidth by animateDpAsState(if (isSelected) 20.dp else 0.dp)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = Spacing.xs)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        BasicText(
                            text = tab.icon,
                            style = typography.headline.copy(
                                color = iconColor,
                                textAlign = TextAlign.Center
                            )
                        )
                        if (tab == NavTab.ALERTS && alertBadgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(colors.loss),
                                contentAlignment = Alignment.Center
                            ) {
                                BasicText(
                                    text = if (alertBadgeCount > 9) "9+" else "$alertBadgeCount",
                                    style = typography.caption.copy(
                                        color = colors.textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .width(indicatorWidth)
                                .height(3.dp)
                                .clip(RoundedCornerShape(50))
                                .background(colors.gain)
                        )
                    }

                    BasicText(
                        text = tab.label,
                        style = typography.caption.copy(
                            color = labelColor,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
