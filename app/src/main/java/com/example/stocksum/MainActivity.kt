package com.example.stocksum

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stocksum.data.NotificationHelper
import com.example.stocksum.ui.components.BottomNavBar
import com.example.stocksum.ui.components.NavTab
import com.example.stocksum.ui.components.StocksumFAB
import com.example.stocksum.ui.navigation.AppNavigation
import com.example.stocksum.ui.navigation.Screen
import com.example.stocksum.ui.theme.Spacing
import com.example.stocksum.ui.theme.StocksumTheme
import com.example.stocksum.ui.theme.ThemeMode
import com.example.stocksum.ui.viewmodels.HomeViewModel
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> /* Permission result handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val imageLoader = ImageLoader.Builder(this)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        Coil.setImageLoader(imageLoader)
        
        // Create notification channels
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannels()
        
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        enableEdgeToEdge()
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            
            StocksumTheme(themeMode = themeMode) {
                StocksumApp(
                    currentTheme = themeMode,
                    onThemeChange = { themeMode = it }
                )
            }
        }
    }
}

@Composable
fun StocksumApp(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    val colors = StocksumTheme.colors
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(NavTab.HOME) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.StockDetail.route

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgBase)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                AppNavigation(
                    navController = navController,
                    currentTheme = currentTheme,
                    onThemeChange = onThemeChange
                )
            }

            if (showBottomBar) {
                BottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        val route = when (tab) {
                            NavTab.HOME -> Screen.Home.route
                            NavTab.PORTFOLIO -> Screen.Portfolio.route
                            NavTab.DISCOVER -> Screen.Discover.route
                            NavTab.ALERTS -> Screen.Alerts.route
                            NavTab.PROFILE -> Screen.Profile.route
                        }
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    alertBadgeCount = 0,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        }

        if (showBottomBar && (selectedTab == NavTab.HOME || selectedTab == NavTab.PORTFOLIO)) {
            StocksumFAB(
                onClick = {
                    selectedTab = NavTab.DISCOVER
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = Spacing.lg,
                        bottom = Spacing.xxl + Spacing.xxl + Spacing.lg
                    )
            )
        }

    }
}