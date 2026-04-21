package com.example.tradetrack.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tradetrack.R
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.ui.animations.buttonScaleFadeAnimation
import com.example.tradetrack.util.ForexUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailScreen(
    viewModel: com.example.tradetrack.viewmodel.TradeDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Trade) -> Unit,
    onDeleted: () -> Unit
) {
    val tradeState = viewModel.trade.collectAsState()

    tradeState.value?.let { trade ->
        val resultColor = when (trade.result) {
            TradeResult.WIN -> TradingGreen
            TradeResult.LOSS -> TradingRed
            TradeResult.BREAK_EVEN -> WarningOrange
            TradeResult.OPEN -> TradingBlue
        }
        
        val profit = ForexUtils.calculateProfit(trade)
        val pips = ForexUtils.calculatePips(trade)

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "TRADE DETAILS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEdit(trade) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TradingBlue)
                        }
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Section: Pair & Result
                HeaderSection(trade, resultColor)

                // P&L Highlights
                Surface(
                    color = resultColor.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, resultColor.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("NET PROFIT", style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary)
                            Text(
                                if (profit >= 0) "+$${String.format(Locale.US, "%.2f", profit)}" else "-$${String.format(Locale.US, "%.2f", -profit)}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = resultColor,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL PIPS", style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary)
                            Text(
                                String.format(Locale.US, "%.1f PIPS", pips),
                                style = MaterialTheme.typography.titleLarge,
                                color = resultColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Key Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(label = "LOT SIZE", value = String.format(Locale.US, "%.2f", trade.lotSize ?: 0.0), icon = Icons.Default.Layers, modifier = Modifier.weight(1f))
                    StatCard(label = "STRATEGY", value = trade.strategy ?: "Unset", icon = Icons.Default.EmojiObjects, modifier = Modifier.weight(1f))
                }

                // Price Section
                InfoGroup(title = "PRICE ACTION") {
                    PriceRow("Entry Price", trade.entryPrice.toString(), TradingBlue)
                    PriceRow("Exit Price", trade.exitPrice?.toString() ?: "-", resultColor)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PriceBox("Stop Loss", trade.stopLoss?.toString() ?: "-", TradingRed, Modifier.weight(1f))
                        PriceBox("Take Profit", trade.takeProfit?.toString() ?: "-", TradingGreen, Modifier.weight(1f))
                    }
                }

                // Analysis Section
                if (!trade.reason.isNullOrBlank() || !trade.notes.isNullOrBlank()) {
                    InfoGroup(title = "ANALYSIS & NOTES") {
                        if (!trade.reason.isNullOrBlank()) {
                            AnalysisText(label = "Confluence", text = trade.reason)
                        }
                        if (!trade.notes.isNullOrBlank()) {
                            AnalysisText(label = "Closing Notes", text = trade.notes)
                        }
                    }
                }

                // Screenshot Section
                if (trade.imagePath != null) {
                    InfoGroup(title = "CHART SCREENSHOT") {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(trade.imagePath).build(),
                                contentDescription = "Trade Chart",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Emotion Tag
                if (!trade.emotion.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = null, tint = TradingPurple, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Feeling: ${trade.emotion}", style = MaterialTheme.typography.labelMedium, color = TradingTextSecondary)
                    }
                }

                Button(
                    onClick = {
                        viewModel.deleteTrade(trade)
                        onDeleted()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TradingRed.copy(alpha = 0.1f), contentColor = TradingRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .buttonScaleFadeAnimation(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, TradingRed.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("DELETE JOURNAL ENTRY", fontWeight = FontWeight.Bold)
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = TradingBlue)
    }
}

@Composable
fun HeaderSection(trade: Trade, resultColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(trade.pair.uppercase(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Row(verticalAlignment = Alignment.CenterVertically) {
                val typeColor = if (trade.type == TradeType.BUY) TradingGreen else TradingRed
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(typeColor))
                Spacer(Modifier.width(8.dp))
                Text(trade.type.name, style = MaterialTheme.typography.titleMedium, color = typeColor, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(12.dp))
                Text(trade.date.substringBefore("T"), style = MaterialTheme.typography.bodyMedium, color = TradingTextSecondary)
            }
        }
        
        Surface(
            color = resultColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, resultColor.copy(alpha = 0.5f))
        ) {
            Text(
                trade.result.name,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleSmall,
                color = resultColor,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TradingBlue, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary)
                Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = TradingBlue, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun PriceRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TradingTextSecondary)
        Text(value, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PriceBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.background).padding(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary)
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnalysisText(label: String, text: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TradingPurple, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
