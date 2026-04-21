package com.example.tradetrack.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {

    @Query("SELECT * FROM trades WHERE userId = :userId ORDER BY date DESC")
    fun getAllTrades(userId: String): Flow<List<Trade>>

    @Query("SELECT * FROM trades WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllTradesOnce(userId: String): List<Trade>

    @Query("SELECT * FROM trades WHERE id = :id")
    suspend fun getTradeById(id: String): Trade?

    @Query("SELECT * FROM trades WHERE id = :id")
    fun getTradeFlow(id: String): Flow<Trade?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trade: Trade): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(trade: Trade)

    @Delete
    suspend fun delete(trade: Trade)

    @Query("DELETE FROM trades WHERE id = :id")
    suspend fun deleteById(id: String)
}
