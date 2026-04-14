package com.example.tradetrack.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tradetrack.data.Trade
import com.example.tradetrack.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    trades: List<Trade>,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    
    var notificationsEnabled by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SETTINGS",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Section
            ProfileHeader(
                name = currentUser?.displayName ?: "Pro Trader",
                tradeCount = trades.size
            )

            // Settings Groups
            SettingsGroup(title = "PREFERENCES") {
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Toggle app theme",
                    checked = isDarkMode,
                    onCheckedChange = onThemeChange
                )
                SettingsToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Receive trade alerts",
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        if (it) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                notificationsEnabled = true
                            }
                        } else {
                            notificationsEnabled = false
                        }
                    }
                )
            }

            SettingsGroup(title = "ACCOUNT") {
                SettingsClickItem(icon = Icons.Default.Person, title = "Edit Profile")
                SettingsClickItem(icon = Icons.Default.Security, title = "Privacy & Security")
            }

            SettingsGroup(title = "ABOUT") {
                SettingsClickItem(icon = Icons.Default.Info, title = "App Version", subtitle = "1.0.0")
                SettingsClickItem(icon = Icons.Default.Star, title = "Rate TradeTrack")
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TradingRed.copy(alpha = 0.1f), contentColor = TradingRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("LOGOUT", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, tradeCount: Int) {
    val initial = if (name.isNotBlank()) name.take(1).uppercase() else "T"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(TradingBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text("Pro Trader • $tradeCount Entries", style = MaterialTheme.typography.labelMedium, color = TradingTextSecondary)
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = TradingBlue, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = TradingBlue, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TradingTextSecondary)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = TradingBlue, checkedTrackColor = TradingBlue.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun SettingsClickItem(icon: ImageVector, title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TradingBlue, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TradingTextSecondary)
            }
        }
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TradingTextSecondary)
    }
}
