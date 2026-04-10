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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.stocksum.data.MarketReminderWorker
import com.example.stocksum.ui.components.SectionHeader
import com.example.stocksum.ui.theme.Radius
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import com.example.stocksum.ui.theme.ThemeMode
import com.example.stocksum.ui.viewmodels.HomeViewModel
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(
    viewModel: HomeViewModel,
    currentTheme: ThemeMode = ThemeMode.SYSTEM,
    onThemeChange: (ThemeMode) -> Unit = {}
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography
    val context = LocalContext.current

    var selectedCurrency by remember { mutableIntStateOf(0) }
    var selectedRefresh by remember { mutableIntStateOf(1) }

    // Load saved notification states from AlertManager
    var alertsEnabled by remember { mutableStateOf(viewModel.alertManager.areAlertsEnabled()) }
    var marketReminders by remember { mutableStateOf(viewModel.alertManager.areMarketRemindersEnabled()) }

    val currencies = listOf("USD ($)", "EUR (€)", "GBP (£)")
    val refreshIntervals = listOf("1 min", "5 min", "15 min")
    val themeOptions = listOf("System", "Light", "Dark")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase),
        contentPadding = PaddingValues(top = Spacing.xxl, bottom = Spacing.xxl)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenHorizontal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(colors.accent),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "HA",
                        style = typography.headline.copy(
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                BasicText(
                    text = "Hummet Azim",
                    style = typography.title.copy(color = colors.textPrimary),
                    modifier = Modifier.padding(top = Spacing.md)
                )
                BasicText(
                    text = "hummet@example.com",
                    style = typography.caption.copy(color = colors.textSecondary),
                    modifier = Modifier.padding(top = Spacing.xs)
                )
            }
        }

        item {
            SectionHeader(
                title = "Preferences",
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            SettingsCard {
                SettingsOptionRow(
                    label = "App Theme",
                    options = themeOptions,
                    selectedIndex = currentTheme.ordinal,
                    onSelect = { onThemeChange(ThemeMode.values()[it]) }
                )
                SettingsDivider()
                SettingsOptionRow(
                    label = "Default Currency",
                    options = currencies,
                    selectedIndex = selectedCurrency,
                    onSelect = { selectedCurrency = it }
                )
            }
        }

        item {
            SectionHeader(
                title = "Notifications",
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            SettingsCard {
                SettingsToggleRow(
                    label = "Alert Notifications",
                    isEnabled = alertsEnabled,
                    onToggle = { enabled ->
                        alertsEnabled = enabled
                        viewModel.alertManager.setAlertsEnabled(enabled)
                    }
                )
                SettingsDivider()
                SettingsToggleRow(
                    label = "Market Reminders",
                    isEnabled = marketReminders,
                    onToggle = { enabled ->
                        marketReminders = enabled
                        viewModel.alertManager.setMarketRemindersEnabled(enabled)

                        if (enabled) {
                            // Schedule periodic market reminder worker
                            val workRequest = PeriodicWorkRequestBuilder<MarketReminderWorker>(
                                15, TimeUnit.MINUTES
                            ).build()
                            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                                "market_reminder",
                                ExistingPeriodicWorkPolicy.KEEP,
                                workRequest
                            )
                        } else {
                            // Cancel the worker
                            WorkManager.getInstance(context).cancelUniqueWork("market_reminder")
                        }
                    }
                )
            }
        }

        item {
            SectionHeader(
                title = "Data",
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            SettingsCard {
                SettingsOptionRow(
                    label = "Refresh Interval",
                    options = refreshIntervals,
                    selectedIndex = selectedRefresh,
                    onSelect = { selectedRefresh = it }
                )
            }
        }



        item {
            SectionHeader(
                title = "About",
                modifier = Modifier.padding(horizontal = Spacing.screenHorizontal)
            )
        }

        item {
            SettingsCard {
                SettingsInfoRow("Version", "1.0.0")
                SettingsDivider()
                SettingsInfoRow("Privacy Policy", "→")
                SettingsDivider()
                SettingsInfoRow("Send Feedback", "→")
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = Spacing.screenHorizontal)
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(StocksumTheme.colors.bgCard)
            .padding(Spacing.lg)
    ) {
        content()
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md)
            .height(0.5.dp)
            .background(StocksumTheme.colors.borderSubtle)
    )
}

@Composable
fun SettingsOptionRow(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Column {
        BasicText(
            text = label,
            style = typography.body.copy(color = colors.textPrimary)
        )
        Row(
            modifier = Modifier.padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(if (isSelected) colors.accentBg else colors.bgElevated)
                        .clickable { onSelect(index) }
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = option,
                        style = typography.label.copy(
                            color = if (isSelected) colors.accent else colors.textSecondary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsToggleRow(
    label: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isEnabled) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = label,
            style = typography.body.copy(color = colors.textPrimary)
        )
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 24.dp)
                .clip(RoundedCornerShape(Radius.full))
                .background(if (isEnabled) colors.gain else colors.bgElevated),
            contentAlignment = if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(colors.textPrimary)
            )
        }
    }
}

@Composable
fun SettingsInfoRow(label: String, value: String) {
    val colors = StocksumTheme.colors
    val typography = StocksumTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicText(
            text = label,
            style = typography.body.copy(color = colors.textPrimary)
        )
        BasicText(
            text = value,
            style = typography.body.copy(color = colors.textSecondary)
        )
    }
}
