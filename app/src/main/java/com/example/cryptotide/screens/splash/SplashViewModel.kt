package com.example.cryptotide.screens.splash

import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : CryptoTideAppViewModel() {

    fun onAppStart(onUserNavigation:() -> Unit, onLoggedOutNavigation:() -> Unit) {
        if (accountService.hasUser())
        {
            onUserNavigation()
        }
        else onLoggedOutNavigation()
    }
}