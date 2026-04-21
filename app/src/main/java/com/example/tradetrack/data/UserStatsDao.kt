package com.example.tradetrack.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getUserStats(userId: String): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: UserStats)

    @Query("UPDATE user_stats SET xp = xp + :amount WHERE userId = :userId")
    suspend fun incrementXp(userId: String, amount: Int)
}
