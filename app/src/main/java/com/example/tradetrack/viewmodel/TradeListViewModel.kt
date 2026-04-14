package com.example.tradetrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tradetrack.data.*
import com.example.tradetrack.repository.FirebaseTradeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TradeListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradeRepository(application)
    private val firebaseRepository = FirebaseTradeRepository()
    private val auth = FirebaseAuth.getInstance()

    // Observe current user ID to filter trades
    private val userId = MutableStateFlow(auth.currentUser?.uid)

    @OptIn(ExperimentalCoroutinesApi::class)
    val trades: StateFlow<List<Trade>> = userId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getAllTrades(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Update userId when auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            userId.value = firebaseAuth.currentUser?.uid
        }
    }

    fun deleteTrade(trade: Trade) {
        viewModelScope.launch {
            try {
                repository.delete(trade)
                firebaseRepository.deleteTradeById(trade.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
