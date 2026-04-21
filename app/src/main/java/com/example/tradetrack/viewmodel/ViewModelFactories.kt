package com.example.tradetrack.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry

/**
 * Factory to create TradeDetailViewModel with application and tradeId
 */
fun tradeDetailViewModelFactory(
    application: Application,
    tradeId: String
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TradeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TradeDetailViewModel(application, tradeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Factory to create AddEditTradeViewModel with application and tradeId
 */
fun addEditTradeViewModelFactory(
    application: Application,
    tradeId: String?
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditTradeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditTradeViewModel(application, tradeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
