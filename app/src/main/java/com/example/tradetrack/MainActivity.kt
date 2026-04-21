package com.example.tradetrack

import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tradetrack.navigation.BottomNavItems
import com.example.tradetrack.navigation.Screen
import com.example.tradetrack.navigation.TradeTrackNavGraph
import com.example.tradetrack.ui.components.*
import com.example.tradetrack.ui.theme.TradeTrackTheme
import com.example.tradetrack.ui.theme.TradingBlue
import com.example.tradetrack.ui.theme.TradingDarkGrey
import com.example.tradetrack.viewmodel.AuthViewModel
import com.example.tradetrack.viewmodel.TradeListViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("tradetrack_prefs", Context.MODE_PRIVATE)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable edge-to-edge UI
        enableEdgeToEdge()

        setContent {
            val savedTheme = sharedPreferences.getBoolean("isDarkMode", isSystemInDarkTheme())
            var isDarkMode by remember { mutableStateOf(savedTheme) }
            val showcaseState = remember { ShowcaseState() }
            var showTourPrompt by remember { mutableStateOf(false) }
            val context = LocalContext.current

            TradeTrackTheme(darkTheme = isDarkMode) {
                ShowcaseHost(state = showcaseState) {
                    val navController = rememberNavController()
                    val listViewModel: TradeListViewModel = viewModel()
                    val authViewModel: AuthViewModel = viewModel()

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val currentUser by authViewModel.currentUser.collectAsState()

                    // Hide bottom bar on screens not in BottomNavItems
                    val showBottomBar = currentDestination?.route?.let { route ->
                        BottomNavItems.any { it.route == route }
                    } ?: false

                    // Robust Tour Trigger Logic
                    LaunchedEffect(currentDestination?.route, currentUser?.uid) {
                        val route = currentDestination?.route
                        val uid = currentUser?.uid
                        
                        // We trigger the tour if the user is on Home and hasn't seen it yet
                        if (route == Screen.Home.route && uid != null) {
                            if (!isTourFinished(context, uid)) {
                                // Give the Home screen ample time to load its content and register targets
                                delay(2000)
                                if (!showcaseState.isVisible && !showTourPrompt) {
                                    showTourPrompt = true
                                }
                            }
                        }
                    }

                    if (showTourPrompt) {
                        TourPromptDialog(
                            onAccept = {
                                showTourPrompt = false
                                showcaseState.start(listOf(
                                    ShowcaseStep("add_trade", "JOURNALING", "Tap this button to log your first trade manually. You can add pairs, lot sizes, and screenshots.", "add_trade_target"),
                                    ShowcaseStep("analytics", "INSIGHTS", "Switch to the Analytics tab to see your win rate, profit curves, and performance metrics.", "analytics_tab_target"),
                                    ShowcaseStep("profile", "PROGRESSION", "Check your level, XP, and achievements here. Complete challenges to level up your status.", "profile_target")
                                ))
                                saveTourFinished(context, currentUser?.uid)
                            },
                            onDismiss = {
                                showTourPrompt = false
                                saveTourFinished(context, currentUser?.uid)
                            }
                        )
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            AnimatedVisibility(
                                visible = showBottomBar,
                                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 16.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(28.dp),
                                        color = if (isDarkMode) TradingDarkGrey.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f),
                                        tonalElevation = 8.dp,
                                        shadowElevation = 12.dp,
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = 0.5.dp,
                                            color = (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.1f)
                                        )
                                    ) {
                                        NavigationBar(
                                            containerColor = Color.Transparent,
                                            contentColor = TradingBlue,
                                            windowInsets = WindowInsets(0, 0, 0, 0),
                                            modifier = Modifier.height(80.dp)
                                        ) {
                                            BottomNavItems.forEach { screen ->
                                                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                                                val iconScale by animateFloatAsState(
                                                    targetValue = if (selected) 1.2f else 1.0f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    ),
                                                    label = "Icon Scale"
                                                )

                                                NavigationBarItem(
                                                    modifier = if (screen == Screen.Analytics) Modifier.showcaseTarget("analytics_tab_target") else Modifier,
                                                    icon = { 
                                                        screen.icon?.let { 
                                                            Icon(
                                                                imageVector = it, 
                                                                contentDescription = null,
                                                                tint = if (selected) TradingBlue else Color.Gray.copy(alpha = 0.6f),
                                                                modifier = Modifier
                                                                    .size(26.dp)
                                                                    .graphicsLayer {
                                                                        scaleX = iconScale
                                                                        scaleY = iconScale
                                                                    }
                                                            ) 
                                                        } 
                                                    },
                                                    label = { 
                                                        Text(
                                                            text = screen.title,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                                            color = if (selected) TradingBlue else Color.Gray.copy(alpha = 0.6f),
                                                            fontSize = 11.sp
                                                        ) 
                                                    },
                                                    selected = selected,
                                                    onClick = {
                                                        if (!selected) {
                                                            navController.navigate(screen.route) {
                                                                popUpTo(navController.graph.findStartDestination().id) {
                                                                    saveState = true
                                                                }
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        }
                                                    },
                                                    colors = NavigationBarItemDefaults.colors(
                                                        indicatorColor = TradingBlue.copy(alpha = 0.12f)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = if (showBottomBar) 0.dp else innerPadding.calculateBottomPadding()),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            TradeTrackNavGraph(
                                navController = navController,
                                listViewModel = listViewModel,
                                authViewModel = authViewModel,
                                isDarkMode = isDarkMode,
                                onThemeChange = { 
                                    isDarkMode = it
                                    sharedPreferences.edit().putBoolean("isDarkMode", it).apply()
                                },
                                showcaseState = showcaseState
                            )
                        }
                    }
                }
            }
        }
    }
}
