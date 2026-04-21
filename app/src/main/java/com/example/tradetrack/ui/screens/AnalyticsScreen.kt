package com.example.tradetrack.ui.screens

import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.tradetrack.R
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.util.ForexUtils
import com.example.tradetrack.viewmodel.TradeListViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.SimpleDateFormat
import java.util.*

enum class TimeFrame(val label: String) {
    DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: TradeListViewModel) {
    val trades by viewModel.trades.collectAsState()
    val timeFrames = TimeFrame.values()
    val pagerState = rememberPagerState(pageCount = { timeFrames.size })
    
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
                            "ANALYTICS",
                            style = TextStyle(
                                brush = Brush.horizontalGradient(PremiumBlueGradient),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = PrimaryBlue,
                divider = {},
                indicator = { tabPositions ->
                    if (pagerState.currentPage < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = PrimaryBlue
                        )
                    }
                }
            ) {
                timeFrames.forEachIndexed { index, timeFrame ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { /* Handle click if needed, but pager handles swipe */ },
                        text = { Text(timeFrame.label, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                val timeFrame = timeFrames[page]
                val filteredTrades = filterTradesByTimeFrame(trades, timeFrame)
                
                AnalyticsContent(filteredTrades)
            }
        }
    }
}

@Composable
fun AnalyticsContent(trades: List<Trade>) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (trades.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(top = 100.dp), contentAlignment = Alignment.Center) {
                Text("No data for this period", color = TradingTextSecondary)
            }
        } else {
            InsightCardsRow(trades)
            
            ChartSection("Win vs Loss Ratio") {
                WinLossPieChart(trades)
            }
            
            ChartSection("Profit Over Time (Standard Lot Calculated)") {
                ProfitLineChart(trades)
            }
            
            ChartSection("Trades Per Day") {
                TradesBarChart(trades)
            }
            
            AssetInsights(trades)
            
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
fun ChartSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = PrimaryBlue,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth().height(300.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun InsightCardsRow(trades: List<Trade>) {
    val wins = trades.count { it.result == TradeResult.WIN }
    val winRate = if (trades.isNotEmpty()) (wins.toFloat() / trades.size * 100).toInt() else 0
    
    val bestDay = getBestDay(trades)
    val topAsset = getTopAsset(trades)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InsightCard(
                label = "Win Rate",
                value = "$winRate%",
                icon = Icons.Default.TrendingUp,
                gradient = PremiumBlueGradient,
                modifier = Modifier.weight(1f)
            )
            InsightCard(
                label = "Best Day",
                value = bestDay,
                icon = Icons.Default.Event,
                gradient = PremiumGreenGradient,
                modifier = Modifier.weight(1f)
            )
        }
        InsightCard(
            label = "Most Profitable Asset",
            value = topAsset,
            icon = Icons.Default.Star,
            gradient = listOf(TradingPurple, Color(0xFF9D50BB)),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InsightCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableStateOf(0) }
    val numericValue = value.replace("%", "").toIntOrNull() ?: 0
    
    LaunchedEffect(value) {
        if (numericValue > 0) {
            animate(0f, numericValue.toFloat(), animationSpec = tween(1500, easing = FastOutSlowInEasing)) { current, _ ->
                animatedValue = current.toInt()
            }
        } else {
            animatedValue = 0
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradient), alpha = 0.1f)
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = gradient[0], modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    if (value.contains("%")) "$animatedValue%" else value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun WinLossPieChart(trades: List<Trade>) {
    val wins = trades.count { it.result == TradeResult.WIN }.toFloat()
    val losses = trades.count { it.result == TradeResult.LOSS }.toFloat()
    val breakeven = trades.count { it.result == TradeResult.BREAK_EVEN }.toFloat()
    
    val entries = listOf(
        PieEntry(wins, "Wins"),
        PieEntry(losses, "Losses"),
        PieEntry(breakeven, "B/E")
    ).filter { it.value > 0 }

    val colors = listOf(
        ProfitGreen.toArgb(),
        LossRed.toArgb(),
        WarningOrange.toArgb()
    )

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.TRANSPARENT)
                setTransparentCircleColor(android.graphics.Color.TRANSPARENT)
                legend.isEnabled = true
                legend.textColor = android.graphics.Color.GRAY
                setEntryLabelColor(android.graphics.Color.WHITE)
                setEntryLabelTypeface(Typeface.DEFAULT_BOLD)
                animateY(1400, Easing.EaseInOutQuad)
            }
        },
        update = { chart ->
            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                sliceSpace = 3f
                valueTextSize = 12f
                valueTextColor = android.graphics.Color.WHITE
                valueTypeface = Typeface.DEFAULT_BOLD
                valueFormatter = PercentFormatter(chart)
            }
            chart.data = PieData(dataSet)
            chart.setUsePercentValues(true)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ProfitLineChart(trades: List<Trade>) {
    val entries = mutableListOf<Entry>()
    var cumulativeProfit = 0.0
    
    val sortedTrades = trades.sortedBy { it.date }
    sortedTrades.forEachIndexed { index, trade ->
        cumulativeProfit += ForexUtils.calculateProfit(trade)
        entries.add(Entry(index.toFloat(), cumulativeProfit.toFloat()))
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = android.graphics.Color.GRAY
                axisLeft.textColor = android.graphics.Color.GRAY
                axisRight.isEnabled = false
                legend.isEnabled = false
                animateX(1500)
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "Profit").apply {
                color = PrimaryBlue.toArgb()
                setCircleColor(PrimaryBlue.toArgb())
                lineWidth = 3f
                circleRadius = 4f
                setDrawCircleHole(false)
                valueTextSize = 0f
                setDrawFilled(true)
                fillAlpha = 50
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun TradesBarChart(trades: List<Trade>) {
    val tradesByDay = trades.groupBy { it.date.substringBefore("T") }
        .mapValues { it.value.size.toFloat() }
        .toSortedMap()
    
    val labels = tradesByDay.keys.toList()
    val entries = tradesByDay.values.mapIndexed { index, count ->
        BarEntry(index.toFloat(), count)
    }

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = android.graphics.Color.GRAY
                axisLeft.textColor = android.graphics.Color.GRAY
                axisRight.isEnabled = false
                legend.isEnabled = false
                animateY(1500)
            }
        },
        update = { chart ->
            val dataSet = BarDataSet(entries, "Trades").apply {
                color = PrimaryBlue.toArgb()
                valueTextColor = android.graphics.Color.GRAY
                valueTextSize = 10f
            }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            chart.data = BarData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AssetInsights(trades: List<Trade>) {
    val assets = trades.groupBy { it.pair.uppercase() }
        .mapValues { entry ->
            val assetTrades = entry.value
            val wins = assetTrades.count { it.result == TradeResult.WIN }
            val winRate = (wins.toFloat() / assetTrades.size * 100).toInt()
            val profit = assetTrades.sumOf { ForexUtils.calculateProfit(it) }
            Triple(assetTrades.size, winRate, profit)
        }.toList().sortedByDescending { it.second.third }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Performance by Asset",
            style = MaterialTheme.typography.labelLarge,
            color = PrimaryBlue,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        
        assets.forEach { (asset, stats) ->
            val (count, winRate, profit) = stats
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(asset, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("$count trades", style = MaterialTheme.typography.bodySmall, color = TradingTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            if (profit >= 0) "+$${String.format(Locale.US, "%.2f", profit)}" else "-$${String.format(Locale.US, "%.2f", -profit)}",
                            color = if (profit >= 0) ProfitGreen else LossRed,
                            fontWeight = FontWeight.Black
                        )
                        Text("$winRate% win rate", style = MaterialTheme.typography.bodySmall, color = TradingTextSecondary)
                    }
                }
            }
        }
    }
}

