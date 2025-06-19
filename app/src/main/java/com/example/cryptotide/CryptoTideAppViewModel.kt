package com.example.cryptotide

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class CryptoTideAppViewModel : ViewModel() {

    var errorMessage by mutableStateOf<String?>(null)
        protected set

    var isLoading by mutableStateOf<Boolean>(false)
        protected set

    fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Log.d(ERROR_TAG, throwable.message.orEmpty())
                errorMessage = throwable.message ?: "Unknown error"
            }
        ) {
            errorMessage = null
            block()
        }

    companion object {
        const val ERROR_TAG = "CRYPTO TIDE APP ERROR"
    }
}