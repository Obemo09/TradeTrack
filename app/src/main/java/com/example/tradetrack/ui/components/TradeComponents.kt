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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = resultColor.copy(alpha = 0.2f)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showDelete = true }
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pair & Type Icon with Gradient Background
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                typeColor.copy(alpha = 0.2f),
                                typeColor.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (trade.type == TradeType.BUY) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    trade.pair.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        typeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = typeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        " • ${trade.date.substringBefore("T")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TradingTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val resultText = when (trade.result) {
                    TradeResult.WIN -> "WIN"
                    TradeResult.LOSS -> "LOSS"
                    TradeResult.BREAK_EVEN -> "B/E"
                    TradeResult.OPEN -> "OPEN"
                }
                
                Surface(
                    color = resultColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        resultText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = resultColor,
                        fontWeight = FontWeight.Black
                    )
                }
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    "@${trade.entryPrice}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
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
                            Text("Cancel", color = Color.Gray)
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
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(listOf(TradingBlue.copy(alpha = 0.15f), Color.Transparent)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddChart,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = TradingBlue
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "READY TO START?", 
                style = MaterialTheme.typography.titleMedium, 
                color = MaterialTheme.colorScheme.onSurface, 
                fontWeight = FontWeight.Black, 
                letterSpacing = 2.sp
            )
            Text(
                "Your journal is empty. Log your first trade.", 
                color = TradingTextSecondary, 
                style = MaterialTheme.typography.bodySmall, 
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun DateHeader(date: String) {
    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("EEEE, MMMM d", Locale.US)
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: Date())
    } catch (e: Exception) {
        date
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        color = Color.Transparent
    ) {
        Text(
            text = formattedDate.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = TradingBlue,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
