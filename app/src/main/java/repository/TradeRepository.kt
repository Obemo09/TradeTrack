package com.example.tradetrack.repository

import com.example.tradetrack.data.Trade
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val tradesCollection = db.collection("trades")

    // Add a trade and return its generated Firestore ID
    suspend fun addTrade(trade: Trade): String {
        val docRef = tradesCollection.add(trade).await()
        return docRef.id
    }

    // Get all trades with Firestore IDs assigned to each Trade
    suspend fun getTrades(): List<Trade> {
        val snapshot = tradesCollection.get().await()
        // Map each document to Trade, storing the Firestore document ID in Trade.id
        return snapshot.documents.map { doc ->
            val trade = doc.toObject(Trade::class.java)!!
            trade.copy(id = doc.id) // id must be a String now
        }
    }

    // Get a trade by its Firestore document ID
    suspend fun getTradeById(id: String): Trade? {
        val doc = tradesCollection.document(id).get().await()
        return doc.toObject(Trade::class.java)?.copy(id = doc.id)
    }

    // Delete a trade by Firestore document ID
    suspend fun deleteTrade(id: String) {
        tradesCollection.document(id).delete().await()
    }

    // Update a trade by Firestore document ID
    suspend fun updateTrade(id: String, trade: Trade) {
        tradesCollection.document(id).set(trade).await()
    }
}