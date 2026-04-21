package com.example.tradetrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.R
import com.example.tradetrack.data.Achievement
import com.example.tradetrack.data.AchievementRegistry
import com.example.tradetrack.data.UserStats
import com.example.tradetrack.ui.theme.BlueGradient
import com.example.tradetrack.ui.theme.ProfitGreen
import com.example.tradetrack.ui.theme.TradingBlue
import com.example.tradetrack.ui.theme.TradingTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    userStats: UserStats?,
    onBack: () -> Unit
) {
    val unlockedIds = userStats?.achievements ?: emptyList()
    val allAchievements = AchievementRegistry.ALL_ACHIEVEMENTS

    var filterMode by remember { mutableStateOf("all") } // "all", "unlocked", "locked"

    val filteredAchievements = when (filterMode) {
        "unlocked" -> allAchievements.filter { unlockedIds.contains(it.id) }
        "locked" -> allAchievements.filter { !unlockedIds.contains(it.id) }
        else -> allAchievements
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "ACHIEVEMENTS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(TradingBlue.copy(alpha = 0.05f), Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Progress Header
                AchievementProgressHeader(unlockedCount = unlockedIds.size, totalCount = allAchievements.size)

                // Filter Tabs
                AchievementFilterTabs(
                    selectedFilter = filterMode,
                    onFilterChange = { filterMode = it },
                    unlockedCount = unlockedIds.size,
                    lockedCount = allAchievements.size - unlockedIds.size
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAchievements) { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            isUnlocked = unlockedIds.contains(achievement.id)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementProgressHeader(unlockedCount: Int, totalCount: Int) {
    val progress = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f
    val completionPercentage = if (totalCount > 0) (unlockedCount * 100) / totalCount else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Your Progress",
                style = MaterialTheme.typography.labelLarge,
                color = TradingTextSecondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                "$unlockedCount / $totalCount (${completionPercentage}%)",
                style = MaterialTheme.typography.labelLarge,
                color = ProfitGreen,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape),
            color = ProfitGreen,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun AchievementFilterTabs(
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    unlockedCount: Int,
    lockedCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterTabButton(
            label = "All",
            isSelected = selectedFilter == "all",
            onClick = { onFilterChange("all") },
            modifier = Modifier.weight(1f)
        )
        FilterTabButton(
            label = "Unlocked ($unlockedCount)",
            isSelected = selectedFilter == "unlocked",
            onClick = { onFilterChange("unlocked") },
            modifier = Modifier.weight(1f),
            color = ProfitGreen
        )
        FilterTabButton(
            label = "Locked ($lockedCount)",
            isSelected = selectedFilter == "locked",
            onClick = { onFilterChange("locked") },
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )
    }
}

@Composable
fun FilterTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = TradingBlue
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) color else TradingTextSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, color)
        } else null
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
        )
    }
}

@Composable
fun AchievementCard(achievement: Achievement, isUnlocked: Boolean) {
    Surface(
        color = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = if (isUnlocked) 8.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .alpha(if (isUnlocked) 1f else 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    color = if (isUnlocked) ProfitGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (isUnlocked) "UNLOCKED" else "LOCKED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUnlocked) ProfitGreen else Color.Gray,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                }
            }

            // Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) Brush.linearGradient(BlueGradient)
                        else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) achievement.icon else Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            // Title and Description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TradingTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    maxLines = 2
                )
            }
        }
    }
}
