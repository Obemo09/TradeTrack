package com.example.tradetrack.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            Gson().fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
