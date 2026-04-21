package com.example.tradetrack.ui.components

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.edit
import com.example.tradetrack.R
import com.example.tradetrack.ui.theme.TradingBlue

data class ShowcaseStep(
    val id: String,
    val title: String,
    val message: String,
    val targetKey: String
)

class ShowcaseState {
    var currentStepIndex by mutableIntStateOf(-1)
    val targets = mutableStateMapOf<String, Rect>()
    var isVisible by mutableStateOf(false)
    var steps = listOf<ShowcaseStep>()

    fun start(steps: List<ShowcaseStep>) {
        if (steps.isEmpty()) return
        this.steps = steps
        currentStepIndex = 0
        isVisible = true
    }

    fun next() {
        if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
        } else {
            finish()
        }
    }

    fun finish() {
        isVisible = false
        currentStepIndex = -1
    }
}

val LocalShowcaseState = staticCompositionLocalOf { ShowcaseState() }

@Composable
fun ShowcaseHost(
    state: ShowcaseState,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalShowcaseState provides state) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            if (state.isVisible && state.currentStepIndex >= 0) {
                val step = state.steps.getOrNull(state.currentStepIndex)
                if (step != null) {
                    val targetRect = state.targets[step.targetKey]
                    val isLastStep = state.currentStepIndex == state.steps.size - 1
                    
                    ShowcaseOverlay(
                        targetRect = targetRect,
                        step = step,
                        isLastStep = isLastStep,
                        onNext = { state.next() },
                        onSkip = { state.finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun ShowcaseOverlay(
    targetRect: Rect?,
    step: ShowcaseStep,
    isLastStep: Boolean,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val density = LocalDensity.current
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = constraints.maxHeight.toFloat()
        val screenWidth = constraints.maxWidth.toFloat()
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNext() }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            ) {
                drawRect(color = Color.Black.copy(alpha = 0.85f))
                
                if (targetRect != null && targetRect.center.isSpecified && !targetRect.center.x.isNaN()) {
                    val radius = (targetRect.maxDimension / 1.5f).coerceIn(40f, 300f)
                    drawCircle(
                        color = Color.Transparent,
                        radius = radius,
                        center = targetRect.center,
                        blendMode = BlendMode.Clear
                    )
                }
            }

            val center = targetRect?.center ?: Offset(screenWidth / 2, screenHeight / 2)
            val isTooltipBelow = if (!center.y.isNaN()) {
                center.y < screenHeight / 2
            } else true
            
            val topPadding = if (isTooltipBelow && targetRect != null) {
                with(density) { (targetRect.bottom + 40f).toDp() }
            } else if (isTooltipBelow) 100.dp else 0.dp
            
            val bottomPadding = if (!isTooltipBelow && targetRect != null) {
                with(density) { (screenHeight - targetRect.top + 40f).toDp() }
            } else if (!isTooltipBelow) 100.dp else 0.dp

            Column(
                modifier = Modifier
                    .align(if (isTooltipBelow) Alignment.TopCenter else Alignment.BottomCenter)
                    .padding(horizontal = 32.dp)
                    .padding(top = topPadding, bottom = bottomPadding)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .clickable(enabled = false) {},
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = step.title.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = TradingBlue,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = step.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onSkip) {
                        Text("Skip Tour", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TradingBlue)
                    ) {
                        Text(if (isLastStep) "Finish" else "Next", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TourPromptDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
        },
        title = { Text("Welcome to TradeTrack", fontWeight = FontWeight.Black) },
        text = { Text("Would you like a brief guide through the key features to help you get started?") },
        confirmButton = {
            Button(
                onClick = onAccept, 
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TradingBlue)
            ) {
                Text("Start Tour")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later", color = Color.Gray)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp)
    )
}

fun Modifier.showcaseTarget(key: String): Modifier = composed {
    val state = LocalShowcaseState.current
    onGloballyPositioned { coordinates ->
        if (coordinates.isAttached) {
            val position = coordinates.positionInRoot()
            val size = coordinates.size.toSize()
            if (position.isSpecified && !position.x.isNaN() && !position.y.isNaN()) {
                state.targets[key] = Rect(position, size)
            }
        }
    }
}

fun isTourFinished(context: Context, userId: String?): Boolean {
    val sharedPref = context.getSharedPreferences("tradetrack_prefs", Context.MODE_PRIVATE)
    val key = if (userId != null) "isTourFinished_v5_$userId" else "isTourFinished_v5_guest"
    return sharedPref.getBoolean(key, false)
}

fun saveTourFinished(context: Context, userId: String?) {
    val sharedPref = context.getSharedPreferences("tradetrack_prefs", Context.MODE_PRIVATE)
    val key = if (userId != null) "isTourFinished_v5_$userId" else "isTourFinished_v5_guest"
    sharedPref.edit {
        putBoolean(key, true)
    }
}
