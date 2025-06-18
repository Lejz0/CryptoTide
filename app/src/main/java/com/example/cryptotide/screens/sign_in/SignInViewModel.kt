package com.example.cryptotide.screens.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService,
) : CryptoTideAppViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun onSignInClick(onSuccessNavigation: () -> Unit) {
        launchCatching {
            accountService.signIn(email, password)
            onSuccessNavigation()
        }
    }
}