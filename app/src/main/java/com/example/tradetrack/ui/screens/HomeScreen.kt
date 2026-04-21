package com.example.tradetrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.R
import com.example.tradetrack.data.PlayerLevel
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.UserStats
import com.example.tradetrack.ui.components.ModernTradeItem
import com.example.tradetrack.ui.components.EmptyState
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.ui.animations.buttonScaleFadeAnimation
import com.example.tradetrack.ui.animations.fabPulseAnimation
import com.example.tradetrack.ui.animations.cardRippleEffect
import com.example.tradetrack.ui.components.showcaseTarget
import com.example.tradetrack.util.ForexUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    trades: List<Trade>,
    userStats: UserStats?,
    onAddTrade: () -> Unit,
    onTradeClick: (Trade) -> Unit,
    onDeleteTrade: (Trade) -> Unit,
    onProfileClick: () -> Unit,
    onWinRateClick: () -> Unit,
    onTotalTradesClick: () -> Unit
) {
    val closedTrades = remember(trades) { trades.filter { it.result != TradeResult.OPEN } }
    val totalPnL = remember(closedTrades) { closedTrades.sumOf { ForexUtils.calculateProfit(it) }.toFloat() }
    val totalPips = remember(closedTrades) { closedTrades.sumOf { ForexUtils.calculatePips(it) }.toFloat() }
    
    // Entrance animation state
    var startAnimations by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        startAnimations = true
    }

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
                            "DASHBOARD",
                            style = TextStyle(
                                brush = Brush.horizontalGradient(BlueGradient),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 2.sp
                            )
                        )
                    }
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
                            .clickable { onProfileClick() }
                            .buttonScaleFadeAnimation()
                            .showcaseTarget("profile_target"),
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
                    .fabPulseAnimation()
                    .showcaseTarget("add_trade_target")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Trade", modifier = Modifier.size(24.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 1. GREETING HEADER
            item {
                AnimatedEntrance(visible = startAnimations, delay = 0) {
                    GreetingHeader()
                }
            }

            // 2. LARGE P&L DASHBOARD CARD
            item {
                AnimatedEntrance(visible = startAnimations, delay = 200) {
                    PnLHighlightCard(totalPnL, totalPips, closedTrades.size)
                }
            }

            // 3. QUICK STATS ROW
            item {
                AnimatedEntrance(visible = startAnimations, delay = 400) {
                    DashboardSummary(closedTrades, onWinRateClick, onTotalTradesClick)
                }
            }

            // 4. XP PROGRESS BAR
            item {
                AnimatedEntrance(visible = startAnimations, delay = 600) {
                    XPProgressSection(userStats)
                }
            }

            // 5. SMART INSIGHTS
            item {
                AnimatedEntrance(visible = startAnimations, delay = 800) {
                    SmartInsightsSection(trades)
                }
            }

            // 6. RECENT ACTIVITY HEADER
            item {
                Text(
                    "RECENT ACTIVITY",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = TradingTextSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            // 7. RECENT TRADES LIST
            if (trades.isEmpty()) {
                item { EmptyState() }
            } else {
                itemsIndexed(trades.take(5), key = { _, trade -> trade.id }) { index, trade ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(index * 100L)
                        visible = true
                    }
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { 20 })
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
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
fun AnimatedEntrance(
    visible: Boolean,
    delay: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800, delayMillis = delay)) + 
                slideInVertically(animationSpec = tween(800, delayMillis = delay), initialOffsetY = { 40 }),
        content = { content() }
    )
}

@Composable
fun GreetingHeader() {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = "$greeting, Trader!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Track your edge. Master the markets.",
            style = MaterialTheme.typography.bodyMedium,
            color = TradingTextSecondary
        )
    }
}

@Composable
fun PnLHighlightCard(totalPnL: Float, totalPips: Float, closedCount: Int) {
    val color = if (totalPnL >= 0) ProfitGreen else LossRed
    val icon = if (totalPnL >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "TOTAL NET PROFIT",
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            if (totalPnL >= 0) "+$" else "-$",
                            style = MaterialTheme.typography.headlineSmall,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        AnimatedFloatCounter(
                            targetValue = Math.abs(totalPnL),
                            style = MaterialTheme.typography.displayMedium,
                            color = color
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = color.copy(alpha = 0.1f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PipStat(label = "TOTAL PIPS", value = totalPips, color = color)
                PipStat(label = "AVG. PROFIT/TRADE", value = if (closedCount > 0) totalPnL / closedCount else 0f, isCurrency = true, color = color)
            }
        }
    }
}

@Composable
fun PipStat(label: String, value: Float, color: Color, isCurrency: Boolean = false) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isCurrency) Text("$", style = MaterialTheme.typography.bodySmall, color = color, fontWeight = FontWeight.Bold)
            AnimatedFloatCounter(targetValue = value, style = MaterialTheme.typography.titleMedium, color = color)
        }
    }
}

@Composable
fun SmartInsightsSection(trades: List<Trade>) {
    val insights = remember(trades) { generateInsights(trades) }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "SMART INSIGHTS",
            modifier = Modifier.padding(horizontal = 24.dp),
            style = MaterialTheme.typography.labelLarge,
            color = TradingBlue,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(insights) { insight ->
                InsightCard(insight)
            }
        }
    }
}

