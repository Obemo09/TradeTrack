package com.example.tradetrack.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tradetrack.data.TradeResult
import com.example.tradetrack.data.TradeType
import com.example.tradetrack.ui.theme.*
import com.example.tradetrack.util.ImageHelper
import com.example.tradetrack.viewmodel.AddEditState
import com.example.tradetrack.viewmodel.AddEditTradeViewModel

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
        containerColor = TradingBlack,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (viewModel.isEditMode) "EDIT ENTRY" else "NEW ENTRY",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TradingTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TradingBlack,
                    titleContentColor = TradingTextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(TradingBlack)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Pair & Type Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModernTextField(
                    value = state.pair,
                    onValueChange = viewModel::updatePair,
                    label = "INSTRUMENT",
                    placeholder = "e.g. EURUSD",
                    modifier = Modifier.weight(1f)
                )
                
                TradeTypeSelector(
                    selectedType = state.type,
                    onTypeChange = viewModel::updateType
                )
            }

            // Prices Section
            Card(
                colors = CardDefaults.cardColors(containerColor = TradingDarkGrey),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, TradingLightGrey)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModernTextField(
                            value = state.entryPrice,
                            onValueChange = viewModel::updateEntryPrice,
                            label = "ENTRY",
                            modifier = Modifier.weight(1f)
                        )
                        ModernTextField(
                            value = state.exitPrice,
                            onValueChange = viewModel::updateExitPrice,
                            label = "EXIT",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModernTextField(
                            value = state.stopLoss,
                            onValueChange = viewModel::updateStopLoss,
                            label = "SL",
                            modifier = Modifier.weight(1f)
                        )
                        ModernTextField(
                            value = state.takeProfit,
                            onValueChange = viewModel::updateTakeProfit,
                            label = "TP",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Result Selection
            Text("OUTCOME", style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutcomeChip(TradeResult.WIN, "WIN", TradingGreen, state.result == TradeResult.WIN) { viewModel.updateResult(it) }
                OutcomeChip(TradeResult.LOSS, "LOSS", TradingRed, state.result == TradeResult.LOSS) { viewModel.updateResult(it) }
                OutcomeChip(TradeResult.BREAK_EVEN, "B/E", TradingTextSecondary, state.result == TradeResult.BREAK_EVEN) { viewModel.updateResult(it) }
            }

            // Reason & Notes
            ModernTextField(
                value = state.reason,
                onValueChange = viewModel::updateReason,
                label = "CONFLUENCE / REASONING",
                minLines = 3
            )

            // Image Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("SCREENSHOT", style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ImagePickerButton(Icons.Default.CameraAlt, "Camera") { cameraLauncher.launch(null) }
                    ImagePickerButton(Icons.Default.Image, "Gallery") { galleryLauncher.launch("image/*") }
                    
                    if (state.imagePath != null) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TradingDarkGrey)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(state.imagePath).build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = viewModel::removeImage,
                                modifier = Modifier.size(20.dp).align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp).background(TradingRed, CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.saveTrade(onSaved = onSaveComplete) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TradingBlue, contentColor = TradingBlack),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(Modifier.size(24.dp), color = TradingBlack)
                } else {
                    Text("LOG TRADE", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                }
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
    minLines: Int = 1
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TradingTextSecondary.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TradingDarkGrey,
                unfocusedContainerColor = TradingDarkGrey,
                focusedTextColor = TradingTextPrimary,
                unfocusedTextColor = TradingTextPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            minLines = minLines
        )
    }
}

@Composable
fun OutcomeChip(result: TradeResult, label: String, color: Color, isSelected: Boolean, onClick: (TradeResult) -> Unit) {
    Surface(
        modifier = Modifier.height(48.dp).padding(vertical = 4.dp).clickable { onClick(result) },
        color = if (isSelected) color else TradingDarkGrey,
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) null else BorderStroke(1.dp, TradingLightGrey)
    ) {
        Box(modifier = Modifier.padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TradingBlack else TradingTextPrimary
            )
        }
    }
}

@Composable
fun TradeTypeSelector(selectedType: TradeType, onTypeChange: (TradeType) -> Unit) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(TradingDarkGrey).padding(4.dp)
    ) {
        TypeButton(TradeType.BUY, "BUY", TradingGreen, selectedType == TradeType.BUY) { onTypeChange(it) }
        TypeButton(TradeType.SELL, "SELL", TradingRed, selectedType == TradeType.SELL) { onTypeChange(it) }
    }
}

@Composable
fun TypeButton(type: TradeType, label: String, color: Color, isSelected: Boolean, onClick: (TradeType) -> Unit) {
    Surface(
        modifier = Modifier.height(40.dp).clickable { onClick(type) },
        color = if (isSelected) color else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = if (isSelected) TradingBlack else color)
        }
    }
}

@Composable
fun ImagePickerButton(icon: ImageVector, desc: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp).background(TradingDarkGrey, RoundedCornerShape(12.dp)).border(BorderStroke(1.dp, TradingLightGrey), RoundedCornerShape(12.dp))
    ) {
        Icon(icon, contentDescription = desc, tint = TradingTextSecondary)
    }
}
