package com.example.tradetrack.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tradetrack.ui.screens.*
import com.example.tradetrack.viewmodel.*

@Composable
fun TradeTrackNavGraph(
    navController: NavHostController,
    listViewModel: TradeListViewModel,
    authViewModel: AuthViewModel,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val currentUser by authViewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) Screen.Login.route else Screen.Home.route
    ) {
        // --- LOGIN ---
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- HOME ---
        composable(Screen.Home.route) {
            val trades by listViewModel.trades.collectAsState()
            HomeScreen(
                trades = trades,
                onAddTrade = { navController.navigate(Screen.AddTrade.route) },
                onTradeClick = { trade ->
                    navController.navigate(Screen.TradeDetail.createRoute(trade.id))
                },
                onDeleteTrade = { listViewModel.deleteTrade(it) },
                onProfileClick = { navController.navigate(Screen.Settings.route) }
            )
        }

        // --- ANALYTICS ---
        composable(Screen.Analytics.route) {
            AnalyticsScreen(viewModel = listViewModel)
        }

        // --- HISTORY ---
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = listViewModel,
                onTradeClick = { trade ->
                    navController.navigate(Screen.TradeDetail.createRoute(trade.id))
                },
                onDeleteTrade = { listViewModel.deleteTrade(it) }
            )
        }

        // --- SETTINGS ---
        composable(Screen.Settings.route) {
            val trades by listViewModel.trades.collectAsState()
            SettingsScreen(
                trades = trades,
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange,
                onLogout = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- ADD TRADE ---
        composable(Screen.AddTrade.route) { backStackEntry ->
            val vm: AddEditTradeViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = addEditTradeViewModelFactory(application, backStackEntry)
            )
            AddEditTradeScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onSaveComplete = { navController.popBackStack() }
            )
        }

        // --- EDIT TRADE ---
        composable(
            route = Screen.EditTrade.route,
            arguments = listOf(navArgument("tradeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vm: AddEditTradeViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = addEditTradeViewModelFactory(application, backStackEntry)
            )
            AddEditTradeScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onSaveComplete = { navController.popBackStack() }
            )
        }

        // --- TRADE DETAIL ---
        composable(
            route = Screen.TradeDetail.route,
            arguments = listOf(navArgument("tradeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tradeId = backStackEntry.arguments?.getString("tradeId") ?: return@composable
            val vm: TradeDetailViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = tradeDetailViewModelFactory(application, tradeId)
            )
            TradeDetailScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onEdit = { trade ->
                    navController.navigate(Screen.EditTrade.createRoute(trade.id))
                },
                onDeleted = { navController.popBackStack() }
            )
        }
    }
}
