package com.example.tradetrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.viewmodel.TradeListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: TradeListViewModel) {
    val trades by viewModel.trades.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "PERFORMANCE ANALYTICS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = TradingBlue
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (trades.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Add trades to see analytics", color = TradingTextSecondary)
                }
            } else {
                OverallStats(trades)
                ResultDistribution(trades)
                TopPairs(trades)
            }
        }
    }
}

@Composable
fun OverallStats(trades: List<Trade>) {
    val wins = trades.count { it.result == TradeResult.WIN }
    val losses = trades.count { it.result == TradeResult.LOSS }
    val total = trades.size
    val winRate = if (total > 0) (wins.toFloat() / total * 100).toInt() else 0

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem("WIN RATE", "$winRate%", TradingBlue)
            StatItem("WINS", wins.toString(), TradingGreen)
            StatItem("LOSSES", losses.toString(), TradingRed)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary)
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun ResultDistribution(trades: List<Trade>) {
    val wins = trades.count { it.result == TradeResult.WIN }
    val losses = trades.count { it.result == TradeResult.LOSS }
    val breakeven = trades.count { it.result == TradeResult.BREAK_EVEN }
    val total = trades.size

    Text("RESULT DISTRIBUTION", style = MaterialTheme.typography.labelMedium, color = TradingBlue, fontWeight = FontWeight.Bold)
    
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DistributionBar("Wins", wins, total, TradingGreen)
            DistributionBar("Losses", losses, total, TradingRed)
            DistributionBar("Break Even", breakeven, total, TradingOrange)
        }
    }
}

@Composable
fun DistributionBar(label: String, count: Int, total: Int, color: Color) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Text("$count (${(progress * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall, color = TradingTextSecondary)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun TopPairs(trades: List<Trade>) {
    val pairs = trades.groupBy { it.pair.uppercase() }
        .map { (pair, list) -> pair to list.size }
        .sortedByDescending { it.second }
        .take(5)

    Text("MOST TRADED ASSETS", style = MaterialTheme.typography.labelMedium, color = TradingBlue, fontWeight = FontWeight.Bold)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            pairs.forEach { (pair, count) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(pair, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Text("$count trades", color = TradingTextSecondary)
                }
            }
        }
    }
}
