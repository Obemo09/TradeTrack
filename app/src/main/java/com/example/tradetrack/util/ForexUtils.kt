package com.example.tradetrack.util

import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType

/**
 * Utility for standard Forex calculations.
 * Assumes account currency is USD.
 */
object ForexUtils {

    private const val STANDARD_LOT_UNIT = 100_000.0 // 1 Standard Lot = 100,000 units
    private const val GOLD_LOT_UNIT = 100.0         // 1 Lot Gold = 100 ounces
    private const val SILVER_LOT_UNIT = 5000.0      // 1 Lot Silver = 5000 ounces

    /**
     * Calculates the Profit/Loss in USD based on Forex industry standards.
     */
    fun calculateProfit(trade: Trade): Double {
        if (trade.result == TradeResult.OPEN || trade.exitPrice == null) return 0.0
        
        val entry = trade.entryPrice
        val exit = trade.exitPrice
        val lots = trade.lotSize ?: 0.01 // Fallback to micro lot if not specified
        val pair = trade.pair.uppercase().replace("/", "").replace("-", "")

        if (entry <= 0.0 || exit <= 0.0) return 0.0

        val priceDiff = if (trade.type == TradeType.BUY) exit - entry else entry - exit

        return when {
            // --- JPY Pairs (Quote is JPY) ---
            pair.endsWith("JPY") -> {
                // Formula: (Diff * Lots * 100,000) / USDJPY_Rate
                // Approximation: Using exit price as conversion if it's USDJPY, 
                // or a default if it's a cross like EURJPY (requires USDJPY live rate).
                if (pair.startsWith("USD")) {
                    (priceDiff * lots * STANDARD_LOT_UNIT) / exit
                } else {
                    // Cross JPY pair - Approximation using a typical USDJPY rate of 150
                    (priceDiff * lots * STANDARD_LOT_UNIT) / 150.0
                }
            }
            
            // --- Metals ---
            pair.contains("XAU") || pair.contains("GOLD") -> priceDiff * lots * GOLD_LOT_UNIT
            pair.contains("XAG") || pair.contains("SILVER") -> priceDiff * lots * SILVER_LOT_UNIT

            // --- Indices (Broker dependent, usually 1 lot = 1 unit or 10 units) ---
            pair.contains("US30") || pair.contains("WS30") || pair.contains("NAS100") || 
            pair.contains("USTEC") || pair.contains("SPX500") || pair.contains("GER40") || 
            pair.contains("DAX") -> {
                // Retail Standard: 1 point = $1 per 1.0 lot
                priceDiff * lots
            }

            // --- Crypto ---
            pair.contains("BTC") || pair.contains("ETH") || pair.contains("SOL") -> {
                // 1 unit change = $1 per 1.0 lot
                priceDiff * lots
            }

            // --- Forex Majors (Quote is USD) ---
            pair.endsWith("USD") -> priceDiff * lots * STANDARD_LOT_UNIT

            // --- Forex Majors (Base is USD) ---
            pair.startsWith("USD") -> (priceDiff * lots * STANDARD_LOT_UNIT) / exit

            // --- Cross Pairs (Approximation) ---
            else -> priceDiff * lots * STANDARD_LOT_UNIT
        }
    }

    /**
     * Calculates the movement in Pips.
     */
    fun calculatePips(trade: Trade): Double {
        val entry = trade.entryPrice
        val exit = trade.exitPrice ?: return 0.0
        val pair = trade.pair.uppercase().replace("/", "").replace("-", "")

        val rawDiff = if (trade.type == TradeType.BUY) exit - entry else entry - exit

        return when {
            // JPY Pairs: 1 pip = 0.01
            pair.endsWith("JPY") -> rawDiff * 100.0
            
            // Gold: 1 pip = 0.1 ($1 move = 10 pips)
            pair.contains("XAU") || pair.contains("GOLD") -> rawDiff * 10.0
            
            // Indices & Crypto: 1 pip = 1.0 (Point)
            pair.contains("US30") || pair.contains("NAS100") || pair.contains("BTC") || pair.contains("ETH") -> rawDiff
            
            // Standard Pairs: 1 pip = 0.0001
            else -> rawDiff * 10000.0
        }
    }
}
