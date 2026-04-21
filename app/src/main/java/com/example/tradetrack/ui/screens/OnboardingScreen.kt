package com.example.tradetrack.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.tradetrack.R
import com.example.tradetrack.ui.theme.TradingBlue
import com.example.tradetrack.ui.theme.TradingTextSecondary
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Track Every Trade",
        description = "Log your entries, exits, and lot sizes with ease. Build a disciplined trading history.",
        icon = Icons.Default.History,
        gradient = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
    ),
    OnboardingPage(
        title = "Master Analytics",
        description = "Visualize your performance with advanced charts and smart insights to improve your edge.",
        icon = Icons.Default.Analytics,
        gradient = listOf(Color(0xFF06B6D4), Color(0xFF0891B2))
    ),
    OnboardingPage(
        title = "Level Up Your Game",
        description = "Earn XP, complete achievements, and maintain streaks. Gamify your path to pro trading.",
        icon = Icons.Default.EmojiEvents,
        gradient = listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header with Logo and Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "TradeTrack",
                    fontWeight = FontWeight.Bold,
                    color = TradingBlue,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
            }

            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(onClick = { 
                    saveOnboardingFinished(context)
                    onFinished() 
                }) {
                    Text("Skip", color = TradingTextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { position ->
            OnboardingPagerItem(page = onboardingPages[position])
        }

        // Bottom Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Page Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(onboardingPages.size) { index ->
                    val color = if (pagerState.currentPage == index) TradingBlue else TradingTextSecondary.copy(alpha = 0.3f)
                    val width = if (pagerState.currentPage == index) 24.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Next / Get Started Button
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        saveOnboardingFinished(context)
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TradingBlue)
            ) {
                Text(
                    text = if (pagerState.currentPage == onboardingPages.size - 1) "Get Started" else "Next",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                }
            }
            
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun OnboardingPagerItem(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(page.gradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
        }
        
        Spacer(Modifier.height(48.dp))
        
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TradingTextSecondary,
            lineHeight = 24.sp
        )
    }
}

fun saveOnboardingFinished(context: Context) {
    val sharedPref = context.getSharedPreferences("tradetrack_prefs", Context.MODE_PRIVATE)
    sharedPref.edit {
        putBoolean("isFirstTime", false)
    }
}

fun isOnboardingFinished(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("tradetrack_prefs", Context.MODE_PRIVATE)
    return !sharedPref.getBoolean("isFirstTime", true)
}
