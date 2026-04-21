package com.example.tradetrack.ui.animations

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*

/**
 * Empty state composable with optional Lottie animation
 * Shows when no trades or data is available
 */
@Composable
fun EmptyStateAnimation(
    title: String = "No Trades Yet",
    subtitle: String = "Start logging your trades to see analytics",
    modifier: Modifier = Modifier,
    lottieResource: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder animated icon since we don't have Lottie files
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Success message animation with celebration effect
 * Shows brief success feedback after actions
 * Duration: 2 seconds
 */
@Composable
fun SuccessMessageAnimation(
    message: String = "Success!",
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    autoDismissMs: Long = 2000
) {
    var isVisible by remember { mutableStateOf(true) }

    // Auto-dismiss after specified duration
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(autoDismissMs)
        isVisible = false
        onDismiss()
    }

    if (!isVisible) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓ $message",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF10B981),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

/**
 * Level up celebration animation
 * Shows when user reaches a new trading level
 * Features: Scale animation + success message
 */
@Composable
fun LevelUpAnimation(
    currentLevel: String = "Advanced",
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(true) }

    // Auto-dismiss after 3 seconds
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        isVisible = false
        onDismiss()
    }

    if (!isVisible) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Celebration emoji animation
        Text(
            text = "🎉",
            fontSize = 64.sp,
            modifier = Modifier
                .padding(16.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "LEVEL UP!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFF59E0B),
            fontWeight = FontWeight.Black,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "You've reached $currentLevel",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Animated loading indicator with pulsing effect
 * Used during data fetch or processing
 */
@Composable
fun AnimatedLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Pulsing circle animation
        Box(
            modifier = Modifier
                .size(60.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Animated check mark for form validation
 * Shows when user completes a field correctly
 */
@Composable
fun CheckMarkAnimation(
    modifier: Modifier = Modifier,
    size: Int = 60
) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimating = true
    }

    if (isAnimating) {
        Text(
            text = "✓",
            fontSize = size.sp,
            color = Color(0xFF10B981),
            fontWeight = FontWeight.Black,
            modifier = modifier
                .size(size.dp)
                .wrapContentSize(Alignment.Center)
        )
    }
}

/**
 * Animated error icon with shake effect
 * Shows when validation fails
 */
@Composable
fun ErrorAnimation(
    message: String = "Error",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠",
            fontSize = 40.sp,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFEF4444),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Animated counter that increments from 0 to target
 * Useful for displaying achievements or milestones
 */
@Composable
fun CounterAnimation(
    targetValue: Int,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    durationMillis: Int = 1500
) {
    var animatedValue by remember { mutableIntStateOf(0) }

    LaunchedEffect(targetValue) {
        animatedValue = 0
        val startTime = System.currentTimeMillis()
        val endTime = startTime + durationMillis

        while (System.currentTimeMillis() < endTime) {
            val elapsed = System.currentTimeMillis() - startTime
            animatedValue = ((elapsed * targetValue) / durationMillis).toInt()
        }
        animatedValue = targetValue
    }

    Text(
        text = "$prefix$animatedValue$suffix",
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Black
    )
}

/**
 * Animated progress bar with smooth transition
 * Shows progress of any operation
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        androidx.compose.material3.LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
