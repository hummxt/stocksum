package com.example.stocksum.ui.navigation

import android.app.Application
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stocksum.ui.screens.AlertsScreen
import com.example.stocksum.ui.screens.DiscoverScreen
import com.example.stocksum.ui.screens.HomeScreen
import com.example.stocksum.ui.screens.PortfolioScreen
import com.example.stocksum.ui.screens.ProfileScreen
import com.example.stocksum.ui.screens.StockDetailScreen
import com.example.stocksum.ui.viewmodels.HomeViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Portfolio : Screen("portfolio")
    data object Discover : Screen("discover")
    data object Alerts : Screen("alerts")
    data object Profile : Screen("profile")
    data object StockDetail : Screen("stock_detail/{ticker}") {
        fun createRoute(ticker: String) = "stock_detail/$ticker"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    currentTheme: com.example.stocksum.ui.theme.ThemeMode,
    onThemeChange: (com.example.stocksum.ui.theme.ThemeMode) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val homeViewModel: HomeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(150)) },
        exitTransition = { fadeOut(animationSpec = tween(150)) }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onStockClick = { ticker ->
                    navController.navigate(Screen.StockDetail.createRoute(ticker))
                },
                onSeeAllHoldings = {
                    navController.navigate(Screen.Portfolio.route)
                },
                onSeeAllMovers = {
                    navController.navigate(Screen.Discover.route)
                }
            )
        }

        composable(Screen.Portfolio.route) {
            PortfolioScreen(
                viewModel = homeViewModel,
                onStockClick = { ticker ->
                    navController.navigate(Screen.StockDetail.createRoute(ticker))
                }
            )
        }

        composable(Screen.Discover.route) {
            DiscoverScreen(
                viewModel = homeViewModel,
                onStockClick = { ticker ->
                    navController.navigate(Screen.StockDetail.createRoute(ticker))
                }
            )
        }

        composable(Screen.Alerts.route) {
            AlertsScreen(
                viewModel = homeViewModel,
                onStockClick = { ticker ->
                    navController.navigate(Screen.StockDetail.createRoute(ticker))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = homeViewModel,
                currentTheme = currentTheme,
                onThemeChange = onThemeChange
            )
        }

        composable(
            route = Screen.StockDetail.route,
            arguments = listOf(navArgument("ticker") { type = NavType.StringType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(200)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(200)
                )
            }
        ) { backStackEntry ->
            val ticker = backStackEntry.arguments?.getString("ticker") ?: "AAPL"
            StockDetailScreen(
                ticker = ticker,
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
