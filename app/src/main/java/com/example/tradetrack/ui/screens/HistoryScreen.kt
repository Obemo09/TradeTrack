package com.example.tradetrack.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.data.Trade
import com.example.tradetrack.ui.components.DateHeader
import com.example.tradetrack.ui.components.EmptyState
import com.example.tradetrack.ui.components.ModernTradeItem
import com.example.tradetrack.ui.theme.TradingBlue
import com.example.tradetrack.ui.theme.TradingTextPrimary
import com.example.tradetrack.viewmodel.TradeListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    viewModel: TradeListViewModel,
    onTradeClick: (Trade) -> Unit,
    onDeleteTrade: (Trade) -> Unit
) {
    val trades by viewModel.trades.collectAsState()
    
    val groupedTrades = trades.groupBy { 
        it.date.substringBefore("T") 
    }.toSortedMap(compareByDescending { it })

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TRADE HISTORY",
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
        if (trades.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedTrades.forEach { (date, tradesInDay) ->
                    stickyHeader {
                        DateHeader(date)
                    }
                    items(tradesInDay, key = { it.id }) { trade ->
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
