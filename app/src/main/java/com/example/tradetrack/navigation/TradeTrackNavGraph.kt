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
import com.example.tradetrack.ui.components.ShowcaseState
import com.example.tradetrack.ui.screens.*
import com.example.tradetrack.viewmodel.*

@Composable
fun TradeTrackNavGraph(
    navController: NavHostController,
    listViewModel: TradeListViewModel,
    authViewModel: AuthViewModel,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    showcaseState: ShowcaseState
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val startDestination = when {
        !isOnboardingFinished(context) -> Screen.Onboarding.route
        currentUser == null -> Screen.Login.route
        else -> Screen.Home.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- ONBOARDING ---
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    // When onboarding finishes, we want to ensure the user lands on the Login screen
                    // even if a previous session was restored by Android Backup.
                    if (authViewModel.currentUser.value != null) {
                        authViewModel.logout {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }

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
            val userStats by listViewModel.userStats.collectAsState()
            HomeScreen(
                trades = trades,
                userStats = userStats,
                onAddTrade = { navController.navigate(Screen.AddTrade.route) },
                onTradeClick = { trade ->
                    navController.navigate(Screen.TradeDetail.createRoute(trade.id))
                },
                onDeleteTrade = { listViewModel.deleteTrade(it) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onWinRateClick = { navController.navigate(Screen.Analytics.route) },
                onTotalTradesClick = { navController.navigate(Screen.Journal.route) }
            )
        }

        // --- ANALYTICS ---
        composable(Screen.Analytics.route) {
            AnalyticsScreen(viewModel = listViewModel)
        }

        // --- JOURNAL (previously History) ---
        composable(Screen.Journal.route) {
            HistoryScreen(
                viewModel = listViewModel,
                onTradeClick = { trade ->
                    navController.navigate(Screen.TradeDetail.createRoute(trade.id))
                },
                onDeleteTrade = { listViewModel.deleteTrade(it) }
            )
        }

        // --- PROFILE (previously Settings) ---
        composable(Screen.Profile.route) {
            val trades by listViewModel.trades.collectAsState()
            val userStats by listViewModel.userStats.collectAsState()
            SettingsScreen(
                trades = trades,
                userStats = userStats,
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange,
                onLogout = {
                    authViewModel.logout {
                        showcaseState.finish() // Reset tour state on logout
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onAchievementsClick = { navController.navigate(Screen.Achievements.route) }
            )
        }

        // --- ACHIEVEMENTS ---
        composable(Screen.Achievements.route) {
            val userStats by listViewModel.userStats.collectAsState()
            AchievementsScreen(
                userStats = userStats,
                onBack = { navController.popBackStack() }
            )
        }

        // --- ADD TRADE ---
        composable(Screen.AddTrade.route) { backStackEntry ->
            val vm: AddEditTradeViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = addEditTradeViewModelFactory(application, null)
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
            val tradeId = backStackEntry.arguments?.getString("tradeId")
            val vm: AddEditTradeViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = addEditTradeViewModelFactory(application, tradeId)
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
                    // Correctly navigate to edit route with trade ID
                    navController.navigate(Screen.EditTrade.createRoute(trade.id))
                },
                onDeleted = { navController.popBackStack() }
            )
        }
    }
}
