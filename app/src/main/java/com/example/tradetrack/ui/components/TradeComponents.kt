package com.example.tradetrack.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.R
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.ui.animations.clickableSlideAnimation
import com.example.tradetrack.ui.animations.buttonScaleFadeAnimation
import com.example.tradetrack.util.ForexUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernTradeItem(trade: Trade, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()

    val resultColor = when (trade.result) {
        TradeResult.WIN -> ProfitGreen
        TradeResult.LOSS -> LossRed
        TradeResult.BREAK_EVEN -> WarningOrange
        TradeResult.OPEN -> PrimaryBlue
    }

    val typeColor = if (trade.type == TradeType.BUY) ProfitGreen else LossRed
    val typeLabel = if (trade.type == TradeType.BUY) "LONG" else "SHORT"
    val profit = ForexUtils.calculateProfit(trade)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isDark) resultColor.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.1f)
            )
            .clickableSlideAnimation()
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
            // Pair & Type Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                typeColor.copy(alpha = 0.15f),
                                typeColor.copy(alpha = 0.02f)
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
                    fontWeight = FontWeight.Black,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (trade.result != TradeResult.OPEN) {
                    Text(
                        text = if (profit >= 0) "+$${String.format(Locale.US, "%.2f", profit)}" 
                               else "-$${String.format(Locale.US, "%.2f", -profit)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (profit >= 0) ProfitGreen else LossRed,
                        fontWeight = FontWeight.Black
                    )
                } else {
                    Surface(
                        color = PrimaryBlue.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "OPEN",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    "@${trade.entryPrice}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TradingTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (showDelete) {
                AlertDialog(
                    onDismissRequest = { showDelete = false },
                    title = { Text("Delete Entry?", fontWeight = FontWeight.Black) },
                    text = { Text("Are you sure you want to remove this trade from your journal?") },
                    confirmButton = {
                        Button(
                            onClick = { onDelete(); showDelete = false },
                            colors = ButtonDefaults.buttonColors(containerColor = LossRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.buttonScaleFadeAnimation()
                        ) {
                            Text("Remove", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDelete = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(28.dp)
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
                    .size(140.dp)
                    .background(
                        Brush.radialGradient(listOf(TradingBlue.copy(alpha = 0.1f), Color.Transparent)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    alpha = 0.6f
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "NO TRADES YET", 
                style = MaterialTheme.typography.titleLarge, 
                color = MaterialTheme.colorScheme.onSurface, 
                fontWeight = FontWeight.Black, 
                letterSpacing = 2.sp
            )
            Text(
                "Start logging your journey today.", 
                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                style = MaterialTheme.typography.bodyMedium, 
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun DateHeader(date: String) {
    val formattedDate = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("EEEE, MMM d", Locale.US)
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: Date())
    } catch (e: Exception) {
        date
    }

    Text(
        text = formattedDate.uppercase(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        color = PrimaryBlue,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp
    )
}
