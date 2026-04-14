package com.example.tradetrack.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType
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
    onProfileClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TopAppBar(
                    title = {
                        Text(
                            "TRADETRACK",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 3.sp,
                            color = TradingBlue
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                         IconButton(onClick = onProfileClick) {
                             Icon(
                                 imageVector = Icons.Default.AccountCircle,
                                 contentDescription = "Profile",
                                 tint = TradingTextSecondary
                             )
                         }
                    }
                )
                DashboardSummary(trades)
            }
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onAddTrade,
                containerColor = TradingBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Trade", modifier = Modifier.size(36.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TRADING HISTORY",
                    style = MaterialTheme.typography.labelMedium,
                    color = TradingTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    "${trades.size} entries",
                    style = MaterialTheme.typography.labelSmall,
                    color = TradingBlue
                )
            }

            if (trades.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(trades, key = { it.id }) { trade ->
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

@Composable
fun DashboardSummary(trades: List<Trade>) {
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
            label = "Performance",
            value = "$winRate%",
            subValue = "Win Rate",
            gradient = Brush.verticalGradient(listOf(TradingGreen, TradingGreenLight)),
            modifier = Modifier.weight(1.2f)
        )
        SummaryCard(
            label = "Activity",
            value = trades.size.toString(),
            subValue = "Total Trades",
            gradient = Brush.verticalGradient(listOf(TradingBlue, Color(0xFF4785FF))),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    label: String, 
    value: String, 
    subValue: String,
    gradient: Brush, 
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, TradingLightGrey.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .alpha(0.05f)
            )
            
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
                Column {
                    Text(
                        value, 
                        style = MaterialTheme.typography.headlineMedium, 
                        color = MaterialTheme.colorScheme.onSurface, 
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(subValue, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary)
                }
            }
        }
    }
}