data class DashboardInsight(
    val title: String,
    val message: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

fun generateInsights(trades: List<Trade>): List<DashboardInsight> {
    val closedTrades = trades.filter { it.result != TradeResult.OPEN }
    if (closedTrades.isEmpty()) return listOf(DashboardInsight("Welcome", "Log your first closed trade to see AI insights.", Icons.Default.Lightbulb, TradingBlue))
    
    val insights = mutableListOf<DashboardInsight>()
    
    val wins = closedTrades.count { it.result == TradeResult.WIN }
    val winRate = (wins.toFloat() / closedTrades.size * 100).toInt()
    if (winRate > 60) {
        insights.add(DashboardInsight("High Edge", "Your strategy has a $winRate% win rate. Scale carefully.", Icons.Default.Verified, ProfitGreen))
    } else if (winRate < 40) {
        insights.add(DashboardInsight("Strategy Review", "Win rate is below 40%. Tighten your entry rules.", Icons.Default.Psychology, WarningOrange))
    }

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    val todayTrades = trades.count { it.date.startsWith(today) }
    if (todayTrades > 4) {
        insights.add(DashboardInsight("Overtrading", "You've taken $todayTrades trades today. Rest your eyes!", Icons.Default.Warning, LossRed))
    }

    val bestAsset = closedTrades.groupBy { it.pair }.maxByOrNull { it.value.sumOf { t -> ForexUtils.calculateProfit(t) } }?.key
    if (bestAsset != null) {
        insights.add(DashboardInsight("Golden Asset", "You are most profitable on $bestAsset.", Icons.Default.Star, TradingPurple))
    }

    return insights.take(3)
}

@Composable
fun InsightCard(insight: DashboardInsight) {
    Surface(
        modifier = Modifier
            .width(260.dp)
            .height(130.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(insight.icon, null, tint = insight.color, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(insight.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
            }
            Text(
                insight.message,
                style = MaterialTheme.typography.bodySmall,
                color = TradingTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun AnimatedCounter(targetValue: Int, style: TextStyle, color: Color) {
    var count by remember { mutableIntStateOf(0) }
    LaunchedEffect(targetValue) {
        animate(0f, targetValue.toFloat(), animationSpec = tween(1500, easing = FastOutSlowInEasing)) { value, _ ->
            count = value.toInt()
        }
    }
    Text(text = count.toString(), style = style, color = color, fontWeight = FontWeight.Black)
}

@Composable
fun AnimatedFloatCounter(targetValue: Float, style: TextStyle, color: Color) {
    var count by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(targetValue) {
        animate(0f, targetValue, animationSpec = tween(1500, easing = FastOutSlowInEasing)) { value, _ ->
            count = value
        }
    }
    Text(text = String.format(Locale.US, "%.2f", count), style = style, color = color, fontWeight = FontWeight.Black)
}

@Composable
fun DashboardSummary(closedTrades: List<Trade>, onWinRateClick: () -> Unit, onTotalTradesClick: () -> Unit) {
    val winRate = if (closedTrades.isNotEmpty()) (closedTrades.count { it.result == TradeResult.WIN }.toFloat() / closedTrades.size * 100).toInt() else 0
    
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SummaryCard(label = "WIN RATE", value = "$winRate%", icon = Icons.Default.AutoGraph, gradient = BlueGradient, onClick = onWinRateClick, modifier = Modifier.weight(1f))
        SummaryCard(label = "CLOSED TRADES", value = closedTrades.size.toString(), icon = Icons.Default.StackedLineChart, gradient = listOf(TradingPurple, Color(0xFF9D50BB)), onClick = onTotalTradesClick, modifier = Modifier.weight(1f))
    }
}

@Composable
fun SummaryCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, gradient: List<Color>, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(110.dp)
            .cardRippleEffect()
            .clickable { onClick() }, 
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(gradient)).padding(16.dp)) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                if (value.contains("%")) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        AnimatedCounter(value.replace("%","").toInt(), MaterialTheme.typography.headlineMedium, Color.White)
                        Text("%", style = MaterialTheme.typography.headlineSmall, color = Color.White, modifier = Modifier.padding(bottom = 2.dp))
                    }
                } else {
                    AnimatedCounter(value.toIntOrNull() ?: 0, MaterialTheme.typography.headlineMedium, Color.White)
                }
            }
        }
    }
}

@Composable
fun XPProgressSection(userStats: UserStats?) {
    if (userStats == null) return
    
    val xp = userStats.xp
    val currentLevel = PlayerLevel.fromXp(xp)
    val nextLevel = PlayerLevel.values().getOrNull(currentLevel.ordinal + 1)
    
    val progress = if (nextLevel != null) {
        val range = nextLevel.xpRequired - currentLevel.xpRequired
        val currentProgress = xp - currentLevel.xpRequired
        currentProgress.toFloat() / range
    } else 1f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "XP Progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = TradingBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        currentLevel.title.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TradingBlue,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "$xp XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = TradingTextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (userStats.currentStreak > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, "Streak", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    Text(
                        "${userStats.currentStreak} DAY STREAK",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF59E0B),
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = TradingBlue,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
