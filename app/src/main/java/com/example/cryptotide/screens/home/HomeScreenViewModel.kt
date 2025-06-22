package com.example.cryptotide.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.Cryptocurrency
import com.example.cryptotide.model.service.impl.RetrofitInstance
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : CryptoTideAppViewModel() {
    private val uid = Firebase.auth.currentUser?.uid ?: ""

    var allCoins by mutableStateOf<List<Cryptocurrency>>(emptyList())
        private set

    var allFavoriteCoinIds by mutableStateOf<List<String>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    val coins: List<Cryptocurrency>
        get() = if (searchQuery.isBlank()) allCoins
        else allCoins.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.symbol.contains(searchQuery, ignoreCase = true)
        }

    val favoriteCoins: List<Cryptocurrency>
        get() = allCoins.filter { coin -> allFavoriteCoinIds.contains(coin.id) }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    init {
        fetchCryptocurrencies()
        initFavoritesDocument()
    }

    private fun fetchCryptocurrencies() {
        launchCatching {
            val result = RetrofitInstance.api.getCryptocurrencies()
            Log.d("all coins", result.toString())
            allCoins = result
        }
    }

    fun addFavoriteCoin(coinId: String) {
        Firebase.firestore.collection("favorites")
            .document(uid)
            .update("coins", FieldValue.arrayUnion(coinId))
            .addOnSuccessListener {
                allFavoriteCoinIds = allFavoriteCoinIds + coinId
                Log.d("Favorites", "Added coin $coinId")
            }
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    fun removeFavoriteCoin(coinId: String) {
        Firebase.firestore.collection("favorites")
            .document(uid)
            .update("coins", FieldValue.arrayRemove(coinId))
            .addOnSuccessListener {
                allFavoriteCoinIds = allFavoriteCoinIds - coinId
            }
            .addOnFailureListener { e -> e.printStackTrace() }
    }


    fun initFavoritesDocument() {
        val docRef = Firebase.firestore.collection("favorites").document(uid)
        docRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val emptyData = mapOf(
                    "coins" to emptyList<String>(),
                    "topHolders" to emptyList<String>()
                )
                docRef.set(emptyData)
                    .addOnSuccessListener {
                        Log.d("Favorites", "Initialized favorites document for user $uid")
                        allFavoriteCoinIds = emptyList()
//                        allFavoriteTopHoldersIds = emptyList()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Favorites", "Failed to initialize favorites doc", e)
                    }
            } else {
                allFavoriteCoinIds = document.get("coins") as? List<String> ?: emptyList()
//                allFavoriteTopHoldersIds =
//                    document.get("topHolders") as? List<String> ?: emptyList()


                Log.d("Favorites", "Favorites document loaded for user $uid")
                Log.d("Favorites List", allFavoriteCoinIds.toString())
            }
        }.addOnFailureListener { e ->
            Log.e("Favorites", "Error checking favorites document", e)
        }
    }
}