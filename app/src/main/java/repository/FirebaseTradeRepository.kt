package com.example.tradetrack.repository

import com.example.tradetrack.data.Trade
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseTradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val tradesCollection = db.collection("trades")
    private val auth = FirebaseAuth.getInstance()

    /** 
     * Add or update a trade in Firestore.
     * We use the trade's UUID as the document ID for consistency.
     */
    suspend fun addTrade(trade: Trade): String {
        tradesCollection.document(trade.id).set(trade).await()
        return trade.id
    }

    suspend fun getTrades(): List<Trade> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val snapshot = tradesCollection
            .whereEqualTo("userId", uid)
            .get().await()
        return snapshot.documents.mapNotNull { it.toObject(Trade::class.java) }
    }

    suspend fun deleteTradeById(id: String) {
        tradesCollection.document(id).delete().await()
    }
}
