package com.example.tradetrack.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class UserStatsRepository(context: Context) {
    private val dao = DatabaseProvider.getDatabase(context).userStatsDao()
    private val tradeDao = DatabaseProvider.getDatabase(context).tradeDao()

    fun getUserStats(userId: String): Flow<UserStats?> = dao.getUserStats(userId)

    suspend fun updateStats(stats: UserStats) = dao.insertOrUpdate(stats)

    suspend fun addXp(userId: String, amount: Int) = dao.incrementXp(userId, amount)

    suspend fun processTradeLogged(userId: String, trade: Trade) {
        val currentStats = dao.getUserStats(userId).first() ?: UserStats(userId)
        
        var xpToAdd = 10 // +10 for logging
        
        // +20 for following risk rules (Stop Loss and Take Profit set)
        val followedRules = trade.stopLoss != null && trade.takeProfit != null
        if (followedRules) {
            xpToAdd += 20
        }

        // Streak tracking
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = sdf.format(Date())
        
        val newStreak = when {
            currentStats.lastTradeDate == todayStr -> currentStats.currentStreak // Already logged today
            isYesterday(currentStats.lastTradeDate) -> currentStats.currentStreak + 1
            else -> 1 // Reset or first trade
        }

        val newAchievements = currentStats.achievements.toMutableList()
        if (!newAchievements.contains("First Trade Logged")) {
            newAchievements.add("First Trade Logged")
        }
        if (newStreak >= 5 && !newAchievements.contains("5-Day Streak")) {
            newAchievements.add("5-Day Streak")
        }
        if (followedRules && !newAchievements.contains("Disciplined Trader")) {
            newAchievements.add("Disciplined Trader")
        }

        // New Achievements
        if (trade.result == TradeResult.WIN && !newAchievements.contains("Profit Pioneer")) {
            newAchievements.add("Profit Pioneer")
        }

        val allTrades = tradeDao.getAllTradesOnce(userId)
        if (allTrades.size >= 100 && !newAchievements.contains("Centurion")) {
            newAchievements.add("Centurion")
        }

        val newXp = currentStats.xp + xpToAdd
        val newLevelEnum = PlayerLevel.fromXp(newXp)
        val newLevel = newLevelEnum.title

        if (newLevelEnum == PlayerLevel.EXPERT && !newAchievements.contains("Market Expert")) {
            newAchievements.add("Market Expert")
        }

        val updatedStats = currentStats.copy(
            xp = newXp,
            currentStreak = newStreak,
            lastTradeDate = todayStr,
            level = newLevel,
            achievements = newAchievements
        )
        
        dao.insertOrUpdate(updatedStats)
    }

    private fun isYesterday(dateStr: String?): Boolean {
        if (dateStr == null) return false
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val lastDate = sdf.parse(dateStr) ?: return false
        
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = cal.time
        
        return sdf.format(lastDate) == sdf.format(yesterday)
    }
}
