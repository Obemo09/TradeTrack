package com.example.tradetrack.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val category: String = "Milestone" // Milestone, Risk Management, Consistency, Performance
)

object AchievementRegistry {
    val ALL_ACHIEVEMENTS = listOf(
        // Milestone Achievements
        Achievement(
            id = "First Trade Logged",
            title = "First Trade",
            description = "You've taken the first step in your journey!",
            icon = Icons.Default.Inventory2,
            category = "Milestone"
        ),
        Achievement(
            id = "5-Day Streak",
            title = "5-Day Streak",
            description = "5 consecutive days of disciplined logging.",
            icon = Icons.Default.LocalFireDepartment,
            category = "Consistency"
        ),
        Achievement(
            id = "Centurion",
            title = "Centurion",
            description = "Log a total of 100 trades.",
            icon = Icons.Default.DoneAll,
            category = "Milestone"
        ),

        // Risk Management Achievements
        Achievement(
            id = "Disciplined Trader",
            title = "Disciplined",
            description = "Logged a trade with both Stop Loss and Take Profit set.",
            icon = Icons.Default.Shield,
            category = "Risk Management"
        ),
        Achievement(
            id = "Risk Manager",
            title = "Risk Manager",
            description = "Set Risk/Reward ratio above 1.5 on 10 trades.",
            icon = Icons.Default.Security,
            category = "Risk Management"
        ),

        // Performance Achievements
        Achievement(
            id = "Profit Pioneer",
            title = "First Win",
            description = "Closed your first winning trade. The first of many!",
            icon = Icons.Default.TrendingUp,
            category = "Performance"
        ),
        Achievement(
            id = "Winning Streak",
            title = "Winning Streak",
            description = "Won 3 consecutive trades.",
            icon = Icons.Default.EmojiEvents,
            category = "Performance"
        ),
        Achievement(
            id = "Market Expert",
            title = "Market Expert",
            description = "Reach the Expert level (4000 XP).",
            icon = Icons.Default.AutoAwesome,
            category = "Milestone"
        ),

        // Consistency Achievements
        Achievement(
            id = "Daily Grind",
            title = "Daily Grind",
            description = "Trade for 7 consecutive days.",
            icon = Icons.Default.VerifiedUser,
            category = "Consistency"
        ),
        Achievement(
            id = "Monthly Master",
            title = "Monthly Master",
            description = "Log at least one trade every day for a month.",
            icon = Icons.Default.DateRange,
            category = "Consistency"
        )
    )
}


