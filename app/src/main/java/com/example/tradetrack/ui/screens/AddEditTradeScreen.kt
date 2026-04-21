package com.example.tradetrack.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tradetrack.R
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.util.ImageHelper
import com.example.tradetrack.viewmodel.AddEditTradeViewModel
import com.example.tradetrack.ui.animations.buttonScaleFadeAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTradeScreen(
    viewModel: AddEditTradeViewModel,
    onBack: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.updateImagePath(ImageHelper.saveImageFromUri(context, it)) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { viewModel.updateImagePath(ImageHelper.saveTradeImage(context, it)) }
    }

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
                            if (viewModel.isEditMode) "EDIT TRADE" else "LOG NEW TRADE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle Background Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(TradingBlue.copy(alpha = 0.05f), Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Asset & Type Card
                SectionCard(title = "TRADE IDENTITY", icon = Icons.Default.Category) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        ModernTextField(
                            value = state.pair,
                            onValueChange = viewModel::updatePair,
                            label = "ASSET PAIR",
                            placeholder = "BTCUSD",
                            modifier = Modifier.weight(1f)
                        )
                        
                        TradeTypeSelector(
                            selectedType = state.type,
                            onTypeChange = viewModel::updateType
                        )
                    }
                }

                // Execution Details Card (Lot size added)
                SectionCard(title = "EXECUTION DETAILS", icon = Icons.Default.PrecisionManufacturing) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ModernTextField(
                                value = state.lotSize,
                                onValueChange = viewModel::updateLotSize,
                                label = "LOT SIZE",
                                placeholder = "0.01",
                                modifier = Modifier.weight(1f),
                                leadingIcon = Icons.Default.Straighten
                            )
                            ModernTextField(
                                value = state.entryPrice,
                                onValueChange = viewModel::updateEntryPrice,
                                label = "ENTRY PRICE",
                                modifier = Modifier.weight(1f),
                                leadingIcon = Icons.AutoMirrored.Filled.Login
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ModernTextField(
                                value = state.exitPrice,
                                onValueChange = viewModel::updateExitPrice,
                                label = "EXIT PRICE",
                                modifier = Modifier.weight(1f),
                                leadingIcon = Icons.AutoMirrored.Filled.Logout
                            )
                            ModernTextField(
                                value = state.stopLoss,
                                onValueChange = viewModel::updateStopLoss,
                                label = "STOP LOSS",
                                modifier = Modifier.weight(1f),
                                color = TradingRed
                            )
                        }
                        ModernTextField(
                            value = state.takeProfit,
                            onValueChange = viewModel::updateTakeProfit,
                            label = "TAKE PROFIT",
                            modifier = Modifier.fillMaxWidth(),
                            color = TradingGreen
                        )
                    }
                }

                // Outcome & Strategy
                SectionCard(title = "OUTCOME & STRATEGY", icon = Icons.Default.AutoFixHigh) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutcomeChip(TradeResult.WIN, "WIN", TradingGreen, state.result == TradeResult.WIN, Modifier.weight(1f)) { viewModel.updateResult(it) }
                            OutcomeChip(TradeResult.LOSS, "LOSS", TradingRed, state.result == TradeResult.LOSS, Modifier.weight(1f)) { viewModel.updateResult(it) }
                            OutcomeChip(TradeResult.BREAK_EVEN, "B/E", TradingOrange, state.result == TradeResult.BREAK_EVEN, Modifier.weight(1f)) { viewModel.updateResult(it) }
                        }
                        ModernTextField(
                            value = state.strategy,
                            onValueChange = viewModel::updateStrategy,
                            label = "STRATEGY USED",
                            placeholder = "e.g. SMC, Trendline Breakout"
                        )
                    }
                }

                // Evidence (Screenshot)
                SectionCard(title = "VISUAL EVIDENCE", icon = Icons.Default.Collections) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImagePickerButton(Icons.Default.AddAPhoto, "Camera") { cameraLauncher.launch(null) }
                        ImagePickerButton(Icons.Default.PhotoLibrary, "Gallery") { galleryLauncher.launch("image/*") }
                        
                        if (state.imagePath != null) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .shadow(4.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data(state.imagePath).build(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(20.dp)
                                        .clickable { viewModel.removeImage() },
                                    color = TradingRed,
                                    shape = CircleShape
                                ) {
                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.saveTrade(onSaved = onSaveComplete) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = TradingBlue)
                        .buttonScaleFadeAnimation(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TradingBlue,
                        contentColor = Color.White
                    ),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("SAVE TRADE ENTRY", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }
                
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SectionCard(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = TradingBlue, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                title, 
                style = MaterialTheme.typography.labelMedium, 
                color = TradingBlue, 
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    color: Color = TradingBlue
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label, 
            style = MaterialTheme.typography.labelSmall, 
            color = TradingTextSecondary, 
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TradingTextSecondary.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = leadingIcon?.let { { Icon(it, null, tint = color.copy(alpha = 0.6f)) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = color.copy(alpha = 0.02f),
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun OutcomeChip(result: TradeResult, label: String, color: Color, isSelected: Boolean, modifier: Modifier = Modifier, onClick: (TradeResult) -> Unit) {
    val alpha by animateFloatAsState(if (isSelected) 1f else 0.1f)
    val textColor = if (isSelected) Color.White else color

    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { onClick(result) }
            ),
        color = color.copy(alpha = alpha),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = textColor
            )
        }
    }
}

@Composable
fun TradeTypeSelector(selectedType: TradeType, onTypeChange: (TradeType) -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        TypeButton(TradeType.BUY, "BUY", TradingGreen, selectedType == TradeType.BUY) { onTypeChange(it) }
        TypeButton(TradeType.SELL, "SELL", TradingRed, selectedType == TradeType.SELL) { onTypeChange(it) }
    }
}

@Composable
fun TypeButton(type: TradeType, label: String, color: Color, isSelected: Boolean, onClick: (TradeType) -> Unit) {
    val bgAlpha by animateFloatAsState(if (isSelected) 1f else 0f)
    
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { onClick(type) }
            ),
        color = color.copy(alpha = bgAlpha),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label, 
                style = MaterialTheme.typography.labelMedium, 
                fontWeight = FontWeight.Black, 
                color = if (isSelected) Color.White else color
            )
        }
    }
}

@Composable
fun ImagePickerButton(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = desc, tint = TradingBlue)
        }
    }
}
