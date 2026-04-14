package com.example.tradetrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Login : Screen("login", "Login")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object History : Screen("history", "History", Icons.Default.History)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    
    object AddTrade : Screen("add_trade", "Add Trade")
    object EditTrade : Screen("edit_trade/{tradeId}", "Edit Trade") {
        fun createRoute(tradeId: String) = "edit_trade/$tradeId"
    }
    object TradeDetail : Screen("trade_detail/{tradeId}", "Trade Detail") {
        fun createRoute(tradeId: String) = "trade_detail/$tradeId"
    }
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Analytics,
    Screen.History,
    Screen.Settings
)
