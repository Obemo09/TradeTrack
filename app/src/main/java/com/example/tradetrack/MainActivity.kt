package com.example.tradetrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tradetrack.navigation.BottomNavItems
import com.example.tradetrack.navigation.Screen
import com.example.tradetrack.navigation.TradeTrackNavGraph
import com.example.tradetrack.ui.screens.SettingsScreen
import com.example.tradetrack.ui.theme.TradeTrackTheme
import com.example.tradetrack.ui.theme.TradingBlack
import com.example.tradetrack.ui.theme.TradingBlue
import com.example.tradetrack.ui.theme.TradingDarkGrey
import com.example.tradetrack.viewmodel.AuthViewModel
import com.example.tradetrack.viewmodel.TradeListViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable edge-to-edge UI
        enableEdgeToEdge()

        setContent {
            val systemTheme = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemTheme) }
            
            TradeTrackTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val listViewModel: TradeListViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Hide bottom bar on login, detail, and edit screens
                val showBottomBar = BottomNavItems.any { it.route == currentDestination?.route }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = if (isDarkMode) TradingDarkGrey else Color(0xFFF5F5F5),
                                contentColor = TradingBlue,
                                tonalElevation = 8.dp
                            ) {
                                BottomNavItems.forEach { screen ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = { 
                                            screen.icon?.let { 
                                                Icon(
                                                    it, 
                                                    contentDescription = null,
                                                    tint = if (selected) TradingBlue else Color.Gray 
                                                ) 
                                            } 
                                        },
                                        label = { 
                                            Text(
                                                screen.title,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (selected) TradingBlue else Color.Gray
                                            ) 
                                        },
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = TradingBlue.copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TradeTrackNavGraph(
                            navController = navController,
                            listViewModel = listViewModel,
                            authViewModel = authViewModel,
                            isDarkMode = isDarkMode,
                            onThemeChange = { isDarkMode = it }
                        )
                    }
                }
            }
        }
    }
}
