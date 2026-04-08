package com.example.stocksum.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.lg, bottom = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = title.uppercase(),
            style = typography.caption.copy(
                color = colors.textSecondary,
                letterSpacing = 0.08.sp
            )
        )
        if (actionText != null) {
            BasicText(
                text = actionText,
                style = typography.caption.copy(
                    color = colors.accent,
                    textAlign = TextAlign.End
                ),
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}
