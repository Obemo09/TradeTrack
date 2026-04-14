package com.example.tradetrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.ui.components.ModernTradeItem
import com.example.tradetrack.ui.components.EmptyState
import com.example.tradetrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    trades: List<Trade>,
    onAddTrade: () -> Unit,
    onTradeClick: (Trade) -> Unit,
    onDeleteTrade: (Trade) -> Unit,
    onProfileClick: () -> Unit,
    onWinRateClick: () -> Unit,
    onTotalTradesClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = {
                        Text(
                            "TRADETRACK",
                            style = TextStyle(
                                brush = Brush.horizontalGradient(BlueGradient),
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                letterSpacing = 2.sp
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onProfileClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = TradingBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
                DashboardSummary(
                    trades = trades,
                    onWinRateClick = onWinRateClick,
                    onTotalTradesClick = onTotalTradesClick
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTrade,
                containerColor = TradingBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = TradingBlue)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Trade", modifier = Modifier.size(24.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "RECENT ACTIVITY",
                    style = MaterialTheme.typography.labelLarge,
                    color = TradingTextSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Surface(
                    color = TradingBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${trades.size} TRADES",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TradingBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (trades.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trades, key = { it.id }) { trade ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically()
                        ) {
                            ModernTradeItem(
                                trade = trade,
                                onClick = { onTradeClick(trade) },
                                onDelete = { onDeleteTrade(trade) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSummary(
    trades: List<Trade>,
    onWinRateClick: () -> Unit,
    onTotalTradesClick: () -> Unit
) {
    val winRate = if (trades.isNotEmpty()) {
        (trades.count { it.result == TradeResult.WIN }.toFloat() / trades.size * 100).toInt()
    } else 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            label = "WIN RATE",
            value = "$winRate%",
            icon = Icons.Default.AutoGraph,
            gradient = BlueGradient,
            onClick = onWinRateClick,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "TOTAL TRADES",
            value = trades.size.toString(),
            icon = Icons.Default.StackedLineChart,
            gradient = listOf(TradingPurple, Color(0xFF9D50BB)),
            onClick = onTotalTradesClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    label: String, 
    value: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(64.dp).align(Alignment.BottomEnd).offset(x = 16.dp, y = 16.dp)
            )
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                Text(
                    value, 
                    style = MaterialTheme.typography.headlineMedium, 
                    color = Color.White, 
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
