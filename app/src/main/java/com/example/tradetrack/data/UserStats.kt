package com.example.tradetrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val userId: String,
    val xp: Int = 0,
    val currentStreak: Int = 0,
    val lastTradeDate: String? = null,
    val level: String = "Beginner",
    val achievements: List<String> = emptyList()
)

enum class PlayerLevel(val title: String, val xpRequired: Int) {
    BEGINNER("Beginner", 0),
    INTERMEDIATE("Intermediate", 500),
    PRO("Pro", 1500),
    EXPERT("Expert", 4000);

    companion object {
        fun fromXp(xp: Int): PlayerLevel {
            return values().findLast { xp >= it.xpRequired } ?: BEGINNER
        }
    }
}
