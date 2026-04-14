package com.example.tradetrack.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class TradeRepository(context: Context) {

    private val dao = DatabaseProvider.getDatabase(context).tradeDao()

    fun getAllTrades(userId: String): Flow<List<Trade>> = dao.getAllTrades(userId)

    suspend fun getAllTradesOnce(userId: String): List<Trade> = dao.getAllTradesOnce(userId)

    suspend fun getTradeById(id: String): Trade? = dao.getTradeById(id)

    suspend fun insert(trade: Trade): Long = dao.insert(trade)

    suspend fun update(trade: Trade) = dao.update(trade)

    suspend fun delete(trade: Trade) = dao.delete(trade)

    suspend fun deleteById(id: String) = dao.deleteById(id)
}
