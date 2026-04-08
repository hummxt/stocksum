package com.example.stocksum.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme

@Composable
fun StocksumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    trailingLabel: String? = null,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        error != null -> colors.loss
        isFocused -> colors.accent
        else -> colors.border
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(Radius.sm))
                .background(colors.bgInput)
                .border(1.dp, borderColor, RoundedCornerShape(Radius.sm))
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        BasicText(
                            text = placeholder,
                            style = typography.body.copy(color = colors.textTertiary)
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = typography.body.copy(color = colors.textPrimary),
                        singleLine = true,
                        cursorBrush = SolidColor(colors.accent),
                        interactionSource = interactionSource,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (trailingLabel != null) {
                    BasicText(
                        text = trailingLabel,
                        style = typography.body.copy(color = colors.textSecondary),
                        modifier = Modifier.padding(start = Spacing.sm)
                    )
                }
            }
        }

        if (error != null) {
            BasicText(
                text = error,
                style = typography.caption.copy(color = colors.loss),
                modifier = Modifier.padding(top = Spacing.xs, start = Spacing.xs)
            )
        }
    }
}
