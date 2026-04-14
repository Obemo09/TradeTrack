package com.example.tradetrack.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType
import com.example.tradetrack.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernTradeItem(trade: Trade, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }

    val resultColor = when (trade.result) {
        TradeResult.WIN -> TradingGreen
        TradeResult.LOSS -> TradingRed
        TradeResult.BREAK_EVEN -> TradingOrange
        TradeResult.OPEN -> TradingBlue
    }

    val typeColor = if (trade.type == TradeType.BUY) TradingGreen else TradingRed
    val typeLabel = if (trade.type == TradeType.BUY) "LONG" else "SHORT"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showDelete = true }
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, TradingLightGrey.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pair & Type Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(typeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (trade.type == TradeType.BUY) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    trade.pair.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    "$typeLabel • ${trade.date.substringBefore("T")}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TradingTextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val resultText = when (trade.result) {
                    TradeResult.WIN -> "WIN"
                    TradeResult.LOSS -> "LOSS"
                    TradeResult.BREAK_EVEN -> "B/E"
                    TradeResult.OPEN -> "OPEN"
                }
                
                Surface(
                    color = resultColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        resultText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = resultColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Text(
                    "@${trade.entryPrice}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TradingTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (showDelete) {
                AlertDialog(
                    onDismissRequest = { showDelete = false },
                    title = { Text("Delete Entry?", fontWeight = FontWeight.Bold) },
                    text = { Text("Are you sure you want to remove this trade from your journal?") },
                    confirmButton = {
                        TextButton(onClick = { onDelete(); showDelete = false }, colors = ButtonDefaults.textButtonColors(contentColor = TradingRed)) {
                            Text("Remove", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDelete = false }) {
                            Text("Cancel", color = TradingTextSecondary)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    textContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddChart,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = TradingBlue.copy(alpha = 0.5f)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text("NO DATA DETECTED", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("Begin your journal by adding your first trade.", color = TradingTextSecondary, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun DateHeader(date: String) {
    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: Date())
    } catch (e: Exception) {
        date
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = Color.Transparent
    ) {
        Text(
            text = formattedDate.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = TradingBlue,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
