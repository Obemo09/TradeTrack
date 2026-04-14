package com.example.tradetrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tradetrack.data.Trade
import com.example.tradetrack.data.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TradeDetailViewModel(
    application: Application,
    private val tradeId: String
) : AndroidViewModel(application) {

    private val repository = TradeRepository(application)

    private val _trade = MutableStateFlow<Trade?>(null)
    val trade = _trade.asStateFlow()

    init {
        viewModelScope.launch {
            _trade.value = repository.getTradeById(tradeId)
        }
    }

    fun deleteTrade(trade: Trade) {
        viewModelScope.launch {
            repository.delete(trade)
        }
    }
}
