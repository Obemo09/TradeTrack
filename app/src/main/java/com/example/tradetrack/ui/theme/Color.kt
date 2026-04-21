package com.example.tradetrack.ui.theme

import androidx.compose.ui.graphics.Color

// --- Premium Modern Palette ---

// Backgrounds
val BgDark = Color(0xFF080A0C)
val BgLight = Color(0xFFF8FAFC)

// Surfaces
val SurfaceDark = Color(0xFF121418)
val SurfaceLight = Color(0xFFFFFFFF)

// Accents
val PrimaryBlue = Color(0xFF3B82F6)
val PrimaryCyan = Color(0xFF06B6D4)
val PrimaryIndigo = Color(0xFF6366F1)

// Success & Loss (Vibrant but balanced)
val ProfitGreen = Color(0xFF10B981)
val LossRed = Color(0xFFEF4444)
val WarningOrange = Color(0xFFF59E0B)

// Gradients
val PremiumBlueGradient = listOf(PrimaryBlue, PrimaryCyan)
val PremiumGreenGradient = listOf(ProfitGreen, Color(0xFF34D399))
val PremiumRedGradient = listOf(LossRed, Color(0xFFF87171))
val PremiumDarkGradient = listOf(BgDark, Color(0xFF111827))

// Text
val TextPrimaryDark = Color(0xFFF1F5F9)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)

// Specific Trading Colors from previous turns (aliased for compatibility)
val TradingBlue = PrimaryBlue
val TradingCyan = PrimaryCyan // Added for compatibility
val TradingGreen = ProfitGreen
val TradingRed = LossRed
val TradingOrange = WarningOrange
val TradingBlack = BgDark
val TradingDarkGrey = SurfaceDark
val TradingLightGrey = Color(0xFF1E293B)
val TradingTextPrimary = TextPrimaryDark
val TradingTextSecondary = TextSecondaryDark
val TradingGreenLight = Color(0xFF34D399)
val BlueGradient = PremiumBlueGradient
val TradingPurple = PrimaryIndigo
