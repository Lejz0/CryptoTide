//package com.example.cryptotide.screens.favorites
//
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import com.example.cryptotide.CryptoTideAppViewModel
//import com.google.firebase.Firebase
//import com.google.firebase.auth.auth
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.firestore
//import dagger.hilt.android.lifecycle.HiltViewModel
//import jakarta.inject.Inject
//
//@HiltViewModel
//class FavoritesScreenViewModel @Inject constructor() : CryptoTideAppViewModel() {
//    private val uid = Firebase.auth.currentUser?.uid ?: ""
//
//    var allFavoriteCoinIds by mutableStateOf<List<String>>(emptyList())
//        private set
////    var allFavoriteTopHoldersIds by mutableStateOf<List<String>>(emptyList())
////        private set
////    val allFavoriteTopHolders by mutableStateOf<List<Cryptocurrency>>(emptyList())
////        private set
//
//    init {
//        initFavoritesDocument()
//    }
//
//    fun addFavoriteCoin(coinId: String) {
//        Firebase.firestore.collection("favorites")
//            .document(uid)
//            .update("coins", FieldValue.arrayUnion(coinId))
//            .addOnSuccessListener {
//                allFavoriteCoinIds = allFavoriteCoinIds + coinId
//                Log.d("Favorites", "Added coin $coinId")
//            }
//            .addOnFailureListener { e -> e.printStackTrace() }
//    }
//
//    fun removeFavoriteCoin(coinId: String) {
//        Firebase.firestore.collection("favorites")
//            .document(uid)
//            .update("coins", FieldValue.arrayRemove(coinId))
//            .addOnSuccessListener {
//                allFavoriteCoinIds = allFavoriteCoinIds - coinId
//            }
//            .addOnFailureListener { e -> e.printStackTrace() }
//    }
//
////    fun addFavoriteTopHolder(holderId: String) {
////        Firebase.firestore.collection("favorites")
////            .document(uid)
////            .update("topHolders", FieldValue.arrayUnion(holderId))
////            .addOnSuccessListener {
////                allFavoriteTopHoldersIds = allFavoriteTopHoldersIds + holderId
////                Log.d("Favorites", "Added holder $holderId")
////            }
////            .addOnFailureListener { e -> e.printStackTrace() }
////    }
////
////    fun removeFavoriteTopHolder(holderId: String) {
////        Firebase.firestore.collection("favorites")
////            .document(uid)
////            .update("topHolders", FieldValue.arrayRemove(holderId))
////            .addOnSuccessListener {
////                allFavoriteTopHoldersIds = allFavoriteTopHoldersIds - holderId
////            }
////            .addOnFailureListener { e -> e.printStackTrace() }
////    }
//
//    fun initFavoritesDocument() {
//        val docRef = Firebase.firestore.collection("favorites").document(uid)
//        docRef.get().addOnSuccessListener { document ->
//            if (!document.exists()) {
//                val emptyData = mapOf(
//                    "coins" to emptyList<String>(),
//                    "topHolders" to emptyList<String>()
//                )
//                docRef.set(emptyData)
//                    .addOnSuccessListener {
//                        Log.d("Favorites", "Initialized favorites document for user $uid")
//                        allFavoriteCoinIds = emptyList()
//                        allFavoriteTopHoldersIds = emptyList()
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("Favorites", "Failed to initialize favorites doc", e)
//                    }
//            } else {
//                allFavoriteCoinIds = document.get("coins") as? List<String> ?: emptyList()
//                allFavoriteTopHoldersIds =
//                    document.get("topHolders") as? List<String> ?: emptyList()
//
//
//                Log.d("Favorites", "Favorites document loaded for user $uid")
//                Log.d("Favorites List", allFavoriteCoinIds.toString())
//            }
//        }.addOnFailureListener { e ->
//            Log.e("Favorites", "Error checking favorites document", e)
//        }
//    }
////
////    fun fetchCryptocurrencies() {
////        allFavoriteCoinIds.forEach { coinId ->
////            launchCatching {
////                val result = RetrofitInstance.api.getCoinDetails(coinId = coinId)
////                Log.d("all coins", result.toString())
////                allFavoriteCoins = (allFavoriteCoins + result) as List<CryptoDetailed>
////            }
////        }
////        Log.d("All coins", allFavoriteCoins.toString())
////    }
//
//}