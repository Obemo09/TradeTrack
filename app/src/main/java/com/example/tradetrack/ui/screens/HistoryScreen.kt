package com.example.tradetrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.R
import com.example.tradetrack.data.Trade
import com.example.tradetrack.ui.components.DateHeader
import com.example.tradetrack.ui.components.EmptyState
import com.example.tradetrack.ui.components.ModernTradeItem
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.viewmodel.TradeListViewModel
import com.example.tradetrack.ui.animations.clickableSlideAnimation

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
                            "TRADING JOURNAL",
                            style = TextStyle(
                                brush = Brush.horizontalGradient(BlueGradient),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )
                        )
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
            // Subtle Background Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(TradingBlue.copy(alpha = 0.03f), Color.Transparent)
                        )
                    )
            )

            if (trades.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp)
                ) {
                    groupedTrades.forEach { (date, tradesInDay) ->
                        stickyHeader {
                            Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                                DateHeader(date)
                            }
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
}