// --- Helper Functions ---

fun filterTradesByTimeFrame(trades: List<Trade>, timeFrame: TimeFrame): List<Trade> {
    val now = Calendar.getInstance()
    return trades.filter {
        val tradeDate = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.date.substringBefore("T"))
        } catch (e: Exception) { null }
        
        if (tradeDate == null) return@filter false
        val tradeCal = Calendar.getInstance().apply { time = tradeDate }
        
        when (timeFrame) {
            TimeFrame.DAILY -> {
                tradeCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                tradeCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
            }
            TimeFrame.WEEKLY -> {
                tradeCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                tradeCal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
            }
            TimeFrame.MONTHLY -> {
                tradeCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                tradeCal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
            }
        }
    }
}

fun getBestDay(trades: List<Trade>): String {
    if (trades.isEmpty()) return "-"
    val winsByDay = trades.filter { it.result == TradeResult.WIN }
        .groupBy { 
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it.date.substringBefore("T"))
            SimpleDateFormat("EEEE", Locale.US).format(date ?: Date())
        }
    return winsByDay.maxByOrNull { it.value.size }?.key ?: "-"
}

fun getTopAsset(trades: List<Trade>): String {
    if (trades.isEmpty()) return "-"
    val assetPerformance = trades.groupBy { it.pair.uppercase() }
        .mapValues { entry -> 
            entry.value.sumOf { ForexUtils.calculateProfit(it) }
        }
    return assetPerformance.maxByOrNull { it.value }?.key ?: "-"
}
