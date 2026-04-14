package com.example.tradetrack.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTradeResult(value: TradeResult): String {
        return value.name
    }

    @TypeConverter
    fun toTradeResult(value: String): TradeResult {
        return try {
            TradeResult.valueOf(value)
        } catch (e: IllegalArgumentException) {
            TradeResult.OPEN
        }
    }

    @TypeConverter
    fun fromTradeType(value: TradeType): String {
        return value.name
    }

    @TypeConverter
    fun toTradeType(value: String): TradeType {
        return try {
            TradeType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            TradeType.BUY
        }
    }
}
