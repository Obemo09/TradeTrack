# TradeTrack Micro-Interactions: Quick Reference

## Quick Copy-Paste Code Snippets

### 1. Button with Press Animation

```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .buttonScaleFadeAnimation()
) {
    Text("CLICK ME")
}
```

**Result**: Button scales to 93% and fades to 80% alpha on press (200ms)

---

### 2. Card with Ripple Effect

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .cardRippleEffect(rippleColor = Color.White.copy(alpha = 0.2f))
        .clickable { /* action */ }
) {
    Text("Tap me for ripple!")
}
```

**Result**: Material Design 3 ripple animation on tap

---

### 3. Floating Action Button with Pulse

```kotlin
FloatingActionButton(
    onClick = { /* action */ },
    modifier = Modifier.fabPulseAnimation()
) {
    Icon(Icons.Default.Add, contentDescription = null)
}
```

**Result**: Continuous breathing effect (1.08x scale + alpha pulse)

---

### 4. Clickable Item with Slide Animation

```kotlin
Surface(
    modifier = Modifier
        .fillMaxWidth()
        .clickableSlideAnimation()
        .combinedClickable(
            onClick = { /* action */ },
            onLongClick = { /* long press action */ }
        )
) {
    Text("Tap to slide up!")
}
```

**Result**: Slides up 4dp on press with spring physics

---

### 5. Success Message with Auto-Dismiss

```kotlin
var showSuccess by remember { mutableStateOf(false) }

if (showSuccess) {
    SuccessMessageAnimation(
        message = "Trade saved successfully!",
        onDismiss = { showSuccess = false },
        autoDismissMs = 2000
    )
}

// Trigger success
Button(
    onClick = {
        saveTrade()
        showSuccess = true
    }
)
```

**Result**: "✓ Trade saved successfully!" shows for 2 seconds then auto-dismisses

---

### 6. Level Up Celebration

```kotlin
var showLevelUp by remember { mutableStateOf(false) }

if (showLevelUp) {
    LevelUpAnimation(
        currentLevel = "Advanced Trader",
        onDismiss = { showLevelUp = false }
    )
}

