package com.example.tradetrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding", "Welcome")
    object Login : Screen("login", "Login")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Journal : Screen("journal", "Journal", Icons.Default.History)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Achievements : Screen("achievements", "Achievements")
    
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
    Screen.Journal,
    Screen.Analytics,
    Screen.Profile
)
