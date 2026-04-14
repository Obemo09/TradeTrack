package com.example.tradetrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/** Result of the trade: Win, Loss, Break Even, or Open. */
enum class TradeResult { WIN, LOSS, BREAK_EVEN, OPEN }

/** Trade type: BUY or SELL. */
enum class TradeType { BUY, SELL }

@Entity(tableName = "trades")
data class Trade(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    /** User ID to separate trades between accounts */
    val userId: String = "",

    /** Pair / Instrument */
    val pair: String = "",

    /** BUY or SELL */
    val type: TradeType = TradeType.BUY,

    val entryPrice: Double = 0.0,

    val exitPrice: Double? = null,

    val stopLoss: Double? = null,

    val takeProfit: Double? = null,

    val lotSize: Double? = null,

    val reason: String? = null,

    val imagePath: String? = null,

    val date: String = "",

    val result: TradeResult = TradeResult.OPEN,

    val notes: String? = null,

    val emotion: String? = null,

    val strategy: String? = null
)