// Trigger when level increases
if (newLevelUnlocked) {
    showLevelUp = true
}
```

**Result**: Large "🎉 LEVEL UP! You've reached Advanced Trader" celebration for 3 seconds

---

### 7. Empty State with Icon

```kotlin
if (trades.isEmpty()) {
    EmptyStateAnimation(
        title = "No Trades Yet",
        subtitle = "Start logging your trades to see analytics",
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    )
}
```

**Result**: Centered icon with title and subtitle for empty state

---

### 8. Loading Indicator with Message

```kotlin
if (isLoading) {
    AnimatedLoadingIndicator(
        message = "Saving your trade...",
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    )
}
```

**Result**: Animated spinner with message

---

### 9. Error Display with Icon

```kotlin
if (error != null) {
    ErrorAnimation(
        message = error,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
```

**Result**: "⚠ Error" text display

---

### 10. Animated Counter

```kotlin
val targetTrades = 150

AnimatedCounter(
    targetValue = targetTrades,
    durationMillis = 1500,
    style = MaterialTheme.typography.headlineLarge,
    color = TradingBlue,
    fontWeight = FontWeight.Black
)
```

**Result**: Numbers animate from 0 to 150 over 1.5 seconds

---

### 11. Animated Progress Bar

```kotlin
var progress by remember { mutableStateOf(0.6f) }

AnimatedProgressBar(
    progress = progress,
    label = "Win Rate",
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
)
```

**Result**: Animated progress bar with "Win Rate: 60%" label

---

### 12. Icon Button with Rotation

```kotlin
IconButton(
    onClick = { /* action */ },
    modifier = Modifier.iconButtonRotateAnimation()
) {
    Icon(Icons.Default.Settings, contentDescription = "Settings")
}
```

**Result**: Icon rotates 10 degrees on press

---

### 13. Bounce Click Animation

```kotlin
Surface(
    modifier = Modifier
        .fillMaxWidth()
        .bounceClickAnimation()
        .clickable { /* action */ }
) {
    Text("Bounce me!")
}
```

**Result**: Spring-based bounce effect on press (97% scale)

---

### 14. Loading Pulse on Button

```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .loadingPulseAnimation()
) {
    Text("Pulsing...")
}
```

**Result**: Button fades in and out continuously (60%-100% alpha)

---

### 15. Error Shake Animation

```kotlin
if (validationFailed) {
    Text(
        "Invalid input",
        modifier = Modifier.shakeAnimation()
    )
}
```

**Result**: Text oscillates left/right for error feedback

---

## Animation Timing Reference

```kotlin
// Fast button feedback
150-200ms with spring animation

// Card/container interactions  
250-300ms with ripple effect

// Screen transitions
300-400ms with fade + slide

// FAB continuous effect
1500ms infinite repeat

// Success/error messages
2000-3000ms auto-dismiss
```

---

## Import Requirements

Add these imports to your screen file:

```kotlin
import com.example.tradetrack.ui.animations.buttonScaleFadeAnimation
import com.example.tradetrack.ui.animations.cardRippleEffect
import com.example.tradetrack.ui.animations.fabPulseAnimation
import com.example.tradetrack.ui.animations.clickableSlideAnimation
import com.example.tradetrack.ui.animations.buttonPressAnimation
import com.example.tradetrack.ui.animations.iconButtonRotateAnimation
import com.example.tradetrack.ui.animations.bounceClickAnimation
import com.example.tradetrack.ui.animations.loadingPulseAnimation
import com.example.tradetrack.ui.animations.shakeAnimation

import com.example.tradetrack.ui.animations.EmptyStateAnimation
import com.example.tradetrack.ui.animations.SuccessMessageAnimation
import com.example.tradetrack.ui.animations.LevelUpAnimation
import com.example.tradetrack.ui.animations.AnimatedLoadingIndicator
import com.example.tradetrack.ui.animations.CheckMarkAnimation
import com.example.tradetrack.ui.animations.ErrorAnimation
import com.example.tradetrack.ui.animations.CounterAnimation
import com.example.tradetrack.ui.animations.AnimatedProgressBar
```

---

## Common Patterns

### Pattern 1: Save Button Flow
```kotlin
var isSaving by remember { mutableStateOf(false) }
var showSuccess by remember { mutableStateOf(false) }

if (showSuccess) {
    SuccessMessageAnimation(
        message = "Saved!",
        onDismiss = { showSuccess = false }
    )
}

Button(
    onClick = {
        isSaving = true
        saveTrade {
            isSaving = false
            showSuccess = true
        }
    },
    enabled = !isSaving,
    modifier = Modifier.buttonScaleFadeAnimation()
) {
    if (isSaving) {
        CircularProgressIndicator(Modifier.size(24.dp))
    } else {
        Text("SAVE")
    }
}
```

### Pattern 2: Clickable List Item
```kotlin
Surface(
    modifier = Modifier
        .fillMaxWidth()
        .clickableSlideAnimation()
        .combinedClickable(
            onClick = { navigateToDetail(item) },
            onLongClick = { showDeleteDialog = true }
        )
) {
    Row(Modifier.padding(16.dp)) {
        // Item content
    }
}
```

### Pattern 3: Empty State
```kotlin
if (items.isEmpty()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyStateAnimation(
            title = "No Items",
            subtitle = "Add your first item to get started"
        )
    }
} else {
    // Show content
}
```

### Pattern 4: Stats Display
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .cardRippleEffect()
            .clickable { navigateToStats() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("WIN RATE")
            AnimatedCounter(
                targetValue = winRate,
                durationMillis = 1500
            )
            Text("%")
        }
    }
    // Second card
}
```

---

## Troubleshooting

### Animation not showing?
1. Check imports are correct
2. Verify Modifier chain order (animation should be before clickable)
3. Make sure recomposition is happening

### Animation too fast/slow?
1. Check duration parameters
2. Adjust spring stiffness/damping values
3. Test on actual device

### Ripple not visible?
1. Adjust ripple color alpha
2. Ensure Surface or Card has proper background
3. Check click area is touchable

### Memory issues?
1. Avoid creating new Modifier instances in loops
2. Use `remember` for state
3. Profile with Android Profiler

---

## Performance Tips

1. **Keep animations short** (150-400ms for interactions)
2. **Use spring animations** for natural feel
3. **Avoid heavy computations** in animation blocks
4. **Test on low-end devices** to ensure smooth 60fps
5. **Use graphicsLayer** for transforms (scale, rotation, alpha)

---

## Additional Resources

- View full documentation: `MICRO_INTERACTIONS_GUIDE.md`
- Implementation details: `IMPLEMENTATION_SUMMARY.md`
- Animation source code: `AnimationUtils.kt`, `LottieAnimations.kt`

---

**Happy animating! 🎨✨**

