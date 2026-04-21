package com.example.tradetrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tradetrack.data.*
import com.example.tradetrack.repository.FirebaseTradeRepository
import com.example.tradetrack.util.ImageHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- UI state for Add/Edit screen ---
data class AddEditState(
    val pair: String = "",
    val type: TradeType = TradeType.BUY,
    val entryPrice: String = "",
    val exitPrice: String = "",
    val stopLoss: String = "",
    val takeProfit: String = "",
    val lotSize: String = "",
    val reason: String = "",
    val notes: String = "",
    val emotion: String = "",
    val strategy: String = "",
    val result: TradeResult = TradeResult.OPEN,
    val imagePath: String? = null,
    val date: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date()),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class AddEditTradeViewModel(
    private val application: Application,
    private val tradeId: String?
) : AndroidViewModel(application) {

    // Repositories
    private val repository = TradeRepository(application)
    private val statsRepository = UserStatsRepository(application)
    private val firebaseRepository = FirebaseTradeRepository()
    private val auth = FirebaseAuth.getInstance()

    // --- StateFlow for UI ---
    private val _state = MutableStateFlow(AddEditState())
    val state = _state.asStateFlow()

    val isEditMode: Boolean
        get() = tradeId != null

    init {
        // Load existing trade if editing
        tradeId?.let { id ->
            viewModelScope.launch {
                repository.getTradeById(id)?.let { trade ->
                    _state.update {
                        it.copy(
                            pair = trade.pair,
                            type = trade.type,
                            entryPrice = trade.entryPrice.toString(),
                            exitPrice = trade.exitPrice?.toString() ?: "",
                            stopLoss = trade.stopLoss?.toString() ?: "",
                            takeProfit = trade.takeProfit?.toString() ?: "",
                            lotSize = trade.lotSize?.toString() ?: "",
                            reason = trade.reason ?: "",
                            notes = trade.notes ?: "",
                            emotion = trade.emotion ?: "",
                            strategy = trade.strategy ?: "",
                            result = trade.result,
                            imagePath = trade.imagePath,
                            date = trade.date
                        )
                    }
                }
            }
        }
    }

    // --- State updates ---
    fun updatePair(value: String) = _state.update { it.copy(pair = value) }
    fun updateType(value: TradeType) = _state.update { it.copy(type = value) }
    fun updateEntryPrice(value: String) = _state.update { it.copy(entryPrice = value) }
    fun updateExitPrice(value: String) { _state.update { it.copy(exitPrice = value) }; validateOutcome() }
    fun updateStopLoss(value: String) = _state.update { it.copy(stopLoss = value) }
    fun updateTakeProfit(value: String) = _state.update { it.copy(takeProfit = value) }
    fun updateLotSize(value: String) = _state.update { it.copy(lotSize = value) }
    fun updateReason(value: String) = _state.update { it.copy(reason = value) }
    fun updateNotes(value: String) = _state.update { it.copy(notes = value) }
    fun updateEmotion(value: String) = _state.update { it.copy(emotion = value) }
    fun updateStrategy(value: String) = _state.update { it.copy(strategy = value) }
    fun updateResult(value: TradeResult) = _state.update { it.copy(result = value) }
    fun updateImagePath(path: String?) = _state.update { it.copy(imagePath = path) }
    fun clearSaveSuccess() = _state.update { it.copy(saveSuccess = false) }

    // --- Outcome validation ---
    private fun validateOutcome() {
        val s = _state.value
        val entry = s.entryPrice.toDoubleOrNull()
        val exit = s.exitPrice.toDoubleOrNull()
        val result = when {
            entry != null && exit != null -> when {
                exit == entry -> TradeResult.BREAK_EVEN
                s.type == TradeType.BUY && exit > entry -> TradeResult.WIN
                s.type == TradeType.BUY && exit < entry -> TradeResult.LOSS
                s.type == TradeType.SELL && exit < entry -> TradeResult.WIN
                s.type == TradeType.SELL && exit > entry -> TradeResult.LOSS
                else -> TradeResult.OPEN
            }
            else -> TradeResult.OPEN
        }
        _state.update { it.copy(result = result) }
    }

    // --- Remove attached image ---
    fun removeImage() {
        _state.value.imagePath?.let { ImageHelper.deleteImage(it) }
        _state.update { it.copy(imagePath = null) }
    }

    // --- Save trade to Room + Firestore ---
    fun saveTrade(onSaved: () -> Unit) {
        validateOutcome()
        val s = _state.value
        val pair = s.pair.trim()
        val entryStr = s.entryPrice.trim()
        if (pair.isBlank() || entryStr.isBlank()) return

        val entryPrice = entryStr.toDoubleOrNull() ?: return
        val exitPrice = s.exitPrice.trim().toDoubleOrNull()
        val stopLoss = s.stopLoss.trim().toDoubleOrNull()
        val takeProfit = s.takeProfit.trim().toDoubleOrNull()
        val lotSize = s.lotSize.trim().toDoubleOrNull()
        
        val currentUserId = auth.currentUser?.uid ?: return

        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val trade = Trade(
                id = tradeId ?: java.util.UUID.randomUUID().toString(),
                userId = currentUserId,
                pair = pair,
                type = s.type,
                entryPrice = entryPrice,
                exitPrice = exitPrice,
                stopLoss = stopLoss,
                takeProfit = takeProfit,
                lotSize = lotSize,
                reason = s.reason.trim().takeIf { it.isNotBlank() },
                imagePath = s.imagePath,
                date = s.date,
                result = s.result,
                notes = s.notes.trim().takeIf { it.isNotBlank() },
                emotion = s.emotion.trim().takeIf { it.isNotBlank() },
                strategy = s.strategy.trim().takeIf { it.isNotBlank() }
            )

            // 1️⃣ Save to Room
            if (tradeId != null) {
                repository.update(trade)
            } else {
                repository.insert(trade)
                // Gamification: Process XP and achievements only for new trades
                statsRepository.processTradeLogged(currentUserId, trade)
            }

            // 2️⃣ Save to Firestore
            try {
                firebaseRepository.addTrade(trade)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _state.update { it.copy(isSaving = false, saveSuccess = true) }
            onSaved()
        }
    }
}
